package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.CompanyInfoRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.CompanyInfoService;
import com.campus.recruitment.vo.CompanyInfoVO;
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

@Tag(name = "企业基础信息")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/info")
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    @Operation(summary = "新增当前企业基础信息")
    @PostMapping
    public Result<CompanyInfoVO> create(@AuthenticationPrincipal SecurityUser securityUser,
                                        @Valid @RequestBody CompanyInfoRequest request) {
        return Result.success(companyInfoService.create(
                securityUser.getUser().getId(),
                securityUser.getUsername(),
                request
        ));
    }

    @Operation(summary = "修改当前企业基础信息")
    @PutMapping
    public Result<CompanyInfoVO> update(@AuthenticationPrincipal SecurityUser securityUser,
                                        @Valid @RequestBody CompanyInfoRequest request) {
        return Result.success(companyInfoService.update(
                securityUser.getUser().getId(),
                securityUser.getUsername(),
                request
        ));
    }

    @Operation(summary = "查询当前企业基础信息")
    @GetMapping
    public Result<CompanyInfoVO> current(@AuthenticationPrincipal SecurityUser securityUser) {
        return Result.success(companyInfoService.getCurrent(
                securityUser.getUser().getId(),
                securityUser.getUsername()
        ));
    }
}
