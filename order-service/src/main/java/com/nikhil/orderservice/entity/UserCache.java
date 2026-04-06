package com.nikhil.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "user_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache {
    @Id
    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;
}
