package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class JobInfoVO {

    private Long id;

    private Long companyId;

    private Long companyUserId;

    private String companyName;

    private String jobName;

    private String city;

    private String jobType;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String education;

    private String experience;

    private Integer headcount;

    private String jobDescription;

    private String jobRequirement;

    private String welfare;

    private String contactPerson;

    private String contactPhone;

    private String auditStatus;

    private String auditRemark;

    private String publishStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
