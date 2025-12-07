package com.E_Commerce.Ecom.repository;

import com.E_Commerce.Ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByNameContaining(String title);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryIdAndBrandId(Long categoryId, Long brandId);

    List<Product> findByStockQuantityLessThanEqual(Long threshold);
}
