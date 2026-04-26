package com.campus.recruitment.entity;

import java.util.Arrays;

public enum JobPublishStatus {

    ONLINE("已发布"),
    OFFLINE("已下架");

    private final String description;

    JobPublishStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static JobPublishStatus from(String status) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("岗位发布状态不正确"));
    }
}
