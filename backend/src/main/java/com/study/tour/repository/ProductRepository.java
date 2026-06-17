package com.study.tour.repository;

import com.study.tour.entity.Product;
import com.study.tour.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(ProductType type);
    List<Product> findByStatus(String status);
    List<Product> findByTypeAndStatus(ProductType type, String status);
}
