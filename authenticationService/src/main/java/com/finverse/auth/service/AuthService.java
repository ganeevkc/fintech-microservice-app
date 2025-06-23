package com.finverse.auth.service;

//import com.finverse.security.user.service.JWTTokenService;
import com.finverse.auth.dto.LoginRequest;
import com.finverse.auth.dto.RegisterRequest;
import com.finverse.auth.dto.TokenResponse;
import com.finverse.auth.exception.UserAlreadyExistsException;
import com.finverse.auth.model.User;
import com.finverse.auth.repository.UserRepository;
//import com.google.common.base.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JWTTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
//    private final LoginRequest loginRequest;

    @Autowired
    public AuthService(JWTTokenService jwtTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, EventPublisher eventPublisher) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
//        this.loginRequest = loginRequest;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public boolean register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException();
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            User savedUser = userRepository.save(user);

            eventPublisher.publishUserRegistrationEvent(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    String.valueOf(savedUser.getRole())
            );
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        Map<String, String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", String.valueOf(user.getRole()));

        String token = jwtTokenService.expiring(claims);
        return new TokenResponse(token);
    }


}

