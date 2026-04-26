package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.LoginRequest;
import com.campus.recruitment.dto.RegisterRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.AuthService;
import com.campus.recruitment.vo.LoginVO;
import com.campus.recruitment.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录注册")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public Result<UserVO> me(@AuthenticationPrincipal SecurityUser securityUser) {
        return Result.success(securityUser.toUserVO());
    }
}
