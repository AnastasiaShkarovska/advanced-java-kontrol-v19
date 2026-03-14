package ua.university.repo;

import ua.university.domain.Order;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepo implements OrderRepo {
    private final Map<String, Order> data = new HashMap<>();

    @Override
    public void save(Order order) {
        data.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }
}