package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.ApplyStatusRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.JobApplyService;
import com.campus.recruitment.vo.JobApplyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "企业-投递管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/applications")
public class CompanyApplyController {

    private final JobApplyService jobApplyService;

    @Operation(summary = "企业查看收到的投递记录")
    @GetMapping
    public Result<PageResult<JobApplyVO>> page(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "投递状态：PENDING、VIEWED、INTERVIEW、REJECTED") @RequestParam(required = false) String applyStatus,
            @Parameter(description = "岗位ID") @RequestParam(required = false) Long jobId,
            @Parameter(description = "关键词：岗位名称、学生姓名、简历名称") @RequestParam(required = false) String keyword) {
        return Result.success(jobApplyService.pageCompanyApplications(
                securityUser.getUser().getId(),
                current,
                size,
                applyStatus,
                jobId,
                keyword
        ));
    }

    @Operation(summary = "企业修改投递状态")
    @PutMapping("/{id}/status")
    public Result<JobApplyVO> updateStatus(@AuthenticationPrincipal SecurityUser securityUser,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ApplyStatusRequest request) {
        return Result.success(jobApplyService.updateStatus(securityUser.getUser().getId(), id, request));
    }
}
