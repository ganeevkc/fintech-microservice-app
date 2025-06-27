package com.finverse.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/rabbitmq")
    public ResponseEntity<String> checkRabbitMQ() {
        try {
            if (rabbitTemplate.isRunning()) {
                return ResponseEntity.ok("RabbitMQ connection active");
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("RabbitMQ connection not active");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("RabbitMQ error: " + e.getMessage());
        }
    }
}