package ua.university.repo;

import ua.university.domain.Order;
import java.util.Optional;

public interface OrderRepo {
    void save(Order order);
    Optional<Order> findById(String id);
}