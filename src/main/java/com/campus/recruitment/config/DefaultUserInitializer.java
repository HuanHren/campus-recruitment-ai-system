package com.campus.recruitment.config;

import com.campus.recruitment.entity.SysUser;
import com.campus.recruitment.entity.UserRole;
import com.campus.recruitment.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements ApplicationRunner {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultUser("admin", "管理员", UserRole.ADMIN);
        createDefaultUser("student", "学生用户", UserRole.STUDENT);
        createDefaultUser("company", "企业用户", UserRole.COMPANY);
        createDefaultUser("teacher", "就业老师", UserRole.TEACHER);
    }

    private void createDefaultUser(String username, String realName, UserRole role) {
        if (sysUserService.existsByUsername(username)) {
            return;
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setRole(role.name());
        user.setStatus(1);
        user.setDeleted(0);
        sysUserService.save(user);
    }
}
