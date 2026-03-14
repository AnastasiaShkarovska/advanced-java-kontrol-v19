package ua.university.payment;
import ua.university.domain.Money;

public class BankTransferPayment implements PaymentMethod {
    @Override
    public void pay(Money amount) {
    }

    public Money addCommission(Money amount) {
        return amount.multiply(1.01);
    }
}