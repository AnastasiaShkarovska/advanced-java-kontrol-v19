package ua.university.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {
    private final BigDecimal value;

    public Money(double value) {
        this.value = BigDecimal.valueOf(value);
    }
    public BigDecimal getValue() {
        return value;
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value).doubleValue());
    }

    public Money multiply(double factor) {
        return new Money(this.value.multiply(BigDecimal.valueOf(factor)).doubleValue());
    }

    public boolean greater(Money other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean less(Money other) {
        return this.value.compareTo(other.value) < 0;
    }

    @Override
    public String toString() {
        return "Money{value=" + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return Objects.equals(value, money.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}