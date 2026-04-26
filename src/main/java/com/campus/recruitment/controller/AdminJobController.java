package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.JobAuditRequest;
import com.campus.recruitment.service.JobInfoService;
import com.campus.recruitment.vo.JobInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员-招聘岗位审核")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/jobs")
public class AdminJobController {

    private final JobInfoService jobInfoService;

    @Operation(summary = "管理员分页查看岗位列表")
    @GetMapping
    public Result<PageResult<JobInfoVO>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "关键词：岗位名称、城市、学历") @RequestParam(required = false) String keyword,
            @Parameter(description = "审核状态：PENDING、APPROVED、REJECTED") @RequestParam(required = false) String auditStatus,
            @Parameter(description = "发布状态：ONLINE、OFFLINE") @RequestParam(required = false) String publishStatus) {
        return Result.success(jobInfoService.pageAdminJobs(current, size, keyword, auditStatus, publishStatus));
    }

    @Operation(summary = "管理员审核岗位")
    @PutMapping("/{id}/audit")
    public Result<JobInfoVO> audit(@PathVariable Long id, @Valid @RequestBody JobAuditRequest request) {
        return Result.success(jobInfoService.audit(id, request));
    }
}
