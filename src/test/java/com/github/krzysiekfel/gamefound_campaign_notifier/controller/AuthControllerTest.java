package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.LoginRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.LoginResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.RegisterRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.UserResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.UserMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.AccessToken;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String USERNAME = "krzysiek";
    private static final String EMAIL = "test.user@example.com";
    private static final String PASSWORD = "secret123";

    @Mock
    private AuthService authService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRegisterUserAndReturnCreated() {
        // GIVEN
        User user = new User(USERNAME, EMAIL, "$2a$10$hash");
        UserResponse response = new UserResponse().username(USERNAME).email(EMAIL);
        when(authService.register(USERNAME, EMAIL, PASSWORD)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        // WHEN
        ResponseEntity<UserResponse> result = authController.register(
                new RegisterRequest().username(USERNAME).email(EMAIL).password(PASSWORD));

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnBearerTokenOnLogin() {
        // GIVEN
        Instant expiresAt = Instant.parse("2026-10-19T10:00:00Z");
        when(authService.login(EMAIL, PASSWORD)).thenReturn(new AccessToken("jwt-value", expiresAt));

        // WHEN
        ResponseEntity<LoginResponse> result = authController.login(
                new LoginRequest().email(EMAIL).password(PASSWORD));

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getAccessToken()).isEqualTo("jwt-value");
        assertThat(result.getBody().getTokenType()).isEqualTo("Bearer");
        assertThat(result.getBody().getExpiresAt()).isEqualTo(OffsetDateTime.parse("2026-10-19T10:00:00Z"));
    }
}
