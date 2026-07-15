package com.hogiabao7725.hotelbooking.dto.response.auth;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String tokenType
) {}
