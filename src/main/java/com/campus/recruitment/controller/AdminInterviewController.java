package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.service.InterviewInvitationService;
import com.campus.recruitment.vo.InterviewInvitationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员-面试邀请记录")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/interviews")
public class AdminInterviewController {

    private final InterviewInvitationService interviewInvitationService;

    @Operation(summary = "管理员查看所有面试邀请记录")
    @GetMapping
    public Result<PageResult<InterviewInvitationVO>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "邀请状态：PENDING、ACCEPTED、REJECTED") @RequestParam(required = false) String invitationStatus,
            @Parameter(description = "关键词：岗位名称、企业名称、学生姓名") @RequestParam(required = false) String keyword) {
        return Result.success(interviewInvitationService.pageAdminInvitations(current, size, invitationStatus, keyword));
    }
}
