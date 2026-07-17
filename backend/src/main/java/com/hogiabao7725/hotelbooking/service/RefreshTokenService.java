package com.hogiabao7725.hotelbooking.service;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenService {
    String create(String payload, String prefix, Duration expiration);
    Optional<String> getPayload(String token, String prefix);
    void revoke(String token, String prefix);
}
