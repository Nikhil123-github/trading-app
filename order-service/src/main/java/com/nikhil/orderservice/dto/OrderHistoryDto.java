package com.nikhil.orderservice.dto;

import com.nikhil.orderservice.entity.Order;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class OrderHistoryDto {
    Long id;
    Long userId;
    String symbol;
    OrderType type;
    Integer quantity;
    BigDecimal priceAtOrder;
    Order.OrderStatus status;
    Instant createdAt;
    Instant executedAt;
}
