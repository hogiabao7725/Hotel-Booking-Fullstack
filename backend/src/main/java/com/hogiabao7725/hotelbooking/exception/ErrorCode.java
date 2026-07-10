package com.hogiabao7725.hotelbooking.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS("Email is already in use"),
    RESOURCE_NOT_FOUND("Requested resource could not be found"),
    ROLE_NOT_FOUND("Role does not exist in the system"),
    VALIDATION_FAILED("Invalid request data"),
    INTERNAL_ERROR("An unexpected internal server error occurred");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
