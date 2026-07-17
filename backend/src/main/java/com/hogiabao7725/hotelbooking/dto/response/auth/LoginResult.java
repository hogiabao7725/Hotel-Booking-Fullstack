package com.hogiabao7725.hotelbooking.dto.response.auth;

public record LoginResult(
        String accessToken,
        String refreshToken
) {}
