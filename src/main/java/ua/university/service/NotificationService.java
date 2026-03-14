package ua.university.service;

import ua.university.domain.Order;

public interface NotificationService {
    void notifyCustomer(Order order) throws Exception, NotificationException;
}