package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.messaging.EmailRequestedEvent;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailRequestServiceTest {

    private static final String RECIPIENT = "test@gamefound.local";

    @Mock
    private EmailRequestRepository emailRequestRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private EmailRequestService emailRequestService;

    @BeforeEach
    void setUp() {
        emailRequestService = new EmailRequestService(emailRequestRepository, eventPublisher, RECIPIENT);
    }

    @Test
    void shouldSavePendingRequestAndPublishEvent() {
        // GIVEN
        when(emailRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        EmailRequest result = emailRequestService.requestCampaignShare("age-of-skorpa");

        // THEN
        assertThat(result.getStatus()).isEqualTo(EmailRequestStatus.PENDING);
        assertThat(result.getRecipient()).isEqualTo(RECIPIENT);
        assertThat(result.getProjectUrlName()).isEqualTo("age-of-skorpa");
        verify(eventPublisher).publishEvent(any(EmailRequestedEvent.class));
    }
}
