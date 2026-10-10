package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingEmailRequeueJobTest {

    @Mock
    private EmailRequestRepository emailRequestRepository;

    @Mock
    private EmailRequestPublisher emailRequestPublisher;

    @InjectMocks
    private PendingEmailRequeueJob pendingEmailRequeueJob;

    @Test
    void shouldRepublishRequestsStuckInPendingForMoreThanFiveMinutes() {
        // GIVEN
        List<EmailRequest> stuckRequests = List.of(
                EmailRequest.campaignShare("test@gamefound.local", "first"),
                EmailRequest.campaignShare("test@gamefound.local", "second"));
        when(emailRequestRepository.findByStatusAndCreatedAtBefore(eq(EmailRequestStatus.PENDING), any()))
                .thenReturn(stuckRequests);

        // WHEN
        pendingEmailRequeueJob.requeueStuckRequests();

        // THEN
        ArgumentCaptor<Instant> createdBefore = ArgumentCaptor.forClass(Instant.class);
        verify(emailRequestRepository)
                .findByStatusAndCreatedAtBefore(eq(EmailRequestStatus.PENDING), createdBefore.capture());
        assertThat(createdBefore.getValue())
                .isCloseTo(Instant.now().minus(Duration.ofMinutes(5)), within(5, ChronoUnit.SECONDS));
        verify(emailRequestPublisher, times(2)).publish(any());
    }

    @Test
    void shouldDoNothingWhenNoRequestsAreStuck() {
        // GIVEN
        when(emailRequestRepository.findByStatusAndCreatedAtBefore(eq(EmailRequestStatus.PENDING), any()))
                .thenReturn(List.of());

        // WHEN
        pendingEmailRequeueJob.requeueStuckRequests();

        // THEN
        verifyNoInteractions(emailRequestPublisher);
    }
}
