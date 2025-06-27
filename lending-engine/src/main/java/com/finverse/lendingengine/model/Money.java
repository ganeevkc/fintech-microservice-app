package com.finverse.lendingengine.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "money")
public final class Money {

    public static final Money ZERO = new Money(Currency.USD, 0.0);

    @Id
    @GeneratedValue(generator = "string-uuid")
    @GenericGenerator(name = "string-uuid", strategy = "com.finverse.lendingengine.config.StringUUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, columnDefinition = "ENUM('USD','INR')")
    private Currency currency;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Amount cannot exceed 999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 digits and 2 decimal places")
    @Column(name = "amount", nullable = false)
    private Double amount;

    public Money() {}

    public Money(Currency currency, double amount) {
        validateAmount(amount);
        this.currency = currency;
        this.amount = amount;
    }

    private void validateAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount > 999999.99) {
            throw new IllegalArgumentException("Amount exceeds maximum limit");
        }
    }

    public Money add(final Money money) {
        if (currency != money.getCurrency()) {
            throw new IllegalArgumentException("Currency mismatch: cannot add " +
                    currency + " and " + money.getCurrency());
        }
        double newAmount = this.amount + money.getAmount();
        validateAmount(newAmount);
        return new Money(currency, newAmount);
    }

    public Money minus(final Money money) {
        if (currency != money.getCurrency()) {
            throw new IllegalArgumentException("Currency mismatch: cannot subtract " +
                    money.getCurrency() + " from " + currency);
        }
        if (amount < money.getAmount()) {
            throw new IllegalArgumentException("Insufficient amount: cannot subtract " +
                    money.getAmount() + " from " + amount);
        }
        return new Money(currency, amount - money.getAmount());
    }

    public double getAmount() {
        return amount != null ? amount : 0.0;
    }

    public Money times(double multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        double newAmount = amount * multiplier;
        validateAmount(newAmount);
        return new Money(currency, newAmount);
    }

    public boolean isGreaterThan(Money other) {
        if (currency != other.getCurrency()) {
            throw new IllegalArgumentException("Cannot compare different currencies");
        }
        return this.amount > other.getAmount();
    }

    public boolean isLessThan(Money other) {
        if (currency != other.getCurrency()) {
            throw new IllegalArgumentException("Cannot compare different currencies");
        }
        return this.amount < other.getAmount();
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
        return String.format("%s %.2f", currency, amount);
    }
}