package com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record BggItem(
        @JacksonXmlProperty(isAttribute = true)
        String type,

        @JacksonXmlProperty(isAttribute = true)
        int id,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "name")
        List<BggName> names,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "link")
        List<BggLink> links
) {
}
