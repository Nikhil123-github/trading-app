package com.nikhil.orderservice.dto;

import com.nikhil.orderservice.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class OrderResponseDto {
    public Long orderId;
    public String symbol;
    public OrderType type;
    public Integer quantity;
    public BigDecimal priceAtOrder;
    public Order.OrderStatus status;
    public Instant createdAt;
}
