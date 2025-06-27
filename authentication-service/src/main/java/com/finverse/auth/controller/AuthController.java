package com.finverse.auth.controller;
//import org.springframework.web.client.RestTemplate;

import com.finverse.auth.dto.RegisterRequest;
import com.finverse.auth.service.JWTTokenService;
import com.finverse.auth.dto.LoginRequest;
import com.finverse.auth.dto.TokenResponse;
import com.finverse.auth.repository.UserRepository;
import com.finverse.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
//    private final TokenService tokenService;
    private final JWTTokenService jwtTokenService;
    private final AuthService authenticationService;


    @Value("${profile.service.url}")
    private String profileServiceUrl;

    @Autowired
    public AuthController(UserRepository userRepository, AuthService authenticationService, PasswordEncoder passwordEncoder, RestTemplate restTemplate, JWTTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.jwtTokenService = jwtTokenService;
//        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/userinfo")
    public String getUserInfo(
            @RequestHeader("X-User-ID") String userId,
            @RequestHeader("X-User-Roles") String roles) {

        return "Authenticated user: " + userId + ", roles: " + roles;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        try {
            if (authenticationService.register(request)) {
                return new ResponseEntity<>("User registered successfully. Please complete user profile.", HttpStatus.OK);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error registering user.", HttpStatus.BAD_REQUEST);
        }
        return null;
    }


    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

//    @PostMapping("/validate")
//    public ResponseEntity<String> validateUser(@RequestHeader("Authorization") String token) {
//        // Should validate JWT token, not just use as ID
//        return ResponseEntity.ok(
//                userRepository.findById(token)
//                        .orElseThrow(UserNotFoundException::new)
//                        .getUsername()
//        );
//    }
//
//    @GetMapping("/{userId}")
//    public String getUserById(@PathVariable("userId") String userId){
//        return userRepository.findById(userId)
//                .orElseThrow(UserNotFoundException::new)
//                .getUsername();
//    }
}
