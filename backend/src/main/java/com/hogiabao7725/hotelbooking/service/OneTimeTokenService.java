package com.hogiabao7725.hotelbooking.service;

import java.time.Duration;

// Infrastructure layer for one-time token management.
public interface OneTimeTokenService {
    String createToken(String payload, String prefix, Duration expiration);
    String consumeToken(String token, String prefix);
}
