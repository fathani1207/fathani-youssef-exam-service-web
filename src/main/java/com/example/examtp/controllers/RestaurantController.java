package com.example.examtp.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @GetMapping
    public String hello() {
        return "Hello, Restaurants!";
    }
}
