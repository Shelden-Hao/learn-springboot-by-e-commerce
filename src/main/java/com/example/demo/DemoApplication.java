package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    // 启动入口，嵌入式 Tomcat，相当于前端 npm run start 启动开发服务器
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
