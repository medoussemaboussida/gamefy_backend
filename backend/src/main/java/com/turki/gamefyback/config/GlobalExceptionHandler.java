package com.turki.gamefyback.config;

import com.turki.gamefyback.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false)); // Get path from WebRequest

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // You can add more @ExceptionHandler methods for other custom or standard exceptions (e.g., ValidationException)
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    //     Map<String, Object> body = new HashMap<>();
    //     body.put("timestamp", LocalDateTime.now());
    //     body.put("message", "An unexpected error occurred: " + ex.getMessage());
    //     body.put("path", request.getDescription(false));
    //     return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    // }
}