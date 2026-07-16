package com.hogiabao7725.hotelbooking.exception;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}
