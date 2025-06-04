package com.finverse.lendingengine.service;

import com.finverse.lendingengine.model.LoanRequest;
import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.LoanApplication;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoanApplicationAdapter {
    @Autowired
    private final UserRepository userRepository;

    public LoanApplicationAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoanApplication transform(LoanRequest loanRequest, User borrower){
        Optional<User> userOptional = userRepository.findById(borrower.getUsername());
        if(userOptional.isPresent()){
            return new LoanApplication(loanRequest.getAmount(),
                    userOptional.get(),
                    loanRequest.getDaysToRepay(),
                    loanRequest.getInterestRate());
        }else {
            throw new UserNotFoundException(borrower.getUsername());
        }
    }
}
