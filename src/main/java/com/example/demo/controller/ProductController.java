package com.example.demo.controller;

import com.example.demo.dto.CreateProductRequest;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    /**
     * POST /api/products — 创建新商品
     *
     * @Valid：    触发 Jakarta Validation 校验，
     *             不通过则直接返回 400 + 错误信息，不会进入方法体
     *
     * @RequestBody：把 HTTP 请求体 JSON → 反序列化为 CreateProductRequest 对象
     * 前端类比：
     *   app.post('/api/products', (req, res) => {
     *       const { name, price, ... } = req.body;  // ← @RequestBody 就是这一步
     *       if (!name) return res.status(400)...      // ← @Valid 就是这一步
     *   })
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // 返回 201 而不是默认的 200
    public Product create(@Valid @RequestBody CreateProductRequest request) {
        // 将 DTO 转为 Entity
        Product product = new Product(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl(),
                request.getStock()
        );

        // save()：新增时自动生成 id
        return productRepository.save(product);
    }
}
