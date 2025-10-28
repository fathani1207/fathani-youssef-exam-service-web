package com.example.examtp.controllers;


import com.example.examtp.dto.authentication.LoginDto;
import com.example.examtp.services.authentication.AuthenticationSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationSevice authService;

    @Autowired
    public AuthController(AuthenticationSevice authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginDto loginDto) {
        return this.authService.login(loginDto);
    }

}
