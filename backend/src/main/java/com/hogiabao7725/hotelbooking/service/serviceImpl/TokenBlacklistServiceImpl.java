package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.JwtProperties;
import com.hogiabao7725.hotelbooking.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public void add(String token, Duration remainingTtl) {
        if (remainingTtl.isNegative() || remainingTtl.isZero()) {
            return;
        }
        String redisKey = jwtProperties.accessBlackListPrefix() + token;
        redisTemplate.opsForValue().set(redisKey, "1", remainingTtl);
    }

    @Override
    public boolean contains(String token) {
        String redisKey = jwtProperties.accessBlackListPrefix() + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
}
