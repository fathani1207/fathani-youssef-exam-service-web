package com.example.examtp.dto.evaluation.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.examtp.entities.Evaluation}
 */
public record CreateEvaluationDto(@Size(min = 5, max = 50) @NotBlank String author, @Size(max = 255) @NotBlank String content, @Positive @Range(min = 0, max = 3) int note,
                                  List<MultipartFile> evaluationImages) implements Serializable {
}