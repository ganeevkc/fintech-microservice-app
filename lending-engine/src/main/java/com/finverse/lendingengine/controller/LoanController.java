package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.dto.CreditScoreResponse;
import com.finverse.lendingengine.dto.LoanApplicationDTO;
import com.finverse.lendingengine.dto.LoanDTO;
import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.*;
import com.finverse.lendingengine.repository.LoanApplicationRepository;
import com.finverse.lendingengine.repository.LoanRepository;
import com.finverse.lendingengine.repository.UserRepository;
import com.finverse.lendingengine.service.CreditScoringService;
import com.finverse.lendingengine.service.LoanApplicationAdapter;
import com.finverse.lendingengine.service.LoanService;
import com.finverse.lendingengine.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loan")
@Slf4j
public class LoanController {

    private final UserRepository userRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationAdapter loanApplicationAdapter;
    private final LoanService loanService;
    private final LoanRepository loanRepository;
    private final CreditScoringService creditScoringService;
    private final UserService userService;

    @Autowired
    public LoanController(LoanApplicationRepository loanApplicationRepository, LoanApplicationAdapter loanApplicationAdapter, LoanService loanService, UserRepository userRepository, LoanRepository loanRepository, CreditScoringService creditScoringService, UserService userService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanApplicationAdapter = loanApplicationAdapter;
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.creditScoringService = creditScoringService;
        this.userService = userService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestLoan(@Valid @RequestBody LoanRequest loanRequest,
                                              @RequestHeader("X-User-ID") @NotNull UUID userId) {
        try {
            LoanApplication application = loanService.createLoanApplication(userId, loanRequest);
            return ResponseEntity.ok("Loan application submitted successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Loan request validation failed for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing loan request for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing loan request. Please try again.");
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getAllLoanRequests(@RequestHeader("X-User-ID") @NotNull UUID userId,
                                                @RequestHeader("X-USER-ROLE") String role) {
        try {
            // RBAC: Only admins can view all loan requests
            if (!"admin".equalsIgnoreCase(role)) {
                log.warn("Unauthorized access attempt to view all loan requests by user: {} with role: {}", userId, role);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied. Admin role required.");
            }
            userService.findUser(userId);
            List<LoanApplication> applications = loanApplicationRepository.findAllByStatusEquals(Status.ACTIVE);
            List<LoanApplicationDTO> dtos = applications.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("Retrieved {} active loan applications for admin user: {}", dtos.size(), userId);
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            log.error("Error retrieving loan requests for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving loan requests");
        }
    }


    @PostMapping("/accept/{loanApplicationId}")
    public ResponseEntity<?> acceptLoan(@PathVariable @NotNull Long loanApplicationId,
                                        @RequestHeader("X-User-ID") @NotNull UUID userId) {
        try {
            log.info("Processing loan acceptance for application: {} by user: {}", loanApplicationId, userId);

            User lender = userService.findUser(userId);

            validateLoanAcceptance(loanApplicationId, lender);

            loanService.acceptLoan(loanApplicationId, lender.getUserId());

            log.info("Loan application {} accepted successfully by user: {}", loanApplicationId, userId);
            return ResponseEntity.ok("Loan accepted successfully");

        } catch (IllegalArgumentException e) {
            log.warn("Loan acceptance validation failed for application {}: {}", loanApplicationId, e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error accepting loan application {}: {}", loanApplicationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing loan acceptance");
        }
    }

    @GetMapping("/{status}/borrowed")
    public ResponseEntity<?> findBorrowedLoans(@RequestHeader("X-User-ID") UUID userId,
                                                        @PathVariable String status) {
        try {
            User borrower = userService.findUser(userId);
            Status statusEnum = Status.valueOf(status.toUpperCase());
            List<Loan> loans = loanService.findBorrowedLoans(borrower, statusEnum);
            List<LoanDTO> loanDTOs = loans.stream()
                    .map(LoanDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(loanDTOs);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{status}/lent")
    public ResponseEntity<?> findLentLoans(@RequestHeader("X-User-ID") UUID userId,
                                                    @PathVariable String status) {
        try {
            User lender = userService.findUser(userId);
            Status statusEnum = Status.valueOf(status.toUpperCase());
            List<Loan> loans = loanService.findLentLoans(lender, statusEnum);
            List<LoanDTO> loanDTOs = loans.stream()
                    .map(LoanDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(loanDTOs);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllLoans(@RequestHeader("X-User-ID") UUID userId) {
        try {
            userService.findUser(userId);
            List<Loan> loans = loanService.getAcceptedLoans();

            List<LoanDTO> loanDTOs = loans.stream()
                    .map(LoanDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(loanDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/id/{loanId}")
    public ResponseEntity<?> getLoanById(@PathVariable String loanId,
                                         @RequestHeader("X-User-ID") UUID userId) {
        try {
            userService.findUser(userId); // Just verify user exists
            Loan loan = loanService.getLoanById(UUID.fromString(loanId));
            LoanDTO loanDTO = new LoanDTO(loan);
            return ResponseEntity.ok(loanDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/repay")
    public ResponseEntity<?> repayLoan(@Valid @RequestBody LoanRepaymentRequest loanRepaymentRequest,
                                       @RequestHeader("X-User-ID") @NotNull UUID userId) {
        try {
            log.info("Processing loan repayment for loan: {} by user: {}, amount: {}",
                    loanRepaymentRequest.getLoanId(), userId, loanRepaymentRequest.getAmountValue());

            User borrower = userService.findUser(userId);

            validateLoanRepayment(loanRepaymentRequest, borrower);

            loanService.repayLoan(loanRepaymentRequest.getAmount(), loanRepaymentRequest.getLoanId(), borrower);

            CreditScoringService.updateScoreAfterLoanEvent(userId, "REPAYMENT_MADE");

            log.info("Loan repayment processed successfully for loan: {}", loanRepaymentRequest.getLoanId());
            return ResponseEntity.ok("Loan repayment processed successfully");

        } catch (IllegalArgumentException e) {
            log.warn("Loan repayment validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing loan repayment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing loan repayment");
        }
    }

    @GetMapping("/credit-score")
    public ResponseEntity<CreditScoreResponse> getCreditScore(@RequestHeader("X-User-ID") @NotNull UUID userId) {
        try {
            var creditScore = creditScoringService.getCreditScore(userId);
            double maxLoanAmount = creditScoringService.calculateMaxLoanAmount(userId);
            double recommendedInterestRate = creditScoringService.getRecommendedInterestRate(userId);

            CreditScoreResponse response = new CreditScoreResponse();
            response.setScore(creditScore.getScore());
            response.setRiskCategory(creditScore.getRiskCategory().toString()); // Convert enum to String
            response.setCreditLimit(creditScore.getCreditLimit());
            response.setMaxLoanAmount(maxLoanAmount);
            response.setRecommendedInterestRate(recommendedInterestRate);
            response.setTotalLoans(creditScore.getTotalLoans());
            response.setCompletedLoans(creditScore.getCompletedLoans());
            response.setRepaymentRate(creditScore.getRepaymentRate());
            response.setScoreRange(creditScore.getRiskCategory().getScoreRange());
            response.setDescription(creditScore.getRiskCategory().getDetailedDescription());

            log.info("✅ Credit score response: Score={}, Risk={}, MaxLoan={}, Rate={}%",
                    creditScore.getScore(), creditScore.getRiskCategory(), maxLoanAmount, recommendedInterestRate);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error getting credit score for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    private void validateLoanRequest(LoanRequest request, User borrower) {
//        // Check if user already has an active loan
//        List<Loan> activeLoans = loanService.findBorrowedLoans(borrower, Status.ACTIVE);
//
//        if (!activeLoans.isEmpty()) {
//            throw new IllegalArgumentException("User already has an active loan");
//        }
//
//        // Use credit scoring system for loan limit
//        double maxLoanAmount = creditScoringService.calculateMaxLoanAmount(borrower.getUserId());
//        if (request.getAmount().getAmount() > maxLoanAmount) {
//            var creditScore = creditScoringService.getCreditScore(borrower.getUserId());
//            throw new IllegalArgumentException(
//                    String.format("Loan amount %.2f exceeds maximum allowed amount %.2f (Credit Score: %d, Risk: %s)",
//                            request.getAmount().getAmount(), maxLoanAmount,
//                            creditScore.getScore(), creditScore.getRiskCategory()));
//        }
//
//        double recommendedRate = creditScoringService.getRecommendedInterestRate(borrower.getUserId());
//        if (request.getInterestRate() < recommendedRate) {
//            throw new IllegalArgumentException(
//                    String.format("Interest rate %.1f%% is below recommended rate %.1f%% for your credit profile",
//                            request.getInterestRate(), recommendedRate));
//        }
//
//        if (request.getPurpose() == null || request.getPurpose().trim().isEmpty()) {
//            throw new IllegalArgumentException("Loan purpose cannot be empty");
//        }
//    }

    private void validateLoanAcceptance(Long loanApplicationId, User lender) {
        LoanApplication application = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));

        if (application.getStatus() != Status.ACTIVE) {
            throw new IllegalArgumentException("Loan application is not active");
        }

        // Check if lender has sufficient balance
        if (lender.getBalance() == null ||
                lender.getBalance().getAmount() < application.getLoanAmount().getAmount()) {
            throw new IllegalArgumentException("Insufficient balance to accept this loan");
        }

        // Prevent self-lending
        if (application.getBorrower().getUserId().equals(lender.getUserId())) {
            throw new IllegalArgumentException("Cannot lend money to yourself");
        }
    }

    private void validateLoanRepayment(LoanRepaymentRequest request, User borrower) {
        // Check if borrower has sufficient balance
        if (borrower.getBalance() == null ||
                borrower.getBalance().getAmount() < request.getAmountValue()) {
            throw new IllegalArgumentException("Insufficient balance for repayment");
        }
    }

    private double calculateMaxLoanAmount(User borrower) {
        return creditScoringService.calculateMaxLoanAmount(borrower.getUserId());
    }

    private LoanApplicationDTO convertToDTO(LoanApplication application) {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(application.getId());
        Money money = new Money();
        money.setCurrency(application.getCurrency());
        money.setAmount(application.getLoanAmount().getAmount());
        dto.setAmount(money);
        dto.setPurpose(application.getPurpose());
        dto.setDaysToRepay(application.getRepaymentTerm());
        dto.setInterestRate(application.getInterestRate());
        dto.setBorrowerId(application.getBorrower().getUserId());
        return dto;
    }


}
