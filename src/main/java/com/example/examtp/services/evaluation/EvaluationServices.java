package com.example.examtp.services.evaluation;

import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EvaluationServices {
    List<EvaluationDto> getAllEvaluations();

    List<EvaluationDto> getEvaluationsByKeyword(String keyword, int maxResults);

    List<EvaluationDto> getMyEvaluations(String name);

    EvaluationDto createEvaluation(CreateEvaluationDto createEvaluationDto, String username);

    void deleteEvaluation(Long evaluationId);

    void deleteMyEvaluation(long id, String name);
}
