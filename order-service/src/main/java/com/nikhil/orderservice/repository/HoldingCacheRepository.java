package com.nikhil.orderservice.repository;

import com.nikhil.orderservice.entity.HoldingCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingCacheRepository extends JpaRepository<HoldingCache, Long> {
    Optional<HoldingCache> findByUserIdAndSymbol(Long userId, String symbol);
    List<HoldingCache> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
