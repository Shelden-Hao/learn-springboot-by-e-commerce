package com.example.demo.exception;

import cn.dev33.satoken.exception.NotRoleException;
import com.example.demo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器 — 拦截所有 Controller 抛出的异常，返回统一 JSON 结构
 *
 * 核心注解：
 *   @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   → 拦截所有 @Controller / @RestController 抛出的异常
 *   → 返回值自动序列化为 JSON
 *
 * 前端类比：Express 的全局错误中间件
 *   app.use((err, req, res, next) => {
 *     res.status(500).json({ message: err.message });
 *   });
 *
 * 只不过 Spring 按异常类型精细路由，而非一个笼统的 catch-all
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *                       Controller 抛异常
     *                             │
     *                             ▼
     *               ┌─────────────────────────────┐
     *               │   GlobalExceptionHandler     │
     *               │                             │
     *               │  ProductNotFoundException    │  ──→  404 + 结构化 JSON
     *               │   → handleProductNotFound()  │
     *               │                             │
     *               │  MethodArgumentNotValidException │
     *               │   → handleValidationFailed() │  ──→  400 + 字段错误列表
     *               │                             │
     *               │  Exception（兜底）            │
     *               │   → handleUnknown()           │  ──→  500
     *               └─────────────────────────────┘
     */

    /**
     * 捕获 ProductNotFoundException → 返回 404
     *
     * @ExceptionHandler 按异常类型路由，类似 try-catch 中不同的 catch 块
     * @ResponseStatus     设置 HTTP 响应状态码
     */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    public ErrorResponse handleProductNotFound(ProductNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }

    /**
     * 捕获 Sa-Token 未登录异常 → 返回 401
     */
    @ExceptionHandler(cn.dev33.satoken.exception.NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    public ErrorResponse handleNotLogin() {
        return new ErrorResponse(401, "请先登录");
    }

    /**
     * 捕获校验失败异常 @Valid 拦截的 → 返回 400 + 具体错误列表
     *
     * MethodArgumentNotValidException：
     *   当 @Valid 发现字段不合法时 Spring 抛出此异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    public ErrorResponse handleValidationFailed(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        return new ErrorResponse(400, "请求参数校验失败", errors);
    }

    /**
     * 非法参数异常 — 例如 toggleStatus 的 action 传了 on/off 以外的值
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorResponse(400, ex.getMessage());
    }

    /**
     * 兜底处理器 — 捕获所有未被上面精确处理的异常 → 返回 500
     *
     * 注意：生产环境不要返回 e.getMessage()，
     *      应该返回"服务器内部错误"，把真实原因写日志。
     *      当前是学习阶段，返回异常信息方便调试。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    public ErrorResponse handleUnknown(Exception ex) {
        return new ErrorResponse(500, "服务器内部错误: " + ex.getMessage());
    }

    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNotRole() {
        return new ErrorResponse(403, "权限不足");
    }
}
