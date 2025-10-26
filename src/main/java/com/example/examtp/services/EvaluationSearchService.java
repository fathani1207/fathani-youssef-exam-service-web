package com.example.examtp.services;

import com.example.examtp.dto.evaluation.read.EvaluationDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EvaluationSearchService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationSearchService.class);
    private static final String INDEX_DIR = "lucene-index/evaluations";

    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;
    private IndexSearcher indexSearcher;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(INDEX_DIR);
            this.directory = FSDirectory.open(path);
            this.analyzer = new StandardAnalyzer();
            IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
            cfg.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            this.indexWriter = new IndexWriter(directory, cfg);
            this.indexReader = DirectoryReader.open(indexWriter);
            this.indexSearcher = new IndexSearcher(indexReader);
            log.info("Evaluation Lucene index initialized at: {}", INDEX_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Evaluation Lucene index", e);
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (indexReader != null) indexReader.close();
            if (indexWriter != null) indexWriter.close();
            if (directory != null) directory.close();
            log.info("Evaluation Lucene index closed successfully");
        } catch (IOException e) {
            log.error("Error closing Evaluation Lucene index", e);
        }
    }

    /**
     * Index an evaluation for searching
     */
    public void indexEvaluation(long id, String author, String content, int note,
                                List<String> evaluationImagesUrls, Long restaurantId) {
        try {
            // Delete existing document if it exists (for updates)
            indexWriter.deleteDocuments(new Term("id", String.valueOf(id)));

            Document doc = new Document();

            // ID fields - both for filtering and storage
            doc.add(new LongPoint("id", id));
            doc.add(new StoredField("id", id));

            // Searchable text fields
            doc.add(new TextField("author", author == null ? "" : author, Field.Store.YES));
            doc.add(new TextField("content", content == null ? "" : content, Field.Store.YES));

            // Note field - both for range queries and storage
            doc.add(new IntPoint("note", note));
            doc.add(new StoredField("note", note));

            // Restaurant ID for filtering by restaurant
            if (restaurantId != null) {
                doc.add(new LongPoint("restaurantId", restaurantId));
                doc.add(new StoredField("restaurantId", restaurantId));
            }

            // Store image URLs as comma-separated string
            if (evaluationImagesUrls != null && !evaluationImagesUrls.isEmpty()) {
                String imagesStr = String.join(",", evaluationImagesUrls);
                doc.add(new StringField("evaluationImagesUrls", imagesStr, Field.Store.YES));
            } else {
                doc.add(new StringField("evaluationImagesUrls", "", Field.Store.YES));
            }

            indexWriter.addDocument(doc);
            indexWriter.commit();

            refreshReader();
            log.debug("Indexed evaluation: {} - author: {}", id, author);
        } catch (IOException e) {
            throw new RuntimeException("Failed to index evaluation: " + id, e);
        }
    }

    /**
     * Remove an evaluation from the index
     */
    public void deleteEvaluation(long id) {
        try {
            indexWriter.deleteDocuments(new Term("id", String.valueOf(id)));
            indexWriter.commit();
            refreshReader();
            log.debug("Deleted evaluation from index: {}", id);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete evaluation from index: " + id, e);
        }
    }

    /**
     * Search evaluations by keyword in content and author
     */
    public List<EvaluationDto> searchEvaluations(String keyword, int maxResults) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String escapedKeyword = QueryParser.escape(keyword.trim());

            QueryParser authorParser = new QueryParser("author", analyzer);
            QueryParser contentParser = new QueryParser("content", analyzer);

            Query authorQuery = authorParser.parse(escapedKeyword);
            Query contentQuery = contentParser.parse(escapedKeyword);

            // Search in both author and content
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(authorQuery, BooleanClause.Occur.SHOULD);
            builder.add(contentQuery, BooleanClause.Occur.SHOULD);

            Query combinedQuery = builder.build();

            return executeSearch(combinedQuery, maxResults);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Failed to search evaluations with keyword: " + keyword, e);
        }
    }

    /**
     * Execute search query and return results
     */
    private List<EvaluationDto> executeSearch(Query query, int maxResults) throws IOException {
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        List<EvaluationDto> results = new ArrayList<>(topDocs.scoreDocs.length);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.storedFields().document(scoreDoc.doc);

            String author = doc.get("author");
            String content = doc.get("content");
            int note = doc.getField("note").numericValue().intValue();

            String imagesStr = doc.get("evaluationImagesUrls");
            List<String> imageUrls = imagesStr != null && !imagesStr.isEmpty()
                    ? Arrays.asList(imagesStr.split(","))
                    : new ArrayList<>();

            results.add(new EvaluationDto(author, content, note, imageUrls));
        }

        log.debug("Found {} evaluation results", results.size());
        return results;
    }

    /**
     * Refresh the index reader to see latest changes
     */
    private void refreshReader() throws IOException {
        DirectoryReader newReader = DirectoryReader.openIfChanged(indexReader, indexWriter);
        if (newReader != null) {
            indexReader.close();
            indexReader = newReader;
            indexSearcher = new IndexSearcher(indexReader);
        }
    }
}