package com.gozzerks.taskflow.auth;

import com.gozzerks.taskflow.auth.dto.AuthResponse;
import com.gozzerks.taskflow.auth.dto.LoginRequest;
import com.gozzerks.taskflow.auth.dto.RegisterRequest;
import com.gozzerks.taskflow.domain.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@Tag(name = "Authentication", description = "Register and log in to obtain a JWT.")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/register")
    @Operation(summary = "Register a new user and return a JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered and JWT returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed or username already taken",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping(path = "/login")
    @Operation(summary = "Authenticate a user and return a JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials accepted and JWT returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }
}
