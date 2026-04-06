package com.nikhil.common.event;

import com.nikhil.common.event.dto.HoldingEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;
    private List<HoldingEvent> holdings;
}
