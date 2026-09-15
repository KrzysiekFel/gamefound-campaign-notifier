package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.GamefoundClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.CampaignMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignSearchServiceTest {

    @Mock
    private GamefoundClient gamefoundClient;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private PublisherService publisherService;

    @InjectMocks
    private CampaignSearchService campaignSearchService;

    private ApiGetCrowdfundingProjectResult matchingProject;
    private ApiGetCrowdfundingProjectResult otherProject;
    private CampaignResponse matchingResponse;

    @BeforeEach
    void setUp() {
        matchingProject = createProject("Matching game", "Found Publisher");
        otherProject = createProject("Other Game", "Other Publisher");
        matchingResponse = new CampaignResponse(
                "Matching game",
                "Found Publisher",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-02-01T00:00:00Z"),
                "Short description",
                new BigDecimal("75000"),
                new BigDecimal("50000"),
                "EUR",
                100,
                "https://gamefound.com/project",
                "https://gamefound.com/image.jpg"
        );
    }

    @Test
    void shouldFindCampaignsByPublisherName() {
        // given
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject, otherProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // when
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("Found");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().projectName()).isEqualTo("Matching game");
        assertThat(result.getFirst().creatorName()).isEqualTo("Found Publisher");
    }

    @Test
    void shouldReturnEmptyListWhenNoMatch() {
        // given
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));

        // when
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("nonexistent");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotBeCaseSensitive() {
        // given
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // when
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("FOUND");

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldMatchPartialName() {
        // given
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // when
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("fou");

        // then
        assertThat(result).hasSize(1);
    }

    private ApiGetCrowdfundingProjectResult createProject(String projectName, String publisherName) {
        return new ApiGetCrowdfundingProjectResult(
                100,
                5,
                3,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-02-01T00:00:00Z"),
                new BigDecimal("50000"),
                publisherName,
                publisherName.toLowerCase().replace(" ", "-"),
                "EUR",
                new BigDecimal("75000"),
                projectName,
                projectName.toLowerCase().replace(" ", "-"),
                "Short description", 50,
                "https://gamefound.com/project",
                "https://gamefound.com/image.jpg"
        );
    }

    @Test
    void shouldFindCampaignsByGameName() {
        // given
        // TODO: zaktualizować gdy GameService będzie zaimplementowany
        // na razie testuje tylko część z publisherService
        when(publisherService.getPublisherByGameId(0)).thenReturn("Found Publisher");
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // when
        List<CampaignResponse> result = campaignSearchService.findCampaignsByGameName("any");

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnEmptyListWhenPublisherNotFoundForGame() {
        // given
        when(publisherService.getPublisherByGameId(0)).thenReturn(null);

        // when
        List<CampaignResponse> result = campaignSearchService.findCampaignsByGameName("any");

        // then
        assertThat(result).isEmpty();
    }
}