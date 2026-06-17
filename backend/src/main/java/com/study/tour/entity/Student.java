package com.study.tour.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "id_card", nullable = false, length = 18, unique = true)
    private String idCard;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "has_insurance", nullable = false)
    private Boolean hasInsurance;

    @Column(name = "insurance_badge_url", length = 500)
    private String insuranceBadgeUrl;

    @PrePersist
    protected void onCreate() {
        if (hasInsurance == null) {
            hasInsurance = false;
        }
    }
}
