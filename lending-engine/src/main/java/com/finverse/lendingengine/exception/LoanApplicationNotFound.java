package com.finverse.lendingengine.exception;

public class LoanApplicationNotFound extends RuntimeException{

    public LoanApplicationNotFound(long loanApplicationId) {
        super("Loan Application with id:"+loanApplicationId+" is not found.");
    }
}
