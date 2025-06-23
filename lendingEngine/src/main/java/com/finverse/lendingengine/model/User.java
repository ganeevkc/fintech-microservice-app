package com.finverse.lendingengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id", columnDefinition = "BINARY(16)")
    private Balance balance;

    public void setBalance(Balance balance) {
        if (this.balance == null) {
            this.balance = balance;
        }
    }

    public void topUp(final Money money) {
        if (balance == null) {
            balance = new Balance();
        }
        balance.topUp(money);
    }

    public void withdraw(final Money money) {
        if (balance == null) {
            throw new IllegalArgumentException("No balance available");
        }
        balance.withdraw(money);
    }
}