package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.BggClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggItem;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggLink;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.bgg.dto.BggThingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private BggClient bggClient;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void shouldReturnFirstPublisher() {
        // given
        List<BggLink> links = List.of(
                new BggLink("boardgamecategory", 123, "Other"),
                new BggLink("boardgamepublisher", 456, "Main Publisher"),
                new BggLink("boardgamepublisher", 789, "Second Publisher")
        );
        BggItem item = new BggItem("boardgame", 264220, List.of(), links);
        BggThingResponse response = new BggThingResponse(List.of(item));

        when(bggClient.getThing(999)).thenReturn(response);

        // when
        String publisher = publisherService.getPublisherByGameId(999);

        // then
        assertThat(publisher).isEqualTo("Main Publisher");
    }

    @Test
    void shouldReturnNullWhenNoPublisher() {
        // given
        List<BggLink> links = List.of(
                new BggLink("boardgamecategory", 123, "Other"),
                new BggLink("boardgamedesigner", 456, "Popular Designer")
        );
        BggItem item = new BggItem("boardgame", 999, List.of(), links);
        BggThingResponse response = new BggThingResponse(List.of(item));

        when(bggClient.getThing(999)).thenReturn(response);

        // when
        String publisher = publisherService.getPublisherByGameId(999);

        // then
        assertThat(publisher).isNull();
    }
}