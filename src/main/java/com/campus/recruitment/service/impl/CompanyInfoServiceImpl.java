package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.CompanyAuditRequest;
import com.campus.recruitment.dto.CompanyInfoRequest;
import com.campus.recruitment.entity.CompanyAuditStatus;
import com.campus.recruitment.entity.CompanyInfo;
import com.campus.recruitment.entity.SysUser;
import com.campus.recruitment.mapper.CompanyInfoMapper;
import com.campus.recruitment.service.CompanyInfoService;
import com.campus.recruitment.service.SysUserService;
import com.campus.recruitment.vo.CompanyInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoMapper, CompanyInfo> implements CompanyInfoService {

    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoVO create(Long userId, String username, CompanyInfoRequest request) {
        if (getByUserId(userId) != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "企业信息已存在，请使用修改接口");
        }

        CompanyInfo info = new CompanyInfo();
        info.setUserId(userId);
        fillInfo(info, request);
        info.setAuditStatus(CompanyAuditStatus.PENDING.name());
        info.setDeleted(0);
        save(info);
        return toVO(info, username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoVO update(Long userId, String username, CompanyInfoRequest request) {
        CompanyInfo info = getByUserId(userId);
        if (info == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "请先新增企业信息");
        }

        fillInfo(info, request);
        // 企业资料变更后需要重新审核。
        info.setAuditStatus(CompanyAuditStatus.PENDING.name());
        info.setAuditRemark(null);
        updateById(info);
        return toVO(info, username);
    }

    @Override
    public CompanyInfoVO getCurrent(Long userId, String username) {
        CompanyInfo info = getByUserId(userId);
        if (info == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "企业信息不存在");
        }
        return toVO(info, username);
    }

    @Override
    public PageResult<CompanyInfoVO> pageForAdmin(Long current, Long size, String keyword, String auditStatus) {
        if (StringUtils.hasText(auditStatus)) {
            CompanyAuditStatus.from(auditStatus);
        }

        Page<CompanyInfo> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<CompanyInfo>()
                        .eq(StringUtils.hasText(auditStatus), CompanyInfo::getAuditStatus, auditStatus)
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(CompanyInfo::getCompanyName, keyword)
                                .or()
                                .like(CompanyInfo::getIndustry, keyword)
                                .or()
                                .like(CompanyInfo::getContactPerson, keyword))
                        .orderByAsc(CompanyInfo::getAuditStatus)
                        .orderByDesc(CompanyInfo::getUpdatedAt));

        Map<Long, SysUser> userMap = getUserMap(page);
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getPages(),
                page.getRecords().stream()
                        .map(info -> toVO(info, getUsername(userMap, info.getUserId())))
                        .toList()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoVO updateAuditStatus(Long id, CompanyAuditRequest request) {
        CompanyInfo info = getById(id);
        if (info == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "企业信息不存在");
        }

        CompanyAuditStatus status = CompanyAuditStatus.from(request.getAuditStatus());
        info.setAuditStatus(status.name());
        info.setAuditRemark(request.getAuditRemark());
        updateById(info);

        SysUser user = sysUserService.getById(info.getUserId());
        return toVO(info, user == null ? null : user.getUsername());
    }

    private CompanyInfo getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<CompanyInfo>()
                .eq(CompanyInfo::getUserId, userId)
                .last("LIMIT 1"));
    }

    private void fillInfo(CompanyInfo info, CompanyInfoRequest request) {
        info.setCompanyName(request.getCompanyName());
        info.setUnifiedSocialCreditCode(request.getUnifiedSocialCreditCode());
        info.setIndustry(request.getIndustry());
        info.setCompanyScale(request.getCompanyScale());
        info.setAddress(request.getAddress());
        info.setContactPerson(request.getContactPerson());
        info.setContactPhone(request.getContactPhone());
        info.setContactEmail(request.getContactEmail());
        info.setDescription(request.getDescription());
    }

    private CompanyInfoVO toVO(CompanyInfo info, String username) {
        return CompanyInfoVO.builder()
                .id(info.getId())
                .userId(info.getUserId())
                .username(username)
                .companyName(info.getCompanyName())
                .unifiedSocialCreditCode(info.getUnifiedSocialCreditCode())
                .industry(info.getIndustry())
                .companyScale(info.getCompanyScale())
                .address(info.getAddress())
                .contactPerson(info.getContactPerson())
                .contactPhone(info.getContactPhone())
                .contactEmail(info.getContactEmail())
                .description(info.getDescription())
                .auditStatus(info.getAuditStatus())
                .auditRemark(info.getAuditRemark())
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
                .build();
    }

    private Map<Long, SysUser> getUserMap(Page<CompanyInfo> page) {
        Set<Long> userIds = page.getRecords().stream().map(CompanyInfo::getUserId).collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return sysUserService.listByIds(userIds).stream().collect(Collectors.toMap(SysUser::getId, Function.identity()));
    }

    private String getUsername(Map<Long, SysUser> userMap, Long userId) {
        SysUser user = userMap.get(userId);
        return user == null ? null : user.getUsername();
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
