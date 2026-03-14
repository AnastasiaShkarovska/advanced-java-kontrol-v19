package ua.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.university.domain.Money;
import ua.university.domain.Order;
import ua.university.domain.OrderItem;
import ua.university.domain.OrderStatus;
import ua.university.exeption.AppException;
import ua.university.exeption.InvalidAddressException;
import ua.university.exeption.PaymentException;
import ua.university.exeption.ValidationException;
import ua.university.payment.BankTransferPayment;
import ua.university.payment.CardPayment;
import ua.university.payment.PayPalPayment;
import ua.university.repo.InMemoryOrderRepo;
import ua.university.repo.OrderRepo;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class StandardOrderProcessorTest {

    private OrderRepo repository;
    private NotificationService notificationService;
    private StandardOrderProcessor processor;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepo();
        notificationService = order -> { };
        processor = new StandardOrderProcessor(repository, notificationService);
    }

    private Order createOrder(String id, int quantity, double price, String address) {
        OrderItem[] items = {
                new OrderItem("Item", quantity, new Money(price))
        };
        return new Order(id, "test@gmail.com", address, items);
    }

    @Test
    void shouldProcessOrderWithCardPayment() {
        Order order = createOrder("1", 2, 500, "Kyiv street 10");

        Order result = processor.process(order, new CardPayment());

        assertEquals(OrderStatus.COMPLETED, result.getStatus());
        assertEquals(new Money(1000), result.getTotal());
    }

    @Test
    void shouldApplyDiscountWhenUnitsAreTenOrMore() {
        Order order = createOrder("2", 10, 100, "Kyiv street 10");

        Order result = processor.process(order, new CardPayment());

        assertEquals(new Money(880), result.getTotal());
    }

    @Test
    void shouldApplyBankTransferCommission() {
        Order order = createOrder("3", 2, 100, "Kyiv street 10");

        Order result = processor.process(order, new BankTransferPayment());

        assertEquals(new Money(202), result.getTotal());
    }

    @Test
    void shouldFindOrderById() {
        Order order = createOrder("4", 2, 100, "Kyiv street 10");
        processor.process(order, new CardPayment());

        Optional<Order> found = repository.findById("4");

        assertTrue(found.isPresent());
        assertEquals("4", found.get().getId());
    }

    @Test
    void shouldReturnDefensiveCopyOfItems() {
        Order order = createOrder("5", 2, 100, "Kyiv street 10");

        OrderItem[] copy = order.getItems();
        copy[0] = new OrderItem("Changed", 1, new Money(1));

        assertEquals("Item", order.getItems()[0].getName());
    }

    @Test
    void shouldThrowValidationExceptionWhenUnitsGreaterThanThirty() {
        Order order = createOrder("6", 31, 100, "Kyiv street 10");

        assertThrows(ValidationException.class,
                () -> processor.process(order, new CardPayment()));
    }

    @Test
    void shouldThrowInvalidAddressExceptionWhenAddressIsBlank() {
        Order order = createOrder("7", 2, 100, "");

        assertThrows(InvalidAddressException.class,
                () -> processor.process(order, new CardPayment()));
    }

    @Test
    void shouldThrowPaymentExceptionWhenCardLimitExceeded() {
        Order order = createOrder("8", 1, 21000, "Kyiv street 10");

        assertThrows(PaymentException.class,
                () -> processor.process(order, new CardPayment()));
    }

    @Test
    void shouldThrowPaymentExceptionWhenPayPalAmountTooSmall() {
        Order order = createOrder("9", 1, 50, "Kyiv street 10");

        assertThrows(PaymentException.class,
                () -> processor.process(order, new PayPalPayment()));
    }

    @Test
    void shouldNotAllowCancelAfterPaid() {
        OrderItem[] items = {
                new OrderItem("Item", 2, new Money(500))
        };
        Order order = new Order("10", "test@gmail.com", "Kyiv street 10", items);
        order.setStatus(OrderStatus.PAID);

        assertThrows(ValidationException.class, order::cancel);
    }

    @Test
    void shouldWrapNotificationErrorIntoAppException() {
        NotificationService failingNotification = order -> {
            try {
                throw new NotificationException("Notification failed");
            } catch (NotificationException e) {
                throw new RuntimeException(e);
            }
        };

        StandardOrderProcessor localProcessor =
                new StandardOrderProcessor(repository, failingNotification);

        Order order = createOrder("11", 2, 100, "Kyiv street 10");

        AppException ex = assertThrows(AppException.class,
                () -> localProcessor.process(order, new CardPayment()));

        assertNotNull(ex.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "abc"})
    void shouldRejectInvalidAddresses(String address) {
        Order order = createOrder("12", 2, 100, address);

        assertThrows(InvalidAddressException.class,
                () -> processor.process(order, new CardPayment()));
    }
}