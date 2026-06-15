package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 — MyBatis-Plus 版本
 *
 * JPA 注解 → MyBatis-Plus 注解对照：
 *   @Entity         →  不需要（靠 Mapper 扫描）
 *   @Table          →  @TableName
 *   @Id             →  @TableId
 *   @GeneratedValue →  @TableId(type = IdType.AUTO)
 *   @Column         →  @TableField
 *   @Enumerated     →  枚举包自动扫描 + @EnumValue
 *   @PrePersist     →  @TableField(fill = FieldFill.INSERT) + MetaObjectHandler
 *
 * 核心差异：
 *   JPA 是"重 ORM"，实体 = 数据库表，自动建表、自动生成 SQL
 *   MyBatis-Plus 是"轻 ORM"，实体 = 字段映射，SQL 由开发者掌控
 */
@TableName("products")   // 对应数据库表名
public class Product {

    @TableId(type = IdType.AUTO)    // 主键自增
    private Long id;

    @TableField("name")
    private String name;

    @TableField("price")
    private BigDecimal price;

    @TableField("description")
    private String description;

    @TableField("image_url")
    private String imageUrl;

    @TableField("stock")
    private Integer stock;

    /**
     * 枚举字段 — 写入数据库时自动取 @EnumValue 标记的值
     * 例如 ProductStatus.ON_SALE → 写入 "ON_SALE"
     *
     * 读取时反向：从数据库读出 "ON_SALE" → ProductStatus.ON_SALE
     */
    @TableField("status")
    private ProductStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // ========== MyBatis-Plus 也要求无参构造方法 ==========
    public Product() {}

    // ========== 创建时使用（默认 ON_SALE） ==========
    public Product(String name, BigDecimal price, String description,
                   String imageUrl, Integer stock) {
        this(name, price, description, imageUrl, stock, ProductStatus.ON_SALE);
    }

    /** 可指定状态的构造方法（种子数据用） */
    public Product(String name, BigDecimal price, String description,
                   String imageUrl, Integer stock, ProductStatus status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.status = status;
    }

    /** 部分更新：只覆盖非 null 字段 */
    public void applyUpdate(String name, BigDecimal price, String description,
                            String imageUrl, Integer stock) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (stock != null) this.stock = stock;
    }

    // ========== Getter / Setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
