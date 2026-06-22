package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CreateProductRequest;
import com.example.demo.dto.UpdateProductRequest;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Product;
import com.example.demo.model.ProductStatus;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器 — MyBatis-Plus 版
 *
 * 核心变化：
 *   ProductRepository           →  ProductMapper
 *   productRepository.findAll() →  productMapper.selectList(null)
 *   Page<Product> (Spring Data) →  IPage<Product> (MyBatis-Plus)
 *   方法命名查询                 →  LambdaQueryWrapper（类型安全的条件构造器）
 *   @Query JPQL                →  LambdaQueryWrapper.like().or()
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductMapper productMapper;

    public ProductController(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * GET /api/products?status=ON_SALE&page=1&size=10
     *
     * IPage vs Page：
     *   IPage 是接口，Page 是默认实现
     *   new Page<>(page, size) 构建分页对象
     *
     * MyBatis-Plus 的分页比 Spring Data 更直观：
     *   - page 从 1 开始（不是 0！）
     *   - 前端传 page=1 就是第一页
     */
    @GetMapping
    public IPage<Product> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProductStatus status) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 有条件就加上，没条件就查全部
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        // 按 id 降序（新商品排前面）
        wrapper.orderByDesc(Product::getId);

        return productMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * GET /api/products/search?keyword=键盘&page=1&size=5
     *
     * LambdaQueryWrapper 的优势：
     *   wrapper.like(Product::getName, keyword)     →  name LIKE '%键盘%'
     *   wrapper.like(Product::getDescription, kw)   →  description LIKE '%键盘%'
     *   .or()                                       →  把条件用 OR 连接
     *
     * 对比 JPA 的 @Query 写 JPQL：
     *   - Lambda 表达式 → 编译期检查字段名，重构不怕改漏
     *   - 链式调用 → 条件按需拼接，动态 SQL 不用 if/else
     *
     * 前端类比：类似 JS 的 array.filter().sort() 链式调用
     */
    @GetMapping("/search")
    public IPage<Product> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Product::getName, keyword)
               .or()
               .like(Product::getDescription, keyword);

        return productMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * GET /api/products/{id}
     *
     * @Cacheable：Spring Cache 的核心注解
     *   value = "product"   → 缓存名叫 "product"（在 Redis 中以 "product::" 为前缀）
     *   key = "#id"         → 缓存 key 用 SpEL 表达式取方法参数，如 product::1
     *
     * 执行流程：
     *   第 1 次请求 GET /api/products/1
     *     → 查 Redis → 没命中 → 执行方法体（查 DB） → 把结果存 Redis
     *   第 2 次请求 GET /api/products/1
     *     → 查 Redis → 命中！ → 直接返回缓存值，不执行方法体（SQL 都没打印）
     */
    @GetMapping("/{id}")
    @Cacheable(value = "product", key = "#id") // 先查缓存，有则跳过方法体（读操作）
    public Product getById(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }

    /**
     * POST /api/products
     *
     * insert() → INSERT INTO products (...) VALUES (...)
     * MyBatis-Plus 的 insert() 和 JPA 的 save() 行为一致：新增时 id 自动回填
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @CacheEvict(value = "product", allEntries = true) // 新增商品 → 清空所有 product 缓存，方法执行后清除缓存(写/删操作)
    public Product create(@Valid @RequestBody CreateProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl(),
                request.getStock()
        );
        productMapper.insert(product);
        // insert 后 product.id 已自动回填
        return product;
    }

    /**
     * PUT /api/products/{id}
     *
     * updateById() → UPDATE products SET ... WHERE id = ?
     * 只更新非 null 字段（由 applyUpdate 保证）
     */
    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = "product", key = "#id")     // 更新 → 清除该商品的缓存
    public Product update(@PathVariable Long id,
                          @Valid @RequestBody UpdateProductRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }

        product.applyUpdate(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl(),
                request.getStock()
        );

        productMapper.updateById(product);
        return product;
    }

    /**
     * DELETE /api/products/{id}
     *
     * deleteById() → DELETE FROM products WHERE id = ?
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(value = "product", key = "#id")     // 删除 → 清除该商品的缓存
    public void delete(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        productMapper.deleteById(id);
    }

    /**
     * PATCH /api/products/{id}/status?action=on|off
     */
    @PatchMapping("/{id}/status")
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public Product toggleStatus(@PathVariable Long id,
                                 @RequestParam String action) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }

        switch (action.toLowerCase()) {
            case "on"  -> product.setStatus(ProductStatus.ON_SALE);
            case "off" -> product.setStatus(ProductStatus.OFF_SHELF);
            default -> throw new IllegalArgumentException("不支持的操作: " + action + "，请使用 on 或 off");
        }

        productMapper.updateById(product);
        return product;
    }
}
