package com.study.tour.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplement_order")
public class SupplementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplement_no", nullable = false, length = 64, unique = true)
    private String supplementNo;

    @Column(name = "parent_order_id", nullable = false)
    private Long parentOrderId;

    @Column(name = "from_level", nullable = false, length = 50)
    private String fromLevel;

    @Column(name = "to_level", nullable = false, length = 50)
    private String toLevel;

    @Column(name = "from_product_id")
    private Long fromProductId;

    @Column(name = "to_product_id")
    private Long toProductId;

    @Column(name = "from_product_name", length = 255)
    private String fromProductName;

    @Column(name = "to_product_name", length = 255)
    private String toProductName;

    @Column(name = "from_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal fromPrice;

    @Column(name = "to_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal toPrice;

    @Column(name = "diff_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal diffAmount;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "effective_level", length = 50)
    private String effectiveLevel;

    @Column(name = "effective_coverage", length = 500)
    private String effectiveCoverage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
