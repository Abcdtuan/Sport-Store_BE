package com.E_Commerce.Ecom.repository;

import com.E_Commerce.Ecom.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);

    List<Review> findByProductId(Long productId);
}
