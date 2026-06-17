package com.study.tour.repository;

import com.study.tour.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOutTradeNo(String outTradeNo);
    List<Payment> findByOutTradeNoOrderByPayTimeDesc(String outTradeNo);
    boolean existsByOutTradeNoAndStatus(String outTradeNo, String status);
}
