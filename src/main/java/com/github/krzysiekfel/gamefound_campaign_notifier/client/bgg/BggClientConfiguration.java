package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class BggClientConfiguration {

    @Value("${bgg.api.token}")
    private String token;

    @Bean
    public RequestInterceptor bggAuthInterceptor() {
        return template -> template.header("Authorization", "Bearer " + token);
    }

    @Bean
    public Decoder feignDecoder() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new feign.jackson.JacksonDecoder(xmlMapper);
    }
}
