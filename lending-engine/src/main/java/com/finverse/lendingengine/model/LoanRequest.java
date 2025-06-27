package com.finverse.lendingengine.model;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Objects;

@Data
public class LoanRequest {

    @NotNull(message = "Currency is required")
    @Valid
    private Money amount;

    @NotBlank(message = "Purpose is required")
    @Size(min = 10, max = 500, message = "Purpose must be between 10-500 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\.,!?'-]+$", message = "Purpose contains invalid characters")
    private String purpose;

    @Min(value = 7, message = "Minimum repayment term is 7 days")
    @Max(value = 365, message = "Maximum repayment term is 365 days")
    private int daysToRepay;

    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1%")
    @DecimalMax(value = "30.0", message = "Interest rate cannot exceed 30%")
    @Digits(integer = 2, fraction = 2, message = "Interest rate must have at most 2 decimal places")
    private double interestRate;

    public LoanRequest(Money amount, String purpose, int daysToRepay, double interestRate) {
        this.amount = amount;
        this.purpose=purpose;
        this.daysToRepay = daysToRepay;
        this.interestRate = interestRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanRequest that = (LoanRequest) o;
        return daysToRepay == that.daysToRepay &&
                Double.compare(that.interestRate, interestRate) == 0 &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(purpose, that.purpose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, purpose, daysToRepay, interestRate);
    }

    @Override
    public String toString() {
        return "LoanRequest{" +
                "amount=" + amount +
                ", purpose='" + purpose + '\'' +
                ", daysToRepay=" + daysToRepay +
                ", interestRate=" + interestRate +
                '}';
    }
}
