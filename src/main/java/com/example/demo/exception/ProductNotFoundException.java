package com.example.demo.exception;

/**
 * 商品不存在异常 — 语义化的自定义异常
 *
 * 为什么不用 RuntimeException？
 *   RuntimeException 太通用了，"网络超时"和"商品不存在"都是它，
 *   @ExceptionHandler 无法区分处理。
 *
 *   自定义异常 → 精确捕获 → 返回不同的 HTTP 状态码和错误信息。
 *
 * 前端类比：
 *   class ProductNotFoundError extends Error {
 *     constructor(id) { super(`Product ${id} not found`); }
 *   }
 */
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("商品不存在: id=" + productId);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
