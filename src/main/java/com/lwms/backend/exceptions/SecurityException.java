package com.lwms.backend.exceptions;

public class SecurityException extends RuntimeException {
    private final SecurityErrorCode errorCode;

    public SecurityException(SecurityErrorCode errorCode) {
        super(errorCode != null ? errorCode.getDefaultMessage() : null);
        this.errorCode = errorCode;
    }

    public SecurityException(SecurityErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SecurityErrorCode getErrorCode() { return errorCode; }
} 