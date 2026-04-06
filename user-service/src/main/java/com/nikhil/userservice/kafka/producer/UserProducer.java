package com.nikhil.userservice.kafka.producer;

import com.nikhil.common.event.UserEvent;
import com.nikhil.common.event.dto.HoldingEvent;
import com.nikhil.userservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserProducer {

    private static final Logger log = LoggerFactory.getLogger(UserProducer.class);
    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;


    public void sendUserEvent(User user) {
        List<HoldingEvent> holdings = getHoldingEvents(user);
        UserEvent event = UserEvent.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .balance(user.getBalance())
                        .holdings(holdings)
                        .build();
        log.info("Sending message to Kafka: {}", event.getId());
        kafkaTemplate.send("user-topic", event);
    }

    private static List<HoldingEvent> getHoldingEvents(User user) {
        if (user.getHoldings() == null) {
            return Collections.emptyList();
        }
        return user.getHoldings().stream().map(h -> HoldingEvent.builder()
                        .id(h.getId())
                        .userId(user.getId())
                        .symbol(h.getSymbol())
                        .quantity(h.getQuantity())
                        .averageCost(h.getAverageCost())
                        .build())
                .collect(Collectors.toList());
    }
}
