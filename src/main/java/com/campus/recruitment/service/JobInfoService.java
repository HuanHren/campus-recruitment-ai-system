package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.dto.JobAuditRequest;
import com.campus.recruitment.dto.JobInfoRequest;
import com.campus.recruitment.entity.JobInfo;
import com.campus.recruitment.vo.JobInfoVO;

import java.math.BigDecimal;

public interface JobInfoService extends IService<JobInfo> {

    JobInfoVO create(Long companyUserId, JobInfoRequest request);

    JobInfoVO update(Long companyUserId, Long id, JobInfoRequest request);

    JobInfoVO offline(Long companyUserId, Long id);

    JobInfoVO getCompanyJob(Long companyUserId, Long id);

    PageResult<JobInfoVO> pageCompanyJobs(Long companyUserId, Long current, Long size, String keyword,
                                          String auditStatus, String publishStatus);

    PageResult<JobInfoVO> pageAdminJobs(Long current, Long size, String keyword, String auditStatus,
                                        String publishStatus);

    JobInfoVO audit(Long id, JobAuditRequest request);

    PageResult<JobInfoVO> pageStudentJobs(Long current, Long size, String jobName, String city,
                                          BigDecimal minSalary, BigDecimal maxSalary, String education);

    JobInfoVO getStudentJob(Long id);
}
