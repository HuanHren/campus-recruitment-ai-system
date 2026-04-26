package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_info")
public class StudentInfo {

    @TableId
    private Long id;

    private Long userId;

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

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
