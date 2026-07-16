package com.hogiabao7725.hotelbooking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.refresh-token")
public record RefreshTokenProperties(Duration expiration, String prefix) {}
