package com.campus.recruitment.service.impl;

import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.ResultCode;
import com.campus.recruitment.dto.LoginRequest;
import com.campus.recruitment.dto.RegisterRequest;
import com.campus.recruitment.entity.SysUser;
import com.campus.recruitment.entity.UserRole;
import com.campus.recruitment.security.JwtUtil;
import com.campus.recruitment.service.AuthService;
import com.campus.recruitment.service.SysUserService;
import com.campus.recruitment.vo.LoginVO;
import com.campus.recruitment.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (sysUserService.existsByUsername(username)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名已存在");
        }

        UserRole role = UserRole.from(request.getRole());
        if (role == UserRole.ADMIN) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不允许通过注册接口创建管理员账号");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName().trim());
        user.setRole(role.name());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setDeleted(0);
        sysUserService.save(user);
        return toUserVO(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        SysUser user = sysUserService.getByUsername(request.getUsername().trim());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user);
        return LoginVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpireSeconds())
                .user(toUserVO(user))
                .build();
    }

    private UserVO toUserVO(SysUser user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }
}
