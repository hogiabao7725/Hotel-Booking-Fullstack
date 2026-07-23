package com.hogiabao7725.hotelbooking.dto.request.auth;

import com.hogiabao7725.hotelbooking.utils.StringNormalizer;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest (
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
        public LoginRequest {
                email = StringNormalizer.trimToNull(email);
        }
}
