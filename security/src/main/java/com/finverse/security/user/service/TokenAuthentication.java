package com.finverse.security.user.service;

import com.finverse.security.user.repository.UserRepository;
import com.google.common.collect.ImmutableMap;
import com.finverse.security.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class TokenAuthentication implements UserAuthenticationService{

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public TokenAuthentication(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> login(String username, String password) {
        Optional<User> userDetails = userRepository.findByUserDetails_Username(username);

        String generatedJWT = userRepository
                .findByUserDetails_Username(username)
                .filter(user->user.getUserDetails().getPassword().equals(password))
                .map(user->tokenService.expiring(ImmutableMap.of("username",username))).get();

        userDetails.get().getUserDetails().setJwtToken(generatedJWT);

        userRepository.save(userDetails.get());

        return Optional.of(generatedJWT);

//        return Optional.of(userRepository
//                .findByUserDetails_Username(username)
//                .filter(user->user.getUserDetails().getPassword().equals(password))
//                .map(user->tokenService.expiring(ImmutableMap.of("username",username)))
//        );
    }

    @Override
    public User findByToken(String token) {
        Map<String,String> result = tokenService.verify(token);
//        return userRepository.findByUserDetails_Username(result.get("username")).get();
        return userRepository.findByUserDetails_JwtToken(result.get("jwtToken")).get();
    }
}
