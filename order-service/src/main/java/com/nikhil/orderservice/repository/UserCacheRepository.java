package com.nikhil.orderservice.repository;

import com.nikhil.orderservice.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends JpaRepository<UserCache,Long> {
}
