package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record BggLink(
        @JacksonXmlProperty(isAttribute = true)
        String type,

        @JacksonXmlProperty(isAttribute = true)
        int id,

        @JacksonXmlProperty(isAttribute = true)
        String value
) {
}
