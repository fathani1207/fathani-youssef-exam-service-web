package com.example.examtp.services.authentication;

import com.example.examtp.dto.authentication.LoginDto;

public interface AuthenticationSevice {
    String authenticate(LoginDto loginDto);
}
