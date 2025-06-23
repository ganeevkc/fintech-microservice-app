package com.finverse.lendingengine.event;

import com.finverse.lendingengine.model.Balance;
import com.finverse.lendingengine.model.Role;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = "${app.events.queue}")
    public void handleUserRegistration(@Payload UserRegisteredEvent event) {
        log.info("=== RECEIVED USER REGISTRATION EVENT ===");
        log.info("Event data: {}", event);

        try {
            log.info("Processing user registration: userId={}, username={}, role={}",
                    event.getUserId(), event.getUsername(), event.getRole());

            if (userRepository.existsById(event.getUserId())) {
                log.warn("User with ID {} already exists, skipping creation", event.getUserId());
                return;
            }

            UUID userId = event.getUserId();
            String username = event.getUsername();
            String role = event.getRole();
//            UUID userId = extractUserId(event);
//            String username = extractUsername(event);
//            Role role = extractRole(event);

            log.info("Processing user registration: userId={}, username={}, role={}", userId, username, role);

            // Create new user with balance
            User user = new User();
            user.setUserId(userId);
            user.setRole(Role.valueOf(role));
            Balance balance = new Balance();
            balance.setAmount(0.0);
            user.setBalance(balance);

            // Save user to database
            User savedUser = userRepository.save(user);
            log.info("✅ User successfully created in lending engine: {}", savedUser);

        } catch (Exception e) {
            log.error("❌ Failed to process user registration event: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Permanent failure processing user registration", e);
        }
    }

//    private UUID extractUserId(Map<String, Object> event) {
//        try {
//            Object userIdObj = event.get("userId");
//            if (userIdObj == null) {
//                throw new IllegalArgumentException("userId is missing from event");
//            }
//            return UUID.fromString(userIdObj.toString());
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid user ID format", e);
//        }
//    }
//
//    private String extractUsername(Map<String, Object> event) {
//        Object usernameObj = event.get("username");
//        if (usernameObj == null || usernameObj.toString().trim().isEmpty()) {
//            throw new IllegalArgumentException("Username cannot be empty");
//        }
//        return usernameObj.toString();
//    }
//
//    private Role extractRole(Map<String, Object> event) {
//        try {
//            Object roleObj = event.get("role");
//            if (roleObj == null) {
//                throw new IllegalArgumentException("Role is missing from event");
//            }
//            return Role.valueOf(roleObj.toString().toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid role format: " + event.get("role"), e);
//        }
//    }
}