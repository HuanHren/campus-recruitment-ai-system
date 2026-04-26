package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.CompanyAuditRequest;
import com.campus.recruitment.service.CompanyInfoService;
import com.campus.recruitment.vo.CompanyInfoVO;
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

@Tag(name = "管理员-企业信息管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/companies")
public class AdminCompanyController {

    private final CompanyInfoService companyInfoService;

    @Operation(summary = "分页查看企业基础信息列表")
    @GetMapping
    public Result<PageResult<CompanyInfoVO>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "关键词：企业名称、行业、联系人") @RequestParam(required = false) String keyword,
            @Parameter(description = "审核状态：PENDING、APPROVED、REJECTED") @RequestParam(required = false) String auditStatus) {
        return Result.success(companyInfoService.pageForAdmin(current, size, keyword, auditStatus));
    }

    @Operation(summary = "更新企业审核状态")
    @PutMapping("/{id}/audit")
    public Result<CompanyInfoVO> audit(@PathVariable Long id, @Valid @RequestBody CompanyAuditRequest request) {
        return Result.success(companyInfoService.updateAuditStatus(id, request));
    }
}
