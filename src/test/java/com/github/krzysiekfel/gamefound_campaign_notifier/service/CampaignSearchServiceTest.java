package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.GamefoundClient;
import com.github.krzysiekfel.gamefound_campaign_notifier.client.gamefound.dto.ApiGetCrowdfundingProjectResult;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.CampaignMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private GameService gameService;

    @InjectMocks
    private CampaignSearchService campaignSearchService;

    private ApiGetCrowdfundingProjectResult matchingProject;
    private ApiGetCrowdfundingProjectResult otherProject;
    private CampaignResponse matchingResponse;

    @BeforeEach
    void setUp() {
        matchingProject = createProject("Matching game", "Found Publisher");
        otherProject = createProject("Other Game", "Other Publisher");
        matchingResponse = new CampaignResponse()
                .projectName("Matching game")
                .creatorName("Found Publisher")
                .campaignStartDate(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                .campaignEndDate(OffsetDateTime.parse("2026-02-01T00:00:00Z"))
                .shortDescription("Short description")
                .fundsGathered(new BigDecimal("75000"))
                .campaignGoal(new BigDecimal("50000"))
                .currencyShortName("EUR")
                .backerCount(100)
                .projectHomeUrl(URI.create("https://gamefound.com/project"))
                .projectImageUrl(URI.create("https://gamefound.com/image.jpg"));
    }

    @Test
    void shouldFindCampaignsByPublisherName() {
        // GIVEN
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject, otherProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("Found");

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getProjectName()).isEqualTo("Matching game");
        assertThat(result.getFirst().getCreatorName()).isEqualTo("Found Publisher");
    }

    @Test
    void shouldReturnEmptyListWhenNoMatch() {
        // GIVEN
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("nonexistent");

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotBeCaseSensitive() {
        // GIVEN
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("FOUND");

        // THEN
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldMatchPartialName() {
        // GIVEN
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByPublisherName("fou");

        // THEN
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
        // GIVEN
        Game game = new Game(264220L, "Matching game", 2020, 10);
        when(gameService.findByName("any")).thenReturn(Optional.of(game));
        when(publisherService.getPublisherByGameId(264220L)).thenReturn("Found Publisher");
        when(gamefoundClient.getActiveCrowdfundingProjects()).thenReturn(List.of(matchingProject,
                otherProject));
        when(campaignMapper.toCampaignResponse(matchingProject)).thenReturn(matchingResponse);

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByGameName("any");

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getProjectName()).isEqualTo("Matching game");
    }

    @Test
    void shouldReturnEmptyListWhenGameNotFound() {
        // GIVEN
        when(gameService.findByName("unknown game")).thenReturn(Optional.empty());

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByGameName("unknown game");

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenPublisherNotFoundForGame() {
        // GIVEN
        Game game = new Game(264220L, "Matching game", 2020, 10);
        when(gameService.findByName("any")).thenReturn(Optional.of(game));
        when(publisherService.getPublisherByGameId(264220L)).thenReturn(null);

        // WHEN
        List<CampaignResponse> result = campaignSearchService.findCampaignsByGameName("any");

        // THEN
        assertThat(result).isEmpty();
    }
}