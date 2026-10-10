package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailDeadLetterListenerTest {

    private static final UUID ID = UUID.randomUUID();

    @Mock
    private EmailRequestRepository emailRequestRepository;

    @InjectMocks
    private EmailDeadLetterListener emailDeadLetterListener;

    @Test
    void shouldMarkPendingRequestAsFailed() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare("test@gamefound.local", "age-of-skorpa");
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.of(request));

        // WHEN
        emailDeadLetterListener.handle(new SendEmailMessage(ID));

        // THEN
        assertThat(request.getStatus()).isEqualTo(EmailRequestStatus.FAILED);
        assertThat(request.getErrorMessage()).isNotBlank();
        verify(emailRequestRepository).save(request);
    }

    @Test
    void shouldNotOverwriteRequestThatIsAlreadySent() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare("test@gamefound.local", "age-of-skorpa");
        request.markSent();
        when(emailRequestRepository.findById(ID)).thenReturn(Optional.of(request));

        // WHEN
        emailDeadLetterListener.handle(new SendEmailMessage(ID));

        // THEN
        assertThat(request.getStatus()).isEqualTo(EmailRequestStatus.SENT);
        verify(emailRequestRepository, never()).save(any());
    }
}
