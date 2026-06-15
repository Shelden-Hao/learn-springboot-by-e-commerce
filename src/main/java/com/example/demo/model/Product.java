package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 — 既当 Java 对象，也当数据库表
 *
 * 关键注解：
 *   @Entity        → "我是一个数据库表"
 *   @Table         → 表名（不写默认用类名小写 products）
 *   @Id            → 主键
 *   @GeneratedValue → 主键自动生成（自增）
 *   @Column        → 字段细节（不写也能自动推断）
 *
 * 前端类比：这就是 TypeScript 的 type/interface，只不过它直接映射到 SQL 表，启动时自动执行 CREATE TABLE products...
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增：1, 2, 3...
    private Long id;

    @Column(nullable = false, length = 200)  // NOT NULL，最长 200 字符
    private String name;

    @Column(nullable = false, precision = 10, scale = 2) // 10 位总长，2 位小数
    private BigDecimal price;

    @Column(length = 1000)  // 描述可能比较长
    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING) // 枚举值存为字符串 "ON_SALE" / "OFF_SHELF"
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ========== JPA 要求必须有默认构造方法，这是给框架用的 ==========
    public Product() {}

    // ========== 我们用的构造方法：创建商品时传值 ==========
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

    /**
     * 在持久化到数据库之前自动调用
     * 如果创建时间没设置，就取当前时间
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * 用 DTO 的值更新当前实体 — 只更新非 null 字段
     *
     * 为什么不用 setter 逐个赋值？
     *   因为 UpdateProductRequest 所有字段都是可选的（允许 null），
     *   直接把 null 赋值给 this.price 会把数据库里的价格清掉。
     *   所以只有在字段非 null 时才覆盖原值。
     */
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
