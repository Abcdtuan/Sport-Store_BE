package com.E_Commerce.Ecom.controller.admin;

import com.E_Commerce.Ecom.dto.AnalyticResponse;
import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.ProductStatisticDto;
import com.E_Commerce.Ecom.entity.Order;
import com.E_Commerce.Ecom.services.admin.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/placedOrders")
    public ResponseEntity<List<OrderDto>> getOrderList(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/changeOrderStatus/{id}/{status}")
    public ResponseEntity<OrderDto> changeOrderStatus(@PathVariable Long id, @PathVariable String status) {
        OrderDto orderDto = orderService.changeOrderStatus(id, status);
        if(orderDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticResponse> getAnalyticsSummary(){
        return ResponseEntity.ok(orderService.calculateAnalytic());
    }


    @GetMapping("/analytics/products")
    public ResponseEntity<List<ProductStatisticDto>> getProductStatistics(

            @RequestParam int month,
            @RequestParam int year
    ){
        return ResponseEntity.ok(orderService.getProductStatisticsForMonth(month, year));
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<OrderDto>> getAllOrdersByName(@PathVariable String name) {
        List<OrderDto> orderDto = orderService.getAllOrdersByName(name);
        return  ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }






}
