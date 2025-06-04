package com.finverse.profile.event;

import com.finverse.profile.model.UserProfile;
import com.finverse.profile.repository.ProfileRepository;
import com.finverse.profile.service.ProfileService;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisteredEventListener {
//    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    private final RabbitTemplate rabbitTemplate;
//    private final ConnectionFactory connectionFactory;

    @RabbitListener(queues = "${app.events.queue}")
    public void handleUserRegisteredEvent(Map<String, Object> event) {
        try {

            // 1. Validate event structure
//            validateEvent(event);

            // 2. Extract and validate fields
            UUID userId = extractUserId(event);
            String username = extractUsername(event);

            log.info("Processing USER_REGISTERED event for user: {}", username);

            // 3. Check for existing profile
//            if (profileRepository.existsByUserId(userId)) {
//                log.warn("Profile already exists for user ID: {}", userId);
//                return;
//            }

            // 4. Create and save profile
            UserProfile profile = profileService.createBasicProfile(userId, username);

            log.info("Successfully created profile for user: {}", username);

        } catch (Exception e) {
            log.error("Failed to process event: {}", event, e);
            throw new AmqpRejectAndDontRequeueException("Permanent failure", e);
        }
    }


    private void validateEvent(Map<String, Object> event) {
        if (!"USER_REGISTERED".equals(event.get("eventType"))) {
            throw new IllegalArgumentException("Invalid event type");
        }

        if (event.get("userId") == null || event.get("username") == null) {
            throw new IllegalArgumentException("Missing required fields");
        }
    }

    private UUID extractUserId(Map<String, Object> event) {
        try {
            return UUID.fromString((String) event.get("userId"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format", e);
        }
    }

    private String extractUsername(Map<String, Object> event) {
        String username = (String) event.get("username");
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return username;
    }
//
//    private UserProfile createProfile(UUID userId, String username) {
//        UserProfile profile = new UserProfile();
//        profile.setUserId(userId);
//        profile.setUsername(username);
//        // Set default values for other fields if needed
//        profile.setRegisteredSince(LocalDate.now());
//        return profile;
//    }
}