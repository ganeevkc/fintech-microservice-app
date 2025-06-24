package com.finverse.lendingengine.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "loan_application")
public final class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "loan_amount")
    private double loanAmount;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_user_id", columnDefinition = "BINARY(16)")
    private User borrower;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "repayment_term")
    private int repaymentTerm;

    @Column(name = "interest_rate")
    private double interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public LoanApplication() {}

    public LoanApplication(Money amount, User borrower, String purpose, int repaymentTerm, double interestRate) {
        this.loanAmount = amount.getAmount();
        this.currency = amount.getCurrency();
        this.purpose = purpose;
        this.borrower = borrower;
        this.repaymentTerm = repaymentTerm;
        this.interestRate = interestRate;
        this.status = Status.ACTIVE;
    }

    public Money getLoanAmount() {
        return new Money(currency, loanAmount);
    }

    public Loan acceptLoanApplication(final User lender) {
        if (lender.getBalance().getAmount() < loanAmount) {
            throw new IllegalArgumentException("Lender has insufficient balance");
        }

        Money loanMoney = new Money(currency, loanAmount);
        lender.withdraw(loanMoney);
        borrower.topUp(loanMoney);
        status = Status.COMPLETED;

        return new Loan(lender, this);
    }
}