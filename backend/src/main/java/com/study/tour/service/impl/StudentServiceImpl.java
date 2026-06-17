package com.study.tour.service.impl;

import com.study.tour.dto.StudentDTO;
import com.study.tour.entity.Student;
import com.study.tour.repository.StudentRepository;
import com.study.tour.service.StudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学员不存在"));
        return convertToDTO(student);
    }

    @Override
    public StudentDTO getStudentByIdCard(String idCard) {
        Student student = studentRepository.findByIdCard(idCard)
                .orElseThrow(() -> new RuntimeException("学员不存在"));
        return convertToDTO(student);
    }

    @Override
    public StudentDTO getStudentPublicInfo(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学员不存在"));
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setHasInsurance(student.getHasInsurance());
        dto.setInsuranceBadgeUrl(student.getInsuranceBadgeUrl());
        return dto;
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        BeanUtils.copyProperties(student, dto);
        return dto;
    }
}
