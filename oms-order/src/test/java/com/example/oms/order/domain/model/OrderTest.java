package com.example.oms.order.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void shouldRejectEmptyItems() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(UUID.randomUUID(), UUID.randomUUID(), List.of(), Instant.now()));
    }

    @Test
    void shouldCalculateTotalAmount() {
        Order order = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new OrderItem("SKU-1", 2, new BigDecimal("10.00"))),
                Instant.now());

        assertEquals(new BigDecimal("20.00"), order.totalAmount());
    }

    @Test
    void shouldRejectNullItem() {
        assertThrows(NullPointerException.class,
                () -> new Order(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of((OrderItem) null),
                        Instant.now()));
    }

    @Test
    void shouldRejectInvalidOrderItem() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(" ", 1, new BigDecimal("10.00")));
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("SKU-1", 0, new BigDecimal("10.00")));
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("SKU-1", 1, BigDecimal.ZERO));
    }
}
