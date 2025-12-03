package com.E_Commerce.Ecom.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class ReviewDto {

    private Long id;

    private Long rating;

    private List<MultipartFile> images;

    private List<byte[]> byteImages;

    private String description;

    private LocalDate date;

    private Long userId;

    private Long productId;

    private String userName;

    private Long orderId;
}
