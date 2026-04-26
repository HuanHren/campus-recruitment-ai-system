package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class JobApplyVO {

    private Long id;

    private Long jobId;

    private String jobName;

    private String city;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String education;

    private Long companyId;

    private Long companyUserId;

    private String companyName;

    private Long studentUserId;

    private Long resumeId;

    private String resumeName;

    private String studentName;

    private String studentPhone;

    private String studentEmail;

    private String applyStatus;

    private String applyStatusText;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
