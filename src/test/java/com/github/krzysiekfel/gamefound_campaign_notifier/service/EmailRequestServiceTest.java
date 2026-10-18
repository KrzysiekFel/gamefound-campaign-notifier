package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.messaging.EmailRequestedEvent;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailRequestServiceTest {

    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "test.user@example.com";
    private static final UUID REQUEST_ID = UUID.randomUUID();

    @Mock
    private EmailRequestRepository emailRequestRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UserService userService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private EmailRequestService emailRequestService;

    @Test
    void shouldCreatePendingRequestForCurrentUserAndPublishEvent() {
        // GIVEN
        when(userService.getCurrentUser()).thenReturn(user());
        when(emailRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        EmailRequest result = emailRequestService.requestCampaignShare("age-of-skorpa");

        // THEN
        assertThat(result.getStatus()).isEqualTo(EmailRequestStatus.PENDING);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRecipient()).isEqualTo(USER_EMAIL);
        assertThat(result.getProjectUrlName()).isEqualTo("age-of-skorpa");
        verify(eventPublisher).publishEvent(any(EmailRequestedEvent.class));
    }

    @Test
    void shouldFindRequestOwnedByCurrentUser() {
        // GIVEN
        EmailRequest request = EmailRequest.campaignShare(USER_ID, USER_EMAIL, "age-of-skorpa");
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(emailRequestRepository.findByIdAndUserId(REQUEST_ID, USER_ID)).thenReturn(Optional.of(request));

        // WHEN
        Optional<EmailRequest> result = emailRequestService.findByIdForCurrentUser(REQUEST_ID);

        // THEN
        assertThat(result).contains(request);
    }

    @Test
    void shouldNotFindRequestOwnedByAnotherUser() {
        // GIVEN
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(emailRequestRepository.findByIdAndUserId(REQUEST_ID, USER_ID)).thenReturn(Optional.empty());

        // WHEN
        Optional<EmailRequest> result = emailRequestService.findByIdForCurrentUser(REQUEST_ID);

        // THEN
        assertThat(result).isEmpty();
    }

    private static User user() {
        User user = new User("krzysiek", USER_EMAIL, "$2a$10$hash");
        ReflectionTestUtils.setField(user, "id", USER_ID);
        return user;
    }
}
