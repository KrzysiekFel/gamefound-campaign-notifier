package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.Role;
import com.github.krzysiekfel.gamefound_campaign_notifier.exception.InvalidCredentialsException;
import com.github.krzysiekfel.gamefound_campaign_notifier.exception.UserAlreadyExistsException;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.UserRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.AccessToken;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String USERNAME = "krzysiek";
    private static final String EMAIL = "test.user@example.com";
    private static final String RAW_PASSWORD = "secret123";
    private static final String PASSWORD_HASH = "$2a$10$hash";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserWithNormalizedEmailAndHashedPassword() {
        // GIVEN
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        User result = authService.register(USERNAME, "  Test.User@Example.COM ", RAW_PASSWORD);

        // THEN
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPasswordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(userRepository).existsByEmail(EMAIL);
    }

    @Test
    void shouldRejectRegistrationWhenEmailIsTaken() {
        // GIVEN
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(USERNAME, EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email is already registered");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectRegistrationWhenUsernameIsTaken() {
        // GIVEN
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(USERNAME, EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username is already taken");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldTranslateUniqueConstraintViolationIntoUserAlreadyExists() {
        // GIVEN – concurrent registration: both checks passed, the database rejects the second insert
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(USERNAME, EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email or username is already taken");
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        // GIVEN
        User user = user();
        AccessToken token = new AccessToken("jwt-value", Instant.parse("2026-10-19T10:00:00Z"));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, PASSWORD_HASH)).thenReturn(true);
        when(tokenService.generate(user)).thenReturn(token);

        // WHEN
        AccessToken result = authService.login("Test.User@Example.com", RAW_PASSWORD);

        // THEN
        assertThat(result).isEqualTo(token);
    }

    @Test
    void shouldRejectLoginWhenPasswordIsWrong() {
        // GIVEN
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(passwordEncoder.matches("wrong-password", PASSWORD_HASH)).thenReturn(false);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(EMAIL, "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
        verifyNoInteractions(tokenService);
    }

    @Test
    void shouldRejectLoginWhenEmailIsUnknownWithTheSameMessage() {
        // GIVEN
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login("nobody@example.com", RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
        verifyNoInteractions(passwordEncoder, tokenService);
    }

    private static User user() {
        User user = new User(USERNAME, EMAIL, PASSWORD_HASH);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}
