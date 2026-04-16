package com.gozzerks.taskflow.auth;

import com.gozzerks.taskflow.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService")
class JwtServiceTest {

    private static final String SECRET =
            "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtNjRieA==";
    private static final String OTHER_SECRET =
            "b3RoZXItc2VjcmV0LW90aGVyLXNlY3JldC1vdGhlci1zZWNyZXQtb3RoZXItc2VjcmV0LW90aGVyLXNlYzY0eA==";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L);
        user = new User(
                UUID.randomUUID(),
                "alice",
                "hash",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("generateToken then parseClaims returns the original subject and user id")
    void roundTrip() {
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("alice");
        assertThat(jwtService.extractUserId(claims)).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("parseClaims fails when the token was signed with a different key")
    void rejectsTamperedSignature() {
        JwtService otherService = new JwtService(OTHER_SECRET, 3_600_000L);
        String foreignToken = otherService.generateToken(user);

        assertThatThrownBy(() -> jwtService.parseClaims(foreignToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("parseClaims fails once the token has expired")
    void rejectsExpiredToken() {
        JwtService shortLived = new JwtService(SECRET, 1L);
        String token = shortLived.generateToken(user);

        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThatThrownBy(() -> jwtService.parseClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
