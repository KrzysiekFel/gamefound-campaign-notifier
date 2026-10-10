package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.BggClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggLink;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggThingResponse;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {

    private final BggClient bggClient;

    public PublisherService(BggClient bggClient) {
        this.bggClient = bggClient;
    }

    public String getPublisherByGameId(Long bggGameId) {
        BggThingResponse response = bggClient.getThing(bggGameId);

        return response.items().getFirst().links().stream()
                .filter(link -> "boardgamepublisher".equals(link.type()))
                .map(BggLink::value)
                .findFirst()
                .orElse(null);
    }
}
