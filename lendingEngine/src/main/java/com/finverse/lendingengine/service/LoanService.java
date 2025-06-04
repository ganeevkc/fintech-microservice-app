package com.finverse.lendingengine.service;

import com.finverse.lendingengine.model.*;
import com.finverse.lendingengine.repository.LoanApplicationRepository;
import com.finverse.lendingengine.repository.Loanrepository;
import com.finverse.lendingengine.exception.LoanApplicationNotFound;
import com.finverse.lendingengine.exception.LoanNotFoundException;
import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.repository.UserRepository;
import com.finverse.lendingengine.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Component
public class LoanService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final Loanrepository loanrepository;

    @Autowired
    public LoanService(LoanApplicationRepository loanApplicationRepository, UserRepository userRepository, Loanrepository loanrepository) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
        this.loanrepository = loanrepository;
    }

    @Transactional
    public void acceptLoan(final long loanApplicationId, final String username){
        User lender = findUser(username);
        LoanApplication loanApplication = findLoanApplication(loanApplicationId);
        loanrepository.save(loanApplication.acceptLoanApplication(lender));
    }

    @Transactional
    public void repayLoan(final Money amountToRepay,
                          final Long loanId,
                          final User borrower){
        Loan loan = loanrepository.findOneByIdAndBorrower(loanId,borrower).
                orElseThrow(()-> new LoanNotFoundException(loanId));
        Money actualPaidAmount = amountToRepay.getAmount() > loan.getAmountDue().getAmount() ?
                loan.getAmountDue() : amountToRepay;
        loan.repay(actualPaidAmount);
    }

    public List<Loan> getAcceptedLoans() {
       return loanrepository.findAll();
    }

    public List<Loan> findBorrowedLoans(final User borrower,final Status status) {
        return loanrepository.findByBorrowerAndStatus(borrower,status);
    }
    public List<Loan> findLentLoans(final User lender,final Status status) {
        return loanrepository.findByLenderAndStatus(lender,status);
    }

    public Loan getLoanById(long loanId) {
        Optional<Loan> loanObject = loanrepository.findById(loanId);
        if(loanObject.isPresent()){
            return loanObject.get();
        }else{
            throw new LoanNotFoundException(loanId);
        }
    }

    private User findUser(String username) {
        return userRepository.findById(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    private LoanApplication findLoanApplication(long loanApplicationId) {
        return loanApplicationRepository.findById(loanApplicationId).
                orElseThrow(() -> new LoanApplicationNotFound(loanApplicationId));
    }

}
