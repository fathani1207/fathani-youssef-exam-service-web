package com.example.examtp.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class S3UploadServiceImp implements S3UploadService{
    @Override
    public String uploadRestaurantImage(MultipartFile file) {
        return "";
    }

    @Override
    public String uploadEvaluationImage(List<MultipartFile> files) {
        return "";
    }


    private String uploadFile(MultipartFile file) {
        return "";
    }
}
