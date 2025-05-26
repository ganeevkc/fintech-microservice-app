package com.finverse.lendingengine.application;

import com.finverse.lendingengine.application.model.LoanRepaymentRequest;
import com.finverse.lendingengine.application.model.LoanRequest;
import com.finverse.lendingengine.application.service.TokenValidationService;
import com.finverse.lendingengine.domain.repository.LoanApplicationRepository;
import com.finverse.lendingengine.domain.model.Loan;
import com.finverse.lendingengine.domain.model.LoanApplication;
import com.finverse.lendingengine.domain.model.Status;
import com.finverse.lendingengine.domain.model.User;
import com.finverse.lendingengine.domain.service.LoanApplicationAdapter;
import com.finverse.lendingengine.domain.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationAdapter loanApplicationAdapter;
    private final LoanService loanService;
    private final TokenValidationService tokenValidationService;

    @Autowired
    public LoanController(LoanApplicationRepository loanApplicationRepository,LoanApplicationAdapter loanApplicationAdapter, LoanService loanService, TokenValidationService tokenValidationService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanApplicationAdapter = loanApplicationAdapter;
        this.loanService = loanService;
        this.tokenValidationService = tokenValidationService;
    }

    @PostMapping("/request")
    public void requestLoan(@RequestBody LoanRequest lonaRequest, HttpServletRequest request){
        User borrower = tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        loanApplicationRepository.save(loanApplicationAdapter.transform(lonaRequest,borrower));
    }

    @GetMapping("/requests")
    public List<LoanApplication> getAllLoanRequests(HttpServletRequest request){
        tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return loanApplicationRepository.findAllByStatusEquals(Status.ONGOING);
    }

    @GetMapping("/{status}/borrowed")
    public List<Loan> findBorrowedLoans(@RequestHeader String authorization, @PathVariable Status status){
        User borrower = tokenValidationService.validateToken(authorization);
        return loanService.findBorrowedLoans(borrower,status);
    }

    @GetMapping("/{status}/lent")
    public List<Loan> findLentLoans(@RequestHeader String authorization,@PathVariable Status status){
        User lender = tokenValidationService.validateToken(authorization);
        return loanService.findLentLoans(lender,status);
    }

    @PostMapping("/repay")
    public void replayLoan(@RequestBody LoanRepaymentRequest loanRepaymentRequest, @RequestHeader String authorization){
        User borrower = tokenValidationService.validateToken(authorization);
        loanService.repayLoan(loanRepaymentRequest.getAmount(),loanRepaymentRequest.getLoanId(),borrower);
    }

    @PostMapping("/accept/{loanApplicationId}")
    public void acceptLoan(@PathVariable final String loanApplicationId,HttpServletRequest request){
        User username =tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        loanService.acceptLoan(Long.parseLong(loanApplicationId),username.getUsername());
    }

    @GetMapping
    public List<Loan> getAllLoans(HttpServletRequest request){
        tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return loanService.getAcceptedLoans();
    }

    @GetMapping("/id/{loanId}")
    public Loan getLoanById(@PathVariable final String loanId,HttpServletRequest request){
        tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return loanService.getLoanById(Long.parseLong(loanId));
    }

}
