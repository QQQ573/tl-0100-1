package com.study.tour.service;

import com.study.tour.dto.StudentDTO;

public interface StudentService {
    StudentDTO getStudentById(Long id);
    StudentDTO getStudentByIdCard(String idCard);
    StudentDTO getStudentPublicInfo(Long id);
}
