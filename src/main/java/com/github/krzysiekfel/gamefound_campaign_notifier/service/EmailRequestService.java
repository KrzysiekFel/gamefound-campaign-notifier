package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.messaging.EmailRequestedEvent;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.CurrentUserProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailRequestService {

    private final EmailRequestRepository emailRequestRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public EmailRequestService(EmailRequestRepository emailRequestRepository,
                               ApplicationEventPublisher eventPublisher,
                               UserService userService,
                               CurrentUserProvider currentUserProvider) {
        this.emailRequestRepository = emailRequestRepository;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public EmailRequest requestCampaignShare(String projectUrlName) {
        User user = userService.getCurrentUser();
        EmailRequest saved = emailRequestRepository.save(
                EmailRequest.campaignShare(user.getId(), user.getEmail(), projectUrlName));
        eventPublisher.publishEvent(new EmailRequestedEvent(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<EmailRequest> findByIdForCurrentUser(UUID id) {
        return emailRequestRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId());
    }
}