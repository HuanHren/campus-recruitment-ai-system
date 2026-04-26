package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.service.JobInfoService;
import com.campus.recruitment.vo.JobInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "学生-岗位浏览")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/jobs")
public class StudentJobController {

    private final JobInfoService jobInfoService;

    @Operation(summary = "学生分页查看已发布岗位列表")
    @GetMapping
    public Result<PageResult<JobInfoVO>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "岗位名称") @RequestParam(required = false) String jobName,
            @Parameter(description = "工作城市") @RequestParam(required = false) String city,
            @Parameter(description = "最低期望薪资") @RequestParam(required = false) BigDecimal minSalary,
            @Parameter(description = "最高期望薪资") @RequestParam(required = false) BigDecimal maxSalary,
            @Parameter(description = "学历要求") @RequestParam(required = false) String education) {
        return Result.success(jobInfoService.pageStudentJobs(
                current,
                size,
                jobName,
                city,
                minSalary,
                maxSalary,
                education
        ));
    }

    @Operation(summary = "学生查看岗位详情")
    @GetMapping("/{id}")
    public Result<JobInfoVO> detail(@PathVariable Long id) {
        return Result.success(jobInfoService.getStudentJob(id));
    }
}
