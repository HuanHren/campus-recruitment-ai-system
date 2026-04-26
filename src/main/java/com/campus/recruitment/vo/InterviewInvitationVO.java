package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InterviewInvitationVO {

    private Long id;

    private Long applyId;

    private Long jobId;

    private String jobName;

    private Long companyId;

    private Long companyUserId;

    private String companyName;

    private Long studentUserId;

    private Long resumeId;

    private String studentName;

    private String studentPhone;

    private String studentEmail;

    private LocalDateTime interviewTime;

    private String interviewType;

    private String interviewAddress;

    private String contactPerson;

    private String contactPhone;

    private String content;

    private String invitationStatus;

    private String invitationStatusText;

    private String replyRemark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
