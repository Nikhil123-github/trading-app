package com.nikhil.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "holding_cache")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String symbol;
    private Integer quantity;
    private BigDecimal averageCost;
}
