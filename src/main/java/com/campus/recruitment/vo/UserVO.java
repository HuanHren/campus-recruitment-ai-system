package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {

    private Long id;

    private String username;

    private String realName;

    private String role;

    private String phone;

    private String email;

    private Integer status;
}
