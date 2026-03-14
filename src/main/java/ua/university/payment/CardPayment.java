package ua.university.payment;

import ua.university.domain.Money;
import ua.university.exeption.PaymentException;

public class CardPayment implements PaymentMethod {
    @Override
    public void pay(Money amount) {
        if (amount.greater(new Money(20000))) {
            throw new PaymentException("Card limit exceeded");
        }
    }
}