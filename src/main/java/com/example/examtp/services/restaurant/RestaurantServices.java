package com.example.examtp.services.restaurant;

import com.example.examtp.dto.restaurant.create.CreateRestaurantDto;
import com.example.examtp.dto.restaurant.read.RestaurantDto;
import com.example.examtp.dto.restaurant.update.UpdateRestaurantDto;

import java.util.List;

public interface RestaurantServices {
    List<RestaurantDto> getAllRestaurants();

    RestaurantDto getRestaurantById(Long id);

    RestaurantDto getRestaurantByName(String name);

    RestaurantDto createRestaurant(CreateRestaurantDto createRestaurantDto);

    RestaurantDto updateRestaurant(Long id, UpdateRestaurantDto updateRestaurantDto);
}
