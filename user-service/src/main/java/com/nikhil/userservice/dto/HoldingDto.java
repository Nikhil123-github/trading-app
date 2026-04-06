package com.nikhil.userservice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class HoldingDto {
    Long id;
    String symbol;
    Integer quantity;
    BigDecimal averageCost;
    BigDecimal currentPrice;
    BigDecimal investedValue;
    BigDecimal marketValue;
    BigDecimal pnl;
}
