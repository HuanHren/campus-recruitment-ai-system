package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.JobAuditRequest;
import com.campus.recruitment.dto.JobInfoRequest;
import com.campus.recruitment.entity.CompanyAuditStatus;
import com.campus.recruitment.entity.CompanyInfo;
import com.campus.recruitment.entity.JobAuditStatus;
import com.campus.recruitment.entity.JobInfo;
import com.campus.recruitment.entity.JobPublishStatus;
import com.campus.recruitment.mapper.JobInfoMapper;
import com.campus.recruitment.service.CompanyInfoService;
import com.campus.recruitment.service.JobInfoService;
import com.campus.recruitment.vo.JobInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobInfoServiceImpl extends ServiceImpl<JobInfoMapper, JobInfo> implements JobInfoService {

    private final CompanyInfoService companyInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobInfoVO create(Long companyUserId, JobInfoRequest request) {
        validateSalaryRange(request);
        CompanyInfo companyInfo = getApprovedCompanyInfo(companyUserId);

        JobInfo jobInfo = new JobInfo();
        jobInfo.setCompanyId(companyInfo.getId());
        jobInfo.setCompanyUserId(companyUserId);
        fillJobInfo(jobInfo, request);
        jobInfo.setAuditStatus(JobAuditStatus.PENDING.name());
        jobInfo.setAuditRemark(null);
        jobInfo.setPublishStatus(JobPublishStatus.ONLINE.name());
        jobInfo.setDeleted(0);
        save(jobInfo);
        return toVO(jobInfo, companyInfo.getCompanyName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobInfoVO update(Long companyUserId, Long id, JobInfoRequest request) {
        validateSalaryRange(request);
        JobInfo jobInfo = getOwnJob(companyUserId, id);
        CompanyInfo companyInfo = getApprovedCompanyInfo(companyUserId);

        fillJobInfo(jobInfo, request);
        // 岗位内容修改后需要管理员重新审核。
        jobInfo.setAuditStatus(JobAuditStatus.PENDING.name());
        jobInfo.setAuditRemark(null);
        jobInfo.setPublishStatus(JobPublishStatus.ONLINE.name());
        updateById(jobInfo);
        return toVO(jobInfo, companyInfo.getCompanyName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobInfoVO offline(Long companyUserId, Long id) {
        JobInfo jobInfo = getOwnJob(companyUserId, id);
        jobInfo.setPublishStatus(JobPublishStatus.OFFLINE.name());
        updateById(jobInfo);
        CompanyInfo companyInfo = companyInfoService.getById(jobInfo.getCompanyId());
        return toVO(jobInfo, companyInfo == null ? null : companyInfo.getCompanyName());
    }

    @Override
    public JobInfoVO getCompanyJob(Long companyUserId, Long id) {
        JobInfo jobInfo = getOwnJob(companyUserId, id);
        CompanyInfo companyInfo = companyInfoService.getById(jobInfo.getCompanyId());
        return toVO(jobInfo, companyInfo == null ? null : companyInfo.getCompanyName());
    }

    @Override
    public PageResult<JobInfoVO> pageCompanyJobs(Long companyUserId, Long current, Long size, String keyword,
                                                 String auditStatus, String publishStatus) {
        validateOptionalStatus(auditStatus, publishStatus);
        Page<JobInfo> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<JobInfo>()
                        .eq(JobInfo::getCompanyUserId, companyUserId)
                        .eq(StringUtils.hasText(auditStatus), JobInfo::getAuditStatus, auditStatus)
                        .eq(StringUtils.hasText(publishStatus), JobInfo::getPublishStatus, publishStatus)
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(JobInfo::getJobName, keyword)
                                .or()
                                .like(JobInfo::getCity, keyword))
                        .orderByDesc(JobInfo::getUpdatedAt));
        return toPageResult(page, getCompanyMap(page));
    }

    @Override
    public PageResult<JobInfoVO> pageAdminJobs(Long current, Long size, String keyword, String auditStatus,
                                               String publishStatus) {
        validateOptionalStatus(auditStatus, publishStatus);
        Page<JobInfo> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<JobInfo>()
                        .eq(StringUtils.hasText(auditStatus), JobInfo::getAuditStatus, auditStatus)
                        .eq(StringUtils.hasText(publishStatus), JobInfo::getPublishStatus, publishStatus)
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(JobInfo::getJobName, keyword)
                                .or()
                                .like(JobInfo::getCity, keyword)
                                .or()
                                .like(JobInfo::getEducation, keyword))
                        .orderByAsc(JobInfo::getAuditStatus)
                        .orderByDesc(JobInfo::getUpdatedAt));
        return toPageResult(page, getCompanyMap(page));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobInfoVO audit(Long id, JobAuditRequest request) {
        JobInfo jobInfo = getById(id);
        if (jobInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "岗位不存在");
        }

        JobAuditStatus status = JobAuditStatus.from(request.getAuditStatus());
        jobInfo.setAuditStatus(status.name());
        jobInfo.setAuditRemark(request.getAuditRemark());
        updateById(jobInfo);
        CompanyInfo companyInfo = companyInfoService.getById(jobInfo.getCompanyId());
        return toVO(jobInfo, companyInfo == null ? null : companyInfo.getCompanyName());
    }

    @Override
    public PageResult<JobInfoVO> pageStudentJobs(Long current, Long size, String jobName, String city,
                                                 BigDecimal minSalary, BigDecimal maxSalary, String education) {
        if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "最低薪资不能大于最高薪资");
        }

        Page<JobInfo> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<JobInfo>()
                        .eq(JobInfo::getAuditStatus, JobAuditStatus.APPROVED.name())
                        .eq(JobInfo::getPublishStatus, JobPublishStatus.ONLINE.name())
                        .like(StringUtils.hasText(jobName), JobInfo::getJobName, jobName)
                        .like(StringUtils.hasText(city), JobInfo::getCity, city)
                        .eq(StringUtils.hasText(education), JobInfo::getEducation, education)
                        .ge(minSalary != null, JobInfo::getSalaryMax, minSalary)
                        .le(maxSalary != null, JobInfo::getSalaryMin, maxSalary)
                        .orderByDesc(JobInfo::getUpdatedAt));
        return toPageResult(page, getCompanyMap(page));
    }

    @Override
    public JobInfoVO getStudentJob(Long id) {
        JobInfo jobInfo = getOne(new LambdaQueryWrapper<JobInfo>()
                .eq(JobInfo::getId, id)
                .eq(JobInfo::getAuditStatus, JobAuditStatus.APPROVED.name())
                .eq(JobInfo::getPublishStatus, JobPublishStatus.ONLINE.name())
                .last("LIMIT 1"));
        if (jobInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "岗位不存在或未发布");
        }
        CompanyInfo companyInfo = companyInfoService.getById(jobInfo.getCompanyId());
        return toVO(jobInfo, companyInfo == null ? null : companyInfo.getCompanyName());
    }

    private CompanyInfo getApprovedCompanyInfo(Long companyUserId) {
        CompanyInfo companyInfo = companyInfoService.getOne(new LambdaQueryWrapper<CompanyInfo>()
                .eq(CompanyInfo::getUserId, companyUserId)
                .last("LIMIT 1"));
        if (companyInfo == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请先完善企业基础信息");
        }
        if (!CompanyAuditStatus.APPROVED.name().equals(companyInfo.getAuditStatus())) {
            throw new BusinessException(403, "企业信息审核通过后才可以发布岗位");
        }
        return companyInfo;
    }

    private JobInfo getOwnJob(Long companyUserId, Long id) {
        JobInfo jobInfo = getOne(new LambdaQueryWrapper<JobInfo>()
                .eq(JobInfo::getId, id)
                .eq(JobInfo::getCompanyUserId, companyUserId)
                .last("LIMIT 1"));
        if (jobInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "岗位不存在");
        }
        return jobInfo;
    }

    private void validateSalaryRange(JobInfoRequest request) {
        if (request.getSalaryMin().compareTo(request.getSalaryMax()) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "最低薪资不能大于最高薪资");
        }
    }

    private void validateOptionalStatus(String auditStatus, String publishStatus) {
        if (StringUtils.hasText(auditStatus)) {
            JobAuditStatus.from(auditStatus);
        }
        if (StringUtils.hasText(publishStatus)) {
            JobPublishStatus.from(publishStatus);
        }
    }

    private void fillJobInfo(JobInfo jobInfo, JobInfoRequest request) {
        jobInfo.setJobName(request.getJobName());
        jobInfo.setCity(request.getCity());
        jobInfo.setJobType(request.getJobType());
        jobInfo.setSalaryMin(request.getSalaryMin());
        jobInfo.setSalaryMax(request.getSalaryMax());
        jobInfo.setEducation(request.getEducation());
        jobInfo.setExperience(request.getExperience());
        jobInfo.setHeadcount(request.getHeadcount());
        jobInfo.setJobDescription(request.getJobDescription());
        jobInfo.setJobRequirement(request.getJobRequirement());
        jobInfo.setWelfare(request.getWelfare());
        jobInfo.setContactPerson(request.getContactPerson());
        jobInfo.setContactPhone(request.getContactPhone());
    }

    private PageResult<JobInfoVO> toPageResult(Page<JobInfo> page, Map<Long, CompanyInfo> companyMap) {
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getPages(),
                page.getRecords().stream()
                        .map(job -> toVO(job, getCompanyName(companyMap, job.getCompanyId())))
                        .toList()
        );
    }

    private Map<Long, CompanyInfo> getCompanyMap(Page<JobInfo> page) {
        Set<Long> companyIds = page.getRecords().stream().map(JobInfo::getCompanyId).collect(Collectors.toSet());
        if (companyIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return companyInfoService.listByIds(companyIds).stream()
                .collect(Collectors.toMap(CompanyInfo::getId, Function.identity()));
    }

    private String getCompanyName(Map<Long, CompanyInfo> companyMap, Long companyId) {
        CompanyInfo companyInfo = companyMap.get(companyId);
        return companyInfo == null ? null : companyInfo.getCompanyName();
    }

    private JobInfoVO toVO(JobInfo jobInfo, String companyName) {
        return JobInfoVO.builder()
                .id(jobInfo.getId())
                .companyId(jobInfo.getCompanyId())
                .companyUserId(jobInfo.getCompanyUserId())
                .companyName(companyName)
                .jobName(jobInfo.getJobName())
                .city(jobInfo.getCity())
                .jobType(jobInfo.getJobType())
                .salaryMin(jobInfo.getSalaryMin())
                .salaryMax(jobInfo.getSalaryMax())
                .education(jobInfo.getEducation())
                .experience(jobInfo.getExperience())
                .headcount(jobInfo.getHeadcount())
                .jobDescription(jobInfo.getJobDescription())
                .jobRequirement(jobInfo.getJobRequirement())
                .welfare(jobInfo.getWelfare())
                .contactPerson(jobInfo.getContactPerson())
                .contactPhone(jobInfo.getContactPhone())
                .auditStatus(jobInfo.getAuditStatus())
                .auditRemark(jobInfo.getAuditRemark())
                .publishStatus(jobInfo.getPublishStatus())
                .createdAt(jobInfo.getCreatedAt())
                .updatedAt(jobInfo.getUpdatedAt())
                .build();
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
