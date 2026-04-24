package com.example.oms.order.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PlaceOrderItemRequest(
        @NotBlank String sku,
        @Positive int quantity,
        @DecimalMin("0.01") BigDecimal price
) {}
