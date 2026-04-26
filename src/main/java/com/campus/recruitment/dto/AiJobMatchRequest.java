package com.campus.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiJobMatchRequest {

    @NotBlank(message = "简历内容不能为空")
    @Size(max = 6000, message = "简历内容不能超过6000个字符")
    private String resumeContent;

    @NotBlank(message = "岗位要求不能为空")
    @Size(max = 6000, message = "岗位要求不能超过6000个字符")
    private String jobRequirement;
}
