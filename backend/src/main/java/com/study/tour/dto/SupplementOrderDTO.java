package com.study.tour.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SupplementOrderDTO {
    private Long id;
    private String supplementNo;
    private Long parentOrderId;
    private String fromLevel;
    private String toLevel;
    private Long fromProductId;
    private Long toProductId;
    private String fromProductName;
    private String toProductName;
    private BigDecimal fromPrice;
    private BigDecimal toPrice;
    private BigDecimal diffAmount;
    private String status;
    private String effectiveLevel;
    private String effectiveCoverage;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
