package com.gozzerks.taskflow.auth;

import com.gozzerks.taskflow.auth.dto.AuthResponse;
import com.gozzerks.taskflow.auth.dto.LoginRequest;
import com.gozzerks.taskflow.auth.dto.RegisterRequest;
import com.gozzerks.taskflow.domain.entities.User;
import com.gozzerks.taskflow.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        User user = new User(
                UUID.randomUUID(),
                request.username(),
                passwordEncoder.encode(request.password()),
                LocalDateTime.now()
        );
        User saved = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(saved), saved.getUsername());
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new AuthResponse(jwtService.generateToken(user), user.getUsername());
    }
}
