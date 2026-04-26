package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.StudentInfoRequest;
import com.campus.recruitment.entity.StudentInfo;
import com.campus.recruitment.entity.SysUser;
import com.campus.recruitment.mapper.StudentInfoMapper;
import com.campus.recruitment.service.StudentInfoService;
import com.campus.recruitment.service.SysUserService;
import com.campus.recruitment.vo.StudentInfoVO;
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
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentInfoVO create(Long userId, String username, StudentInfoRequest request) {
        if (getByUserId(userId) != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "学生信息已存在，请使用修改接口");
        }
        validateStudentNo(request.getStudentNo(), null);

        StudentInfo info = new StudentInfo();
        info.setUserId(userId);
        fillInfo(info, request);
        info.setDeleted(0);
        save(info);
        return toVO(info, username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentInfoVO update(Long userId, String username, StudentInfoRequest request) {
        StudentInfo info = getByUserId(userId);
        if (info == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "请先新增学生信息");
        }
        validateStudentNo(request.getStudentNo(), info.getId());

        fillInfo(info, request);
        updateById(info);
        return toVO(info, username);
    }

    @Override
    public StudentInfoVO getCurrent(Long userId, String username) {
        StudentInfo info = getByUserId(userId);
        if (info == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "学生信息不存在");
        }
        return toVO(info, username);
    }

    @Override
    public PageResult<StudentInfoVO> pageForAdmin(Long current, Long size, String keyword) {
        Page<StudentInfo> page = page(Page.of(normalizeCurrent(current), normalizeSize(size)),
                new LambdaQueryWrapper<StudentInfo>()
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(StudentInfo::getName, keyword)
                                .or()
                                .like(StudentInfo::getStudentNo, keyword)
                                .or()
                                .like(StudentInfo::getSchool, keyword)
                                .or()
                                .like(StudentInfo::getMajor, keyword))
                        .orderByDesc(StudentInfo::getUpdatedAt));

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

    private StudentInfo getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getUserId, userId)
                .last("LIMIT 1"));
    }

    private void validateStudentNo(String studentNo, Long currentId) {
        StudentInfo exists = getOne(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getStudentNo, studentNo)
                .last("LIMIT 1"));
        if (exists != null && !exists.getId().equals(currentId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "学号已存在");
        }
    }

    private void fillInfo(StudentInfo info, StudentInfoRequest request) {
        info.setStudentNo(request.getStudentNo());
        info.setName(request.getName());
        info.setGender(request.getGender());
        info.setSchool(request.getSchool());
        info.setCollege(request.getCollege());
        info.setMajor(request.getMajor());
        info.setGrade(request.getGrade());
        info.setEducation(request.getEducation());
        info.setPhone(request.getPhone());
        info.setEmail(request.getEmail());
        info.setExpectedJob(request.getExpectedJob());
        info.setSelfIntro(request.getSelfIntro());
    }

    private StudentInfoVO toVO(StudentInfo info, String username) {
        return StudentInfoVO.builder()
                .id(info.getId())
                .userId(info.getUserId())
                .username(username)
                .studentNo(info.getStudentNo())
                .name(info.getName())
                .gender(info.getGender())
                .school(info.getSchool())
                .college(info.getCollege())
                .major(info.getMajor())
                .grade(info.getGrade())
                .education(info.getEducation())
                .phone(info.getPhone())
                .email(info.getEmail())
                .expectedJob(info.getExpectedJob())
                .selfIntro(info.getSelfIntro())
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
                .build();
    }

    private Map<Long, SysUser> getUserMap(Page<StudentInfo> page) {
        Set<Long> userIds = page.getRecords().stream().map(StudentInfo::getUserId).collect(Collectors.toSet());
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
