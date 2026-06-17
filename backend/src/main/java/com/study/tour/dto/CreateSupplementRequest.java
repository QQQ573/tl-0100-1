package com.study.tour.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSupplementRequest {
    @NotNull(message = "父订单ID不能为空")
    private Long parentOrderId;

    @NotNull(message = "目标档位不能为空")
    private String toLevel;
}
