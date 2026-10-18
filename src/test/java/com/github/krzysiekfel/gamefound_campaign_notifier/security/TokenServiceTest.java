package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Real encoder and decoder from JwtConfig (no mocks) – checks that a generated token passes verification
class TokenServiceTest {

    private static final String SECRET = "test-only-jwt-secret-at-least-32-bytes-long";
    private static final String ISSUER = "gamefound-campaign-notifier";
    private static final Duration EXPIRATION = Duration.ofHours(24);

    private final JwtConfig jwtConfig = new JwtConfig();

    private TokenService tokenService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(SECRET, EXPIRATION, ISSUER);
        tokenService = tokenService(properties);
        jwtDecoder = jwtConfig.jwtDecoder(jwtConfig.jwtSecretKey(properties), properties);
    }

    @Test
    void shouldGenerateSignedTokenWithUserClaims() {
        // GIVEN
        User user = user(Role.ADMIN);

        // WHEN
        AccessToken token = tokenService.generate(user);
        Jwt jwt = jwtDecoder.decode(token.value());

        // THEN
        assertThat(jwt.getHeaders()).containsEntry("alg", "HS256");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo(ISSUER);
        assertThat(jwt.getSubject()).isEqualTo("1");
        assertThat(jwt.getClaimAsString("email")).isEqualTo("test.user@example.com");
        assertThat(jwt.getClaimAsStringList(TokenService.ROLES_CLAIM)).containsExactly("ADMIN");
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(EXPIRATION);
        assertThat(jwt.getExpiresAt()).isEqualTo(token.expiresAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {
        // GIVEN
        JwtProperties otherProperties = new JwtProperties("another-secret-that-is-at-least-32-bytes", EXPIRATION, ISSUER);
        AccessToken foreignToken = tokenService(otherProperties).generate(user(Role.USER));

        // WHEN / THEN
        assertThatThrownBy(() -> jwtDecoder.decode(foreignToken.value()))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid signature");
    }

    @Test
    void shouldRejectExpiredToken() {
        // GIVEN – issued a day ago, expired 5 minutes ago (beyond the decoder's 60 s clock skew tolerance)
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject("1")
                .issuedAt(now.minus(Duration.ofDays(1)))
                .expiresAt(now.minus(Duration.ofMinutes(5)))
                .build();
        JwtEncoder encoder = jwtConfig.jwtEncoder(
                jwtConfig.jwtSecretKey(new JwtProperties(SECRET, EXPIRATION, ISSUER)));
        String expiredToken = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        // WHEN / THEN
        assertThatThrownBy(() -> jwtDecoder.decode(expiredToken))
                .isInstanceOf(JwtValidationException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void shouldRejectTokenFromDifferentIssuer() {
        // GIVEN
        JwtProperties otherIssuer = new JwtProperties(SECRET, EXPIRATION, "someone-else");
        AccessToken foreignToken = tokenService(otherIssuer).generate(user(Role.USER));

        // WHEN / THEN
        assertThatThrownBy(() -> jwtDecoder.decode(foreignToken.value()))
                .isInstanceOf(JwtValidationException.class);
    }

    private TokenService tokenService(JwtProperties properties) {
        return new TokenService(jwtConfig.jwtEncoder(jwtConfig.jwtSecretKey(properties)), properties);
    }

    private static User user(Role role) {
        User user = new User("krzysiek", "test.user@example.com", "$2a$10$hash");
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", role);
        return user;
    }
}
