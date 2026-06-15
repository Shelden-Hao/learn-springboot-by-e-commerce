package com.example.demo.model;

/**
 * 商品状态枚举 — 比 String / int 安全得多
 *
 * 为什么不用 String status = "on_sale"？
 *   - 拼写错误 "on_sale" vs "onsale" → 运行时才发现
 *   - 没法限制取值 → status = "哈哈哈哈" 也能过编译
 *   - IDE 没提示 → 每次都要查文档
 *
 * 枚举的优势：
 *   - 编译期检查：写 ProductStatus.ON_SALE 拼错了 IDE 直接标红
 *   - 取值受限：只能是 ON_SALE 或 OFF_SHELF，没有第三种可能
 *   - 数据库映射：@Enumerated(STRING) → 存 "ON_SALE" / "OFF_SHELF"
 *
 * 前端类比：TypeScript 的 union type
 *   type ProductStatus = 'ON_SALE' | 'OFF_SHELF'
 */
public enum ProductStatus {

    ON_SALE("在售"),
    OFF_SHELF("已下架");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
