package com.example.examtp.services;

import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationDto;

import java.util.List;

public interface EvaluationServices {
    List<EvaluationDto> getAllEvaluations();

    List<EvaluationDto> getEvaluationsByKeyword(String keyword, int maxResults);

    List<EvaluationDto> getMyEvaluations(String name);

    EvaluationDto createEvaluation(CreateEvaluationDto createEvaluationDto);

    void deleteEvaluation(Long evaluationId);
}
