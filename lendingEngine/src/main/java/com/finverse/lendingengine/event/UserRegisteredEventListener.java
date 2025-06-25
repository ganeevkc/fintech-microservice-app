
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
            UUID userId = event.getUserId();
            String userIdString = userId.toString();
            String username = event.getUsername();
            String role = event.getRole();

            log.info("Processing user registration: userId={}, username={}, role={}", userId, username, role);

            // Check if user already exists - SIMPLE way
            Boolean userExists = userRepository.existsByUserIdString(userIdString);
            if (Boolean.TRUE.equals(userExists)) {
                log.warn("User with ID {} already exists, skipping creation", userId);
                return;
            }

            // Create new user with balance
            User user = new User();
            user.setUserIdString(userIdString); // Use the string setter directly
            user.setRole(Role.valueOf(role));

            // Create balance first
            Balance balance = new Balance();
            balance.setAmount(0.0);

            // Set balance to user
            user.setBalance(balance);

            // Save user to database (balance will be saved due to CascadeType.ALL)
            User savedUser = userRepository.save(user);
            log.info("✅ User successfully created in lending engine: {} with balance ID: {}",
                    savedUser.getUserIdString(), savedUser.getBalance().getId());

        } catch (Exception e) {
            log.error("❌ Failed to process user registration event: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Permanent failure processing user registration", e);
        }
    }
}