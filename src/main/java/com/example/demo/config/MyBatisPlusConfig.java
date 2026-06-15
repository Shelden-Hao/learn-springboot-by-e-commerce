package com.example.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 *
 * 核心：注册分页插件 — 不配这个，分页查询不会自动加 LIMIT + COUNT
 *
 * 前端类比：Vue.use(VueRouter) — 注册插件让框架识别并处理分页请求
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件：自动拦截分页查询，添加 LIMIT ? OFFSET ? 和 SELECT COUNT(*)
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 溢出处理：page 太大时回到首页，避免空结果
        pagination.setOverflow(true);
        // 单页最大 100 条，防止恶意请求
        pagination.setMaxLimit(100L);

        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
