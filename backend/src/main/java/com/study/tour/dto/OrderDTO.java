package com.study.tour.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String outTradeNo;
    private BigDecimal totalAmount;
    private String status;
    private Long studentId;
    private String studentName;
    private String guardianName;
    private String guardianIdCard;
    private String guardianPhone;
    private LocalDate campStartDate;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private List<OrderItemDTO> items;
}
