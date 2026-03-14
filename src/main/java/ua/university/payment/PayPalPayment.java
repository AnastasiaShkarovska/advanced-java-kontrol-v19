package ua.university.payment;
import ua.university.domain.Money;
import ua.university.exeption.PaymentException;

public class PayPalPayment implements PaymentMethod {
    @Override
    public void pay(Money amount) {
        if (amount.less(new Money(100))) {
            throw new PaymentException("PayPal requires at least 100");
        }
    }
}