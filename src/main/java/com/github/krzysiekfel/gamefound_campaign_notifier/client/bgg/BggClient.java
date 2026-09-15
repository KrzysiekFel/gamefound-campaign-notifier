package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggCollectionResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggThingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "bgg-client",
        url = "${bgg.api.base-url}",
        configuration = BggClientConfiguration.class
)
public interface BggClient {

    @GetMapping("/thing")
    BggThingResponse getThing(@RequestParam("id") Long id);

    @GetMapping("/collection")
    BggCollectionResponse getUserCollection(@RequestParam("username") String username,
                                            @RequestParam("own") int own,  // TODO: sprawdzic dokladnie co tu sie podaje i kiedy w jakiej sytuacji
                                            @RequestParam("subtype") String subtype);
}
