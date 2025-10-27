package com.example.examtp.services.uploadS3;

import org.springframework.web.multipart.MultipartFile;


public interface S3UploadService {
    String uploadRestaurantImage(MultipartFile file);

    String uploadEvaluationImage(MultipartFile file);
}
