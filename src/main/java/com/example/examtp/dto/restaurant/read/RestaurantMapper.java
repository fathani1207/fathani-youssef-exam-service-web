package com.example.examtp.dto.restaurant.read;

import com.example.examtp.entities.Restaurant;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EvaluationMapper.class})
public interface RestaurantMapper {
    Restaurant toEntity(RestaurantDto restaurantDto);

    @AfterMapping
    default void linkEvaluations(@MappingTarget Restaurant restaurant) {
        restaurant.getEvaluations().forEach(evaluation -> evaluation.setRestaurant(restaurant));
    }

    RestaurantDto toDto(Restaurant restaurant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Restaurant partialUpdate(RestaurantDto restaurantDto, @MappingTarget Restaurant restaurant);
}