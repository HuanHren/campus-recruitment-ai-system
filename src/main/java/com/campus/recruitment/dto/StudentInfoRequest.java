package com.campus.recruitment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentInfoRequest {

    @NotBlank(message = "学号不能为空")
    @Size(max = 50, message = "学号不能超过50个字符")
    private String studentNo;

    @NotBlank(message = "学生姓名不能为空")
    @Size(max = 50, message = "学生姓名不能超过50个字符")
    private String name;

    @Size(max = 10, message = "性别不能超过10个字符")
    private String gender;

    @NotBlank(message = "学校不能为空")
    @Size(max = 100, message = "学校不能超过100个字符")
    private String school;

    @Size(max = 100, message = "学院不能超过100个字符")
    private String college;

    @NotBlank(message = "专业不能为空")
    @Size(max = 100, message = "专业不能超过100个字符")
    private String major;

    @Size(max = 20, message = "年级不能超过20个字符")
    private String grade;

    @NotBlank(message = "学历不能为空")
    @Size(max = 30, message = "学历不能超过30个字符")
    private String education;

    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @Size(max = 100, message = "期望岗位不能超过100个字符")
    private String expectedJob;

    @Size(max = 1000, message = "个人简介不能超过1000个字符")
    private String selfIntro;
}
