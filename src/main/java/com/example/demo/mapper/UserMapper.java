package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 *
 * 演示 @Select 注解 SQL — 另一种写 SQL 的方式（第三选择）
 * 查询优先级：BaseMapper 能搞定的 → LambdaQueryWrapper 拼接 → XML → @Select 注解
 *
 * 实际上这里完全能用 LambdaQueryWrapper 替代，写成注解 SQL 只是为了教学演示
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 按用户名查用户（登录时用） */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
}
