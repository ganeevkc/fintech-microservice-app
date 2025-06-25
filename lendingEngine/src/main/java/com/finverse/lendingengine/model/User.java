package com.finverse.lendingengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id",columnDefinition = "VARCHAR(36)", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('lender','borrower','admin')")
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "balance_id",columnDefinition = "VARCHAR(36)")
    private Balance balance;

    public User(UUID userId, Role role) {
        this.userId = userId.toString();
        this.role = role;
    }
    public UUID getUserId() {
        return UUID.fromString(this.userId);
    }
    public String getUserIdString() {
        return this.userId;
    }
    public void setUserIdString(String userId) {
        this.userId = userId;
    }

    public void setBalance(Balance balance) {
        if (this.balance == null) {
            this.balance = balance;
        }
    }

    public void topUp(final Money money) {
        if (balance == null) {
            balance = new Balance();
            balance.setAmount(0.0);
        }
        balance.topUp(money);
    }

    public void withdraw(final Money money) {
        if (balance == null) {
            throw new IllegalArgumentException("No balance available");
        }
        log.debug("Current balance before withdrawal: {}", balance.getAmount());
        balance.withdraw(money);
        log.debug("New balance after withdrawal: {}", balance.getAmount());
    }
}