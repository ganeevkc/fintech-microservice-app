package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.model.*;
import com.finverse.lendingengine.repository.LoanApplicationRepository;
import com.finverse.lendingengine.service.TokenValidationService;
import com.finverse.lendingengine.service.LoanApplicationAdapter;
import com.finverse.lendingengine.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationAdapter loanApplicationAdapter;
    private final LoanService loanService;
    private final TokenValidationService tokenValidationService;

    @Autowired
    public LoanController(LoanApplicationRepository loanApplicationRepository, LoanApplicationAdapter loanApplicationAdapter, LoanService loanService, TokenValidationService tokenValidationService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanApplicationAdapter = loanApplicationAdapter;
        this.loanService = loanService;
        this.tokenValidationService = tokenValidationService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestLoan(@RequestBody LoanRequest loanRequest, HttpServletRequest request) {
        try {
            User borrower = tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            LoanApplication application = loanApplicationAdapter.transform(loanRequest, borrower);
            loanApplicationRepository.save(application);
            return ResponseEntity.ok("Loan application submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<List<LoanApplication>> getAllLoanRequests(HttpServletRequest request) {
        try {
            tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            List<LoanApplication> applications = loanApplicationRepository.findAllByStatusEquals(Status.ACTIVE);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept/{loanApplicationId}")
    public ResponseEntity<String> acceptLoan(@PathVariable Long loanApplicationId, HttpServletRequest request) {
        try {
            User lender = tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            loanService.acceptLoan(loanApplicationId, lender.getUserId());
            return ResponseEntity.ok("Loan accepted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{status}/borrowed")
    public ResponseEntity<List<Loan>> findBorrowedLoans(@RequestHeader String authorization, @PathVariable Status status) {
        try {
            User borrower = tokenValidationService.validateToken(authorization);
            List<Loan> loans = loanService.findBorrowedLoans(borrower, status);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{status}/lent")
    public ResponseEntity<List<Loan>> findLentLoans(@RequestHeader String authorization, @PathVariable Status status) {
        try {
            User lender = tokenValidationService.validateToken(authorization);
            List<Loan> loans = loanService.findLentLoans(lender, status);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/repay")
    public ResponseEntity<String> repayLoan(@RequestBody LoanRepaymentRequest loanRepaymentRequest,
                                            @RequestHeader String authorization) {
        try {
            User borrower = tokenValidationService.validateToken(authorization);
            loanService.repayLoan(loanRepaymentRequest.getAmount(), loanRepaymentRequest.getLoanId(), borrower);
            return ResponseEntity.ok("Loan repayment processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans(HttpServletRequest request) {
        try {
            tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            List<Loan> loans = loanService.getAcceptedLoans();
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/id/{loanId}")
    public ResponseEntity<Loan> getLoanById(@PathVariable String loanId, HttpServletRequest request) {
        try {
            tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            Loan loan = loanService.getLoanById(UUID.fromString(loanId));
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
