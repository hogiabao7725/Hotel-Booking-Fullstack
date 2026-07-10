package com.hogiabao7725.hotelbooking.dto.response.auth;

public record RegisterResponse(
        Long accountId,
        String email,
        String fullName,
        String role
) {
}
