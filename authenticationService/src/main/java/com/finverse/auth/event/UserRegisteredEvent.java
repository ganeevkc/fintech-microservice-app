package com.finverse.auth.event;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class UserRegisteredEvent implements Serializable {
    private UUID userId;
    private String username;
    private String role;
    private Instant timestamp;
    public UserRegisteredEvent(UUID userId, String username, String role, Instant timestamp) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.timestamp = timestamp;
    }

}
