package com.hogiabao7725.hotelbooking.dto.request.facility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CreateFacilityRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 5, max = 50, message = "Name must be between {min} and {max} characters")
        String name,

        String description,

        @NotNull(message = "Icon file cannot be blank")
        MultipartFile icon
) {}
