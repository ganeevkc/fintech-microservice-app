package com.finverse.lendingengine.dto;

import com.finverse.lendingengine.model.Money;
import com.finverse.lendingengine.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationDTO {
    private long id;
    private Money amount;
    private String purpose;
    private UUID borrowerId;
    private int daysToRepay;
    private double interestRate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplicationDTO that = (LoanApplicationDTO) o;
        return id == that.id &&
                daysToRepay == that.daysToRepay &&
                Double.compare(that.interestRate, interestRate) == 0 &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(borrowerId, that.borrowerId);
    }
    public int hashCode() {
        return Objects.hash(id, amount, borrowerId, daysToRepay, interestRate);
    }

    @Override
    public String toString() {
        return "LoanApplicationDTO{" +
                "id=" + id +
                ", amount=" + amount +
                ", borrowerId=" + borrowerId +
                ", daysToRepay=" + daysToRepay +
                ", interestRate=" + interestRate +
                '}';
    }
}
