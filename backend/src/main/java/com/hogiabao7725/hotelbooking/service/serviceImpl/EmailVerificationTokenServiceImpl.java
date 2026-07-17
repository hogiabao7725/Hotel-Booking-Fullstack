package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.VerifyEmailProperties;
import com.hogiabao7725.hotelbooking.service.OneTimeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements OneTimeTokenService {

    private final StringRedisTemplate redisTemplate;
    private final VerifyEmailProperties props;

    @Override
    public String createToken(String payload) {
        String token = UUID.randomUUID().toString();
        String redisKey = props.prefix() + token;
        redisTemplate.opsForValue().set(redisKey, payload, props.expiration());
        return token;
    }

    @Override
    public Optional<String> consumeToken(String token) {
        String redisKey = props.prefix() + token;
        String payload = redisTemplate.opsForValue().getAndDelete(redisKey);
        return Optional.ofNullable(payload);
    }
}
