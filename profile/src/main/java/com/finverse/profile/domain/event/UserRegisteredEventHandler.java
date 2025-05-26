package com.finverse.profile.domain.event;

import com.finverse.profile.domain.model.UserProfile;
import com.google.gson.Gson;
import com.finverse.profile.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventHandler {

    private Logger logger = LoggerFactory.getLogger(UserRegisteredEventHandler.class);

    private static final Gson GSON = new Gson();
    private final UserRepository userRepository;

    @Autowired
    public UserRegisteredEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleUserRegistration(String userDetails){
        UserProfile user = GSON.fromJson(userDetails, UserProfile.class);
        logger.info("user {} registered.", user.getUsername());
        userRepository.save(user);
    }
}
