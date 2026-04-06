package com.nikhil.orderservice.kafka.consumer;

import com.nikhil.common.event.OrderEvent;
import com.nikhil.common.event.UserEvent;
import com.nikhil.common.event.dto.HoldingEvent;
import com.nikhil.orderservice.entity.HoldingCache;
import com.nikhil.orderservice.entity.Order;
import com.nikhil.orderservice.entity.UserCache;
import com.nikhil.orderservice.repository.HoldingCacheRepository;
import com.nikhil.orderservice.repository.OrderRepository;
import com.nikhil.orderservice.repository.UserCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class OrderExecutor {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private HoldingCacheRepository holdingCacheRepository;

    @Autowired
    private KafkaTemplate<String,OrderEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, UserEvent> userEventKafkaTemplate;

    @KafkaListener(topics = "order-event", groupId = "order-group")
    public void executeOrder(OrderEvent orderEvent){
        log.info("Executing order {}", orderEvent.getOrderId());
        Order order = orderRepository.findById(orderEvent.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderEvent.getOrderId()));


        UserCache userCache = userCacheRepository.findById(orderEvent.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found: " + orderEvent.getUserId()));
        try {
            var totalCost = order.getPriceAtOrder().multiply(BigDecimal.valueOf(order.getQuantity()));
            if (orderEvent.getType() == OrderEvent.OrderType.BUY) {
                executeBuy(userCache, order, totalCost);
            } else {
                executeSell(userCache, order, totalCost);
            }
            order.setStatus(Order.OrderStatus.EXECUTED);
            order.setExecutedAt(Instant.now());
            orderRepository.save(order);
            userCacheRepository.save(userCache);
            userEventKafkaTemplate.send("order-execute", buildUserEvent(userCache));
            log.info("Order {} executed and user snapshot published", order.getId());
        } catch (Exception e) {
            log.error("Failed to execute order {}: {}", order.getId(), e.getMessage());
            order.setStatus(Order.OrderStatus.FAILED);
            orderRepository.save(order);
        }
    }

    private void executeSell(UserCache userCache, Order order, BigDecimal totalCost) {
        HoldingCache holdingCache = holdingCacheRepository.findByUserIdAndSymbol(userCache.getId(), order.getSymbol())
                .orElseThrow(() -> new IllegalStateException(
                        "No holding found for " + order.getSymbol()));
        if (holdingCache.getQuantity() < order.getQuantity()) {
            throw new IllegalStateException("Not enough shares to sell");
        }
        var revisedQuantity = holdingCache.getQuantity() - order.getQuantity();
        if (revisedQuantity != 0) {
            holdingCache.setQuantity(revisedQuantity);
            holdingCacheRepository.save(holdingCache);
        } else {
            holdingCacheRepository.delete(holdingCache);
        }
        userCache.setBalance(userCache.getBalance().add(totalCost));
    }

    private void executeBuy(UserCache userCache, Order order, BigDecimal totalCost) {
        if (userCache.getBalance().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient balance at execution time");
        }
        userCache.setBalance(userCache.getBalance().subtract(totalCost));
        holdingCacheRepository.findByUserIdAndSymbol(userCache.getId(), order.getSymbol())
                .ifPresentOrElse(holdingCache -> {
                    var currentValue = holdingCache.getAverageCost()
                            .multiply(BigDecimal.valueOf(holdingCache.getQuantity()));
                    var purchaseValue = order.getPriceAtOrder()
                            .multiply(BigDecimal.valueOf(order.getQuantity()));
                    var newQuantity = holdingCache.getQuantity() + order.getQuantity();
                    holdingCache.setQuantity(holdingCache.getQuantity() + order.getQuantity());
                    holdingCache.setAverageCost(
                            currentValue.add(purchaseValue).divide(BigDecimal.valueOf(newQuantity), 8, RoundingMode.HALF_UP)
                    );
                    holdingCacheRepository.save(holdingCache);
                },
                        () -> holdingCacheRepository.save(HoldingCache.builder()
                .userId(userCache.getId())
                .symbol(order.getSymbol())
                .quantity(order.getQuantity())
                .averageCost(order.getPriceAtOrder())
                .build())
                );
    }

    private UserEvent buildUserEvent(UserCache userCache) {
        List<HoldingEvent> holdings = holdingCacheRepository.findByUserId(userCache.getId()).stream()
                .map(holding -> HoldingEvent.builder()
                        .id(holding.getId())
                        .userId(holding.getUserId())
                        .symbol(holding.getSymbol())
                        .quantity(holding.getQuantity())
                        .averageCost(holding.getAverageCost())
                        .build())
                .toList();

        return UserEvent.builder()
                .id(userCache.getId())
                .name(userCache.getName())
                .email(userCache.getEmail())
                .balance(userCache.getBalance())
                .holdings(holdings)
                .build();
    }
}
