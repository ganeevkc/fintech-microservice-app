package com.finverse.lendingengine.model;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Data
public final class LoanRepaymentRequest {

    @NotNull(message = "Repayment amount is required")
    @DecimalMin(value = "0.01", message = "Repayment amount must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Repayment amount cannot exceed 999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 digits and 2 decimal places")
    private final double amount;

    @NotNull(message = "Loan ID is required")
    private final UUID loanId;

    public LoanRepaymentRequest(double amount, UUID loanId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }
        if (loanId == null) {
            throw new IllegalArgumentException("Loan ID cannot be null");
        }

        this.amount = amount;
        this.loanId = loanId;
    }

    public Money getAmount() {
        return new Money(Currency.USD, amount);
    }

    public double getAmountValue() {
        return amount;
    }

    public UUID getLoanId() {
        return loanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanRepaymentRequest that = (LoanRepaymentRequest) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(loanId, that.loanId);
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

