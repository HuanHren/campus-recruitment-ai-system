package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.ResumeInfoRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.ResumeInfoService;
import com.campus.recruitment.vo.ResumeInfoVO;
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

@Tag(name = "学生-简历管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/resume")
public class StudentResumeController {

    private final ResumeInfoService resumeInfoService;

    @Operation(summary = "学生新增简历")
    @PostMapping
    public Result<ResumeInfoVO> create(@AuthenticationPrincipal SecurityUser securityUser,
                                       @Valid @RequestBody ResumeInfoRequest request) {
        return Result.success(resumeInfoService.create(securityUser.getUser().getId(), request));
    }

    @Operation(summary = "学生修改简历")
    @PutMapping
    public Result<ResumeInfoVO> update(@AuthenticationPrincipal SecurityUser securityUser,
                                       @Valid @RequestBody ResumeInfoRequest request) {
        return Result.success(resumeInfoService.update(securityUser.getUser().getId(), request));
    }

    @Operation(summary = "学生查看自己的简历")
    @GetMapping
    public Result<ResumeInfoVO> current(@AuthenticationPrincipal SecurityUser securityUser) {
        return Result.success(resumeInfoService.getCurrent(securityUser.getUser().getId()));
    }
}
