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
    public List<CampaignResponse> searchByCreator(@RequestParam String creatorName) {
        return campaignSearchService.findCampaignsByCreatorName(creatorName);
    }
}
