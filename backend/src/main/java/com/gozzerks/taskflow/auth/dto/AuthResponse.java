package com.gozzerks.taskflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a successful register or login.")
public record AuthResponse(
        @Schema(description = "Signed JWT. Send as Authorization: Bearer <token> on protected endpoints.",
                example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGljZSJ9.signature")
        String token,

        @Schema(description = "Username the token authenticates as.", example = "alice")
        String username
) {
}
