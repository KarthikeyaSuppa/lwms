package com.lwms.backend.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalSecurityExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
        SecurityErrorCode code = ex.getErrorCode() != null ? ex.getErrorCode() : SecurityErrorCode.SEC_AUTH_FAILURE;
        Map<String, Object> body = new HashMap<>();
        body.put("code", code.getCode());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(code.getStatus().value()).body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", SecurityErrorCode.SEC_USER_NOT_FOUND.getCode());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(SecurityErrorCode.SEC_USER_NOT_FOUND.getStatus().value()).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", SecurityErrorCode.SEC_BAD_CREDENTIALS.getCode());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(SecurityErrorCode.SEC_BAD_CREDENTIALS.getStatus().value()).body(body);
    }
} 