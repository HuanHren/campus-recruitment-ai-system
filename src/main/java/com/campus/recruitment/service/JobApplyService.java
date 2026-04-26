package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.dto.ApplyStatusRequest;
import com.campus.recruitment.entity.JobApply;
import com.campus.recruitment.vo.JobApplyVO;

public interface JobApplyService extends IService<JobApply> {

    JobApplyVO apply(Long studentUserId, Long jobId);

    PageResult<JobApplyVO> pageStudentApplications(Long studentUserId, Long current, Long size, String applyStatus);

    PageResult<JobApplyVO> pageCompanyApplications(Long companyUserId, Long current, Long size, String applyStatus,
                                                   Long jobId, String keyword);

    JobApplyVO updateStatus(Long companyUserId, Long applyId, ApplyStatusRequest request);
}
