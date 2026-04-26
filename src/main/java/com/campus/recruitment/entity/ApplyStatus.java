package com.campus.recruitment.entity;

import java.util.Arrays;

public enum ApplyStatus {

    PENDING("待查看"),
    VIEWED("已查看"),
    INTERVIEW("邀请面试"),
    REJECTED("已拒绝");

    private final String description;

    ApplyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ApplyStatus from(String status) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("投递状态不正确"));
    }
}
