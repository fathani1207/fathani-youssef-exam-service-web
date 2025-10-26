package com.example.examtp.exceptions;


import com.example.examtp.dto.exception.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAppException(AppException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorDto(e.getMessage()));
    }

}