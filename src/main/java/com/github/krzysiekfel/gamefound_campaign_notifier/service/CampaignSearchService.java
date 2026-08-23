package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.GamefoundClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.CampaignMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignSearchService {

    private final GamefoundClient gamefoundClient;
    private final CampaignMapper campaignMapper;

    public CampaignSearchService(GamefoundClient gamefoundClient, CampaignMapper campaignMapper) {
        this.gamefoundClient = gamefoundClient;
        this.campaignMapper = campaignMapper;
    }

    public List<CampaignResponse> findCampaignsByCreatorName(String creatorName) {
        List<ApiGetCrowdfundingProjectResult> allActive = gamefoundClient.getActiveCrowdfundingProjects();

        return allActive.stream()
                .filter(project -> project.creatorName().toLowerCase().contains(creatorName.toLowerCase()))
                .map(campaignMapper::toCampaignResponse)
                .toList();
    }

    public List<CampaignResponse> findCampaignsByGameName(String gameName) {
        // TODO: resolve game to publisher via BGG, then search campaigns by publisher in gamefound
        return List.of();
    }

    public List<CampaignResponse> findCampaignsByBggUsername(String bggUsername) {
        // TODO: fetch user's collection from BGG, resolve publishers, search campaigns for each
        return List.of();
    }
}
