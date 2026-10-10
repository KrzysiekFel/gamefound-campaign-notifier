package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.notification.CampaignEmailContentBuilder;
import com.github.krzysiekfel.gamefound_campaign_notifier.notification.EmailContent;
import com.github.krzysiekfel.gamefound_campaign_notifier.notification.EmailSender;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.CampaignSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class EmailSendListener {

    private final EmailRequestRepository emailRequestRepository;
    private final CampaignSearchService campaignSearchService;
    private final CampaignEmailContentBuilder campaignEmailContentBuilder;
    private final EmailSender emailSender;

    public EmailSendListener(EmailRequestRepository emailRequestRepository,
                             CampaignSearchService campaignSearchService,
                             CampaignEmailContentBuilder campaignEmailContentBuilder,
                             EmailSender emailSender) {
        this.emailRequestRepository = emailRequestRepository;
        this.campaignSearchService = campaignSearchService;
        this.campaignEmailContentBuilder = campaignEmailContentBuilder;
        this.emailSender = emailSender;
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_SEND_QUEUE)
    public void handle(SendEmailMessage message) {
        UUID id = message.emailRequestId();

        Optional<EmailRequest> found = emailRequestRepository.findById(id);
        if (found.isEmpty()) {
            log.warn("Email request {} not found - skipping message", id);
            return;
        }

        EmailRequest request = found.get();
        if (!request.isPending()) {
            log.info("Email request {} is already {} - skipping duplicate message", id, request.getStatus());
            return;
        }

        Optional<CampaignResponse> campaign =
                campaignSearchService.findActiveCampaignByProjectUrlName(request.getProjectUrlName());
        if (campaign.isEmpty()) {
            request.markFailed("Campaign not found or no longer active: " + request.getProjectUrlName());
            emailRequestRepository.save(request);
            log.warn("Email request {} failed - campaign {} not found", id, request.getProjectUrlName());
            return;
        }

        EmailContent content = campaignEmailContentBuilder.build(campaign.get());
        emailSender.send(request.getRecipient(), content.subject(), content.htmlBody());

        request.markSent();
        emailRequestRepository.save(request);
        log.info("Email request {} sent", id);
    }
}
