package com.finverse.lendingengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Loan {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId; // Link to Auth user

    @ManyToOne
    private User borrower;
    @ManyToOne
    private User lender;
    @OneToOne(cascade = CascadeType.ALL)
    private Money loanAmount;

    private double interestRate;
    private LocalDate dateLent;
    private LocalDate dateDue;
    @OneToOne(cascade = CascadeType.ALL)
    private Money amountPaid;

    @Enumerated(EnumType.STRING)
    private Status status;


    public Loan() {}

    public Loan(User lender,LoanApplication loanApplication){
        this.borrower=loanApplication.getBorrower();
        this.lender=lender;
        this.loanAmount=loanApplication.getLoanAmount();
        this.interestRate=loanApplication.getInterestRate();
        this.dateLent=LocalDate.now();
        this.dateDue=LocalDate.now().plusDays(loanApplication.getRepaymentTerm());
        this.amountPaid=Money.ZERO;
        this.status=Status.ACTIVE;
    }

    public Money getAmountDue(){
        return loanAmount.times(1 /* principal */+interestRate/100d).minus(amountPaid);
    }

    public void repay(final Money money){
        borrower.withdraw(money);
        lender.topUp(money);
        amountPaid = amountPaid.add(money);

        if(getAmountDue().equals(Money.ZERO)){
            status=Status.COMPLETED;
        }
    }

//    public Long getId() {
//        return id;
//    }
//
//    public User getBorrower() {
//        return borrower;
//    }
//
//    public User getLender() {
//        return lender;
//    }
//
//    public Money getAmountPaid() {
//        return amountPaid;
//    }
}
