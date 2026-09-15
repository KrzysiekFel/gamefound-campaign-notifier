package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.controller.api.CampaignsApi;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.CampaignSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
public class CampaignSearchController implements CampaignsApi {

    private final CampaignSearchService campaignSearchService;

    public CampaignSearchController(CampaignSearchService campaignSearchService) {
        this.campaignSearchService = campaignSearchService;
    }

    @Override
    public ResponseEntity<List<CampaignResponse>> searchCampaigns(
            String publisherName, String gameName, String bggUsername) {

        long providedCount = Stream.of(publisherName, gameName, bggUsername)
                .filter(Objects::nonNull)
                .count();

        if (providedCount != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide exactly one of: publisherName, gameName, bggUsername");
        }

        if (publisherName != null) {
            return ResponseEntity.ok(campaignSearchService.findCampaignsByPublisherName(publisherName));
        }
        if (gameName != null) {
            return ResponseEntity.ok(campaignSearchService.findCampaignsByGameName(gameName));
        }
        return ResponseEntity.ok(campaignSearchService.findCampaignsByBggUsername(bggUsername));
    }
}
