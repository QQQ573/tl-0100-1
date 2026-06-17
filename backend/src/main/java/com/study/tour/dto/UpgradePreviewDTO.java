package com.study.tour.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpgradePreviewDTO {
    private Long parentOrderId;
    private String currentLevel;
    private String currentProductName;
    private BigDecimal currentPrice;
    private List<UpgradeOptionDTO> upgradeOptions;
}
