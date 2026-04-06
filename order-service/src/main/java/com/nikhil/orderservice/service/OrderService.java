package com.nikhil.orderservice.service;

import com.nikhil.common.event.OrderEvent;
import com.nikhil.orderservice.dto.OrderHistoryDto;
import com.nikhil.orderservice.dto.OrderRequestDto;
import com.nikhil.orderservice.dto.OrderType;
import com.nikhil.orderservice.entity.Order;
import com.nikhil.orderservice.finnhub.FinnhubClient;
import com.nikhil.orderservice.repository.OrderRepository;
import com.nikhil.orderservice.repository.UserCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FinnhubClient finnhubClient;

    @Autowired
    private KafkaTemplate<String,OrderEvent> kafkaTemplate;

    public void placeOrder(Long userId, OrderRequestDto orderRequest) throws Exception {
        var user = userCacheRepository.findById(userId)
                .orElseThrow(() -> new Exception("User "+ userId + "not found"));

        var currentPrice= BigDecimal.valueOf(finnhubClient.getStockPrices(orderRequest.getSymbol()).getC());

        if (orderRequest.getType() == OrderType.BUY){
            var totalCost = currentPrice.multiply(BigDecimal.valueOf(orderRequest.getQuantity()));
            if(user.getBalance().compareTo(totalCost) < 0 ){
                throw new IllegalStateException(
                        "Insufficient balance. Required: " + totalCost + ", Available: " + user.getBalance());
            }
        }

        Order order = Order.builder()
                .userCache(user)
                .symbol(orderRequest.getSymbol().toUpperCase())
                .type(orderRequest.getType())
                .quantity(orderRequest.getQuantity())
                .priceAtOrder(currentPrice)
                .status(Order.OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(order.getId())
                .userId(userId)
                .symbol(orderRequest.getSymbol().toUpperCase())
                .type(OrderEvent.OrderType.valueOf(String.valueOf(orderRequest.getType())))
                .quantity(orderRequest.getQuantity())
                .build();

        kafkaTemplate.send("order-event", orderEvent);
        log.info("Order {} placed and published to Kafka", order.getId());
    }

    public List<OrderHistoryDto> getOrderHistory(Long userId) {
        return orderRepository.findByUserCacheIdOrderByCreatedAtDesc(userId).stream()
                .map(order -> OrderHistoryDto.builder()
                        .id(order.getId())
                        .userId(order.getUserCache().getId())
                        .symbol(order.getSymbol())
                        .type(order.getType())
                        .quantity(order.getQuantity())
                        .priceAtOrder(order.getPriceAtOrder())
                        .status(order.getStatus())
                        .createdAt(order.getCreatedAt())
                        .executedAt(order.getExecutedAt())
                        .build())
                .toList();
    }
}
