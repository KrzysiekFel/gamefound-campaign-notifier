package com.github.krzysiekfel.gamefound_campaign_notifier.mapper;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.CampaignResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignResponse toCampaignResponse(ApiGetCrowdfundingProjectResult project);
}
