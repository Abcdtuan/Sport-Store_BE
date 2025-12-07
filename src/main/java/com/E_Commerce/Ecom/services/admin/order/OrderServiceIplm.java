package com.E_Commerce.Ecom.services.admin.order;

import com.E_Commerce.Ecom.dto.AnalyticResponse;
import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.dto.ProductStatisticDto;
import com.E_Commerce.Ecom.entity.CartItems;
import com.E_Commerce.Ecom.entity.Order;
import com.E_Commerce.Ecom.entity.Product;
import com.E_Commerce.Ecom.enums.OrderStatus;
import com.E_Commerce.Ecom.repository.OrderRepository;
import com.E_Commerce.Ecom.repository.ProductRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service("adminOrderService")
@RequiredArgsConstructor

public class OrderServiceIplm implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    @Override
    public List<OrderDto> getAllOrders(){
        List<Order> orderList = orderRepository.findAllByOrderStatusIn(List.of(OrderStatus.PLACED, OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CANCELLED));

        return orderList.stream().map(Order::getOrderDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto changeOrderStatus(Long id, String status) {
         Optional<Order> optionalOrder = orderRepository.findById(id);
         if(optionalOrder.isPresent()) {
             Order order = optionalOrder.get();
             if(Objects.equals(status, "Shipped")) {
                 order.setOrderStatus(OrderStatus.SHIPPED);
             } else if (Objects.equals(status, "Delivered")) {
                 order.setOrderStatus(OrderStatus.DELIVERED);
             } else if (Objects.equals(status, "Cancelled")) {
                 order.setOrderStatus(OrderStatus.CANCELLED);
                 for (CartItems item : order.getCartItems()) {
                     Product product = item.getProduct();
                     long newStock = product.getStockQuantity() + item.getQuantity();
                     product.setStockQuantity(newStock);
                     productRepository.save(product);
                 }
             }
             return orderRepository.save(order).getOrderDto();
         }
         return null;
    }

    public AnalyticResponse calculateAnalytic() {

        Calendar current = Calendar.getInstance();
        Calendar previous = (Calendar) current.clone();
        previous.add(Calendar.MONTH, -1);


        Long currentMonthOrders = getTotalOrdersForMonth(current.get(Calendar.MONTH) + 1, current.get(Calendar.YEAR));
        Long previousMonthOrders = getTotalOrdersForMonth(previous.get(Calendar.MONTH) + 1, previous.get(Calendar.YEAR));


        Long currentMonthEarnings = getTotalEarningsForMonth(current.get(Calendar.MONTH) + 1, current.get(Calendar.YEAR));
        Long previousMonthEarnings = getTotalEarningsForMonth(previous.get(Calendar.MONTH) + 1, previous.get(Calendar.YEAR));


        Long placed = orderRepository.countByOrderStatus(OrderStatus.PLACED);
        Long shipped = orderRepository.countByOrderStatus(OrderStatus.SHIPPED);
        Long delivered = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
        Long cancelled = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);



        List<ProductStatisticDto> currentMonthProducts = getProductStatisticsForMonth(current.get(Calendar.MONTH) + 1, current.get(Calendar.YEAR));
        List<ProductStatisticDto> previousMonthProducts = getProductStatisticsForMonth(previous.get(Calendar.MONTH) + 1, previous.get(Calendar.YEAR));

        return new AnalyticResponse(
                placed, shipped, delivered,  cancelled,
                currentMonthOrders, previousMonthOrders,
                currentMonthEarnings, previousMonthEarnings,
                currentMonthProducts, previousMonthProducts
        );
    }

    public Long getTotalOrdersForMonth(int month, int year) {
        Date[] range = getMonthDateRange(month, year);
        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(range[0], range[1], OrderStatus.DELIVERED);
        return (long) orders.size();
    }


    public Long getTotalEarningsForMonth(int month, int year) {
        Date[] range = getMonthDateRange(month, year);
        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(range[0], range[1], OrderStatus.DELIVERED);

        long sum = 0L;
        for (Order order : orders) {
            sum += order.getAmount();
        }
        return sum;
    }


    public List<ProductStatisticDto> getProductStatisticsForMonth(int month, int year) {
        Date[] range = getMonthDateRange(month, year);
        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(range[0], range[1], OrderStatus.DELIVERED);

        Map<Long, ProductStatisticDto> productMap = new HashMap<>();

        for (Order order : orders) {
            for (CartItems item : order.getCartItems()) {
                Long productId = item.getProduct().getId();
                ProductStatisticDto stat = productMap.getOrDefault(productId,
                        new ProductStatisticDto(
                                productId,
                                item.getProduct().getName(),
                                item.getProduct().getDto().getThumbnail(),
                                item.getProduct().getPrice(),
                                0L
                        ));
                stat.setQuantitySold(stat.getQuantitySold() + item.getQuantity());
                productMap.put(productId, stat);
            }
        }

        return new ArrayList<>(productMap.values());
    }


    private Date[] getMonthDateRange(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);
        Date start = calendar.getTime();

        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date end = calendar.getTime();

        return new Date[]{start, end};
    }

    @Override
    public List<OrderDto> getAllOrdersByName(String name){
        List<Order> orders = orderRepository.findAllByNameContaining(name);
        return orders.stream().map(Order::getOrderDto).collect(Collectors.toList());
    }






}

