package com.finverse.lendingengine.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Slf4j
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(generator = "string-uuid")
    @GenericGenerator(name = "string-uuid", strategy = "com.finverse.lendingengine.config.StringUUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String id;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36)")
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrower_user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private User borrower;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lender_user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private User lender;

    @Column(name = "loan_amount", nullable = false)
    private double loanAmount;

    @Column(name = "currency", nullable = false, columnDefinition = "ENUM('USD', 'INR') DEFAULT 'USD'")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "interest_rate", nullable = false)
    private double interestRate;

    @Column(name = "date_lent", nullable = false)
    private LocalDate dateLent;

    @Column(name = "date_due", nullable = false)
    private LocalDate dateDue;

    @Column(name = "amount_paid", nullable = false)
    private double amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING','REJECTED','COMPLETED', 'ACTIVE')")
    private Status status;

    public Loan() {}

    public Loan(User lender, LoanApplication loanApplication) {
        this.borrower = loanApplication.getBorrower();
        this.lender = lender;
        this.userId = loanApplication.getBorrower().getUserIdString();
        this.loanAmount = loanApplication.getLoanAmount().getAmount();
        this.currency = Currency.USD;
        this.interestRate = loanApplication.getInterestRate();
        this.dateLent = LocalDate.now();
        this.dateDue = LocalDate.now().plusDays(loanApplication.getRepaymentTerm());
        this.amountPaid = 0.0;
        this.status = Status.ACTIVE;
    }

    public UUID getIdAsUUID() {
        return id != null ? UUID.fromString(id) : null;
    }

    public UUID getUserIdAsUUID() {
        return userId != null ? UUID.fromString(userId) : null;
    }

    public Money getAmountDue() {
        double totalWithInterest = this.loanAmount * (1 + this.interestRate / 100d);
        double remaining = totalWithInterest - this.amountPaid;
        remaining = Math.max(0, remaining);
        return new Money(this.currency, remaining);
    }

    public void repay(final Money money) {
        log.info("💰 Processing repayment - Amount: {}, Current amount paid: {}, Amount due: {}",
                money.getAmount(), this.amountPaid, this.getAmountDue().getAmount());
        if (money.getAmount() <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }
        if (borrower.getBalance().getAmount() < money.getAmount()) {
            throw new IllegalArgumentException("Borrower has insufficient balance");
        }

        borrower.withdraw(money);
        lender.topUp(money);
        this.amountPaid += money.getAmount();
        log.info("💵 After repayment - Amount paid: {}, Remaining due: {}",
                this.amountPaid, this.getAmountDue().getAmount());
        double totalAmountDue = this.loanAmount * (1 + this.interestRate / 100d);

        log.info("📊 Total amount due: {}, Amount paid: {}, Difference: {}",
                totalAmountDue, this.amountPaid, Math.abs(totalAmountDue - this.amountPaid));

        // Check if loan is fully paid (with small tolerance for rounding errors)
        if (Math.abs(totalAmountDue - this.amountPaid) <= 0.01) {
            this.status = Status.COMPLETED;
            log.info("✅ LOAN COMPLETED! Status changed to: {}", this.status);
        } else {
            log.info("🔄 Loan still active. Need to pay: {}", totalAmountDue - this.amountPaid);
        }
    }
}