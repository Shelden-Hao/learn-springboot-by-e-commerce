package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 更新商品的请求体 — 所有字段都是可选的
 *
 * 和 CreateProductRequest 的关键区别：
 *   Create: 所有必填字段缺一不可
 *   Update:  只传要更新的字段，其余保持不变（PATCH 语义）
 *
 * 前端类比：
 *   PUT /api/products/1  { "price": 899, "stock": 50 }
 *   → 只改价格和库存，名称、描述等保留原值
 */
public class UpdateProductRequest {

    @Size(min = 2, max = 200, message = "商品名称为 2~200 个字符")
    private String name;

    @DecimalMin(value = "0.01", message = "商品价格必须大于 0")
    @Digits(integer = 8, fraction = 2, message = "价格最多 8 位整数 + 2 位小数")
    private BigDecimal price;

    @Size(max = 1000, message = "商品描述最多 1000 个字符")
    private String description;

    @Size(max = 500, message = "图片 URL 最多 500 个字符")
    private String imageUrl;

    @PositiveOrZero(message = "库存不能为负数")
    private Integer stock;

    // ========== Getter / Setter ==========

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
