package com.E_Commerce.Ecom.entity;

import com.E_Commerce.Ecom.dto.ReviewDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "review")
public class Review {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long rating;

    private String description;

    private LocalDate date;


    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReviewImages> reviewImages = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_Id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;



    public ReviewDto getDto() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(id);
        reviewDto.setRating(rating);
        reviewDto.setDescription(description);
        reviewDto.setUserId(user.getId());
        reviewDto.setProductId(product.getId());
        reviewDto.setUserName(user.getName());
        reviewDto.setDate(date);
        reviewDto.setOrderId(order.getId());
        List<byte[]> images = reviewImages.stream()
                .map(ReviewImages::getImage)
                .collect(Collectors.toList());
        reviewDto.setByteImages(images);

        return reviewDto;


    }



}
