package com.hogiabao7725.hotelbooking.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Auth
    EMAIL_ALREADY_EXISTS("Email is already in use"),
    INVALID_ONE_TIME_TOKEN("Token invalid or expired"),
    INVALID_CREDENTIALS("Email or password is incorrect"),
    ACCOUNT_DISABLED("Account is inactive or deleted."),
    ACCOUNT_BANNED("Account has been banned."),

    // User
    ROLE_NOT_FOUND("Role not found"),

    // Email
    EMAIL_SENDING_FAILED("Failed to send email"),

    // Common
    RESOURCE_NOT_FOUND("Resource not found"),
    VALIDATION_FAILED("Invalid request data"),
    INTERNAL_ERROR("Internal server error"),

    // JWT
    ACCESS_TOKEN_EXPIRED("Access token expired"),
    ACCESS_TOKEN_INVALID("Invalid access token");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
