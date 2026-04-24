package com.example.oms.order.domain.model;

import java.math.BigDecimal;

public record OrderItem(String sku, int quantity, BigDecimal price) {}
