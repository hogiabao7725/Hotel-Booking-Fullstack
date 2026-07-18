package com.hogiabao7725.hotelbooking.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Refresh token cannot be blank")
        String refreshToken
) {
}
