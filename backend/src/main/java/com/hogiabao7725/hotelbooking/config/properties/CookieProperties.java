package com.hogiabao7725.hotelbooking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(
        CookieDetail refresh
) {
    public record CookieDetail(
            String name,
            boolean secure,
            String sameSite,
            String path
    ) {}
}
