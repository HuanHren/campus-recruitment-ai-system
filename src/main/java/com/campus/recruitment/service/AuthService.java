package com.campus.recruitment.service;

import com.campus.recruitment.dto.LoginRequest;
import com.campus.recruitment.dto.RegisterRequest;
import com.campus.recruitment.vo.LoginVO;
import com.campus.recruitment.vo.UserVO;

public interface AuthService {

    UserVO register(RegisterRequest request);

    LoginVO login(LoginRequest request);
}
