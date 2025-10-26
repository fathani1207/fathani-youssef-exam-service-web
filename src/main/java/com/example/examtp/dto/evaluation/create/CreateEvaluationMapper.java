package com.example.examtp.dto.evaluation.create;

import com.example.examtp.entities.Evaluation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateEvaluationMapper {
    Evaluation toEntity(CreateEvaluationDto createEvaluationDto);

    CreateEvaluationDto toDto(Evaluation evaluation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Evaluation partialUpdate(CreateEvaluationDto createEvaluationDto, @MappingTarget Evaluation evaluation);
}