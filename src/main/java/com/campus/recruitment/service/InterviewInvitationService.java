package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.dto.InterviewInvitationRequest;
import com.campus.recruitment.dto.InterviewReplyRequest;
import com.campus.recruitment.entity.InterviewInvitation;
import com.campus.recruitment.vo.InterviewInvitationVO;

public interface InterviewInvitationService extends IService<InterviewInvitation> {

    InterviewInvitationVO send(Long companyUserId, InterviewInvitationRequest request);

    PageResult<InterviewInvitationVO> pageStudentInvitations(Long studentUserId, Long current, Long size,
                                                             String invitationStatus);

    InterviewInvitationVO reply(Long studentUserId, Long id, InterviewReplyRequest request);

    PageResult<InterviewInvitationVO> pageAdminInvitations(Long current, Long size, String invitationStatus,
                                                           String keyword);
}
