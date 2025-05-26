package com.finverse.lendingengine.domain.event;

import com.finverse.lendingengine.domain.model.Balance;
import com.finverse.lendingengine.domain.model.User;
import com.finverse.lendingengine.domain.repository.UserRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventHandler {

    private Logger logger = LoggerFactory.getLogger(UserRegisteredEventHandler.class);

    private static final Gson GSON = new Gson(); // to deserialize JSON coming from RabbitMQ into a Java object

    private final UserRepository userRepository;

    @Autowired
    public UserRegisteredEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleUserRegistration(String userDetails){ //userDetails coming from RabbitMQ
        User user = GSON.fromJson(userDetails, User.class);
        user.setBalance(new Balance());
        logger.info("user {} registered.", user.getUsername());
        userRepository.save(user);
    }
}
