package com.study.tour.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String name;
    private String idCard;
    private String phone;
    private String email;
    private Boolean hasInsurance;
    private String insuranceBadgeUrl;
}
