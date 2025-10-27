package com.example.examtp.controllers;


import com.example.examtp.dto.restaurant.create.CreateRestaurantDto;
import com.example.examtp.dto.restaurant.read.RestaurantDto;
import com.example.examtp.dto.restaurant.update.UpdateRestaurantDto;
import com.example.examtp.services.restaurant.RestaurantServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantServices restaurantServices;

    @Autowired
    public RestaurantController(RestaurantServices restaurantServices) {
        this.restaurantServices = restaurantServices;
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String hello() {
        return "Hello, Restaurants!";
    }

    @GetMapping
    public List<RestaurantDto> getRestaurants() {
        return this.restaurantServices.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public RestaurantDto getRestaurantById(@PathVariable Long id) {
        return this.restaurantServices.getRestaurantById(id);
    }

    @GetMapping("/name/{name}")
    public RestaurantDto getRestaurantByName(@PathVariable String name) {
        return this.restaurantServices.getRestaurantByName(name);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @ModelAttribute CreateRestaurantDto restaurantDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.restaurantServices.createRestaurant(restaurantDto));
    }

    @PutMapping("/{id}")
    public RestaurantDto updateRestaurant(@PathVariable Long id, @RequestBody UpdateRestaurantDto restaurantDto) {
        return this.restaurantServices.updateRestaurant(id, restaurantDto);
    }
}
