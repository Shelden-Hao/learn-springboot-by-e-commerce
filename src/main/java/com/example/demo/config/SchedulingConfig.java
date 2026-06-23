package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 开启 Spring 定时任务功能
 *
 * @Configuration - 标记 Spring 配置类
 * @EnableScheduling → 告诉 Spring 扫描所有 @Scheduled 注解
 * 不加这个注解，下面写的定时任务永远不会执行
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

}
