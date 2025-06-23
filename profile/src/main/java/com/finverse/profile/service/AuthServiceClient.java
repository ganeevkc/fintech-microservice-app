package com.finverse.profile.service;

import com.finverse.auth.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/api/auth/users/{userId}")
    UserDTO getUserById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID userId
    );

    @GetMapping("/api/auth/users/validate")
    boolean validateToken(
            @RequestHeader("Authorization") String token
    );
}
