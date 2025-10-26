package com.example.examtp.dto.restaurant.create;

import com.example.examtp.entities.Restaurant;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateRestaurantMapper {
    Restaurant toEntity(CreateRestaurantDto createRestaurantDto);

    CreateRestaurantDto toDto(Restaurant restaurant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Restaurant partialUpdate(CreateRestaurantDto createRestaurantDto, @MappingTarget Restaurant restaurant);
}