package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.dto.CompanyAuditRequest;
import com.campus.recruitment.dto.CompanyInfoRequest;
import com.campus.recruitment.entity.CompanyInfo;
import com.campus.recruitment.vo.CompanyInfoVO;

public interface CompanyInfoService extends IService<CompanyInfo> {

    CompanyInfoVO create(Long userId, String username, CompanyInfoRequest request);

    CompanyInfoVO update(Long userId, String username, CompanyInfoRequest request);

    CompanyInfoVO getCurrent(Long userId, String username);

    PageResult<CompanyInfoVO> pageForAdmin(Long current, Long size, String keyword, String auditStatus);

    CompanyInfoVO updateAuditStatus(Long id, CompanyAuditRequest request);
}
