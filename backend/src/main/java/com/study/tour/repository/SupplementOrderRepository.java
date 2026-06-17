package com.study.tour.repository;

import com.study.tour.entity.SupplementOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplementOrderRepository extends JpaRepository<SupplementOrder, Long> {
    List<SupplementOrder> findByParentOrderId(Long parentOrderId);
    List<SupplementOrder> findByParentOrderIdAndStatus(Long parentOrderId, String status);
    boolean existsByParentOrderIdAndStatus(Long parentOrderId, String status);
    List<SupplementOrder> findByStatus(String status);
}
