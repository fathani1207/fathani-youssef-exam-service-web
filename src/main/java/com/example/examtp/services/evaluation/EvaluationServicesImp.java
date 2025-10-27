package com.example.examtp.services.evaluation;

import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.create.CreateEvaluationMapper;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationMapper;
import com.example.examtp.entities.Evaluation;
import com.example.examtp.entities.Restaurant;
import com.example.examtp.exceptions.AppException;
import com.example.examtp.repositories.EvaluationRepository;
import com.example.examtp.repositories.RestaurantRepository;
import com.example.examtp.services.uploadS3.S3UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class EvaluationServicesImp implements EvaluationServices{
    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final CreateEvaluationMapper createEvaluationMapper;
    private final S3UploadService uploadService;
    private final RestaurantRepository restaurantRepository;
    private final EvaluationSearchService evaluationSearchService;

    @Autowired
    public EvaluationServicesImp(EvaluationRepository evaluationRepository, EvaluationMapper evaluationMapper, CreateEvaluationMapper createEvaluationMapper, S3UploadService uploadService, RestaurantRepository restaurantRepository, EvaluationSearchService evaluationSearchService) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.createEvaluationMapper = createEvaluationMapper;
        this.uploadService = uploadService;
        this.restaurantRepository = restaurantRepository;
        this.evaluationSearchService = evaluationSearchService;
    }


    @Override
    public List<EvaluationDto> getAllEvaluations() {
        return this.evaluationRepository.findAll().stream().map(this.evaluationMapper::toDto).toList();
    }

    @Override
    public List<EvaluationDto> getEvaluationsByKeyword(String keyword, int maxResults) {
        return this.evaluationSearchService.searchEvaluations(keyword, maxResults);
    }

    @Override
    public List<EvaluationDto> getMyEvaluations(String name) {
        List<Evaluation> evaluations = this.evaluationRepository.findByAuthor(name);
        if (evaluations.isEmpty()){
            throw new AppException("No evaluations found for the given author", HttpStatus.NOT_FOUND);
        }
        return evaluations.stream().map(this.evaluationMapper::toDto).toList();
    }

    @Override
    public EvaluationDto createEvaluation(CreateEvaluationDto createEvaluationDto) {
        Evaluation evaluation = createEvaluationMapper.toEntity(createEvaluationDto);
        Restaurant restaurant = restaurantRepository.findById(createEvaluationDto.restaurantId()).orElseThrow(()->new AppException("Restaurant not found", HttpStatus.NOT_FOUND));
        evaluation.setRestaurant(restaurant);
        List<MultipartFile> files = createEvaluationDto.evaluationImages();
        if (!files.isEmpty()){
            List<String> evaluationImageUrls = files.stream().map(this.uploadService::uploadEvaluationImage).toList();
            evaluation.setEvaluationImagesUrls(evaluationImageUrls);
        }
        evaluation = this.evaluationRepository.save(evaluation);
        this.evaluationSearchService.indexEvaluation(evaluation.getId(), evaluation.getAuthor(), evaluation.getContent(), evaluation.getNote(), evaluation.getEvaluationImagesUrls(), restaurant.getName());
        return this.evaluationMapper.toDto(evaluation);
    }

    @Override
    public void deleteEvaluation(Long evaluationId) {
        if (this.evaluationRepository.existsById(evaluationId)){
            this.evaluationRepository.deleteById(evaluationId);
            this.evaluationSearchService.deleteEvaluation(evaluationId);
        } else {
            throw new AppException("Evaluation not found", HttpStatus.NOT_FOUND);
        }
    }
}
