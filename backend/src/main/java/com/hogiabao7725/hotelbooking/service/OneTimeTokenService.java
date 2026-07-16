package com.hogiabao7725.hotelbooking.service;

import java.time.Duration;
import java.util.Optional;

// Infrastructure layer for one-time token management.
public interface OneTimeTokenService {
    String createToken(String payload, String prefix, Duration expiration);
    Optional<String> consumeToken(String token, String prefix);
}
