package com.example.examtp.dto.evaluation.read;

import com.example.examtp.entities.Evaluation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RestaurantMapper.class})
public interface EvaluationMapper {
    Evaluation toEntity(EvaluationDto evaluationDto);

    EvaluationDto toDto(Evaluation evaluation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Evaluation partialUpdate(EvaluationDto evaluationDto, @MappingTarget Evaluation evaluation);
}