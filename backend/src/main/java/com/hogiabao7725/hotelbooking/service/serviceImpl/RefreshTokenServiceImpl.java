package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String create(String payload, String prefix, Duration expiration) {
        String token = UUID.randomUUID().toString();
        String redisKey = prefix + token;
        redisTemplate.opsForValue().set(redisKey, payload, expiration);
        return token;
    }

    @Override
    public Optional<String> getPayload(String token, String prefix) {
        String redisKey = prefix + token;
        String payload = redisTemplate.opsForValue().get(redisKey);
        return Optional.ofNullable(payload);
    }

    @Override
    public void revoke(String token, String prefix) {
        String redisKey = prefix + token;
        redisTemplate.delete(redisKey);
    }
}
