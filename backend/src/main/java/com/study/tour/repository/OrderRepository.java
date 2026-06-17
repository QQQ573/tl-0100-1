package com.study.tour.repository;

import com.study.tour.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOutTradeNo(String outTradeNo);
    List<Order> findByStudentId(Long studentId);
    boolean existsByOutTradeNo(String outTradeNo);
}
