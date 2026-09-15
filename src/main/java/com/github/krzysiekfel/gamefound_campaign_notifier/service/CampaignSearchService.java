package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.GamefoundClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.CampaignMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignSearchService {

    private final GamefoundClient gamefoundClient;
    private final CampaignMapper campaignMapper;
    private final PublisherService publisherService;
    private final GameService gameService;

    public CampaignSearchService(GamefoundClient gamefoundClient,
                                 CampaignMapper campaignMapper,
                                 PublisherService publisherService,
                                 GameService gameService) {
        this.gamefoundClient = gamefoundClient;
        this.campaignMapper = campaignMapper;
        this.publisherService = publisherService;
        this.gameService = gameService;
    }

    public List<CampaignResponse> findCampaignsByPublisherName(String publisherName) {
        return findActiveCampaignsForPublisher(publisherName);
    }

    public List<CampaignResponse> findCampaignsByGameName(String gameName) {

        Optional<Game> game = gameService.findByName(gameName);
        if (game.isEmpty()) {
            return List.of();
        }

        String publisherName = publisherService.getPublisherByGameId(game.get().getBggId());

        // TODO: na przyszlosc dopisz do bazy kto byl autorem

        if (publisherName == null) {
            return List.of();
        }

        return findActiveCampaignsForPublisher(publisherName);
    }

    public List<CampaignResponse> findCampaignsByBggUsername(String bggUsername) {
        // Przemyslec czy
        // TODO: pobrać kolekcję usera z BGG
        // TODO: dla każdej gry z kolekcji znaleźć publishera (rate limit BGG 5s)
        // TODO: zebrać unikalnych publisherów
        // TODO: dla każdego publishera szukać kampanii na Gamefound

        return List.of();
    }

    private List<CampaignResponse> findActiveCampaignsForPublisher(String publisherName) {
        List<ApiGetCrowdfundingProjectResult> allActive = gamefoundClient.getActiveCrowdfundingProjects();

        return allActive.stream()
                .filter(project -> project.creatorName().toLowerCase().contains(publisherName.toLowerCase()))  // TODO premature optimization
                .map(campaignMapper::toCampaignResponse)
                .toList();
    }
}
