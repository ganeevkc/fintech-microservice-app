package com.finverse.profile.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {
    @GetMapping("/api/auth/exists/{username}")
    boolean userExists(@PathVariable String username);
}
