package com.campus.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    boolean existsByUsername(String username);
}
