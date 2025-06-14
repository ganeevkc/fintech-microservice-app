package com.finverse.lendingengine.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public final class LoanApplication {

    @Id
    @GeneratedValue
    private Long id;

    private Money LoanAmount;
    @ManyToOne
    private User borrower;
    private int repaymentTerm;
    private double interestRate;
    private Status status;

    public LoanApplication() {}

    public LoanApplication(Money amount, User borrower, int repaymentTerm, double interestRate) {
        this.LoanAmount = amount;
        this.borrower = borrower;
        this.repaymentTerm = repaymentTerm;
        this.interestRate = interestRate;
        this.status=Status.ACTIVE;
    }

    public Loan acceptLoanApplication(final User lender){
        lender.withdraw(getLoanAmount());
        borrower.topUp(getLoanAmount());
        status = Status.COMPLETED;
        return new Loan(lender, this);
    }

//    public Long getId() {
//        return id;
//    }
//
//    public Money getAmount() {
//        return new Money(Currency.USD,amount);
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }
//
//    public User getBorrower() {
//        return borrower;
//    }
//
//    public void setBorrower(User borrower) {
//        this.borrower = borrower;
//    }
//
//    public int getRepaymentTerm() {
//        return repaymentTerm;
//    }
//
//    public void setRepaymentTerm(int repaymentTerm) {
//        this.repaymentTerm = repaymentTerm;
//    }
//
//    public double getInterestRate() {
//        return interestRate;
//    }
//
//    public void setInterestRate(double interestRate) {
//        this.interestRate = interestRate;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplication that = (LoanApplication) o;
        return LoanAmount == that.LoanAmount && Double.compare(that.interestRate, interestRate) == 0 && Objects.equals(id, that.id) && Objects.equals(borrower, that.borrower) && Objects.equals(repaymentTerm, that.repaymentTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, LoanAmount, borrower, repaymentTerm, interestRate);
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "id=" + id +
                ", amount=" + LoanAmount +
                ", borrower=" + borrower +
                ", repaymentTerm=" + repaymentTerm +
                ", interestRate=" + interestRate +
                '}';
    }
}
