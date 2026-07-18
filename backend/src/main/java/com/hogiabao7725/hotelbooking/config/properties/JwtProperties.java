package com.hogiabao7725.hotelbooking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        Duration accessExpiration,
        String accessBlackListPrefix
){}
