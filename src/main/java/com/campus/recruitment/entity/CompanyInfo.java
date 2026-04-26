package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("company_info")
public class CompanyInfo {

    @TableId
    private Long id;

    private Long userId;

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

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
