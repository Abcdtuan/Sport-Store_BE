package com.E_Commerce.Ecom.services.customer.customerproduct;


import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.entity.*;
import com.E_Commerce.Ecom.enums.OrderStatus;
import com.E_Commerce.Ecom.repository.CategoryRepository;
import com.E_Commerce.Ecom.repository.OrderRepository;
import com.E_Commerce.Ecom.repository.ProductRepository;
import com.E_Commerce.Ecom.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerProductIplm implements CustomerProduct {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final ReviewRepository reviewRepository;

    @Override
    public Page<ProductDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(Product::getDto);
    }

    @Override
    public List<ProductDto> getAllProductsByName(String name){
        List<Product> products = productRepository.findAllByNameContaining(name);
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long id){
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return null;
        }

        Product product = productOptional.get();
        ProductDto dto = product.getDto();

        List<Order> deliveredOrders = orderRepository.findByOrderStatus(OrderStatus.DELIVERED);
        long totalSold = 0L;

        for (Order order : deliveredOrders) {
            for (CartItems item : order.getCartItems()) {
                if (item.getProduct().getId().equals(id)) {
                    totalSold += item.getQuantity();
                }
            }
        }
        dto.setTotalSold(totalSold);

        List<OrderStatus> subtractStatuses = List.of(
                OrderStatus.PLACED,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED
        );

        List<Order> activeOrders = orderRepository.findAllByOrderStatusIn(subtractStatuses);

        long totalReserved = activeOrders.stream()
                .flatMap(order -> order.getCartItems().stream())
                .filter(item -> item.getProduct().getId().equals(id))
                .mapToLong(CartItems::getQuantity)
                .sum();

        long remainingStock = product.getStockQuantity() - totalReserved;
        if (remainingStock < 0) remainingStock = 0;

        dto.setRemainingStock(remainingStock);


        List<Review> reviews = reviewRepository.findByProductId(id);
        double averageRating = reviews.stream()
                .mapToLong(Review::getRating)
                .average()
                .orElse(0.0);
        dto.setAverageRating(averageRating);

        return dto;
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId){
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductByCategoryIdAndBrandId(Long categoryId, Long brandId){
        List<Product> products = productRepository.findByCategoryIdAndBrandId(categoryId, brandId);
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }
}
