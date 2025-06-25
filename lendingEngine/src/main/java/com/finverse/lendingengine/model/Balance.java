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
    @GeneratedValue(generator = "string-uuid")
    @GenericGenerator(name = "string-uuid", strategy = "com.finverse.lendingengine.config.StringUUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String id;

    @Column(name = "amount",nullable = false)
    private double amount;

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