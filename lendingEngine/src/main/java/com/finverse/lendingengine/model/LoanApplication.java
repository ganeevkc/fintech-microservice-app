package com.finverse.lendingengine.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public final class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Money loanAmount;

    @ManyToOne
    private User borrower;

    private int repaymentTerm;
    private double interestRate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public LoanApplication() {}

    public LoanApplication(Money amount, User borrower, int repaymentTerm, double interestRate) {
        this.loanAmount = amount;
        this.borrower = borrower;
        this.repaymentTerm = repaymentTerm;
        this.interestRate = interestRate;
        this.status = Status.ACTIVE;
    }

    public Loan acceptLoanApplication(final User lender){
        if (lender.getBalance().getAmount() < 0) {
            throw new IllegalArgumentException("Lender has insufficient balance");
        }
        lender.withdraw(getLoanAmount());
        borrower.topUp(getLoanAmount());
        status = Status.COMPLETED;
        return new Loan(lender, this);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplication that = (LoanApplication) o;
        return Objects.equals(loanAmount, that.loanAmount) &&
                Double.compare(that.interestRate, interestRate) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(borrower, that.borrower) &&
                Objects.equals(repaymentTerm, that.repaymentTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loanAmount, borrower, repaymentTerm, interestRate);
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "id=" + id +
                ", amount=" + loanAmount +
                ", borrower=" + borrower +
                ", repaymentTerm=" + repaymentTerm +
                ", interestRate=" + interestRate +
                '}';
    }
}
