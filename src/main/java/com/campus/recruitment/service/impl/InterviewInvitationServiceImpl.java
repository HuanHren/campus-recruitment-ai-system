package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.InterviewInvitationRequest;
import com.campus.recruitment.dto.InterviewReplyRequest;
import com.campus.recruitment.entity.ApplyStatus;
import com.campus.recruitment.entity.CompanyInfo;
import com.campus.recruitment.entity.InterviewInvitation;
import com.campus.recruitment.entity.InterviewInvitationStatus;
import com.campus.recruitment.entity.JobApply;
import com.campus.recruitment.entity.JobInfo;
import com.campus.recruitment.entity.ResumeInfo;
import com.campus.recruitment.mapper.InterviewInvitationMapper;
import com.campus.recruitment.service.CompanyInfoService;
import com.campus.recruitment.service.InterviewInvitationService;
import com.campus.recruitment.service.JobApplyService;
import com.campus.recruitment.service.JobInfoService;
import com.campus.recruitment.service.ResumeInfoService;
import com.campus.recruitment.vo.InterviewInvitationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InterviewInvitationServiceImpl extends ServiceImpl<InterviewInvitationMapper, InterviewInvitation>
        implements InterviewInvitationService {

    private final JobApplyService jobApplyService;
    private final JobInfoService jobInfoService;
    private final CompanyInfoService companyInfoService;
    private final ResumeInfoService resumeInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InterviewInvitationVO send(Long companyUserId, InterviewInvitationRequest request) {
        JobApply jobApply = jobApplyService.getOne(new LambdaQueryWrapper<JobApply>()
                .eq(JobApply::getId, request.getApplyId())
                .eq(JobApply::getCompanyUserId, companyUserId)
                .last("LIMIT 1"));
        if (jobApply == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "投递记录不存在");
        }
        if (exists(new LambdaQueryWrapper<InterviewInvitation>().eq(InterviewInvitation::getApplyId, jobApply.getId()))) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该投递记录已发送过面试邀请");
        }

        InterviewInvitation invitation = new InterviewInvitation();
        invitation.setApplyId(jobApply.getId());
        invitation.setJobId(jobApply.getJobId());
        invitation.setCompanyId(jobApply.getCompanyId());
        invitation.setCompanyUserId(jobApply.getCompanyUserId());
        invitation.setStudentUserId(jobApply.getStudentUserId());
        invitation.setResumeId(jobApply.getResumeId());
        invitation.setInterviewTime(request.getInterviewTime());
        invitation.setInterviewType(request.getInterviewType());
        invitation.setInterviewAddress(request.getInterviewAddress());
        invitation.setContactPerson(request.getContactPerson());
        invitation.setContactPhone(request.getContactPhone());
        invitation.setContent(request.getContent());
        invitation.setInvitationStatus(InterviewInvitationStatus.PENDING.name());
        invitation.setDeleted(0);
        save(invitation);

        jobApply.setApplyStatus(ApplyStatus.INTERVIEW.name());
        jobApply.setRemark("企业已发送面试邀请");
        jobApplyService.updateById(jobApply);

        return toVO(invitation);
    }

    @Override
    public PageResult<InterviewInvitationVO> pageStudentInvitations(Long studentUserId, Long current, Long size,
                                                                    String invitationStatus) {
        if (StringUtils.hasText(invitationStatus)) {
            InterviewInvitationStatus.from(invitationStatus);
        }
        Page<InterviewInvitation> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<InterviewInvitation>()
                        .eq(InterviewInvitation::getStudentUserId, studentUserId)
                        .eq(StringUtils.hasText(invitationStatus), InterviewInvitation::getInvitationStatus, invitationStatus)
                        .orderByDesc(InterviewInvitation::getInterviewTime)
                        .orderByDesc(InterviewInvitation::getUpdatedAt));
        return toPageResult(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InterviewInvitationVO reply(Long studentUserId, Long id, InterviewReplyRequest request) {
        InterviewInvitation invitation = getOne(new LambdaQueryWrapper<InterviewInvitation>()
                .eq(InterviewInvitation::getId, id)
                .eq(InterviewInvitation::getStudentUserId, studentUserId)
                .last("LIMIT 1"));
        if (invitation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试邀请不存在");
        }
        if (!InterviewInvitationStatus.PENDING.name().equals(invitation.getInvitationStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该面试邀请已回复，不能重复操作");
        }

        InterviewInvitationStatus status = InterviewInvitationStatus.from(request.getInvitationStatus());
        invitation.setInvitationStatus(status.name());
        invitation.setReplyRemark(request.getReplyRemark());
        updateById(invitation);
        return toVO(invitation);
    }

    @Override
    public PageResult<InterviewInvitationVO> pageAdminInvitations(Long current, Long size, String invitationStatus,
                                                                  String keyword) {
        if (StringUtils.hasText(invitationStatus)) {
            InterviewInvitationStatus.from(invitationStatus);
        }
        Page<InterviewInvitation> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<InterviewInvitation>()
                        .eq(StringUtils.hasText(invitationStatus), InterviewInvitation::getInvitationStatus, invitationStatus)
                        .orderByDesc(InterviewInvitation::getInterviewTime)
                        .orderByDesc(InterviewInvitation::getUpdatedAt));

        PageResult<InterviewInvitationVO> result = toPageResult(page);
        if (!StringUtils.hasText(keyword)) {
            return result;
        }
        result.setRecords(result.getRecords().stream()
                .filter(item -> contains(item.getJobName(), keyword)
                        || contains(item.getCompanyName(), keyword)
                        || contains(item.getStudentName(), keyword))
                .toList());
        result.setTotal((long) result.getRecords().size());
        result.setPages(result.getTotal() == 0 ? 0L : 1L);
        return result;
    }

    private PageResult<InterviewInvitationVO> toPageResult(Page<InterviewInvitation> page) {
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getPages(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    private InterviewInvitationVO toVO(InterviewInvitation invitation) {
        JobInfo jobInfo = jobInfoService.getById(invitation.getJobId());
        CompanyInfo companyInfo = companyInfoService.getById(invitation.getCompanyId());
        ResumeInfo resumeInfo = resumeInfoService.getById(invitation.getResumeId());
        InterviewInvitationStatus status = InterviewInvitationStatus.from(invitation.getInvitationStatus());
        return InterviewInvitationVO.builder()
                .id(invitation.getId())
                .applyId(invitation.getApplyId())
                .jobId(invitation.getJobId())
                .jobName(jobInfo == null ? null : jobInfo.getJobName())
                .companyId(invitation.getCompanyId())
                .companyUserId(invitation.getCompanyUserId())
                .companyName(companyInfo == null ? null : companyInfo.getCompanyName())
                .studentUserId(invitation.getStudentUserId())
                .resumeId(invitation.getResumeId())
                .studentName(resumeInfo == null ? null : resumeInfo.getRealName())
                .studentPhone(resumeInfo == null ? null : resumeInfo.getPhone())
                .studentEmail(resumeInfo == null ? null : resumeInfo.getEmail())
                .interviewTime(invitation.getInterviewTime())
                .interviewType(invitation.getInterviewType())
                .interviewAddress(invitation.getInterviewAddress())
                .contactPerson(invitation.getContactPerson())
                .contactPhone(invitation.getContactPhone())
                .content(invitation.getContent())
                .invitationStatus(invitation.getInvitationStatus())
                .invitationStatusText(status.getDescription())
                .replyRemark(invitation.getReplyRemark())
                .createdAt(invitation.getCreatedAt())
                .updatedAt(invitation.getUpdatedAt())
                .build();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }

    private long normalizeCurrent(Long current) {
        return current == null || current < 1 ? 1 : current;
    }

    private long normalizeSize(Long size) {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }
}
