package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * 密码存储的铁律：永不明文存密码！
 * 注册时 BCrypt 加密 → 数据库存密文
 * 登录时 BCrypt 比对 → 明文 vs 密文
 */
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")     // BCrypt 密文，不是明文！
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("role")
    private UserRole role;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public User() {}

    public User(String username, String password, String nickname, UserRole role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    // ========== Getter / Setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
