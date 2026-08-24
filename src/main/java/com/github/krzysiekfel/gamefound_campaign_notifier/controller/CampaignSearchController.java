package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.CampaignSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignSearchController {

    private final CampaignSearchService campaignSearchService;

    public CampaignSearchController(CampaignSearchService campaignSearchService) {
        this.campaignSearchService = campaignSearchService;
    }

    @GetMapping
    public List<CampaignResponse> searchByPublisher(@RequestParam String publisherName) {
        return campaignSearchService.findCampaignsByPublisherName(publisherName);
    }

    @GetMapping("/by-game")
    public List<CampaignResponse> searchByGame(@RequestParam String gameName) {
        return campaignSearchService.findCampaignsByGameName(gameName);
    }

    @GetMapping("/by-user")
    public List<CampaignResponse> searchByBggUser(@RequestParam String bggUsername) {
        return campaignSearchService.findCampaignsByBggUsername(bggUsername);
    }
}
