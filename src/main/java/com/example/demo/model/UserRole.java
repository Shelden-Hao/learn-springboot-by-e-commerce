package com.example.demo.model;

public enum UserRole {
    // 类似于构造方法调用
    USER("普通用户"),
    ADMIN("管理员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
