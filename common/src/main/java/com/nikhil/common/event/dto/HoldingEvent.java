package com.nikhil.common.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingEvent {
    private Long id;
    private Long userId;
    private String symbol;
    private Integer quantity;
    private BigDecimal averageCost;

}
