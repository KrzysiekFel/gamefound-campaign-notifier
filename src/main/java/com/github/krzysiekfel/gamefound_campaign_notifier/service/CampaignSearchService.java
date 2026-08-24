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
    private final PublisherService publisherService;

    public CampaignSearchService(GamefoundClient gamefoundClient,
                                 CampaignMapper campaignMapper,
                                 PublisherService publisherService) {
        this.gamefoundClient = gamefoundClient;
        this.campaignMapper = campaignMapper;
        this.publisherService = publisherService;
    }

    public List<CampaignResponse> findCampaignsByPublisherName(String publisherName) {
        return findActiveCampaignsForPublisher(publisherName);
    }

    public List<CampaignResponse> findCampaignsByGameName(String gameName) {
        // TODO: szukaj gry po nazwie w bazie i zwracaj bggId
        // int bggGameId = gameService.findByName(gameName).getBggId();

        // TODO: tymczasowo hardkodowane ID do testów, usunąć po implementacji GameService
        int bggGameId = 151347;  // gra Millennium Blades (od Level 99) -> to powinno doprowadzić do nowej kampani Level 99 -> Dead by Daylight: The Board Game - Auris Box
        String publisherName = publisherService.getPublisherByGameId(bggGameId);

        if (publisherName == null) {
            return List.of();
        }

        return findActiveCampaignsForPublisher(publisherName);
    }

    public List<CampaignResponse> findCampaignsByBggUsername(String bggUsername) {
        // TODO: pobrać kolekcję usera z BGG
        // TODO: dla każdej gry z kolekcji znaleźć publishera (rate limit BGG 5s)
        // TODO: zebrać unikalnych publisherów
        // TODO: dla każdego publishera szukać kampanii na Gamefound

        return List.of();
    }

    private List<CampaignResponse> findActiveCampaignsForPublisher(String publisherName) {
        List<ApiGetCrowdfundingProjectResult> allActive = gamefoundClient.getActiveCrowdfundingProjects();

        return allActive.stream()
                .filter(project -> project.creatorName().toLowerCase().contains(publisherName.toLowerCase()))
                .map(campaignMapper::toCampaignResponse)
                .toList();
    }
}
