package com.hogiabao7725.hotelbooking.exception;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(
                ErrorCode.RESOURCE_NOT_FOUND,
                String.format("%s not found with %s: '%s'", resourceName, field, value)
        );
    }
}