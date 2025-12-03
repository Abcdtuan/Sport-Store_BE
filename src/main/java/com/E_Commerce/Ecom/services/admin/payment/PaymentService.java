package com.E_Commerce.Ecom.services.admin.payment;

import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.PaymentDto;
import com.E_Commerce.Ecom.dto.PaymentRefundDto;

import java.util.List;

public interface PaymentService {

    List<PaymentDto> getAllPayments();

    PaymentDto refund(PaymentRefundDto paymentRefundDto);


}
