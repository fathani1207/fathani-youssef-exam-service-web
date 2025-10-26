package com.example.examtp.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {
    @GetMapping
    public String hello() {
        return "Hello, Evaluations!";
    }
}
