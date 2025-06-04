package com.finverse.auth.service;

import com.finverse.auth.event.UserRegisteredEvent;
import com.finverse.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange userEventsExchange;

    @Value("${app.events.exchange}")
    private String exchangeName;

    @Value("${app.events.routing-key}")
    private String routingKey;

    public EventPublisher(RabbitTemplate rabbitTemplate, TopicExchange userEventsExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.userEventsExchange = userEventsExchange;
    }


    public void publishUserRegisteredEvent(UUID userId, String username) {
        log.info("Publishing event for user: {}", username);
        Map<String, Object> event = Map.of(
                "eventType", "USER_REGISTERED",
                "userId", userId.toString(),
                "username", username,
                "timestamp", Instant.now()
        );
        rabbitTemplate.convertAndSend(
//                userEventsExchange.getName(),
                exchangeName,
                routingKey,
                event,
                message -> {
                    message.getMessageProperties()
                            .setContentType("application/json");
                    return message;
                }
        );
    }
}
