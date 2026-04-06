package com.nikhil.orderservice.dto;

import lombok.Data;

@Data
public class PriceDto {
    private double c; //current price
    private double h; //high of the day
    private double l; //low of the day
    private double o; //open price
}
