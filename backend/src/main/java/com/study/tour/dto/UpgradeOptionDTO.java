package com.study.tour.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpgradeOptionDTO {
    private Long productId;
    private String level;
    private String productName;
    private BigDecimal price;
    private BigDecimal diffAmount;
}
