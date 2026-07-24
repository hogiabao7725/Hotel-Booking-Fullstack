package com.hogiabao7725.hotelbooking.event;

public record UserRegisteredEvent(
        String email,
        String fullName,
        String token
) {
}
