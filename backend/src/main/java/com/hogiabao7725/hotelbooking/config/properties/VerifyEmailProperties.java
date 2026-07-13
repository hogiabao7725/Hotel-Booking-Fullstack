package com.hogiabao7725.hotelbooking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.verify-email")
public record VerifyEmailProperties(Duration expiration, String prefix) {}
