package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建商品的请求体 — 只包含客户端需要提交的字段
 *
 * 为什么不用 Product 直接接收？这是后端非常重要的分层概念：
 *
 *   Product（Entity）    → 数据库表映射，包含 id、createdAt 等不需要用户传的字段
 *   CreateProductRequest  → 仅包含用户提交的字段 + 校验规则
 *
 * 混用 = 安全风险。假设 Product 有 isAdmin 字段，前端传 {"isAdmin": true}，
 * 如果你直接用 Product 接收就出事了。
 *
 * 前端类比：TypeScript 的 interface，但多了运行时校验能力
 */
public class CreateProductRequest {

    /**
     * POST /api/products
     * Body: { "name": "", "price": -5, "stock": null }
     *
     *         │
     *         ▼
     *   Spring 收到请求
     *         │
     *         ▼
     *   Jackson 将 JSON 反序列化为 CreateProductRequest
     *   { name="", price=-5, stock=null }
     *         │
     *         ▼
     *   @Valid 触发校验 ←────────────── 关键！校验不通过直接短路返回
     *         │
     *         │  name="" → @NotBlank ❌
     *         │  price=-5 → @DecimalMin("0.01") ❌
     *         │  stock=null → @NotNull ❌
     *         │
     *         ▼
     *   返回 HTTP 400（请求不会进入 create() 方法体）
     */

    @NotBlank(message = "商品名称不能为空") // 不能为 null、空串、纯空格；而 @NotEmpty 不能为 null、空（集合/字符串）
    @Size(max = 200, message = "商品名称最多 200 个字符")
    private String name;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于 0")
    @Digits(integer = 8, fraction = 2, message = "价格最多 8 位整数 + 2 位小数")
    private BigDecimal price;

    @NotBlank(message = "商品描述不能为空")
    @Size(max = 1000, message = "商品描述最多 1000 个字符")
    private String description;

    @Size(max = 500, message = "图片 URL 最多 500 个字符")
    private String imageUrl;

    @NotNull(message = "库存不能为空")
    @PositiveOrZero(message = "库存不能为负数") // 必须 >=0
    private Integer stock;

    // ========== Getter / Setter（Jackson 反序列化 JSON 时依赖它们） ==========

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
