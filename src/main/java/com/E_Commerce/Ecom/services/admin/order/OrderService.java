package com.E_Commerce.Ecom.services.admin.order;

import com.E_Commerce.Ecom.dto.AnalyticResponse;
import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.ProductStatisticDto;
import com.E_Commerce.Ecom.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrderService {

    List<OrderDto> getAllOrders();

    OrderDto changeOrderStatus(Long id, String status);

    AnalyticResponse calculateAnalytic();

    List<ProductStatisticDto> getProductStatisticsForMonth(int month, int year);

    List<OrderDto> getAllOrdersByName(String name);


}
