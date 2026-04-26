package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.StudentInfoRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.StudentInfoService;
import com.campus.recruitment.vo.StudentInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学生基础信息")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/info")
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    @Operation(summary = "新增当前学生基础信息")
    @PostMapping
    public Result<StudentInfoVO> create(@AuthenticationPrincipal SecurityUser securityUser,
                                        @Valid @RequestBody StudentInfoRequest request) {
        return Result.success(studentInfoService.create(
                securityUser.getUser().getId(),
                securityUser.getUsername(),
                request
        ));
    }

    @Operation(summary = "修改当前学生基础信息")
    @PutMapping
    public Result<StudentInfoVO> update(@AuthenticationPrincipal SecurityUser securityUser,
                                        @Valid @RequestBody StudentInfoRequest request) {
        return Result.success(studentInfoService.update(
                securityUser.getUser().getId(),
                securityUser.getUsername(),
                request
        ));
    }

    @Operation(summary = "查询当前学生基础信息")
    @GetMapping
    public Result<StudentInfoVO> current(@AuthenticationPrincipal SecurityUser securityUser) {
        return Result.success(studentInfoService.getCurrent(
                securityUser.getUser().getId(),
                securityUser.getUsername()
        ));
    }
}
