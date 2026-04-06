package com.nikhil.userservice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class UserResponseDto {
    Long id;
    String name;
    String email;
    BigDecimal balance;
}
