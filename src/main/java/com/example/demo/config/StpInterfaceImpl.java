package com.example.demo.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限扩展接口 — 必须实现，否则角色校验永远不通过
 *
 * Sa-Token 的 @SaCheckRole / @SaCheckPermission 注解
 * 最终都会调到这里来查用户的角色和权限列表
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回用户的角色列表
     *
     * loginId  → 即 StpUtil.login() 时传入的 userId
     * 从该用户的 SaSession 中取出之前存的 "role" 字段
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 根据 loginId 获取该用户的 Session
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        String role = (String) session.get("role");

        List<String> roles = new ArrayList<>();
        if (role != null) {
            roles.add(role);
        }
        return roles;
    }

    /**
     * 返回用户的权限码列表（本项目暂不需要权限，只做角色校验）
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }
}
