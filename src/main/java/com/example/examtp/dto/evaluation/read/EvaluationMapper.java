package com.example.examtp.dto.evaluation.read;

import com.example.examtp.entities.Evaluation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EvaluationMapper {
    @Mapping(source = "restaurantName", target = "restaurant.name")
    Evaluation toEntity(EvaluationDto evaluationDto);

    @Mapping(source = "restaurant.name", target = "restaurantName")
    EvaluationDto toDto(Evaluation evaluation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "restaurantName", target = "restaurant.name")
    Evaluation partialUpdate(EvaluationDto evaluationDto, @MappingTarget Evaluation evaluation);
}