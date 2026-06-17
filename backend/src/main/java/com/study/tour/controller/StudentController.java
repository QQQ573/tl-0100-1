package com.study.tour.controller;

import com.study.tour.common.Result;
import com.study.tour.dto.StudentDTO;
import com.study.tour.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "学员管理", description = "学员相关接口")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/{id}")
    @Operation(summary = "获取学员详情")
    public Result<StudentDTO> getStudentById(@PathVariable Long id) {
        return Result.success(studentService.getStudentById(id));
    }

    @GetMapping("/idCard/{idCard}")
    @Operation(summary = "根据身份证号获取学员")
    public Result<StudentDTO> getStudentByIdCard(@PathVariable String idCard) {
        return Result.success(studentService.getStudentByIdCard(idCard));
    }

    @GetMapping("/{id}/public")
    @Operation(summary = "获取学员公开信息（不显示身份证号）")
    public Result<StudentDTO> getStudentPublicInfo(@PathVariable Long id) {
        return Result.success(studentService.getStudentPublicInfo(id));
    }
}
