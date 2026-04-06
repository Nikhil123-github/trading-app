package com.nikhil.orderservice.controller;

import com.nikhil.orderservice.dto.OrderHistoryDto;
import com.nikhil.orderservice.dto.OrderRequestDto;
import com.nikhil.orderservice.dto.PriceDto;
import com.nikhil.orderservice.finnhub.FinnhubClient;
import com.nikhil.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private FinnhubClient finnhubClient;

    @Autowired
    private OrderService orderService;

    @GetMapping("/test")
    public String test() {
        return "Order Service is working!";
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<PriceDto> getPrice(@PathVariable String symbol){
        return ResponseEntity.ok(finnhubClient.getStockPrices(symbol));
    }

    @PostMapping("/{userId}")
    public void placeOrder(@RequestBody OrderRequestDto orderRequest, @PathVariable Long userId) throws Exception {
        orderService.placeOrder(userId,orderRequest);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<OrderHistoryDto>> getOrderHistory(@PathVariable Long userId){
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }
}
