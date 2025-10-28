package com.example.examtp.exceptions;


import com.example.examtp.dto.exception.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAppException(AppException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto("Access denied: You don't have permission to access this resource"));
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto("Authentication failed: " + e.getMessage()));
    }
}