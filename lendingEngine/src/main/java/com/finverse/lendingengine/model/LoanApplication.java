package com.finverse.lendingengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loan_application")
public final class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "loan_amount", nullable = false)
    private double loanAmount;

    @Column(name = "currency", nullable = false, columnDefinition = "ENUM('USD','INR')")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrower_user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private User borrower;

    @Column(name = "purpose", columnDefinition = "TEXT", nullable = false)
    private String purpose;

    @Column(name = "repayment_term", nullable = false)
    private int repaymentTerm;

    @Column(name = "interest_rate", nullable = false)
    private double interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING','REJECTED','COMPLETED', 'ACTIVE', 'DEFAULTED')")
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public LoanApplication() {}

    public LoanApplication(Money amount, User borrower, String purpose, int repaymentTerm, double interestRate) {
        this.loanAmount = amount.getAmount();
        this.currency = amount.getCurrency();
        this.purpose = purpose;
        this.borrower = borrower;
        this.repaymentTerm = repaymentTerm;
        this.interestRate = interestRate;
        this.status = Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
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

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}