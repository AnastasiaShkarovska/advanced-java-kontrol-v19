package ua.university.domain;

import ua.university.exeption.OrderStateException;
import java.util.Arrays;
public class Order {
    private final String id;
    private final String customerEmail;
    private final String deliveryAddress;
    private final OrderItem[] items;
    private OrderStatus status;
    private Money total;

    public Order(String id, String customerEmail, String deliveryAddress, OrderItem[] items) {
        this(id, customerEmail, deliveryAddress, items, OrderStatus.NEW);
    }

    public Order(String id, String email, String deliveryAddress, OrderItem[] items, OrderStatus status) {
        this.id = id;
        this.customerEmail = email;
        this.deliveryAddress = deliveryAddress;
        this.items = Arrays.copyOf(items, items.length);
        this.status = status;
        this.total = new Money(0);
    }

    public String getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public OrderItem[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Money getTotal() {
        return total;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public int getTotalUnits() {
        int sum = 0;
        for (OrderItem item : items) {
            sum += item.getQuantity();
        }
        return sum;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new OrderStateException("Cannot cancel paid order");
        }
        status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Order{id='" + id + "', status=" + status + ", total=" + total + "}";
    }

}