package com.example.oms.order.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderResponse(UUID orderId, String status, BigDecimal totalAmount) {}
