package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.messaging.EmailRequestedEvent;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailRequestService {

    private final EmailRequestRepository emailRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    // TODO: po dodaniu logowania (Spring Security + JWT) usunąć to pole (i notifications.mail.temporary-recipient)
    //  i pobierać adres e-mail zalogowanego użytkownika (z SecurityContextHolder).
    //  Do tego czasu wszystkie maile idą na ten jeden adres.
    private final String recipientEmail;

    public EmailRequestService(EmailRequestRepository emailRequestRepository,
                               ApplicationEventPublisher eventPublisher,
                               @Value("${notifications.mail.temporary-recipient}") String recipientEmail) {
        this.emailRequestRepository = emailRequestRepository;
        this.eventPublisher = eventPublisher;
        this.recipientEmail = recipientEmail;
    }

    @Transactional
    public EmailRequest requestCampaignShare(String projectUrlName) {
        EmailRequest saved = emailRequestRepository.save(
                EmailRequest.campaignShare(recipientEmail, projectUrlName));
        eventPublisher.publishEvent(new EmailRequestedEvent(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<EmailRequest> findById(UUID id) {
        return emailRequestRepository.findById(id);
    }
}
