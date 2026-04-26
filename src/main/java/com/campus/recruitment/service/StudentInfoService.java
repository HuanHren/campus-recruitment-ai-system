package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.dto.StudentInfoRequest;
import com.campus.recruitment.entity.StudentInfo;
import com.campus.recruitment.vo.StudentInfoVO;

public interface StudentInfoService extends IService<StudentInfo> {

    StudentInfoVO create(Long userId, String username, StudentInfoRequest request);

    StudentInfoVO update(Long userId, String username, StudentInfoRequest request);

    StudentInfoVO getCurrent(Long userId, String username);

    PageResult<StudentInfoVO> pageForAdmin(Long current, Long size, String keyword);
}
