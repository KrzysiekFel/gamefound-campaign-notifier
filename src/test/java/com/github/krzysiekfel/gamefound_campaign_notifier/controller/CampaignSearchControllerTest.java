package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.CampaignSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignSearchControllerTest {

    @Mock
    private CampaignSearchService campaignSearchService;

    @InjectMocks
    private CampaignSearchController campaignSearchController;

    @Test
    void shouldReturn400WhenNoParameterProvided() {
        // GIVEN & WHEN
        ResponseStatusException exception = catchThrowableOfType(
                () -> campaignSearchController.searchCampaigns(null, null, null),
                ResponseStatusException.class);

        // THEN
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(campaignSearchService);
    }

    @Test
    void shouldReturn400WhenTwoParametersProvided() {
        // GIVEN & WHEN
        ResponseStatusException exception = catchThrowableOfType(
                () -> campaignSearchController.searchCampaigns("Publisher", "Game", null),
                ResponseStatusException.class);

        // THEN
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(campaignSearchService);
    }

    @Test
    void shouldDelegateToPublisherSearchWhenOnlyPublisherNameProvided() {
        // GIVEN
        List<CampaignResponse> expected = List.of(new CampaignResponse());
        when(campaignSearchService.findCampaignsByPublisherName("Lucky Duck")).thenReturn(expected);

        // WHEN
        ResponseEntity<List<CampaignResponse>> response =
                campaignSearchController.searchCampaigns("Lucky Duck", null, null);

        // THEN
        assertThat(response.getBody()).isEqualTo(expected);
        verify(campaignSearchService).findCampaignsByPublisherName("Lucky Duck");
    }

    @Test
    void shouldDelegateToGameSearchWhenOnlyGameNameProvided() {
        // GIVEN
        List<CampaignResponse> expected = List.of(new CampaignResponse());
        when(campaignSearchService.findCampaignsByGameName("Catan")).thenReturn(expected);

        // WHEN
        ResponseEntity<List<CampaignResponse>> response =
                campaignSearchController.searchCampaigns(null, "Catan", null);

        // THEN
        assertThat(response.getBody()).isEqualTo(expected);
        verify(campaignSearchService).findCampaignsByGameName("Catan");
    }

    @Test
    void shouldDelegateToBggUserSearchWhenOnlyBggUsernameProvided() {
        // GIVEN
        List<CampaignResponse> expected = List.of(new CampaignResponse());
        when(campaignSearchService.findCampaignsByBggUsername("someuser")).thenReturn(expected);

        // WHEN
        ResponseEntity<List<CampaignResponse>> response =
                campaignSearchController.searchCampaigns(null, null, "someuser");

        // THEN
        assertThat(response.getBody()).isEqualTo(expected);
        verify(campaignSearchService).findCampaignsByBggUsername("someuser");
    }
}