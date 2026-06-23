package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect // 切面
@Component // 交给 Spring 容器
public class RequestLogAspect {
    /**
     * @Around: 环绕通知 — 在方法执行前后各插入一段代码
     *
     * "execution(* com.example.demo.controller..*.*(..))"
     *   *               → 任意返回值
     *   ..controller..  → controller 包及其子包下的
     *   *               → 任意类
     *   *(..)           → 任意方法的任意参数
     *
     * 翻译：拦截 controller 包下所有类的所有方法
     */
    @Around("execution(* com.example.demo.controller..*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // ① 获取当前 http 请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String method = "";
        String path = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            method = request.getMethod();  // GET / POST / PUT / DELETE
            path = request.getRequestURI(); // /api/products/1
        }
        // ② 记录开始时间
        long start = System.currentTimeMillis();

        // ③ 执行真正的业务方法（controller 里的代码）
        Object result = joinPoint.proceed();

        // ④ 计算耗时并打印
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(method + " " + path + " → 用时 " + elapsed + "ms");

        return result;
    }
}
