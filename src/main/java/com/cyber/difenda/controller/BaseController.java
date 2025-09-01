package com.cyber.difenda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BaseController {

    protected <T> ResponseEntity<T> buildResponse(T body) {
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
                .status(500)
                .body("Internal Server Error: " + e.getMessage());
    }
}

