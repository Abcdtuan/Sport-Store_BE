package com.E_Commerce.Ecom.entity;

import com.E_Commerce.Ecom.dto.PaymentDto;
import com.E_Commerce.Ecom.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payment")
public class Payment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private LocalDateTime refundDate;

    private Long amount;

    private Long amountPaid;

    private Long amountRefunded;

    private String refundReason;



    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public PaymentDto getDto(){
        PaymentDto dto = new PaymentDto();
        dto.setId(id);
        dto.setRefundDate(refundDate);
        dto.setAmountPaid(amountPaid);
        dto.setAmountRefunded(amountRefunded);
        dto.setRefundReason(refundReason);
        dto.setAmount(order.getAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setOrderId(order.getId());
        dto.setName(order.getName());
        return dto;
    }



}
