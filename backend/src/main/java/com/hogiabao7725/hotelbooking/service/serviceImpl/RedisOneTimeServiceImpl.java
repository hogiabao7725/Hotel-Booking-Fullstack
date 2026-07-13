package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.exception.BusinessException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.service.OneTimeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisOneTimeServiceImpl implements OneTimeTokenService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String createToken(String payload, String prefix, Duration expiration) {
        String token = UUID.randomUUID().toString();
        String redisKey = prefix + token;
        redisTemplate.opsForValue().set(redisKey, payload, expiration);
        return token;
    }

    @Override
    public String consumeToken(String token, String prefix) {
        String redisKey = prefix + token;
        String payload = redisTemplate.opsForValue().get(redisKey);

        if (payload == null) {
            throw new BusinessException(ErrorCode.INVALID_ONE_TIME_TOKEN);
        }

        redisTemplate.delete(redisKey);
        return payload;
    }

}
