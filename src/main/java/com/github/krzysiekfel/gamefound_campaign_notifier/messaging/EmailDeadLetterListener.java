package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class EmailDeadLetterListener {

    private final EmailRequestRepository emailRequestRepository;

    public EmailDeadLetterListener(EmailRequestRepository emailRequestRepository) {
        this.emailRequestRepository = emailRequestRepository;
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_SEND_DLQ)
    public void handle(SendEmailMessage message) {
        UUID id = message.emailRequestId();
        emailRequestRepository.findById(id)
                .filter(EmailRequest::isPending)
                .ifPresent(request -> {
                    request.markFailed("Sending failed after all retry attempts");
                    emailRequestRepository.save(request);
                    log.error("Email request {} landed in DLQ and was marked as FAILED", id);
                });
    }
}
