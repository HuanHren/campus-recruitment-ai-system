package com.campus.recruitment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewInvitationRequest {

    @NotNull(message = "投递ID不能为空")
    private Long applyId;

    @NotNull(message = "面试时间不能为空")
    @Future(message = "面试时间必须晚于当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;

    @NotBlank(message = "面试方式不能为空")
    @Size(max = 50, message = "面试方式不能超过50个字符")
    private String interviewType;

    @NotBlank(message = "面试地点或会议链接不能为空")
    @Size(max = 255, message = "面试地点或会议链接不能超过255个字符")
    private String interviewAddress;

    @Size(max = 50, message = "联系人不能超过50个字符")
    private String contactPerson;

    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;

    @Size(max = 1000, message = "邀请说明不能超过1000个字符")
    private String content;
}
