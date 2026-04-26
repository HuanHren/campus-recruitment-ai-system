package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompanyInfoVO {

    private Long id;

    private Long userId;

    private String username;

    private String companyName;

    private String unifiedSocialCreditCode;

    private String industry;

    private String companyScale;

    private String address;

    private String contactPerson;

    private String contactPhone;

    private String contactEmail;

    private String description;

    private String auditStatus;

    private String auditRemark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
