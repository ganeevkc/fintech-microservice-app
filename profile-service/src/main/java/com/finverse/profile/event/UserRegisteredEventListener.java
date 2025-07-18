package com.finverse.profile.event;

import com.finverse.profile.model.Role;
import com.finverse.profile.model.UserProfile;
import com.finverse.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final ProfileService profileService;

    @PostConstruct
    public void init() {
        log.info("=== USER EVENT LISTENER INITIALIZED ===");
        log.info("Ready to listen on queue: user-registered-queue");
    }

    @RabbitListener(queues = "${app.events.queue}")
    public void handleUserRegisteredEvent(@Payload UserRegisteredEvent event) {
        try {
            log.info("=== RECEIVED USER REGISTRATION EVENT ===");
            log.info("Event details: {}", event);
            log.info("User ID: {}, Username: {}, Role: {}",
                    event.getUserId(), event.getUsername(), event.getRole());

//            Role role = Role.valueOf(event.getRole().toUpperCase());
            String role = event.getRole();
            UserProfile profile = profileService.createBasicProfile(
                    event.getUserId(),
                    event.getUsername(),
                    Role.valueOf(role)
            );

            log.info("Successfully created profile for user: {}", event.getUsername());

        } catch (Exception e) {
            log.error("Failed to process event: {}", event, e);
            throw new AmqpRejectAndDontRequeueException("Permanent failure", e);
        }
    }


//    private void validateEvent(Map<String, Object> event) {
//        if (!"USER_REGISTERED".equals(event.get("eventType"))) {
//            throw new IllegalArgumentException("Invalid event type");
//        }
//
//        if (event.get("userId") == null || event.get("username") == null || event.get("role") == null) {
//            throw new IllegalArgumentException("Missing required fields");
//        }
//    }

}