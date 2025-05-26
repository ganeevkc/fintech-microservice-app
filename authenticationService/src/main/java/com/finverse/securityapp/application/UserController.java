package com.finverse.securityapp.application;
//import org.springframework.web.client.RestTemplate;

import com.finverse.securityapp.user.config.RestTemplateConfig;
import com.finverse.securityapp.user.dto.UserDTO;
import com.finverse.securityapp.user.exception.InvalidCredentialsException;
import com.finverse.securityapp.user.exception.UserNotFoundException;
import com.finverse.securityapp.user.model.LoginRequest;
import com.finverse.securityapp.user.model.User;
import com.finverse.securityapp.user.repository.UserRepository;
import com.finverse.securityapp.user.service.AuthenticationService;
import com.finverse.securityapp.user.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${profile.service.url}")
    private String profileServiceUrl;

    @Autowired
    public UserController(UserRepository userRepository, NotificationService notificationService, AuthenticationService authenticationService, PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO){
//        System.out.println("Registering user with raw password: '" + userDTO.getPassword() + "'");

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User(
                userDTO.getUsername(),
                passwordEncoder.encode(userDTO.getPassword())
        );
//        System.out.println("Encoded password stored: " + user.getPassword());

        this.userRepository.save(user);
        this.notificationService.sendMessage(userDTO);
        try {
//            Map<String, Object> profilePayload = new HashMap<>();
////            profilePayload.put("username", userDTO.getUsername());
////            profilePayload.put("firstName", userDTO.getFirst_name());
////            profilePayload.put("lastName", userDTO.getLast_name());
//            profilePayload.put("userDTO", userDTO);

            restTemplate.postForEntity(
                    profileServiceUrl + "/create-user",
                    userDTO,
                    Void.class
            );
            System.out.println("Profile created successfully");

        }catch (Exception e) {
            System.err.println("Failed to create user profile: " + e.getMessage());
            // Optional: return error or rollback user creation
        }
        return ResponseEntity.ok("User registered successfully");
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        return ResponseEntity.ok(authenticationService.login(loginRequest));
//    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestHeader("Authorization") String token) {
        // Should validate JWT token, not just use as ID
        return ResponseEntity.ok(
                userRepository.findById(token)
                        .orElseThrow(UserNotFoundException::new)
                        .getUsername()
        );
    }

    @GetMapping("/{userId}")
    public String getUserById(@PathVariable("userId") String userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new)
                .getUsername();
    }
}
