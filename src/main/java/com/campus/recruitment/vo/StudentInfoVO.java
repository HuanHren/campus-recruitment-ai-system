package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudentInfoVO {

    private Long id;

    private Long userId;

    private String username;

    private String studentNo;

    private String name;

    private String gender;

    private String school;

    private String college;

    private String major;

    private String grade;

    private String education;

    private String phone;

    private String email;

    private String expectedJob;

    private String selfIntro;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
