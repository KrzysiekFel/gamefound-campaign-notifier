package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import java.util.UUID;

public record EmailRequestedEvent(UUID emailRequestId) {
}
