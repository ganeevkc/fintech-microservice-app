package com.finverse.lendingengine.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId; // Link to Auth user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_user_id", columnDefinition = "BINARY(16)")
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lender_user_id", columnDefinition = "BINARY(16)")
    private User lender;

    @Column(name = "loan_amount")
    private double loanAmount;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.USD;

    @Column(name = "interest_rate")
    private double interestRate;

    @Column(name = "date_lent")
    private LocalDate dateLent;

    @Column(name = "date_due")
    private LocalDate dateDue;

    @Column(name = "amount_paid")
    private double amountPaid = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public Loan() {}

    public Loan(User lender, LoanApplication loanApplication) {
        this.borrower = loanApplication.getBorrower();
        this.lender = lender;
        this.loanAmount = loanApplication.getLoanAmount().getAmount();
        this.currency = Currency.USD;
        this.interestRate = loanApplication.getInterestRate();
        this.dateLent = LocalDate.now();
        this.dateDue = LocalDate.now().plusDays(loanApplication.getRepaymentTerm());
        this.amountPaid = 0.0;
        this.status = Status.ACTIVE;
    }

    public Money getAmountDue() {
        double totalWithInterest = loanAmount * (1 + interestRate / 100d);
        return new Money(currency, totalWithInterest - amountPaid);
    }

    public void repay(final Money money) {
        if (money.getAmount() <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }
        if (borrower.getBalance().getAmount() < money.getAmount()) {
            throw new IllegalArgumentException("Borrower has insufficient balance");
        }

        borrower.withdraw(money);
        lender.topUp(money);
        amountPaid += money.getAmount();

        if (getAmountDue().getAmount() <= 0.01) { // Allow for small rounding errors
            status = Status.COMPLETED;
        }
    }
}