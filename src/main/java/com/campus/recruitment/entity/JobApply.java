package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("job_apply")
public class JobApply {

    @TableId
    private Long id;

    private Long jobId;

    private Long companyId;

    private Long companyUserId;

    private Long studentUserId;

    private Long resumeId;

    private String applyStatus;

    private String remark;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
