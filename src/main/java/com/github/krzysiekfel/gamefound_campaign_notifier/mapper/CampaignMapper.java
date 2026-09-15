package com.github.krzysiekfel.gamefound_campaign_notifier.mapper;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import org.mapstruct.Mapper;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignResponse toCampaignResponse(ApiGetCrowdfundingProjectResult project);

    default OffsetDateTime map(Instant value) {
        return value == null ? null : value.atOffset(java.time.ZoneOffset.UTC);
    }

    default URI map(String value) {
        return value == null ? null : URI.create(value);
    }
}
