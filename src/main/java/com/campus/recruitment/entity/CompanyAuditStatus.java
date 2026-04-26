package com.campus.recruitment.entity;

import java.util.Arrays;

public enum CompanyAuditStatus {

    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("已驳回");

    private final String description;

    CompanyAuditStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static CompanyAuditStatus from(String status) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("企业审核状态不正确"));
    }
}
