package com.E_Commerce.Ecom.services.admin.payment;

import com.E_Commerce.Ecom.dto.OrderDto;
import com.E_Commerce.Ecom.dto.PaymentDto;
import com.E_Commerce.Ecom.dto.PaymentRefundDto;
import com.E_Commerce.Ecom.entity.Order;
import com.E_Commerce.Ecom.entity.Payment;
import com.E_Commerce.Ecom.repository.OrderRepository;
import com.E_Commerce.Ecom.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("paymentadmin")
@RequiredArgsConstructor
public class PaymentServiceIplm implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    @Override
    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(Payment::getDto).collect(Collectors.toList());
    }

    @Override
    public PaymentDto refund(PaymentRefundDto paymentRefundDto) {
        Optional<Payment> payment = paymentRepository.findById(paymentRefundDto.getId());
        if (payment.isPresent()) {
            Payment payment1 = payment.get();
            payment1.setAmountPaid(paymentRefundDto.getAmountPaid());
            payment1.setAmountRefunded(paymentRefundDto.getAmountRefunded());
            payment1.setRefundReason(paymentRefundDto.getRefundReason());
            payment1.setRefundDate(LocalDateTime.now());
            paymentRepository.save(payment1);

            return payment1.getDto();

        }
        return null;
    }


}
