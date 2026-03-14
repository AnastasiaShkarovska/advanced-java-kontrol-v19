package ua.university.payment;

import ua.university.domain.Money;

public interface PaymentMethod {
    void pay(Money amount);
}