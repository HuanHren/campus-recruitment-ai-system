package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("resume_info")
public class ResumeInfo {

    @TableId
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

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
