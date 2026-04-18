package com.gozzerks.taskflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials for authenticating an existing user.")
public record LoginRequest(
        @Schema(description = "Username of the existing account.", example = "alice")
        @NotBlank(message = "Username must not be blank")
        String username,

        @Schema(description = "Plaintext password.", example = "correct-horse-battery-staple")
        @NotBlank(message = "Password must not be blank")
        String password
) {
}
