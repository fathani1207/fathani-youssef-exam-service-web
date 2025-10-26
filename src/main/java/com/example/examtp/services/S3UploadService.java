package com.example.examtp.services;

import org.springframework.web.multipart.MultipartFile;


public interface S3UploadService {
    String uploadRestaurantImage(MultipartFile file);

    String uploadEvaluationImage(MultipartFile file);
}
