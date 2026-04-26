package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("job_info")
public class JobInfo {

    @TableId
    private Long id;

    private Long companyId;

    private Long companyUserId;

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

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
