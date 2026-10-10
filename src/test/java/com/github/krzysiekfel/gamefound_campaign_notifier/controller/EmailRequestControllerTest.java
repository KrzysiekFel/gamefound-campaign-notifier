package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CreateEmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.EmailRequestResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.EmailRequestMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.EmailRequestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailRequestControllerTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String SLUG = "age-of-skorpa";

    @Mock
    private EmailRequestService emailRequestService;

    @Mock
    private EmailRequestMapper emailRequestMapper;

    @InjectMocks
    private EmailRequestController emailRequestController;

    @BeforeEach
    void setUpCurrentRequest() {
        // ServletUriComponentsBuilder.fromCurrentRequest() needs the current HTTP request
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/email-requests");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearCurrentRequest() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldAcceptEmailRequestAndReturnLocationOfStatusResource() {
        // GIVEN
        EmailRequest emailRequest = EmailRequest.campaignShare("test@gamefound.local", SLUG);
        ReflectionTestUtils.setField(emailRequest, "id", ID);
        EmailRequestResponse response = new EmailRequestResponse()
                .id(ID)
                .status(EmailRequestResponse.StatusEnum.PENDING)
                .projectUrlName(SLUG);
        when(emailRequestService.requestCampaignShare(SLUG)).thenReturn(emailRequest);
        when(emailRequestMapper.toEmailRequestResponse(emailRequest)).thenReturn(response);

        // WHEN
        ResponseEntity<EmailRequestResponse> result =
                emailRequestController.createEmailRequest(new CreateEmailRequest().projectUrlName(SLUG));

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(result.getHeaders().getLocation()).hasToString("http://localhost/api/v1/email-requests/" + ID);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnEmailRequestStatus() {
        // GIVEN
        EmailRequest emailRequest = EmailRequest.campaignShare("test@gamefound.local", SLUG);
        EmailRequestResponse response = new EmailRequestResponse()
                .id(ID)
                .status(EmailRequestResponse.StatusEnum.SENT);
        when(emailRequestService.findById(ID)).thenReturn(Optional.of(emailRequest));
        when(emailRequestMapper.toEmailRequestResponse(emailRequest)).thenReturn(response);

        // WHEN
        ResponseEntity<EmailRequestResponse> result = emailRequestController.getEmailRequest(ID);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnNotFoundWhenEmailRequestDoesNotExist() {
        // GIVEN
        when(emailRequestService.findById(ID)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<EmailRequestResponse> result = emailRequestController.getEmailRequest(ID);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNull();
    }
}
