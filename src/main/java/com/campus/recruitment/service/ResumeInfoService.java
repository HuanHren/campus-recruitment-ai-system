package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.dto.ResumeInfoRequest;
import com.campus.recruitment.entity.ResumeInfo;
import com.campus.recruitment.vo.ResumeInfoVO;

public interface ResumeInfoService extends IService<ResumeInfo> {

    ResumeInfoVO create(Long studentUserId, ResumeInfoRequest request);

    ResumeInfoVO update(Long studentUserId, ResumeInfoRequest request);

    ResumeInfoVO getCurrent(Long studentUserId);

    ResumeInfo getByStudentUserId(Long studentUserId);
}
