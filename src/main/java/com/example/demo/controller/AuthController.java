package com.example.demo.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import jakarta.validation.Valid;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 — 注册 / 登录 / 登出 / 当前用户
 *
 * Sa-Token 核心思想：
 *   登录 → StpUtil.login(userId) → 生成 Token → 返回给前端
 *   后续请求 → 前端带 Token → SaInterceptor 拦截 → StpUtil.checkLogin() 校验
 *   退出 → StpUtil.logout() → Token 失效
 *
 * 前端类比：
 *   传统的 Session → Cookie → SessionStore
 *   Sa-Token    → Token → Redis/内存 → 无状态，不依赖 Cookie
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;

    public AuthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * POST /api/auth/register — 注册
     *
     * 流程：
     *   1. 校验用户名是否已存在
     *   2. BCrypt 加密密码 → 密文存入数据库
     *   3. 返回成功（不自动登录）
     *
     * BCrypt 的盐值（salt）自动生成并嵌入到密文中，
     * 所以相同的明文每次加密结果都不同——防彩虹表攻击
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest request) {
        // ① 查重
        User exist = userMapper.selectByUsername(request.getUsername());
        if (exist != null) {
            throw new RuntimeException("用户名已存在: " + request.getUsername());
        }

        // ② BCrypt 加密：明文 → $2a$10$...密文
        String encodedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // ③ 入库
        User user = new User(
                request.getUsername(),
                encodedPassword,
                request.getNickname() != null ? request.getNickname() : request.getUsername(),
                UserRole.USER
        );
        userMapper.insert(user);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "注册成功");
        result.put("userId", user.getId());
        return result;
    }

    /**
     * ┌──────────────────────────────────────────────────┐
     * │  StpUtil.login(user.getId())                     │
     * │                                                  │
     * │  1. 生成随机 Token： "satoken: a3f8c2..."         │
     * │  2. 写入存储（内存/Redis）：Token → userId = 1     │
     * │  3. 返回 Token 给前端                              │
     * │                                                  │
     * │  后续请求：                                        │
     * │  前端 Header: { satoken: "a3f8c2..." }            │
     * │      ↓                                           │
     * │  SaInterceptor 拦截 → 从存储查 Token → 找到 userId │
     * │      ↓                                           │
     * │  StpUtil.getLoginIdAsLong() → 1                  │
     * └──────────────────────────────────────────────────┘
     */

    /**
     * POST /api/auth/login — 登录
     *
     * 流程：
     *   1. 根据用户名查出用户
     *   2. BCrypt.checkpw(明文, 密文) → 比对密码
     *   3. StpUtil.login(userId) → Sa-Token 生成 Token 并写入 Redis/内存
     *   4. 返回 tokenInfo（包括 token 值、过期时间等）
     *
     * StpUtil.login() 做了什么？
     *   - 生成一个随机 Token（如 "a1b2c3..."）
     *   - 在 Redis/内存中建立 Token → userId 的映射
     *   - 设置过期时间（默认 30 天，可配置）
     */
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        // ① 查用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // ② 验密码：BCrypt.checkpw(用户输入的明文, 数据库里的密文)
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // ③ Sa-Token 登录：生成 Token，建立会话
        StpUtil.login(user.getId());
        // 这样 Sa-Token 的 @SaCheckRole("ADMIN") 才能识别
        // 注意：这里不能存 user.getRole() 枚举值，因为 Sa-Token 的 @SaCheckRole("ADMIN") 内部会 "ADMIN".equals(session.get("role"))，这里必须存 ADMIN 字符串
        StpUtil.getSession().set("role", user.getRole().name());

        // ④ 返回 Token 信息给前端
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "登录成功");
        result.put("tokenName", tokenInfo.getTokenName());   // 默认 "satoken"
        result.put("tokenValue", tokenInfo.getTokenValue()); // 实际的 Token 值
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        return result;
    }

    /**
     * POST /api/auth/logout — 登出
     *
     * StpUtil.logout() → 删除 Token，会话失效
     * 前端收到响应后把 Token 从 localStorage 中删掉
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        StpUtil.logout();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "已退出登录");
        return result;
    }

    /**
     * GET /api/auth/me — 获取当前登录用户信息
     *
     * StpUtil.getLoginIdAsLong() → 从当前请求的 Token 中解析出 userId
     * 不需要前端传 userId——Token 本身就包含了身份信息
     */
    @GetMapping("/me")
    public User me() {
        long userId = StpUtil.getLoginIdAsLong();
        return userMapper.selectById(userId);
    }
}
