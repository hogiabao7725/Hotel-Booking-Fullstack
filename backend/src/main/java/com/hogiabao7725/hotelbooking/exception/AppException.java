package com.hogiabao7725.hotelbooking.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    public static AppException notFound(String resourceName, String field, Object value) {
        String message = String.format("%s not found with %s: '%s'", resourceName, field, value);
        return new AppException(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
