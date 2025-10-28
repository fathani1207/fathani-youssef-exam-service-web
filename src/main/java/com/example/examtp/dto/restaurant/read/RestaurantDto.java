package com.example.examtp.dto.restaurant.read;

import com.example.examtp.dto.evaluation.read.EvaluationDto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.examtp.entities.Restaurant}
 */
public record RestaurantDto(long id, String name, String address,
                            String restaurantImageUrl, float averageRating,
                            List<EvaluationDto> evaluations) implements Serializable {
}