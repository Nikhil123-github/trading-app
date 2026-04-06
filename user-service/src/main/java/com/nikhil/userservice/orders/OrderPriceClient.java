package com.nikhil.userservice.orders;

import com.nikhil.userservice.dto.PriceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderPriceClient {

    @Value("${order-service.base-url}")
    private String orderServiceBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public PriceDto getStockPrices(String symbol) {
        return restTemplate.getForObject(
                orderServiceBaseUrl + "/order/price/" + symbol,
                PriceDto.class
        );
    }
}
