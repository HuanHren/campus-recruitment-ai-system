package com.campus.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyStatusRequest {

    @NotBlank(message = "投递状态不能为空")
    @Pattern(regexp = "PENDING|VIEWED|INTERVIEW|REJECTED", message = "投递状态只能是PENDING、VIEWED、INTERVIEW或REJECTED")
    private String applyStatus;

    @Size(max = 255, message = "处理备注不能超过255个字符")
    private String remark;
}
