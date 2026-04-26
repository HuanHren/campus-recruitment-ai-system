package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.ResumeInfoRequest;
import com.campus.recruitment.entity.ResumeInfo;
import com.campus.recruitment.mapper.ResumeInfoMapper;
import com.campus.recruitment.service.ResumeInfoService;
import com.campus.recruitment.vo.ResumeInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResumeInfoServiceImpl extends ServiceImpl<ResumeInfoMapper, ResumeInfo> implements ResumeInfoService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResumeInfoVO create(Long studentUserId, ResumeInfoRequest request) {
        if (getByStudentUserId(studentUserId) != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "简历已存在，请使用修改接口");
        }

        ResumeInfo resumeInfo = new ResumeInfo();
        resumeInfo.setStudentUserId(studentUserId);
        fillResumeInfo(resumeInfo, request);
        resumeInfo.setDeleted(0);
        save(resumeInfo);
        return toVO(resumeInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResumeInfoVO update(Long studentUserId, ResumeInfoRequest request) {
        ResumeInfo resumeInfo = getByStudentUserId(studentUserId);
        if (resumeInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "请先新增简历");
        }

        fillResumeInfo(resumeInfo, request);
        updateById(resumeInfo);
        return toVO(resumeInfo);
    }

    @Override
    public ResumeInfoVO getCurrent(Long studentUserId) {
        ResumeInfo resumeInfo = getByStudentUserId(studentUserId);
        if (resumeInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        return toVO(resumeInfo);
    }

    @Override
    public ResumeInfo getByStudentUserId(Long studentUserId) {
        return getOne(new LambdaQueryWrapper<ResumeInfo>()
                .eq(ResumeInfo::getStudentUserId, studentUserId)
                .last("LIMIT 1"));
    }

    private void fillResumeInfo(ResumeInfo resumeInfo, ResumeInfoRequest request) {
        resumeInfo.setResumeName(request.getResumeName());
        resumeInfo.setRealName(request.getRealName());
        resumeInfo.setGender(request.getGender());
        resumeInfo.setPhone(request.getPhone());
        resumeInfo.setEmail(request.getEmail());
        resumeInfo.setSchool(request.getSchool());
        resumeInfo.setMajor(request.getMajor());
        resumeInfo.setEducation(request.getEducation());
        resumeInfo.setGraduationYear(request.getGraduationYear());
        resumeInfo.setExpectedPosition(request.getExpectedPosition());
        resumeInfo.setExpectedCity(request.getExpectedCity());
        resumeInfo.setSkills(request.getSkills());
        resumeInfo.setProjectExperience(request.getProjectExperience());
        resumeInfo.setInternshipExperience(request.getInternshipExperience());
        resumeInfo.setSelfEvaluation(request.getSelfEvaluation());
    }

    private ResumeInfoVO toVO(ResumeInfo resumeInfo) {
        return ResumeInfoVO.builder()
                .id(resumeInfo.getId())
                .studentUserId(resumeInfo.getStudentUserId())
                .resumeName(resumeInfo.getResumeName())
                .realName(resumeInfo.getRealName())
                .gender(resumeInfo.getGender())
                .phone(resumeInfo.getPhone())
                .email(resumeInfo.getEmail())
                .school(resumeInfo.getSchool())
                .major(resumeInfo.getMajor())
                .education(resumeInfo.getEducation())
                .graduationYear(resumeInfo.getGraduationYear())
                .expectedPosition(resumeInfo.getExpectedPosition())
                .expectedCity(resumeInfo.getExpectedCity())
                .skills(resumeInfo.getSkills())
                .projectExperience(resumeInfo.getProjectExperience())
                .internshipExperience(resumeInfo.getInternshipExperience())
                .selfEvaluation(resumeInfo.getSelfEvaluation())
                .createdAt(resumeInfo.getCreatedAt())
                .updatedAt(resumeInfo.getUpdatedAt())
                .build();
    }
}
