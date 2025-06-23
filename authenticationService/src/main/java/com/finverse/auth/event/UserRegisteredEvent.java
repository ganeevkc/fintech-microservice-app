package com.finverse.auth.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements Serializable {
    private UUID userId;
    private String username;
    private String role;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;


}
