package com.finverse.lendingengine.service;

import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.Money;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
public class BalanceService {

    private final UserRepository userRepository;

    @Autowired
    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void topUpBalance(final Money money, String authToken){
        User user = findUser(authToken);
        user.topUp(money);
    }

    @Transactional
    public void withdrawBalance(final Money money,String authToken){
        User user = findUser(authToken);
        user.withdraw(money);
    }

    private User findUser(String authToken) {
        return userRepository.findById(UUID.fromString(authToken))
                .orElseThrow(() -> new UserNotFoundException(authToken));
    }

}
