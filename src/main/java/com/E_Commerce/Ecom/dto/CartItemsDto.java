package com.E_Commerce.Ecom.dto;

import com.E_Commerce.Ecom.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class CartItemsDto {

    private Long id;

    private Long price;

    private Long quantity;

    private String productName;

    private Long orderId;

    private Long productId;

    private Long userId;

    private byte[] returnedImg;

    private Long remainingStock;



}
