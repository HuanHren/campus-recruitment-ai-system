package com.campus.recruitment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobInfoRequest {

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 100, message = "岗位名称不能超过100个字符")
    private String jobName;

    @NotBlank(message = "工作城市不能为空")
    @Size(max = 50, message = "工作城市不能超过50个字符")
    private String city;

    @Size(max = 50, message = "岗位类型不能超过50个字符")
    private String jobType;

    @NotNull(message = "最低薪资不能为空")
    @DecimalMin(value = "0.00", message = "最低薪资不能小于0")
    private BigDecimal salaryMin;

    @NotNull(message = "最高薪资不能为空")
    @DecimalMin(value = "0.00", message = "最高薪资不能小于0")
    private BigDecimal salaryMax;

    @NotBlank(message = "学历要求不能为空")
    @Size(max = 30, message = "学历要求不能超过30个字符")
    private String education;

    @Size(max = 50, message = "经验要求不能超过50个字符")
    private String experience;

    @NotNull(message = "招聘人数不能为空")
    @Min(value = 1, message = "招聘人数不能小于1")
    private Integer headcount;

    @NotBlank(message = "岗位描述不能为空")
    @Size(max = 2000, message = "岗位描述不能超过2000个字符")
    private String jobDescription;

    @Size(max = 2000, message = "岗位要求不能超过2000个字符")
    private String jobRequirement;

    @Size(max = 500, message = "福利待遇不能超过500个字符")
    private String welfare;

    @Size(max = 50, message = "联系人不能超过50个字符")
    private String contactPerson;

    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;
}
