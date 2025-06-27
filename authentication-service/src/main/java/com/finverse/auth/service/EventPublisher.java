package com.finverse.auth.service;

import com.finverse.auth.event.UserRegisteredEvent;
import com.finverse.auth.model.Role;
import com.finverse.auth.model.User;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;
//    private final TopicExchange userEventsExchange;

    @Value("${app.events.exchange}")
    private String exchange;

    @Value("${app.events.routing-key}")
    private String routingKey;

    public void publishUserRegistrationEvent(UUID userId, String username, String role) {
        try {
            log.info("Publishing event for user: {}", username);

            UserRegisteredEvent event = new UserRegisteredEvent(
                    userId,
                    username,
                    role,
                    Instant.now()
            );

            rabbitTemplate.convertAndSend(exchange, routingKey, event);

            log.info("✅ Event published successfully to exchange: {}, routing key: {}", exchange, routingKey);
            log.info("Event details: {}", event);

        } catch (Exception e) {
            log.error("❌ Failed to publish event for user: {}", username, e);
            throw e;
        }
    }
}
