package com.finverse.lendingengine.service;

import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Primary
@Component
public class TestUserValidationService implements TokenValidationService {

    private final UserRepository userRepository;

    @Autowired
    public TestUserValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User validateToken(String token) {
        return userRepository.findById(token)
                .orElseThrow(()-> new UserNotFoundException(token));
    }
}
