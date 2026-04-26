package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.service.StudentInfoService;
import com.campus.recruitment.vo.StudentInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员-学生信息管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/students")
public class AdminStudentController {

    private final StudentInfoService studentInfoService;

    @Operation(summary = "分页查看学生基础信息列表")
    @GetMapping
    public Result<PageResult<StudentInfoVO>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "关键词：姓名、学号、学校、专业") @RequestParam(required = false) String keyword) {
        return Result.success(studentInfoService.pageForAdmin(current, size, keyword));
    }
}
