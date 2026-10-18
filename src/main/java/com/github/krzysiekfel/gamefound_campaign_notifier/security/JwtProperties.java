package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) {
}
