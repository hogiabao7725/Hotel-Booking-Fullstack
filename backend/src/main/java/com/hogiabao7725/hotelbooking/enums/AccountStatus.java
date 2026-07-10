package com.hogiabao7725.hotelbooking.enums;


public enum AccountStatus implements ValueEnum {
    INACTIVE(0),
    ACTIVE(1),
    BANNED(2),
    DELETED(3);

    private final int value;

    AccountStatus(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
