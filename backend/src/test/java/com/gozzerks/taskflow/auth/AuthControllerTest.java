package com.gozzerks.taskflow.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozzerks.taskflow.auth.dto.AuthResponse;
import com.gozzerks.taskflow.auth.dto.LoginRequest;
import com.gozzerks.taskflow.auth.dto.RegisterRequest;
import com.gozzerks.taskflow.controllers.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /auth/register - returns token and username on success")
    void registerReturnsToken() throws Exception {
        RegisterRequest request = new RegisterRequest("alice", "password1");
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("a.b.c", "alice"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("a.b.c")))
                .andExpect(jsonPath("$.username", is("alice")));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register - returns 400 when username is taken")
    void registerReturns400OnDuplicate() throws Exception {
        RegisterRequest request = new RegisterRequest("alice", "password1");
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username is already taken"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("already taken")));
    }

    @Test
    @DisplayName("POST /auth/register - returns 400 with field errors on validation failure")
    void registerReturns400OnValidation() throws Exception {
        RegisterRequest request = new RegisterRequest("", "short");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors").isArray());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - returns token and username on success")
    void loginReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest("alice", "password1");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("a.b.c", "alice"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("a.b.c")))
                .andExpect(jsonPath("$.username", is("alice")));

        verify(authService).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - returns 401 on bad credentials")
    void loginReturns401OnBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("alice", "wrongpassword");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("Invalid")));
    }

    @Test
    @DisplayName("POST /auth/login - returns 400 with field errors when fields blank")
    void loginReturns400OnBlankFields() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors").isArray());

        verify(authService, never()).authenticate(any(LoginRequest.class));
    }
}
