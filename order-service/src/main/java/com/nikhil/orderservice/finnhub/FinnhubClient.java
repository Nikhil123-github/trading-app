package com.nikhil.orderservice.finnhub;

import com.nikhil.orderservice.dto.PriceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class FinnhubClient {

    @Value("${finnhub.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public PriceDto getStockPrices(String symbol){
        String url = "https://finnhub.io/api/v1/quote?symbol="
                + symbol + "&token=" + apiKey;
        return restTemplate.getForObject(url, PriceDto.class);
    }
}
