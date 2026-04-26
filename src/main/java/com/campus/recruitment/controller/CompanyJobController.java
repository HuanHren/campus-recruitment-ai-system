package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.JobInfoRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.JobInfoService;
import com.campus.recruitment.vo.JobInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "企业-招聘岗位管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/jobs")
public class CompanyJobController {

    private final JobInfoService jobInfoService;

    @Operation(summary = "企业发布岗位")
    @PostMapping
    public Result<JobInfoVO> create(@AuthenticationPrincipal SecurityUser securityUser,
                                    @Valid @RequestBody JobInfoRequest request) {
        return Result.success(jobInfoService.create(securityUser.getUser().getId(), request));
    }

    @Operation(summary = "企业修改岗位")
    @PutMapping("/{id}")
    public Result<JobInfoVO> update(@AuthenticationPrincipal SecurityUser securityUser,
                                    @PathVariable Long id,
                                    @Valid @RequestBody JobInfoRequest request) {
        return Result.success(jobInfoService.update(securityUser.getUser().getId(), id, request));
    }

    @Operation(summary = "企业下架岗位")
    @PutMapping("/{id}/offline")
    public Result<JobInfoVO> offline(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long id) {
        return Result.success(jobInfoService.offline(securityUser.getUser().getId(), id));
    }

    @Operation(summary = "企业查看自己的岗位详情")
    @GetMapping("/{id}")
    public Result<JobInfoVO> detail(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long id) {
        return Result.success(jobInfoService.getCompanyJob(securityUser.getUser().getId(), id));
    }

    @Operation(summary = "企业分页查看自己的岗位")
    @GetMapping
    public Result<PageResult<JobInfoVO>> page(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "关键词：岗位名称、城市") @RequestParam(required = false) String keyword,
            @Parameter(description = "审核状态：PENDING、APPROVED、REJECTED") @RequestParam(required = false) String auditStatus,
            @Parameter(description = "发布状态：ONLINE、OFFLINE") @RequestParam(required = false) String publishStatus) {
        return Result.success(jobInfoService.pageCompanyJobs(
                securityUser.getUser().getId(),
                current,
                size,
                keyword,
                auditStatus,
                publishStatus
        ));
    }
}
