package com.campus.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiJobDescriptionRequest {

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 100, message = "岗位名称不能超过100个字符")
    private String jobName;

    @NotBlank(message = "薪资范围不能为空")
    @Size(max = 100, message = "薪资范围不能超过100个字符")
    private String salaryRange;

    @NotBlank(message = "技能要求不能为空")
    @Size(max = 1000, message = "技能要求不能超过1000个字符")
    private String skillRequirement;
}
