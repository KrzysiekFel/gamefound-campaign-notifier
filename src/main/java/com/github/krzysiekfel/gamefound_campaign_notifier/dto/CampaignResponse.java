package com.github.krzysiekfel.gamefound_campaign_notifier.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CampaignResponse(
        String projectName,
        String creatorName,
        Instant campaignStartDate,
        Instant campaignEndDate,
        String shortDescription,
        BigDecimal fundsGathered,
        BigDecimal campaignGoal,
        String currencyShortName,
        int backerCount,
        String projectHomeUrl,
        String projectImageUrl
) {
}
