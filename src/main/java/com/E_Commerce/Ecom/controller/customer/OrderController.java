package com.E_Commerce.Ecom.controller.customer;

import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.entity.Order;
import com.E_Commerce.Ecom.services.customer.orders.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/placedOrders/{userId}")
    public ResponseEntity<List<OrderDto>> getAllOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getAllOrders(userId));
    }

    @PutMapping("/changeOrderStatus/{id}/{status}")
    public ResponseEntity<OrderDto> changeOrderStatus(@PathVariable Long id, @PathVariable String status) {
        OrderDto orderDto = orderService.changeOrderStatus(id, status);
        if(orderDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderDto);
    }



}
