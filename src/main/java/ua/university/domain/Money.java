package ua.university.domain;

public class Money {
    private final double value;

    public Money(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public Money add(Money other) {
        return new Money(this.value + other.value);
    }

    public Money multiply(double factor) {
        return new Money(this.value * factor);
    }

    public boolean greater(Money other) {
        return this.value > other.value;
    }

    public boolean less(Money other) {
        return this.value < other.value;
    }

    @Override
    public String toString() {
        return "Money{value=" + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return Double.compare(money.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
}

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
