package com.nikhil.orderservice.entity;

import com.nikhil.orderservice.dto.OrderType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCache userCache;

    @Column(nullable = false)
    private String symbol;   // e.g. "AAPL"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type;  // BUY or SELL

    @Column(nullable = false)
    private Integer quantity;

    // Price at the time the order was placed (from Finnhub)
    @Column(precision = 19, scale = 4)
    private BigDecimal priceAtOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = Order.OrderStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant executedAt;

    public enum OrderStatus {PENDING, EXECUTED, FAILED}
}
