package com.finverse.security.user.service;

import com.finverse.security.user.model.User;

import java.util.Optional;

public interface UserAuthenticationService {
    Optional<String> login(String username, String password);
    User findByToken(String token);
}
