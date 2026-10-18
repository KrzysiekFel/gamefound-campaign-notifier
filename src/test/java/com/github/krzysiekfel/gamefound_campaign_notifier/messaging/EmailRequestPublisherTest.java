package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.net.ConnectException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailRequestPublisherTest {

    private static final UUID ID = UUID.randomUUID();

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EmailRequestPublisher emailRequestPublisher;

    @Test
    void shouldPublishMessageToEmailSendQueueAfterCommit() {
        // WHEN
        emailRequestPublisher.onEmailRequested(new EmailRequestedEvent(ID));

        // THEN
        verify(rabbitTemplate).convertAndSend(
                RabbitConfig.NOTIFICATIONS_EXCHANGE,
                RabbitConfig.EMAIL_SEND_ROUTING_KEY,
                new SendEmailMessage(ID));
    }

    @Test
    void shouldNotThrowWhenRabbitMqIsUnavailable() {
        // GIVEN
        doThrow(new AmqpConnectException(new ConnectException("Connection refused")))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // WHEN / THEN
        assertThatCode(() -> emailRequestPublisher.publish(ID)).doesNotThrowAnyException();
    }
}
