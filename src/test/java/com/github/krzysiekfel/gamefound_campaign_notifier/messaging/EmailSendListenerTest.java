package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.notification.CampaignEmailContentBuilder;
import com.github.krzysiekfel.gamefound_campaign_notifier.notification.EmailContent;
import com.github.krzysiekfel.gamefound_campaign_notifier.notification.EmailSender;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.CampaignSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailSendListenerTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String SLUG = "age-of-skorpa";
    private static final String RECIPIENT = "test@gamefound.local";

    @Mock
    private EmailRequestRepository emailRequestRepository;

    @Mock
    private CampaignSearchService campaignSearchService;

    @Mock
    private CampaignEmailContentBuilder campaignEmailContentBuilder;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailSendListener emailSendListener;

    @Test
    void shouldSendEmailAndMarkRequestAsSent() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare(1L, RECIPIENT, SLUG);
        CampaignResponse campaign = new CampaignResponse().projectName("Age of Skorpa");
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.of(request));
        when(campaignSearchService.findActiveCampaignByProjectUrlName(SLUG)).thenReturn(Optional.of(campaign));
        when(campaignEmailContentBuilder.build(campaign)).thenReturn(new EmailContent("Subject", "<p>Body</p>"));

        // WHEN
        emailSendListener.handle(new SendEmailMessage(ID));

        // THEN
        verify(emailSender).send(RECIPIENT, "Subject", "<p>Body</p>");
        assertThat(request.getStatus()).isEqualTo(EmailRequestStatus.SENT);
        assertThat(request.getSentAt()).isNotNull();
        verify(emailRequestRepository).save(request);
    }

    @Test
    void shouldSkipMessageWhenRequestNotFound() {
        // GIVEN
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.empty());

        // WHEN
        emailSendListener.handle(new SendEmailMessage(ID));

        // THEN
        verifyNoInteractions(campaignSearchService, emailSender);
    }

    @Test
    void shouldSkipDuplicateMessageWhenRequestAlreadySent() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare(1L, RECIPIENT, SLUG);
        request.markSent();
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.of(request));

        // WHEN
        emailSendListener.handle(new SendEmailMessage(ID));

        // THEN
        verifyNoInteractions(campaignSearchService, emailSender);
        verify(emailRequestRepository, never()).save(any());
    }

    @Test
    void shouldMarkRequestAsFailedWithoutSendingWhenCampaignNotFound() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare(1L, RECIPIENT, SLUG);
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.of(request));
        when(campaignSearchService.findActiveCampaignByProjectUrlName(SLUG)).thenReturn(Optional.empty());

        // WHEN
        emailSendListener.handle(new SendEmailMessage(ID));

        // THEN
        assertThat(request.getStatus()).isEqualTo(EmailRequestStatus.FAILED);
        assertThat(request.getErrorMessage()).contains(SLUG);
        verify(emailRequestRepository).save(request);
        verifyNoInteractions(emailSender);
    }

    @Test
    void shouldPropagateExceptionAndKeepRequestPendingWhenSendingFails() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare(1L, RECIPIENT, SLUG);
        CampaignResponse campaign = new CampaignResponse().projectName("Age of Skorpa");
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.of(request));
        when(campaignSearchService.findActiveCampaignByProjectUrlName(SLUG)).thenReturn(Optional.of(campaign));
        when(campaignEmailContentBuilder.build(campaign)).thenReturn(new EmailContent("Subject", "<p>Body</p>"));
        doThrow(new MailSendException("SMTP server unavailable"))
                .when(emailSender).send(any(), any(), any());

        // WHEN / THEN
        assertThatThrownBy(() -> emailSendListener.handle(new SendEmailMessage(ID)))
                .isInstanceOf(MailSendException.class);
        assertThat(request.getStatus()).isEqualTo(EmailRequestStatus.PENDING);
        verify(emailRequestRepository, never()).save(any());
    }
}
