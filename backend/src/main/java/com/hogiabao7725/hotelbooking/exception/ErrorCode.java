package com.hogiabao7725.hotelbooking.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Auth
    EMAIL_ALREADY_EXISTS("Email is already in use"),
    INVALID_ONE_TIME_TOKEN("The provided token is invalid, expired, or already used"),

    // User
    ROLE_NOT_FOUND("Role does not exist in the system"),

    // Email
    EMAIL_SENDING_FAILED("Failed to send email. Please try again later"),

    // Common
    RESOURCE_NOT_FOUND("Requested resource could not be found"),
    VALIDATION_FAILED("Invalid request data"),
    INTERNAL_ERROR("An unexpected internal server error occurred");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
