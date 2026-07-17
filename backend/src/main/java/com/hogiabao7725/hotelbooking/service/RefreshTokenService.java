package com.hogiabao7725.hotelbooking.service;

import java.util.Optional;

public interface RefreshTokenService {
    String create(String payload);
    Optional<String> getPayload(String token);
    void revoke(String token);
}
