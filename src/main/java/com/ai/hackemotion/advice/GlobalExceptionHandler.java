package com.ai.hackemotion.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    String bodyStatus = "status";
    String bodyError = "error";
    String bodyMessage = "message";

    // RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(bodyStatus, HttpStatus.BAD_REQUEST.value());
        body.put(bodyError, "Bad Request");
        body.put(bodyMessage, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(bodyStatus, HttpStatus.FORBIDDEN.value());
        body.put(bodyError, "Forbidden");
        body.put(bodyMessage, "You don't have permission to access this resource");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(bodyStatus, HttpStatus.BAD_REQUEST.value());
        body.put(bodyError, "Validation Error");

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));

        body.put(bodyMessage, "Validation failed");
        body.put("details", validationErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // All other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(bodyStatus, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(bodyError, "Internal Server Error");
        body.put(bodyMessage, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
