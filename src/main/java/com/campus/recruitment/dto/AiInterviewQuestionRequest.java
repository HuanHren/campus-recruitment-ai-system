package com.campus.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiInterviewQuestionRequest {

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 100, message = "岗位名称不能超过100个字符")
    private String jobName;

    @NotBlank(message = "岗位要求不能为空")
    @Size(max = 3000, message = "岗位要求不能超过3000个字符")
    private String jobRequirement;
}
