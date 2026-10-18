package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String NOTIFICATIONS_EXCHANGE = "notifications";
    public static final String EMAIL_SEND_QUEUE = "email.send";
    public static final String EMAIL_SEND_ROUTING_KEY = "email.send";

    public static final String NOTIFICATIONS_DLX = "notifications.dlx";
    public static final String EMAIL_SEND_DLQ = "email.send.dlq";
    public static final String EMAIL_SEND_DLQ_ROUTING_KEY = "email.send.dlq";

    @Bean
    public DirectExchange notificationsExchange() {
        return new DirectExchange(NOTIFICATIONS_EXCHANGE);
    }

    @Bean
    public Queue emailSendQueue() {
        return QueueBuilder.durable(EMAIL_SEND_QUEUE)
                .deadLetterExchange(NOTIFICATIONS_DLX)
                .deadLetterRoutingKey(EMAIL_SEND_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding emailSendBinding() {
        return BindingBuilder.bind(emailSendQueue())
                .to(notificationsExchange())
                .with(EMAIL_SEND_ROUTING_KEY);
    }

    @Bean
    public DirectExchange notificationsDeadLetterExchange() {
        return new DirectExchange(NOTIFICATIONS_DLX);
    }

    @Bean
    public Queue emailSendDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_SEND_DLQ).build();
    }

    @Bean
    public Binding emailSendDeadLetterBinding() {
        return BindingBuilder.bind(emailSendDeadLetterQueue())
                .to(notificationsDeadLetterExchange())
                .with(EMAIL_SEND_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}