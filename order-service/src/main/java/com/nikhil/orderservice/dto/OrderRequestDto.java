package com.nikhil.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequestDto {
    private String symbol;
    private OrderType type;
    private Integer quantity;
}
