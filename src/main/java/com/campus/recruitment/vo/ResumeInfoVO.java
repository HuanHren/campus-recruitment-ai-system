package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResumeInfoVO {

    private Long id;

    private Long studentUserId;

    private String resumeName;

    private String realName;

    private String gender;

    private String phone;

    private String email;

    private String school;

    private String major;

    private String education;

    private String graduationYear;

    private String expectedPosition;

    private String expectedCity;

    private String skills;

    private String projectExperience;

    private String internshipExperience;

    private String selfEvaluation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
