package com.finverse.securityapp.application;

import com.finverse.securityapp.user.dto.UserDTO;
import com.finverse.securityapp.user.exception.InvalidCredentialsException;
import com.finverse.securityapp.user.exception.UserNotFoundException;
import com.finverse.securityapp.user.model.LoginRequest;
import com.finverse.securityapp.user.model.User;
import com.finverse.securityapp.user.repository.UserRepository;
import com.finverse.securityapp.user.service.AuthenticationService;
import com.finverse.securityapp.user.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, NotificationService notificationService, AuthenticationService authenticationService,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO){
        System.out.println("Registering user with raw password: '" + userDTO.getPassword() + "'");

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User(
                userDTO.getUsername(),
                passwordEncoder.encode(userDTO.getPassword())
        );
        System.out.println("Encoded password stored: " + user.getPassword());

        this.userRepository.save(user);
        this.notificationService.sendMessage(userDTO);
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
