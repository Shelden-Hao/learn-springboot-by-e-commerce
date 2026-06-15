package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一错误响应体 — 前端收到的所有错误都是这个结构
 *
 * 之前：抛 RuntimeException → Spring 返回 HTML 500 页面，前端 get 不到有用信息
 * 现在：所有异常 → GlobalExceptionHandler 拦截 → 返回这个 JSON
 *
 * 前端收到：
 * {
 *   "status": 404,
 *   "message": "商品不存在: id=999",
 *   "timestamp": "2026-06-15T10:50:00",
 *   "errors": null
 * }
 */
public class ErrorResponse {

    private int status;              // HTTP 状态码
    private String message;          // 人类可读的错误描述
    private LocalDateTime timestamp; // 发生时间
    private List<String> errors;     // 字段级校验错误列表（可选）

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message, List<String> errors) {
        this(status, message);
        this.errors = errors;
    }

    // ========== Getter ==========

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<String> getErrors() { return errors; }
}
