package com.example.oms.order.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private final List<OrderItem> items = new ArrayList<>();
    private final Instant createdAt;
    private OrderStatus status;
    private final BigDecimal totalAmount;

    public Order(UUID id, UUID customerId, List<OrderItem> orderItems, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Order id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer id must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created time must not be null");

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        for (OrderItem orderItem : orderItems) {
            this.items.add(Objects.requireNonNull(orderItem, "Order item must not be null"));
        }
        this.status = OrderStatus.CREATED;

        BigDecimal amount = BigDecimal.ZERO;
        for (OrderItem item : this.items) {
            amount = amount.add(item.price().multiply(BigDecimal.valueOf(item.quantity())));
        }
        this.totalAmount = amount;
    }

    public UUID id() { return id; }
    public UUID customerId() { return customerId; }
    public List<OrderItem> items() { return List.copyOf(items); }
    public Instant createdAt() { return createdAt; }
    public OrderStatus status() { return status; }
    public BigDecimal totalAmount() { return totalAmount; }
}
