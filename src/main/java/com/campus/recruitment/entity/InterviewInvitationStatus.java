package com.campus.recruitment.entity;

import java.util.Arrays;

public enum InterviewInvitationStatus {

    PENDING("待确认"),
    ACCEPTED("已接受"),
    REJECTED("已拒绝");

    private final String description;

    InterviewInvitationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static InterviewInvitationStatus from(String status) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("面试邀请状态不正确"));
    }
}
