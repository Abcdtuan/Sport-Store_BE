package com.E_Commerce.Ecom.repository;

import com.E_Commerce.Ecom.entity.Order;
import com.E_Commerce.Ecom.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {


}
