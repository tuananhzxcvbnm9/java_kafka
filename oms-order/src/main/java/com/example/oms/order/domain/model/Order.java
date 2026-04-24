package com.example.oms.order.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private final List<OrderItem> items = new ArrayList<>();
    private final Instant createdAt;
    private OrderStatus status;
    private final BigDecimal totalAmount;

    public Order(UUID id, UUID customerId, List<OrderItem> orderItems, Instant createdAt) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        this.id = id;
        this.customerId = customerId;
        this.items.addAll(orderItems);
        this.createdAt = createdAt;
        this.status = OrderStatus.CREATED;
        this.totalAmount = items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID id() { return id; }
    public UUID customerId() { return customerId; }
    public List<OrderItem> items() { return List.copyOf(items); }
    public Instant createdAt() { return createdAt; }
    public OrderStatus status() { return status; }
    public BigDecimal totalAmount() { return totalAmount; }
}
