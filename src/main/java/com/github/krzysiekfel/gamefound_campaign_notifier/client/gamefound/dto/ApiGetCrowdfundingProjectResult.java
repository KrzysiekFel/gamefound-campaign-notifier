package com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ApiGetCrowdfundingProjectResult(
        int backerCount,
        int updateCount,
        int rewardCount,
        Instant campaignStartDate,
        Instant campaignEndDate,
        BigDecimal campaignGoal,
        String creatorName,
        String creatorUrlName,
        String currencyShortName,
        BigDecimal fundsGathered,
        String projectName,
        String projectUrlName,
        String shortDescription,
        int commentCount,
        String projectHomeUrl,
        String projectImageUrl
) {}
