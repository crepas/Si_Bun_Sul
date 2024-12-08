package com.sys.OrderSystem.Repository;

import com.sys.OrderSystem.Entity.Order;
import com.sys.OrderSystem.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrder(Order order);
}