package com.campus.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InterviewReplyRequest {

    @NotBlank(message = "回复状态不能为空")
    @Pattern(regexp = "ACCEPTED|REJECTED", message = "回复状态只能是ACCEPTED或REJECTED")
    private String invitationStatus;

    @Size(max = 255, message = "回复备注不能超过255个字符")
    private String replyRemark;
}
