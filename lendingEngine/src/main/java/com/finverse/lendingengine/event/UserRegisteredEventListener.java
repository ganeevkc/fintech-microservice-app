package com.finverse.lendingengine.event;

import com.finverse.lendingengine.model.Balance;
import com.finverse.lendingengine.model.Role;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class UserRegisteredEventListener {

//    private final RabbitTemplate rabbitTemplate;
//    private final Logger logger = LoggerFactory.getLogger(UserRegisteredEventListener.class);

//    private static final Gson GSON = new Gson(); // to deserialize JSON coming from RabbitMQ into a Java object

//    private final UserRepository userRepository;

//    @Autowired
//    public UserRegisteredEventListener(RabbitTemplate rabbitTemplate, UserRepository userRepository) {
//        this.rabbitTemplate = rabbitTemplate;
//        this.userRepository = userRepository;
//    }

    @RabbitListener(queues = "${app.events.queue}")
    public void handleUserRegistration(Map<String, Object> event){

        try {
            validateEvent(event);

            UUID userId = extractUserId(event);
            Role role = extractRole(event);

            User user = new User();
            user.setUserId(userId);
            user.setBalance(new Balance());
            user.setRole(role);

        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Permanent failure", e);
        }
    }

    private void validateEvent(Map<String, Object> event) {
        if (!"USER_REGISTERED".equals(event.get("eventType"))) {
            throw new IllegalArgumentException("Invalid event type");
        }

        if (event.get("userId") == null || event.get("username") == null || event.get("role") == null) {
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
    private Role extractRole(Map<String, Object> event) {
        Role role = (Role) event.get("role");
        if (role == null) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return role;
    }
}
