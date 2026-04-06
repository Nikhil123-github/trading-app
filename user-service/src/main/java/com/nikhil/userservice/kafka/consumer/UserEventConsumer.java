package com.nikhil.userservice.kafka.consumer;

import com.nikhil.common.event.UserEvent;
import com.nikhil.common.event.dto.HoldingEvent;
import com.nikhil.userservice.entity.Holding;
import com.nikhil.userservice.entity.User;
import com.nikhil.userservice.repository.HoldingRepository;
import com.nikhil.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserEventConsumer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @KafkaListener(topics = "order-execute", groupId = "user-group")
    public void consume(UserEvent userEvent) {
        User user = userRepository.findById(userEvent.getId())
                .orElseThrow(() -> new IllegalStateException("User not found: " + userEvent.getId()));

        user.setName(userEvent.getName());
        user.setEmail(userEvent.getEmail());
        user.setBalance(userEvent.getBalance());
        userRepository.save(user);

        holdingRepository.deleteByUserId(user.getId());
        List<HoldingEvent> holdings = userEvent.getHoldings() == null
                ? Collections.<HoldingEvent>emptyList()
                : userEvent.getHoldings();
        for (HoldingEvent holdingEvent : holdings) {
            holdingRepository.save(Holding.builder()
                    .user(user)
                    .symbol(holdingEvent.getSymbol())
                    .quantity(holdingEvent.getQuantity())
                    .averageCost(holdingEvent.getAverageCost())
                    .build());
        }
    }
}
