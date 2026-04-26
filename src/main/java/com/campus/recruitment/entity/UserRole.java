package com.campus.recruitment.entity;

import java.util.Arrays;

public enum UserRole {

    ADMIN("管理员"),
    STUDENT("学生"),
    COMPANY("企业"),
    TEACHER("就业老师");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole from(String role) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("角色类型不正确"));
    }
}
