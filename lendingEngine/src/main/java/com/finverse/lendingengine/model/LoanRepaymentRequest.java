package com.finverse.lendingengine.model;

import lombok.Data;

import java.util.Objects;
import java.util.UUID;

@Data
public final class LoanRepaymentRequest {

    private final double amount;
    private final UUID loanId;

    public LoanRepaymentRequest(double amount, UUID loanId) {
        this.amount = amount;
        this.loanId = loanId;
    }

    public Money getAmount() {
        return new Money(Currency.USD,amount);
    }

//    public long getLoanId() {
//        return loanId;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanRepaymentRequest that = (LoanRepaymentRequest) o;
        return Double.compare(that.amount, amount) == 0 && loanId == that.loanId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, loanId);
    }

    @Override
    public String toString() {
        return "LoanRepaymentRequest{" +
                "amount=" + amount +
                ", loanId=" + loanId +
                '}';
    }
}

