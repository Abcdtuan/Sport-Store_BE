package com.E_Commerce.Ecom.services.customer.orders;

import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.entity.Order;
import com.E_Commerce.Ecom.entity.Product;
import com.E_Commerce.Ecom.enums.OrderStatus;
import com.E_Commerce.Ecom.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("customerOrderService")
@RequiredArgsConstructor
public class OrderServiceIplm implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<OrderDto> getAllOrders(Long userId) {
        List<OrderStatus> statuses = List.of(OrderStatus.PLACED, OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CANCELLED);
        List<Order> activeOrder = orderRepository.findByUserIdAndOrderStatusIn(userId, statuses);
        return activeOrder.stream().map(Order::getOrderDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto searchOrderByTrackingId(UUID trackingId) {
        Optional<Order> optionalOrder = orderRepository.findOrderByTrackingId(trackingId);
        return optionalOrder.map(Order::getOrderDto).orElse(null);
    }


    @Override
    public OrderDto changeOrderStatus(Long id, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if(Objects.equals(status, "Cancelled")) {
                if (order.getOrderStatus() == OrderStatus.PLACED) {
                    order.setOrderStatus(OrderStatus.CANCELLED);
                }
            }
            return orderRepository.save(order).getOrderDto();
        }
        return null;
    }


}
