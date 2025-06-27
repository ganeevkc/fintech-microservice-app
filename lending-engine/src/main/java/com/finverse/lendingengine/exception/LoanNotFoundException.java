package com.finverse.lendingengine.exception;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException{
    public LoanNotFoundException(UUID loanId) {
        super("No loan with id:"+loanId+" is present in the system.");
    }
}
