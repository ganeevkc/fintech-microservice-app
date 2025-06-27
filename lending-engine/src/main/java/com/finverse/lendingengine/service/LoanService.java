package com.finverse.lendingengine.service;

import com.finverse.lendingengine.model.*;
import com.finverse.lendingengine.repository.LoanApplicationRepository;
import com.finverse.lendingengine.repository.LoanRepository;
import com.finverse.lendingengine.exception.LoanApplicationNotFound;
import com.finverse.lendingengine.exception.LoanNotFoundException;
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
    private final LoanApplicationAdapter loanApplicationAdapter;
    private final LoanRepository loanrepository;
    private final LoanService loanService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CreditScoringService creditScoringService;

    @Autowired
    public LoanService(LoanApplicationRepository loanApplicationRepository, LoanApplicationAdapter loanApplicationAdapter, LoanService loanService, UserService userService, UserRepository userRepository, LoanRepository loanrepository, CreditScoringService creditScoringService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanApplicationAdapter = loanApplicationAdapter;
        this.loanService = loanService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.loanrepository = loanrepository;
        this.creditScoringService = creditScoringService;
    }

    public LoanApplication createLoanApplication(UUID userId, LoanRequest loanRequest) {
        User borrower = userService.findUser(userId);

        validateLoanRequest(loanRequest, borrower);

        LoanApplication application = loanApplicationAdapter.transform(loanRequest, borrower);
        loanApplicationRepository.save(application);
        return application;
    }

    @Transactional
    public void acceptLoan(final long loanApplicationId, final UUID userId){
        log.info("🔥 Starting loan acceptance - Application ID: {}, Lender ID: {}", loanApplicationId, userId);

        String userIdString = userId.toString();
        Optional<User> lenderOpt = userRepository.findByUserIdString(userIdString);

        if (lenderOpt.isPresent()) {
            User lender = lenderOpt.get();
            log.info("✅ Lender found: {}", lender.getUserId());

            LoanApplication loanApplication = findLoanApplication(loanApplicationId);
            log.info("✅ Loan application found: {}, Borrower: {}, Amount: {}",
                    loanApplication.getId(),
                    loanApplication.getBorrower().getUserId(),
                    loanApplication.getLoanAmount().getAmount());

            // Accept the loan application - this creates the Loan entity
            Loan loan = loanApplication.acceptLoanApplication(lender);
            log.info("✅ Loan created: ID={}, Borrower={}, Lender={}, Amount={}, Status={}",
                    loan.getId(),
                    loan.getBorrower().getUserId(),
                    loan.getLender().getUserId(),
                    loan.getLoanAmount(),
                    loan.getStatus());

            // Save the loan
            Loan savedLoan = loanrepository.save(loan);
            log.info("✅ Loan saved to database: {}", savedLoan.getId());

            // Update the loan application status to COMPLETED
            loanApplication.setStatus(Status.COMPLETED);
            loanApplicationRepository.save(loanApplication);
            log.info("Loan application {} marked as COMPLETED", loanApplication.getId());

            log.info("Loan acceptance completed successfully");
        } else {
            log.error("Lender not found: {}", userId);
            throw new IllegalArgumentException("Lender not found");
        }
    }

    @Transactional
    public void repayLoan(final Money amountToRepay,
                          final UUID loanId,
                          final User borrower){
        log.info("Processing loan repayment: Loan ID: {}, Borrower: {}, Amount: {}",
                loanId, borrower.getUserId(), amountToRepay.getAmount());

        String loanIdString = loanId.toString();
        Loan loan = loanrepository.findOneByIdAndBorrower(loanIdString, borrower)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        log.info("📋 Loan found - Current status: {}, Amount due: {}, Amount paid: {}",
                loan.getStatus(), loan.getAmountDue().getAmount(), loan.getAmountPaid());

        Money actualPaidAmount = amountToRepay.getAmount() > loan.getAmountDue().getAmount() ?
                loan.getAmountDue() : amountToRepay;

        log.info("💰 Actual payment amount: {}", actualPaidAmount.getAmount());

        loan.repay(actualPaidAmount);

        log.info("📊 After repayment - Status: {}, Amount due: {}, Amount paid: {}",
                loan.getStatus(), loan.getAmountDue().getAmount(), loan.getAmountPaid());

        Loan savedLoan = loanrepository.save(loan);
        log.info("💾 Loan saved with status: {}", savedLoan.getStatus());

        log.info("✅ Loan repayment processed successfully");
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

    private void validateLoanRequest(LoanRequest request, User borrower) {
        // Check if user already has an active loan
        List<Loan> activeLoans = loanService.findBorrowedLoans(borrower, Status.ACTIVE);

        if (!activeLoans.isEmpty()) {
            throw new IllegalArgumentException("User already has an active loan");
        }

        // Use credit scoring system for loan limit
        double maxLoanAmount = creditScoringService.calculateMaxLoanAmount(borrower.getUserId());
        if (request.getAmount().getAmount() > maxLoanAmount) {
            var creditScore = creditScoringService.getCreditScore(borrower.getUserId());
            throw new IllegalArgumentException(
                    String.format("Loan amount %.2f exceeds maximum allowed amount %.2f (Credit Score: %d, Risk: %s)",
                            request.getAmount().getAmount(), maxLoanAmount,
                            creditScore.getScore(), creditScore.getRiskCategory()));
        }

        double recommendedRate = creditScoringService.getRecommendedInterestRate(borrower.getUserId());
        if (request.getInterestRate() < recommendedRate) {
            throw new IllegalArgumentException(
                    String.format("Interest rate %.1f%% is below recommended rate %.1f%% for your credit profile",
                            request.getInterestRate(), recommendedRate));
        }

        if (request.getPurpose() == null || request.getPurpose().trim().isEmpty()) {
            throw new IllegalArgumentException("Loan purpose cannot be empty");
        }
    }
}
