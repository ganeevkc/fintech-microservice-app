package com.finverse.auth.event;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class UserRegisteredEvent implements Serializable {
    private UUID userId;
    private String username;
    private Instant timestamp;
    public UserRegisteredEvent(UUID userId, String username, Instant timestamp) {
        this.userId = userId;
        this.username = username;
        this.timestamp = timestamp;
    }

}
