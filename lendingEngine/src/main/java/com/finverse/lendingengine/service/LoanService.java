package com.finverse.lendingengine.service;

import com.finverse.lendingengine.model.*;
import com.finverse.lendingengine.repository.LoanApplicationRepository;
import com.finverse.lendingengine.repository.LoanRepository;
import com.finverse.lendingengine.exception.LoanApplicationNotFound;
import com.finverse.lendingengine.exception.LoanNotFoundException;
import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoanService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanrepository;

    @Autowired
    public LoanService(LoanApplicationRepository loanApplicationRepository, UserRepository userRepository, LoanRepository loanrepository, CreditScoringService creditScoringService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
        this.loanrepository = loanrepository;
    }

    @Transactional
    public void acceptLoan(final long loanApplicationId, final UUID userId){
        String userIdString = userId.toString();
        Optional<User> lender = userRepository.findById(userIdString);
        if (lender.isPresent()) {
            LoanApplication loanApplication = findLoanApplication(loanApplicationId);
            Loan loan = loanApplication.acceptLoanApplication(lender.get());
            loanrepository.save(loan);
            CreditScoringService.updateScoreAfterLoanEvent(
                    loanApplication.getBorrower().getUserId(), "LOAN_ACCEPTED");
            CreditScoringService.updateScoreAfterLoanEvent(userId, "LOAN_GIVEN");
            log.info("Loan {} accepted by lender {}. Credit scores updated.",
                    loanApplicationId, userId);
        }
    }

    @Transactional
    public void repayLoan(final Money amountToRepay,
                          final UUID loanId,
                          final User borrower){
        Loan loan = loanrepository.findOneByIdAndBorrower(loanId,borrower).
                orElseThrow(()-> new LoanNotFoundException(loanId));
        Money actualPaidAmount = amountToRepay.getAmount() > loan.getAmountDue().getAmount() ?
                loan.getAmountDue() : amountToRepay;
        loan.repay(actualPaidAmount);
        loanrepository.save(loan);
        String eventType = loan.getStatus() == Status.COMPLETED ? "LOAN_COMPLETED" : "REPAYMENT_MADE";
        CreditScoringService.updateScoreAfterLoanEvent(borrower.getUserId(), eventType);
        log.info("Loan repayment processed for loan {}. Amount: {}, Event: {}, Credit score updated.",
                loanId, actualPaidAmount, eventType);
    }

    @Transactional
    public void processLoanDefaults() {
        LocalDate today = LocalDate.now();
        List<Loan> overdueLoans = loanrepository.findByStatus(Status.ACTIVE)
                .stream()
                .filter(loan -> loan.getDateDue().isBefore(today.minusDays(30))) // 30 days overdue
                .collect(Collectors.toList());

        for (Loan loan : overdueLoans) {
            // Mark as defaulted (you might want to add a DEFAULTED status)
            log.warn("Loan {} is 30+ days overdue. Updating credit score for borrower {}",
                    loan.getId(), loan.getBorrower().getUserId());

            CreditScoringService.updateScoreAfterLoanEvent(
                    loan.getBorrower().getUserId(), "LOAN_DEFAULTED");
        }
    }

    public List<Loan> getAcceptedLoans() {
       return loanrepository.findAll();
    }

    public List<Loan> findBorrowedLoans(final User borrower,final Status status) {
        return loanrepository.findByBorrowerAndStatus(borrower, status);
    }
    public List<Loan> findLentLoans(final User lender,final Status status) {
        return loanrepository.findByLenderAndStatus(lender,status);
    }

    public Loan getLoanById(UUID loanId) {
        String loanIdString = loanId.toString();
        Optional<Loan> loanObject = loanrepository.findById(loanIdString);
        if(loanObject.isPresent()){
            return loanObject.get();
        }else{
            throw new LoanNotFoundException(loanId);
        }
    }

    private LoanApplication findLoanApplication(long loanApplicationId) {
        return loanApplicationRepository.findById(loanApplicationId).
                orElseThrow(() -> new LoanApplicationNotFound(loanApplicationId));
    }

}
