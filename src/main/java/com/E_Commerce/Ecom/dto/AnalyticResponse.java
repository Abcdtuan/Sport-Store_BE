package com.E_Commerce.Ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalyticResponse {

    private Long placed;

    private Long shipped;

    private Long delivered;

    private Long cancelled;

    private Long currentMonthOrders;

    private Long previousMonthOrders;

    private Long currentMonthEarnings;

    private Long previousMonthEarnings;

    private List<ProductStatisticDto> currentMonthProducts;
    private List<ProductStatisticDto> previousMonthProducts;

}
