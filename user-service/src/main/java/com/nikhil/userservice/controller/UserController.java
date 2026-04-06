package com.nikhil.userservice.controller;

import com.nikhil.userservice.dto.UserDto;
import com.nikhil.userservice.dto.HoldingDto;
import com.nikhil.userservice.dto.UserResponseDto;
import com.nikhil.userservice.orders.OrderPriceClient;
import com.nikhil.userservice.entity.User;
import com.nikhil.userservice.kafka.producer.UserProducer;
import com.nikhil.userservice.repository.HoldingRepository;
import com.nikhil.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProducer userProducer;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private OrderPriceClient orderPriceClient;

    @GetMapping("/test")
    public String test() {
        return "User Service is working!";
    }

    @PostMapping("/add")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserDto userReq){
        var user = User.builder()
                        .name(userReq.getName())
                        .email(userReq.getEmail())
                .build();
        var savedUser = userRepository.save(user);
        userProducer.sendUserEvent(savedUser);
        return ResponseEntity.ok(toDto(savedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id){
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/holdings")
    public ResponseEntity<List<HoldingDto>> getHoldings(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<HoldingDto> holdings = holdingRepository.findByUserId(id).stream()
                .map(holding -> {
                    BigDecimal currentPrice = BigDecimal.valueOf(
                            orderPriceClient.getStockPrices(holding.getSymbol()).getC()
                    );
                    BigDecimal investedValue = holding.getAverageCost()
                            .multiply(BigDecimal.valueOf(holding.getQuantity()));
                    BigDecimal marketValue = currentPrice
                            .multiply(BigDecimal.valueOf(holding.getQuantity()));
                    return HoldingDto.builder()
                            .id(holding.getId())
                            .symbol(holding.getSymbol())
                            .quantity(holding.getQuantity())
                            .averageCost(holding.getAverageCost())
                            .currentPrice(currentPrice)
                            .investedValue(investedValue)
                            .marketValue(marketValue)
                            .pnl(marketValue.subtract(investedValue))
                            .build();
                })
                .toList();
        return ResponseEntity.ok(holdings);
    }

    private static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .balance(user.getBalance())
                .build();
    }

}
