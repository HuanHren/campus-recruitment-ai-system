package com.campus.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobAuditRequest {

    @NotBlank(message = "审核状态不能为空")
    @Pattern(regexp = "PENDING|APPROVED|REJECTED", message = "审核状态只能是PENDING、APPROVED或REJECTED")
    private String auditStatus;

    @Size(max = 255, message = "审核备注不能超过255个字符")
    private String auditRemark;
}
