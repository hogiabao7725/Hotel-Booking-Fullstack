package com.hogiabao7725.hotelbooking.dto.response.facility;

import java.time.Instant;

public record FacilityResponse(
        Long id,
        String name,
        String iconUrl,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}
