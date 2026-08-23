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

    public List<CampaignResponse> findCampaignsByCreatorName(String creatorName) {
        List<ApiGetCrowdfundingProjectResult> allActive = gamefoundClient.getActiveCrowdfundingProjects();

        return allActive.stream()
                .filter(project -> project.creatorName().toLowerCase().contains(creatorName.toLowerCase()))
                .map(campaignMapper::toCampaignResponse)
                .toList();
    }

    public List<CampaignResponse> findCampaignsByGameName(String gameName) {
        // TODO: szukaj gry po nazwie w bazie i zwracaj bggId
        // int bggGameId = gameService.findByName(gameName).getBggId();

        // TODO: tymczasowo hardkodowane ID do testów, usunąć po implementacji GameService
        int bggGameId = 0;

        return findCampaignsByPublisherOfGame(bggGameId);
    }

    public List<CampaignResponse> findCampaignsByPublisherOfGame(int bggGameId) {
        String publisher = publisherService.getPublisherByGameId(bggGameId);

        if (publisher == null) {
            return List.of();
        }

        List<ApiGetCrowdfundingProjectResult> allActive = gamefoundClient.getActiveCrowdfundingProjects();

        return allActive.stream()
                .filter(project -> project.creatorName().toLowerCase()
                        .contains(publisher.toLowerCase()))
                .map(campaignMapper::toCampaignResponse)
                .toList();
    }

    public List<CampaignResponse> findCampaignsByBggUsername(String bggUsername) {
        // TODO: pobrać kolekcję usera z BGG
        // BggCollectionResponse collection = bggClient.getUserCollection(bggUsername, 1, "boardgame");

        // TODO: dla każdej gry z kolekcji znaleźć publishera (rate limit BGG 5s)
        // TODO: zebrać unikalnych publisherów
        // TODO: dla każdego publishera szukać kampanii na Gamefound

        return List.of();
    }
}
