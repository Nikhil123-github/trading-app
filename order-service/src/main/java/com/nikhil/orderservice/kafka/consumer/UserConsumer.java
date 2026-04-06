package com.nikhil.orderservice.kafka.consumer;


import com.nikhil.common.event.UserEvent;
import com.nikhil.common.event.dto.HoldingEvent;
import com.nikhil.orderservice.entity.HoldingCache;
import com.nikhil.orderservice.entity.UserCache;
import com.nikhil.orderservice.repository.HoldingCacheRepository;
import com.nikhil.orderservice.repository.UserCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

    @Autowired
    private UserCacheRepository userCacheRepository;

    @Autowired
    private HoldingCacheRepository holdingCacheRepository;

    @KafkaListener(topics = "user-topic", groupId = "order-group")
    public void consume(UserEvent userEvent) {
        var user = UserCache.builder()
                        .id(userEvent.getId())
                        .name(userEvent.getName())
                        .email(userEvent.getEmail())
                        .balance(userEvent.getBalance())
                        .build();
        userCacheRepository.save(user);
        holdingCacheRepository.deleteByUserId(userEvent.getId());
        List<HoldingEvent> holdings = userEvent.getHoldings() == null ? Collections.<HoldingEvent>emptyList() : userEvent.getHoldings();
        for (HoldingEvent h : holdings) {
            HoldingCache cache = new HoldingCache();
            cache.setUserId(userEvent.getId());
            cache.setSymbol(h.getSymbol());
            cache.setQuantity(h.getQuantity());
            cache.setAverageCost(h.getAverageCost());
            holdingCacheRepository.save(cache);
        }
        log.info("Received message : {}", userEvent.getId());
    }
}
