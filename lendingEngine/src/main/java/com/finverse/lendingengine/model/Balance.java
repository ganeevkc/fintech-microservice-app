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
@Table(name = "balance")
public class Balance {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "amount")
    private double amount = 0.0;

    // Simplified - remove complex money map for now
    // We can add it back later if needed

    public void topUp(final Money money) {
        this.amount += money.getAmount();
    }

    public void withdraw(final Money money) {
        if (this.amount < money.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.amount -= money.getAmount();
    }
}