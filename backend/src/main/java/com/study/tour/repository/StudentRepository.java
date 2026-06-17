package com.study.tour.repository;

import com.study.tour.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByIdCard(String idCard);
    boolean existsByIdCard(String idCard);
}
