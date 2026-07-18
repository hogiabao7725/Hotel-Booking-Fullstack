package com.hogiabao7725.hotelbooking.security.config;

public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/auth/verify-email",
            "/auth/resend-verification",
            "/auth/refresh",
            "/health",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}
