package com.nikhil.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private String symbol;
    private OrderType type;
    private Integer quantity;

    public enum OrderType { BUY, SELL }
}


