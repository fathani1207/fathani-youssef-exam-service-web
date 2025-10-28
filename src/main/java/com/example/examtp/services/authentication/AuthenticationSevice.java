package com.example.examtp.services.authentication;

import com.example.examtp.dto.authentication.LoginDto;

public interface AuthenticationSevice {
    Object login(LoginDto loginDto);
}
