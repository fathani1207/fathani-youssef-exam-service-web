package com.example.examtp.services;

import com.example.examtp.dto.restaurant.create.CreateRestaurantDto;
import com.example.examtp.dto.restaurant.create.CreateRestaurantMapper;
import com.example.examtp.dto.restaurant.read.RestaurantDto;
import com.example.examtp.dto.restaurant.read.RestaurantMapper;
import com.example.examtp.dto.restaurant.update.UpdateRestaurantDto;
import com.example.examtp.entities.Restaurant;
import com.example.examtp.exceptions.AppException;
import com.example.examtp.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RestaurantServicesImp implements RestaurantServices {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final CreateRestaurantMapper createRestaurantMapper;
    private final S3UploadService uploadService;


    @Autowired
    public RestaurantServicesImp(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper, CreateRestaurantMapper createRestaurantMapper, S3UploadService uploadService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.createRestaurantMapper = createRestaurantMapper;
        this.uploadService = uploadService;
    }


    @Override
    public List<RestaurantDto> getAllRestaurants() {
        return this.restaurantRepository.findAll().stream().map(restaurantMapper::toDto).toList();
    }

    @Override
    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restau = this.restaurantRepository.findById(id).orElseThrow(() -> new AppException("Restaurant not found", HttpStatus.NOT_FOUND));
        return this.restaurantMapper.toDto(restau);
    }

    @Override
    public RestaurantDto getRestaurantByName(String name) {
        Restaurant restaurant = this.restaurantRepository.findByName(name).orElseThrow(() -> new AppException("Restaurant not found", HttpStatus.NOT_FOUND));
        return this.restaurantMapper.toDto(restaurant);
    }

    @Override
    public RestaurantDto createRestaurant(CreateRestaurantDto createRestaurantDto) {
        return null;
    }

    @Override
    public RestaurantDto updateRestaurant(Long id, UpdateRestaurantDto updateRestaurantDto) {
        return null;
    }
}
