package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.JobApplyService;
import com.campus.recruitment.vo.JobApplyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学生-岗位投递")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentApplyController {

    private final JobApplyService jobApplyService;

    @Operation(summary = "学生投递岗位")
    @PostMapping("/jobs/{jobId}/apply")
    public Result<JobApplyVO> apply(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long jobId) {
        return Result.success(jobApplyService.apply(securityUser.getUser().getId(), jobId));
    }

    @Operation(summary = "学生查看投递记录")
    @GetMapping("/applications")
    public Result<PageResult<JobApplyVO>> page(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "投递状态：PENDING、VIEWED、INTERVIEW、REJECTED") @RequestParam(required = false) String applyStatus) {
        return Result.success(jobApplyService.pageStudentApplications(
                securityUser.getUser().getId(),
                current,
                size,
                applyStatus
        ));
    }
}
