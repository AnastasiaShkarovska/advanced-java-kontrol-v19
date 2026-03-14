package ua.university.service;

import ua.university.domain.Money;
import ua.university.domain.Order;
import ua.university.exeption.InvalidAddressException;
import ua.university.payment.BankTransferPayment;
import ua.university.payment.PaymentMethod;
import ua.university.repo.OrderRepo;

public class StandardOrderProcessor extends OrderProcessorTemplate {

    public StandardOrderProcessor(OrderRepo repository, NotificationService notificationService) {
        super(repository, notificationService);
    }

    @Override
    protected void validateDeliveryAddress(Order order) {
        String address = order.getDeliveryAddress();
        if (address == null || address.isBlank() || address.length() < 5) {
            throw new InvalidAddressException("Invalid delivery address");
        }
    }

    @Override
    protected Money applyExtra(Money sum, PaymentMethod paymentMethod) {
        if (paymentMethod instanceof BankTransferPayment bankTransferPayment) {
            return bankTransferPayment.addCommission(sum);
        }
        return sum;
    }
}