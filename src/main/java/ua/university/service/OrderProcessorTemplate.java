package ua.university.service;

import ua.university.domain.Money;
import ua.university.domain.Order;
import ua.university.domain.OrderItem;
import ua.university.domain.OrderStatus;
import ua.university.exeption.InfrastructureException;
import ua.university.exeption.ValidationException;
import ua.university.payment.PaymentMethod;
import ua.university.repo.OrderRepo;

import java.util.logging.Logger;

public abstract class OrderProcessorTemplate {
    protected final OrderRepo repository;
    protected final NotificationService notificationService;
    protected final Logger logger = Logger.getLogger(OrderProcessorTemplate.class.getName());

    public OrderProcessorTemplate(OrderRepo repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public final Order process(Order order, PaymentMethod paymentMethod) {
        logger.info("Start process");

        validate(order);
        validateDeliveryAddress(order);

        Money total = calculate(order, paymentMethod);
        order.setTotal(total);

        paymentMethod.pay(total);
        order.setStatus(OrderStatus.PAID);

        complete(order);
        notifyClient(order);

        repository.save(order);
        logger.info("Order completed");
        return order;
    }

    protected void validate(Order order) {
        if (order.getTotalUnits() > 30) {
            throw new ValidationException("Total units must be <= 30");
        }
    }

    protected abstract void validateDeliveryAddress(Order order);

    protected Money calculate(Order order, PaymentMethod paymentMethod) {
        Money sum = new Money(0);
        for (OrderItem item : order.getItems()) {
            sum = sum.add(item.getTotal());
        }

        if (order.getTotalUnits() >= 10) {
            sum = sum.multiply(0.88);
        }

        return applyExtra(sum, paymentMethod);
    }

    protected abstract Money applyExtra(Money sum, PaymentMethod paymentMethod);

    protected void complete(Order order) {
        order.setStatus(OrderStatus.COMPLETED);
    }

    protected void notifyClient(Order order) {
        try {
            notificationService.notifyCustomer(order);
        } catch (Exception e) {
            throw new InfrastructureException("Notification failed", e);
        }
    }
}