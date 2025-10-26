package com.example.examtp.dto.evaluation.read;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.examtp.entities.Evaluation}
 */
public record EvaluationDto(String author, String content, int note, List<String> evaluationImagesUrls) implements Serializable {
}