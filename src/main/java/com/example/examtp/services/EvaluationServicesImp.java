package com.example.examtp.services;

import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.create.CreateEvaluationMapper;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationMapper;
import com.example.examtp.repositories.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EvaluationServicesImp implements EvaluationServices{
    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final CreateEvaluationMapper createEvaluationMapper;
    private final S3UploadService uploadService;

    @Autowired
    public EvaluationServicesImp(EvaluationRepository evaluationRepository, EvaluationMapper evaluationMapper, CreateEvaluationMapper createEvaluationMapper, S3UploadService uploadService) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.createEvaluationMapper = createEvaluationMapper;
        this.uploadService = uploadService;
    }


    @Override
    public List<EvaluationDto> getAllEvaluations() {
        return List.of();
    }

    @Override
    public List<EvaluationDto> getEvaluationsByKeyword(String keyword) {
        return List.of();
    }

    @Override
    public List<EvaluationDto> getMyEvaluations() {
        return List.of();
    }

    @Override
    public EvaluationDto createEvaluation(CreateEvaluationDto createEvaluationDto) {
        return null;
    }

    @Override
    public void deleteEvaluation(Long evaluationId) {

    }
}
