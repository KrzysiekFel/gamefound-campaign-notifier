package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import java.time.Instant;

public record AccessToken(String value, Instant expiresAt) {
}
