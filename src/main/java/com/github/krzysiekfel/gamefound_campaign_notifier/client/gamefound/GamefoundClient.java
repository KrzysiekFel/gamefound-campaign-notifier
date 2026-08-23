package com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "gamefound-client",
        url = "${gamefound.api.base-url}"
)
public interface GamefoundClient {

    @GetMapping("/api/public/projects/getActiveCrowdfundingProjects")
    List<ApiGetCrowdfundingProjectResult> getActiveCrowdfundingProjects();
}
