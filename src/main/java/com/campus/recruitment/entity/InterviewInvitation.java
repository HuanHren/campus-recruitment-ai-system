package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview_invitation")
public class InterviewInvitation {

    @TableId
    private Long id;

    private Long applyId;

    private Long jobId;

    private Long companyId;

    private Long companyUserId;

    private Long studentUserId;

    private Long resumeId;

    private LocalDateTime interviewTime;

    private String interviewType;

    private String interviewAddress;

    private String contactPerson;

    private String contactPhone;

    private String content;

    private String invitationStatus;

    private String replyRemark;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
