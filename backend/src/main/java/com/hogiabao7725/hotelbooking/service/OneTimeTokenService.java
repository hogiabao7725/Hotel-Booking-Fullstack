package com.hogiabao7725.hotelbooking.service;

import java.util.Optional;

// Infrastructure layer for one-time token management.
public interface OneTimeTokenService {
    String createToken(String payload);
    Optional<String> consumeToken(String token);
}
