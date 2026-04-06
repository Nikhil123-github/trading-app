package com.nikhil.userservice.repository;

import com.nikhil.userservice.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
