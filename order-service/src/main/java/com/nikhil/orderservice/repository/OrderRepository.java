package com.nikhil.orderservice.repository;

import com.nikhil.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserCacheIdOrderByCreatedAtDesc(Long userId);
}
