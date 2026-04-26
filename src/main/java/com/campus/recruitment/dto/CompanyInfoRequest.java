package com.campus.recruitment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyInfoRequest {

    @NotBlank(message = "企业名称不能为空")
    @Size(max = 100, message = "企业名称不能超过100个字符")
    private String companyName;

    @Size(max = 50, message = "统一社会信用代码不能超过50个字符")
    private String unifiedSocialCreditCode;

    @Size(max = 100, message = "所属行业不能超过100个字符")
    private String industry;

    @Size(max = 50, message = "企业规模不能超过50个字符")
    private String companyScale;

    @Size(max = 255, message = "企业地址不能超过255个字符")
    private String address;

    @NotBlank(message = "联系人不能为空")
    @Size(max = 50, message = "联系人不能超过50个字符")
    private String contactPerson;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;

    @Email(message = "联系邮箱格式不正确")
    @Size(max = 100, message = "联系邮箱不能超过100个字符")
    private String contactEmail;

    @Size(max = 1500, message = "企业简介不能超过1500个字符")
    private String description;
}
