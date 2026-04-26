package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.ApplyStatusRequest;
import com.campus.recruitment.entity.ApplyStatus;
import com.campus.recruitment.entity.CompanyInfo;
import com.campus.recruitment.entity.JobApply;
import com.campus.recruitment.entity.JobAuditStatus;
import com.campus.recruitment.entity.JobInfo;
import com.campus.recruitment.entity.JobPublishStatus;
import com.campus.recruitment.entity.ResumeInfo;
import com.campus.recruitment.mapper.JobApplyMapper;
import com.campus.recruitment.service.CompanyInfoService;
import com.campus.recruitment.service.JobApplyService;
import com.campus.recruitment.service.JobInfoService;
import com.campus.recruitment.service.ResumeInfoService;
import com.campus.recruitment.vo.JobApplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class JobApplyServiceImpl extends ServiceImpl<JobApplyMapper, JobApply> implements JobApplyService {

    private final JobInfoService jobInfoService;
    private final ResumeInfoService resumeInfoService;
    private final CompanyInfoService companyInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplyVO apply(Long studentUserId, Long jobId) {
        ResumeInfo resumeInfo = resumeInfoService.getByStudentUserId(studentUserId);
        if (resumeInfo == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请先新增简历后再投递岗位");
        }

        JobInfo jobInfo = jobInfoService.getById(jobId);
        if (jobInfo == null
                || !JobAuditStatus.APPROVED.name().equals(jobInfo.getAuditStatus())
                || !JobPublishStatus.ONLINE.name().equals(jobInfo.getPublishStatus())) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "岗位不存在或未发布");
        }

        if (exists(new LambdaQueryWrapper<JobApply>()
                .eq(JobApply::getJobId, jobId)
                .eq(JobApply::getStudentUserId, studentUserId))) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该岗位已投递，请勿重复投递");
        }

        JobApply jobApply = new JobApply();
        jobApply.setJobId(jobInfo.getId());
        jobApply.setCompanyId(jobInfo.getCompanyId());
        jobApply.setCompanyUserId(jobInfo.getCompanyUserId());
        jobApply.setStudentUserId(studentUserId);
        jobApply.setResumeId(resumeInfo.getId());
        jobApply.setApplyStatus(ApplyStatus.PENDING.name());
        jobApply.setDeleted(0);
        save(jobApply);
        return toVO(jobApply, jobInfo, resumeInfo, getCompanyInfo(jobInfo.getCompanyId()));
    }

    @Override
    public PageResult<JobApplyVO> pageStudentApplications(Long studentUserId, Long current, Long size, String applyStatus) {
        if (StringUtils.hasText(applyStatus)) {
            ApplyStatus.from(applyStatus);
        }
        Page<JobApply> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<JobApply>()
                        .eq(JobApply::getStudentUserId, studentUserId)
                        .eq(StringUtils.hasText(applyStatus), JobApply::getApplyStatus, applyStatus)
                        .orderByDesc(JobApply::getUpdatedAt));
        return toPageResult(page);
    }

    @Override
    public PageResult<JobApplyVO> pageCompanyApplications(Long companyUserId, Long current, Long size, String applyStatus,
                                                         Long jobId, String keyword) {
        if (StringUtils.hasText(applyStatus)) {
            ApplyStatus.from(applyStatus);
        }
        Page<JobApply> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<JobApply>()
                        .eq(JobApply::getCompanyUserId, companyUserId)
                        .eq(StringUtils.hasText(applyStatus), JobApply::getApplyStatus, applyStatus)
                        .eq(jobId != null, JobApply::getJobId, jobId)
                        .orderByDesc(JobApply::getUpdatedAt));

        PageResult<JobApplyVO> result = toPageResult(page);
        if (!StringUtils.hasText(keyword)) {
            return result;
        }
        result.setRecords(result.getRecords().stream()
                .filter(item -> contains(item.getJobName(), keyword)
                        || contains(item.getStudentName(), keyword)
                        || contains(item.getResumeName(), keyword))
                .toList());
        result.setTotal((long) result.getRecords().size());
        result.setPages(result.getTotal() == 0 ? 0L : 1L);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplyVO updateStatus(Long companyUserId, Long applyId, ApplyStatusRequest request) {
        JobApply jobApply = getOne(new LambdaQueryWrapper<JobApply>()
                .eq(JobApply::getId, applyId)
                .eq(JobApply::getCompanyUserId, companyUserId)
                .last("LIMIT 1"));
        if (jobApply == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "投递记录不存在");
        }

        ApplyStatus status = ApplyStatus.from(request.getApplyStatus());
        jobApply.setApplyStatus(status.name());
        jobApply.setRemark(request.getRemark());
        updateById(jobApply);
        return toVO(jobApply);
    }

    private PageResult<JobApplyVO> toPageResult(Page<JobApply> page) {
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getPages(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    private JobApplyVO toVO(JobApply jobApply) {
        JobInfo jobInfo = jobInfoService.getById(jobApply.getJobId());
        ResumeInfo resumeInfo = resumeInfoService.getById(jobApply.getResumeId());
        CompanyInfo companyInfo = jobInfo == null ? null : getCompanyInfo(jobInfo.getCompanyId());
        return toVO(jobApply, jobInfo, resumeInfo, companyInfo);
    }

    private JobApplyVO toVO(JobApply jobApply, JobInfo jobInfo, ResumeInfo resumeInfo, CompanyInfo companyInfo) {
        ApplyStatus applyStatus = ApplyStatus.from(jobApply.getApplyStatus());
        return JobApplyVO.builder()
                .id(jobApply.getId())
                .jobId(jobApply.getJobId())
                .jobName(jobInfo == null ? null : jobInfo.getJobName())
                .city(jobInfo == null ? null : jobInfo.getCity())
                .salaryMin(jobInfo == null ? null : jobInfo.getSalaryMin())
                .salaryMax(jobInfo == null ? null : jobInfo.getSalaryMax())
                .education(jobInfo == null ? null : jobInfo.getEducation())
                .companyId(jobApply.getCompanyId())
                .companyUserId(jobApply.getCompanyUserId())
                .companyName(companyInfo == null ? null : companyInfo.getCompanyName())
                .studentUserId(jobApply.getStudentUserId())
                .resumeId(jobApply.getResumeId())
                .resumeName(resumeInfo == null ? null : resumeInfo.getResumeName())
                .studentName(resumeInfo == null ? null : resumeInfo.getRealName())
                .studentPhone(resumeInfo == null ? null : resumeInfo.getPhone())
                .studentEmail(resumeInfo == null ? null : resumeInfo.getEmail())
                .applyStatus(jobApply.getApplyStatus())
                .applyStatusText(applyStatus.getDescription())
                .remark(jobApply.getRemark())
                .createdAt(jobApply.getCreatedAt())
                .updatedAt(jobApply.getUpdatedAt())
                .build();
    }

    private CompanyInfo getCompanyInfo(Long companyId) {
        return companyInfoService.getById(companyId);
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
