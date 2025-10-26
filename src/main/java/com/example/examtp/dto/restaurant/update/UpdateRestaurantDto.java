package com.example.examtp.dto.restaurant.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link com.example.examtp.entities.Restaurant}
 */
public record UpdateRestaurantDto(@Size(min = 5) @NotBlank String name,
                                  @Size(min = 15, max = 255) @NotBlank String address) implements Serializable {
}