package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtConfigTest {

    private final JwtConfig jwtConfig = new JwtConfig();

    @Test
    void shouldRejectSecretShorterThan256Bits() {
        // GIVEN
        JwtProperties properties = new JwtProperties("too-short-secret", Duration.ofHours(24), "issuer");

        // WHEN / THEN
        assertThatThrownBy(() -> jwtConfig.jwtSecretKey(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }

    @Test
    void shouldMapRolesClaimToSpringRoleAuthorities() {
        // GIVEN
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("1")
                .claim(TokenService.ROLES_CLAIM, List.of("ADMIN"))
                .build();

        // WHEN
        AbstractAuthenticationToken authentication = jwtConfig.jwtAuthenticationConverter().convert(jwt);

        // THEN
        assertThat(authentication.getName()).isEqualTo("1");
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }
}
