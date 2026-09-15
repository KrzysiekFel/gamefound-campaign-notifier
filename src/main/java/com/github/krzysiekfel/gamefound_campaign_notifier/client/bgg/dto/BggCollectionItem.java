package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record BggCollectionItem(
        @JacksonXmlProperty(isAttribute = true)
        int objectid,

        @JacksonXmlProperty(isAttribute = true)
        String subtype,

        @JacksonXmlProperty(localName = "name")
        String name
) {
}
