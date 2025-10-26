package com.example.examtp.services;

import com.example.examtp.exceptions.AppException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@Service
@Slf4j
public class S3UploadServiceImp implements S3UploadService{

    private final MinioClient minioClient;

    private String restaurantBucket = "exo-cover";
    private String evaluationBucket = "exo-screenshot";

    public S3UploadServiceImp(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey)
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        this.minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();

        log.info("Bucked restaurant exists ? {}", this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(restaurantBucket).build()));
        log.info("Bucked evaluation exists ? {}", this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(evaluationBucket).build()));
    }


    @Override
    public String uploadRestaurantImage(MultipartFile file) {
        return this.uploadFile(file.getOriginalFilename(), file, file.getContentType(), restaurantBucket);
    }

    @Override
    public String uploadEvaluationImage(MultipartFile file) {
        return this.uploadFile(file.getOriginalFilename(), file, file.getContentType(), evaluationBucket);
    }


    private String uploadFile(String objectName, MultipartFile file, String contentType, String bucketName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            InputStream inputStream = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build());

            // Generate presigned URL (example expiry: 24 hours = 86400 seconds)
            int expirySeconds = 24 * 60 * 60;
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirySeconds)
                            .build());

            return url;
        } catch (Exception e) {
            throw new AppException("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
