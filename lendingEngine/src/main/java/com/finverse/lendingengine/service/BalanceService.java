
package com.finverse.lendingengine.service;

import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.Balance;
import com.finverse.lendingengine.model.Money;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class BalanceService {

    private final UserRepository userRepository;

    @Autowired
    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void topUpBalance(final Money amount, UUID userId){
        log.info("Top up request for user: {}, amount: {}", userId, amount.getAmount());

        String userIdString = userId.toString();
        User user = findUser(userIdString);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        log.info("User found: {}, current balance: {}",
                user.getUserIdString(),
                user.getBalance() != null ? user.getBalance().getAmount() : "NULL");

        // Create balance if it doesn't exist
        if (user.getBalance() == null) {
            log.info("Creating new balance for user: {}", userId);
            Balance balance = new Balance();
            balance.setAmount(0.0);
            user.setBalance(balance);
        }

        user.topUp(amount);
        userRepository.save(user);

        log.info("✅ Top up successful. New balance: {}", user.getBalance().getAmount());
    }

    @Transactional
    public void withdrawBalance(final Money amount, UUID userId){
        log.info("Withdrawal request for user: {}, amount: {}", userId, amount.getAmount());

        String userIdString = userId.toString();
        User user = findUser(userIdString);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        if (user.getBalance() == null) {
            throw new IllegalArgumentException("User has no balance account");
        }

        log.info("User found: {}, current balance: {}", user.getUserIdString(), user.getBalance().getAmount());

        user.withdraw(amount);
        userRepository.save(user);

        log.info("✅ Withdrawal successful. New balance: {}", user.getBalance().getAmount());
    }

    private User findUser(String userIdString) {
        Optional<User> userOpt = userRepository.findByUserIdString(userIdString);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            log.error("❌ User not found: {}", userIdString);
            return null;
        }
    }
}