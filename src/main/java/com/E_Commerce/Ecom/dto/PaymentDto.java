package com.E_Commerce.Ecom.dto;


import com.E_Commerce.Ecom.enums.PaymentMethod;
import lombok.Data;

import java.time.LocalDateTime;
@Data

public class PaymentDto {

    private Long id;

    private LocalDateTime refundDate;

    private Long amountPaid;

    Long amountRefunded;

    private String refundReason;

    private Long orderId;

    private String name;

    private Long amount;

    private PaymentMethod paymentMethod;


}
