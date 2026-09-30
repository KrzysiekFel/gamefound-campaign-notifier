package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.BggClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggCollectionItem;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.GamefoundClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.CampaignMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CampaignSearchService {

    private final GamefoundClient gamefoundClient;
    private final BggClient bggClient;
    private final CampaignMapper campaignMapper;
    private final PublisherService publisherService;
    private final GameService gameService;

    public CampaignSearchService(GamefoundClient gamefoundClient,
                                 BggClient bggClient,
                                 CampaignMapper campaignMapper,
                                 PublisherService publisherService,
                                 GameService gameService) {
        this.gamefoundClient = gamefoundClient;
        this.bggClient = bggClient;
        this.campaignMapper = campaignMapper;
        this.publisherService = publisherService;
        this.gameService = gameService;
    }

    public List<CampaignResponse> findAllActiveCampaigns() {
        return gamefoundClient.getActiveCrowdfundingProjects().stream()
                .map(campaignMapper::toCampaignResponse)
                .toList();
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
        List<BggCollectionItem> userCollection = bggClient.getUserCollection(bggUsername, "boardgame").items();
        if (userCollection == null || userCollection.isEmpty()) {
            return List.of();
        }

        // TODO: BGG rate-limits requests; for large collections this fires one request per game.
        Set<String> publisherNames = userCollection.stream()
                .map(item -> publisherService.getPublisherByGameId((long) item.objectid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (publisherNames.isEmpty()) {
            return List.of();
        }

        List<ApiGetCrowdfundingProjectResult> allActiveCampaigns = gamefoundClient.getActiveCrowdfundingProjects();

    // Ze dla wszystkich aktywnych kampani z gamefound wez te, gdzie dla jej tworcy , ten
    //  tworca pasuje do ktoregos z tworcow usera kolekcji
    return allActiveCampaigns.stream()
        .filter(
            project -> {
              String creatorName = project.creatorName().toLowerCase();
              return publisherNames.stream()
                  .anyMatch(publisherName -> creatorName.contains(publisherName.toLowerCase()));
            })
        .map(campaignMapper::toCampaignResponse)
        .toList();
    }

    private List<CampaignResponse> findActiveCampaignsForPublisher(String publisherName) {
        List<ApiGetCrowdfundingProjectResult> allActiveCampaigns = gamefoundClient.getActiveCrowdfundingProjects();

        return allActiveCampaigns.stream()
                .filter(project -> project.creatorName().toLowerCase().contains(publisherName.toLowerCase()))
                .map(campaignMapper::toCampaignResponse)
                .toList();
    }
}
