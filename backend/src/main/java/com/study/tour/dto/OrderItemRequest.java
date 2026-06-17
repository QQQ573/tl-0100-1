package com.study.tour.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
