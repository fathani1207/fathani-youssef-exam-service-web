package com.example.examtp.services;

import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.create.CreateEvaluationMapper;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationMapper;
import com.example.examtp.entities.Evaluation;
import com.example.examtp.exceptions.AppException;
import com.example.examtp.repositories.EvaluationRepository;
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

    @Autowired
    public EvaluationServicesImp(EvaluationRepository evaluationRepository, EvaluationMapper evaluationMapper, CreateEvaluationMapper createEvaluationMapper, S3UploadService uploadService) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.createEvaluationMapper = createEvaluationMapper;
        this.uploadService = uploadService;
    }


    @Override
    public List<EvaluationDto> getAllEvaluations() {
        return this.evaluationRepository.findAll().stream().map(this.evaluationMapper::toDto).toList();
    }

    @Override
    public List<EvaluationDto> getEvaluationsByKeyword(String keyword) {
        return List.of();
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
        List<MultipartFile> files = createEvaluationDto.evaluationImages();
        if (!files.isEmpty()){
            List<String> evaluationImageUrls = files.stream().map(this.uploadService::uploadEvaluationImage).toList();
            evaluation.setEvaluationImagesUrls(evaluationImageUrls);
        }
        return this.evaluationMapper.toDto(evaluationRepository.save(evaluation));
    }

    @Override
    public void deleteEvaluation(Long evaluationId) {
        if (this.evaluationRepository.existsById(evaluationId)){
            this.evaluationRepository.deleteById(evaluationId);
        } else {
            throw new AppException("Evaluation not found", HttpStatus.NOT_FOUND);
        }
    }
}
