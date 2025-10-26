package com.example.examtp.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3UploadService {
    String uploadRestaurantImage(MultipartFile file);

    String uploadEvaluationImage(List<MultipartFile> files);
}
