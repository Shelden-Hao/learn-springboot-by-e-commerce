package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品控制器 — 处理 /api/products 相关的 HTTP 请求
 *
 * 变化：从硬编码数据 → 注入 ProductRepository，从数据库读写
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    /**
     * 构造方法注入 ProductRepository
     *
     * Spring 发现构造方法参数是 ProductRepository，
     * 会自动把它的实现类（JPA 生成的代理类）注入进来
     *
     * 前端类比：
     *   const ProductController = ({ productRepository }) => { ... }
     *   <ProductController productRepository={...} />
     *
     * — 只不过 Spring 帮你完成了"传 props"这一步
     */
    public ProductController(ProductRepository productRepository) {
        // DI 依赖注入，无需 new ProductRepository(...)
        this.productRepository = productRepository;
    }

    /**
     * GET /api/products — 查询全部商品
     */
    @GetMapping
    public List<Product> list() {
        // 一行代码替代之前的 List.of(...) 大段硬编码
        // findAll() 是 JpaRepository 自带的方法
        return productRepository.findAll();
    }

    /**
     * GET /api/products/{id} — 查询单个商品
     *
     * @PathVariable：从 URL 路径中提取变量
     *
     * 例如 GET /api/products/1 → id = 1
     */
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        // findById 返回 Optional<Product>（可能查到，也可能查不到）
        // orElseThrow → 查到了返回 Product，没查到抛出异常（Spring 自动转 404）
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: id=" + id));
    }
}
