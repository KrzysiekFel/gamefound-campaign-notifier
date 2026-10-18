package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
public class EmailRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EmailRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmailRequested(EmailRequestedEvent event) {
        publish(event.emailRequestId());
    }

    public void publish(UUID emailRequestId) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.NOTIFICATIONS_EXCHANGE,
                    RabbitConfig.EMAIL_SEND_ROUTING_KEY,
                    new SendEmailMessage(emailRequestId));
            log.info("Published email request {} to RabbitMQ", emailRequestId);
        } catch (AmqpException e) {
            log.warn("Could not publish email request {} - it stays PENDING and will be republished later",
                    emailRequestId, e);
        }
    }
}
