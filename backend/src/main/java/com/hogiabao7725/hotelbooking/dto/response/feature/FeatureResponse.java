package com.hogiabao7725.hotelbooking.dto.response.feature;

import java.time.Instant;

public record FeatureResponse(
        Long id,
        String name,
        String iconUrl,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}
