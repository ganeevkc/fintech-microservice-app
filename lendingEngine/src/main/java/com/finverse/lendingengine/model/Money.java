package com.finverse.lendingengine.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "money")
public final class Money {

    public static final Money ZERO = new Money(Currency.USD, 0.0);

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "amount")
    private Double amount;

    public Money() {}

    public Money(Currency currency, double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Money add(final Money money) {
        if (currency != money.getCurrency()) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(currency, amount + money.getAmount());
    }

    public Money minus(final Money money) {
        if (currency != money.getCurrency() || amount < money.getAmount()) {
            throw new IllegalArgumentException("Invalid operation");
        }
        return new Money(currency, amount - money.getAmount());
    }

    public double getAmount() {
        return amount != null ? amount.doubleValue() : 0.0;
    }

    public Money times(double multiplier) {
        return new Money(currency, amount.doubleValue() * multiplier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return currency == money.currency && Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }

    @Override
    public String toString() {
        return "Money{" +
                "currency=" + currency +
                ", amount=" + amount +
                '}';
    }
}