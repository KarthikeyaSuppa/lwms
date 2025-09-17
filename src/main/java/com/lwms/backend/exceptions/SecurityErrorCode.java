package com.lwms.backend.exceptions;

import org.springframework.http.HttpStatus;

public enum SecurityErrorCode {
    SEC_UNAUTHORIZED("SEC_UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Unauthorized"),
    SEC_FORBIDDEN("SEC_FORBIDDEN", HttpStatus.FORBIDDEN, "Forbidden"),
    SEC_BAD_CREDENTIALS("SEC_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Bad credentials"),
    SEC_USER_NOT_FOUND("SEC_USER_NOT_FOUND", HttpStatus.UNAUTHORIZED, "User not found"),
    SEC_AUTH_FAILURE("SEC_AUTH_FAILURE", HttpStatus.UNAUTHORIZED, "Authentication failed");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    SecurityErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
    public String getDefaultMessage() { return defaultMessage; }
} 