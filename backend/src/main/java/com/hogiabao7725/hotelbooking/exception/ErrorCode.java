package com.hogiabao7725.hotelbooking.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 400 - Bad Request
    VALIDATION_FAILED("Invalid request data"),
    INVALID_ONE_TIME_TOKEN("Token invalid or expired"),

    // 401 - Unauthorized
    INVALID_CREDENTIALS("Email or password is incorrect"),
    ACCESS_TOKEN_EXPIRED("Access token expired"),
    ACCESS_TOKEN_INVALID("Invalid access token"),

    // 403 - Forbidden
    ACCOUNT_DISABLED("Account is inactive or deleted."),
    ACCOUNT_BANNED("Account has been banned."),

    // 404 - Not Found
    RESOURCE_NOT_FOUND("Resource not found"),

    // 409 - Conflict
    EMAIL_ALREADY_EXISTS("Email is already in use"),

    // 500 - Internal server
    EMAIL_SENDING_FAILED("Failed to send email"),
    ROLE_NOT_FOUND("Role not found"),
    INTERNAL_ERROR("Internal server error");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
