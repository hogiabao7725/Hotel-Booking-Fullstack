package com.hogiabao7725.hotelbooking.service;

import java.util.Optional;

// Dedicated service for managing email verification tokens in Redis
public interface OneTimeTokenService {
    String createToken(String payload);
    Optional<String> consumeToken(String token);
}
