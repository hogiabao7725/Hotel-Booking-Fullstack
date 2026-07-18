package com.hogiabao7725.hotelbooking.dto.response.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        String refreshToken
) {}
