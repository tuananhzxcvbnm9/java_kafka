package com.example.oms.order.application.usecase;

import com.example.oms.order.api.dto.PlaceOrderRequest;

import java.util.UUID;

public interface PlaceOrderUseCase {
    UUID execute(PlaceOrderRequest request);
}
