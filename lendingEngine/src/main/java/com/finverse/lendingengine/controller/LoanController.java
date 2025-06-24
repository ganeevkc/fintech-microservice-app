package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.dto.LoanApplicationDTO;
import com.finverse.lendingengine.dto.LoanDTO;
import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.*;
import com.finverse.lendingengine.repository.LoanApplicationRepository;
import com.finverse.lendingengine.repository.UserRepository;
import com.finverse.lendingengine.service.LoanApplicationAdapter;
import com.finverse.lendingengine.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    public LoanController(LoanApplicationRepository loanApplicationRepository, LoanApplicationAdapter loanApplicationAdapter, LoanService loanService, UserRepository userRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanApplicationAdapter = loanApplicationAdapter;
        this.loanService = loanService;
        this.userRepository = userRepository;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestLoan(@RequestBody LoanRequest loanRequest, @RequestHeader("X-User-ID") UUID userId) {
        try {
            User borrower = findUser(userId);
            LoanApplication application = loanApplicationAdapter.transform(loanRequest, borrower);
            loanApplicationRepository.save(application);
            return ResponseEntity.ok("Loan application submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getAllLoanRequests(@RequestHeader("X-User-ID") UUID userId,
                                                                       @RequestHeader("X-USER-ROLE") String role) {
        try {
            if (!"admin".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied. Admin role required.");
            }
            findUser(userId);
            List<LoanApplication> applications = loanApplicationRepository.findAllByStatusEquals(Status.ACTIVE);
            List<LoanApplicationDTO> dtos = applications.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

    @PostMapping("/accept/{loanApplicationId}")
    public ResponseEntity<String> acceptLoan(@PathVariable Long loanApplicationId,
                                             @RequestHeader("X-User-ID") UUID userId) {
        try {
            User lender = findUser(userId);
            loanService.acceptLoan(loanApplicationId, lender.getUserId());
            return ResponseEntity.ok("Loan accepted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{status}/borrowed")
    public ResponseEntity<?> findBorrowedLoans(@RequestHeader("X-User-ID") UUID userId,
                                                        @PathVariable String status) {
        try {
            User borrower = findUser(userId);
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
            User lender = findUser(userId);
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
            findUser(userId); // Just verify user exists
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
            findUser(userId); // Just verify user exists
            Loan loan = loanService.getLoanById(UUID.fromString(loanId));
            LoanDTO loanDTO = new LoanDTO(loan);
            return ResponseEntity.ok(loanDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/repay")
    public ResponseEntity<String> repayLoan(@RequestBody LoanRepaymentRequest loanRepaymentRequest,
                                            @RequestHeader("X-User-ID") UUID userId) {
        try {
            User borrower = findUser(userId);
            loanService.repayLoan(loanRepaymentRequest.getAmount(), loanRepaymentRequest.getLoanId(), borrower);
            return ResponseEntity.ok("Loan repayment processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
}
