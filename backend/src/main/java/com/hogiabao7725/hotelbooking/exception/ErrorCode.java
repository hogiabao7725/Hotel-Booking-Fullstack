package com.hogiabao7725.hotelbooking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===== System, Common =====
    INVALID_INPUT("Invalid input parameters", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== Auth =====
    AUTH_INVALID_CREDENTIALS("Invalid email or password", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_TOKEN_EXPIRED("Access token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_TOKEN_INVALID("Invalid access token", HttpStatus.UNAUTHORIZED),
    AUTH_REFRESH_TOKEN_INVALID("Refresh token is invalid or expired", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_ONE_TIME_TOKEN("Verification token is invalid or expired", HttpStatus.BAD_REQUEST),
    AUTH_UNAUTHORIZED("Full authentication is required to access this resource", HttpStatus.UNAUTHORIZED),
    AUTH_FORBIDDEN("You do not have permission to access this resource", HttpStatus.FORBIDDEN),

    // ===== Account =====
    ACCOUNT_EMAIL_ALREADY_EXISTS("Email is already in use", HttpStatus.CONFLICT),
    ACCOUNT_ALREADY_ACTIVE("Account is already active", HttpStatus.CONFLICT),
    ACCOUNT_INACTIVE("Account is inactive", HttpStatus.FORBIDDEN),
    ACCOUNT_BANNED("Account has been banned", HttpStatus.FORBIDDEN),
    ACCOUNT_ROLE_NOT_FOUND("Required role not found", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== Facility =====
    FACILITY_NAME_ALREADY_EXISTS("hYeah Yea", HttpStatus.CONFLICT),

    // ===== Infra =====
    EMAIL_SENDING_FAILED("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_EMPTY("Uploaded file must not be empty", HttpStatus.BAD_REQUEST),
    FILE_INVALID_PATH("Invalid storage path", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("Failed to upload file to storage", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED("Failed to delete file from storage", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String defaultMessage, HttpStatus httpStatus) {
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
