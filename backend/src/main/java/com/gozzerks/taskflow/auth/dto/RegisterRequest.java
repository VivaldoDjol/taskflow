package com.gozzerks.taskflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credentials for creating a new user account.")
public record RegisterRequest(
        @Schema(description = "Desired username, unique per instance.", example = "alice")
        @NotBlank(message = "Username must not be blank")
        @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
        String username,

        @Schema(description = "Plaintext password. Stored hashed with BCrypt.", example = "correct-horse-battery-staple")
        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password
) {
}
