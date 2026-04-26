package com.campus.recruitment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeInfoRequest {

    @NotBlank(message = "简历名称不能为空")
    @Size(max = 100, message = "简历名称不能超过100个字符")
    private String resumeName;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String realName;

    @Size(max = 10, message = "性别不能超过10个字符")
    private String gender;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @NotBlank(message = "学校不能为空")
    @Size(max = 100, message = "学校不能超过100个字符")
    private String school;

    @NotBlank(message = "专业不能为空")
    @Size(max = 100, message = "专业不能超过100个字符")
    private String major;

    @NotBlank(message = "学历不能为空")
    @Size(max = 30, message = "学历不能超过30个字符")
    private String education;

    @Size(max = 20, message = "毕业年份不能超过20个字符")
    private String graduationYear;

    @Size(max = 100, message = "期望岗位不能超过100个字符")
    private String expectedPosition;

    @Size(max = 50, message = "期望城市不能超过50个字符")
    private String expectedCity;

    @Size(max = 1000, message = "专业技能不能超过1000个字符")
    private String skills;

    @Size(max = 2000, message = "项目经历不能超过2000个字符")
    private String projectExperience;

    @Size(max = 2000, message = "实习经历不能超过2000个字符")
    private String internshipExperience;

    @Size(max = 1000, message = "自我评价不能超过1000个字符")
    private String selfEvaluation;
}
