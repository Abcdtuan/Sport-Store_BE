package com.E_Commerce.Ecom.services.customer.review;

import com.E_Commerce.Ecom.dto.OrderProductsResponseDto;
import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.dto.ReviewDto;
import com.E_Commerce.Ecom.entity.*;
import com.E_Commerce.Ecom.repository.OrderRepository;
import com.E_Commerce.Ecom.repository.ProductRepository;
import com.E_Commerce.Ecom.repository.ReviewRepository;
import com.E_Commerce.Ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceIplm implements ReviewService{

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    @Override
    public OrderProductsResponseDto getOrderProductsById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderProductsResponseDto orderProductsResponseDto = new OrderProductsResponseDto();
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            orderProductsResponseDto.setOrderAmount(optionalOrder.get().getAmount());
            List<ProductDto> productDtoList = new ArrayList<>();
            for (CartItems cartItems : optionalOrder.get().getCartItems()) {
                Product product = cartItems.getProduct();
                ProductDto productDto = new ProductDto();
                productDto.setId(cartItems.getProduct().getId());
                productDto.setName(cartItems.getProduct().getName());
                productDto.setPrice(cartItems.getProduct().getPrice());
                productDto.setQuantity(cartItems.getQuantity());
                productDto.setThumbnail(cartItems.getProduct().getDto().getThumbnail());

                boolean reviewed = reviewRepository.existsByUserIdAndProductIdAndOrderId(order.getUser().getId(), product.getId(), order.getId());
                productDto.setReviewed(reviewed);
                productDtoList.add(productDto);

            }
            orderProductsResponseDto.setProductDtoList(productDtoList);
        }
        return orderProductsResponseDto;
    }

    @Override
    public ReviewDto giveReview(ReviewDto reviewDto) throws Exception {
        Optional<User> optionalUser = userRepository.findById(reviewDto.getUserId());
        Optional<Product> optionalProduct = productRepository.findById(reviewDto.getProductId());
        Optional<Order> optionalOrder = orderRepository.findById(reviewDto.getOrderId());
        if (optionalUser.isPresent() && optionalProduct.isPresent()) {
            Review review = new Review();

            review.setRating(reviewDto.getRating());
            review.setDescription(reviewDto.getDescription());
            review.setDate(LocalDate.now());
            review.setUser(optionalUser.get());
            review.setProduct(optionalProduct.get());
            review.setOrder(optionalOrder.get());
            if (reviewDto.getImages() != null) {
                for (MultipartFile file : reviewDto.getImages()) {
                    ReviewImages reviewImage = new ReviewImages();
                    reviewImage.setImage(file.getBytes());
                    reviewImage.setReview(review);
                    review.getReviewImages().add(reviewImage);
                }
            }
            return reviewRepository.save(review).getDto();
        }
        return null;
    }

    @Override
    public List<ReviewDto> getAllReviewsByProductId(Long productId) {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isPresent()) {
                List<Review> reviewList = reviewRepository.findByProductId(productId);
                return reviewList.stream().map(Review::getDto).collect(Collectors.toList());
            }
            return new ArrayList<>();
    }


}
