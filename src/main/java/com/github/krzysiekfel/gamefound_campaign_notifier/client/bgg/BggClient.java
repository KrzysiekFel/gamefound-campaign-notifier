package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name ="bgg-client",
        url = "${bgg.api.base-url}",
        configuration = BggClientConfiguration.class
)
public interface BggClient {
}
