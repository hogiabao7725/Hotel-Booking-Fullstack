package com.hogiabao7725.hotelbooking.service;

import java.time.Duration;

public interface TokenBlacklistService {
    void add(String token, Duration remainingTtl);
    boolean contains(String token);
}
