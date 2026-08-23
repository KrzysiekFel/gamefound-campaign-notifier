package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class BggClientConfiguration {

    @Value("${bgg.api.token}")
    private String token;

    @Bean
    public RequestInterceptor bggAuthInterceptor() {
        return template -> template.header("Authorization", "Bearer " + token);
    }
}
