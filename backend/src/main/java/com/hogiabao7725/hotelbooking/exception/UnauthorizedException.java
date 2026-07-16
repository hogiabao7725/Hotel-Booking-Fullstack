package com.hogiabao7725.hotelbooking.exception;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
