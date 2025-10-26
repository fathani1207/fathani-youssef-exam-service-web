package com.example.examtp.dto.restaurant.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * DTO for {@link com.example.examtp.entities.Restaurant}
 */
public record CreateRestaurantDto(@Size(min = 5, max = 90) @NotBlank String name,
                                  @Size(min = 15, max = 255) @NotBlank String address,
                                  @NotNull MultipartFile restaurantImage) implements Serializable {
}