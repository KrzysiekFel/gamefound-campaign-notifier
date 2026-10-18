package com.github.krzysiekfel.gamefound_campaign_notifier.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapUserAlreadyExistsToConflict() {
        // WHEN
        ProblemDetail result = handler.handleUserAlreadyExists(
                new UserAlreadyExistsException("Email is already registered"));

        // THEN
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(result.getDetail()).isEqualTo("Email is already registered");
    }

    @Test
    void shouldMapInvalidCredentialsToUnauthorized() {
        // WHEN
        ProblemDetail result = handler.handleInvalidCredentials(new InvalidCredentialsException());

        // THEN
        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(result.getDetail()).isEqualTo("Invalid email or password");
    }

    @Test
    void shouldMapUserNotFoundToNotFound() {
        // WHEN
        ProblemDetail result = handler.handleUserNotFound(new UserNotFoundException(7L));

        // THEN
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getDetail()).isEqualTo("User 7 not found");
    }
}
