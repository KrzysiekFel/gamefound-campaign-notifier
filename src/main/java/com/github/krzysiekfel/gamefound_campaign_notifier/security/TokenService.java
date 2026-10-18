package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    public static final String ROLES_CLAIM = "roles";

    public TokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public AccessToken generate(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.expiration());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("email", user.getEmail())
                .claim(ROLES_CLAIM, List.of(user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AccessToken(tokenValue, expiresAt);
    }
}
