package com.finverse.lendingengine.service;

import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.Money;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Component
public class BalanceService {

    private final UserRepository userRepository;

    @Autowired
    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void topUpBalance(final Money amount, UUID userId){
        User user = findUser(userId);
        user.topUp(amount);
    }

    @Transactional
    public void withdrawBalance(final Money amount,UUID userId){
        User user = findUser(userId);
        user.withdraw(amount);
    }

    private User findUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

}
