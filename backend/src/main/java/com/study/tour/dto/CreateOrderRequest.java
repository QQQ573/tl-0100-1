package com.study.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "产品列表不能为空")
    private List<OrderItemRequest> items;

    @NotBlank(message = "学员姓名不能为空")
    private String studentName;

    @NotBlank(message = "学员身份证号不能为空")
    private String studentIdCard;

    private String studentPhone;
    private String studentEmail;

    @NotBlank(message = "监护人姓名不能为空")
    private String guardianName;

    @NotBlank(message = "监护人身份证号不能为空")
    private String guardianIdCard;

    @NotBlank(message = "监护人电话不能为空")
    private String guardianPhone;

    @NotNull(message = "开营日期不能为空")
    private LocalDate campStartDate;
}
