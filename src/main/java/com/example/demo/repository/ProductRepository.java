package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 商品仓库 — 操作数据库的接口
 *
 * 核心魔法：
 *   你只需定义一个接口 + 继承 JpaRepository<Product, Long>
 *   Spring Data JPA 会在运行时自动生成实现类，把增删改查全部给你写好！
 *
 * JpaRepository<Product, Long>：
 *   Product = 操作的实体类
 *   Long    = 主键的类型
 *
 * 内置方法（不用写）：
 *   findAll()           → SELECT * FROM products
 *   findById(Long id)   → SELECT * FROM products WHERE id = ?
 *   save(Product p)     → INSERT 或 UPDATE
 *   deleteById(Long id) → DELETE FROM products WHERE id = ?
 *   count()             → SELECT COUNT(*) FROM products
 *
 * 前端类比：就像你引入了一个封装好的 API 客户端库（axios 封装），
 *           你不用写 fetch 代码，调方法就行。
 */
@Repository  // 标记为数据访问层的 Bean，让 Spring 管理它
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Spring 启动
     *     │
     *     ├─ 扫描到 @Repository → 创建 ProductRepository 的代理实现
     *     ├─ 扫描到 @RestController → 发现构造方法需要 ProductRepository
     *     │                                        │
     *     │              自动注入 ←────────────────┘
     *     │
     *     ├─ 扫描到 @Component → 同理注入给 DataInitializer
     *     │
     *     ├─ 执行 DataInitializer.run() → save() 3 条商品到数据库
     *     │
     *     └─ 启动完成，等待请求
     */

    // 你可以在这里定义自定义查询方法
    // 按命名规则写方法名，Spring 自动解析成 SQL！
    // 例如：
    //   findByName(String name)   → SELECT ... WHERE name = ?
    //   findByPriceBetween(...)   → SELECT ... WHERE price BETWEEN ? AND ?
    // 后续课程会用到
}
