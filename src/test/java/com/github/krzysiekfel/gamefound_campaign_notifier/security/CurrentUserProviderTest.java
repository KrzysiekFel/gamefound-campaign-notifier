package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnUserIdFromTokenSubject() {
        // GIVEN – what BearerTokenAuthenticationFilter puts into the context after a valid token
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("42")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        // WHEN
        Long result = currentUserProvider.getCurrentUserId();

        // THEN
        assertThat(result).isEqualTo(42L);
    }

    @Test
    void shouldFailWhenNobodyIsAuthenticated() {
        // WHEN / THEN
        assertThatThrownBy(currentUserProvider::getCurrentUserId)
                .isInstanceOf(IllegalStateException.class);
    }
}
