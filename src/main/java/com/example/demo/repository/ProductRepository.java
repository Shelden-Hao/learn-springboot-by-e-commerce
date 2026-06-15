package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
 * 前端类比：就像你引入了一个封装好的 API 客户端库（axios 封装），
 *           你不用写 fetch 代码，调方法就行。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ═══════════════════════════════════════════════════════════
    // 方式一：方法命名查询（不用写 SQL！）
    // ═══════════════════════════════════════════════════════════

    /**
     * 按商品名称模糊搜索 + 分页
     *
     * 方法名 = 查询意图，Spring 自动翻译成 SQL：
     *   find  → SELECT
     *   By    → WHERE
     *   Name  → name 字段
     *   Containing → LIKE %keyword%
     *
     * 最终生成：
     *   SELECT * FROM products WHERE name LIKE '%键盘%' LIMIT 5 OFFSET 0
     *
     * 常用命名关键字：
     *   Containing   → LIKE %?%      包含
     *   StartingWith → LIKE ?%       前缀匹配
     *   EndingWith   → LIKE %?       后缀匹配
     *   IgnoreCase   → UPPER(name)   忽略大小写
     *   Between      → BETWEEN ? AND ?
     *   LessThan     → < ?
     *   GreaterThanEqual → >= ?
     *   And / Or     → AND / OR 连接多个条件
     *   OrderByXxxDesc → ORDER BY xxx DESC
     */
    Page<Product> findByNameContaining(String keyword, Pageable pageable);

    /**
     * 按状态查询 — 枚举也能当查询条件
     *
     * 生成 SQL：SELECT * FROM products WHERE status = 'ON_SALE'
     *
     * @Enumerated(STRING) 确保存的是字符串 "ON_SALE" 而非数字 0/1，
     * 数字枚举在扩展时容易错位（中间插入新值全员序号全乱）
     */
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // 方式二：@Query 手写 JPQL（类 SQL 语法，操作的是 Java 对象而非表）
    // ═══════════════════════════════════════════════════════════

    /**
     * 多字段模糊搜索 — 在商品名称 OR 描述中搜索
     *
     * JPQL vs SQL：
     *   SQL:   SELECT * FROM products WHERE name LIKE '%键盘%' OR description LIKE '%键盘%'
     *   JPQL:  SELECT p FROM Product p WHERE p.name LIKE %:kw% OR p.description LIKE %:kw%
     *          ↑                                          ↑
     *          操作的是实体类 Product，不是表名 products       属性名，不是列名
     *
     * @Param("kw")：把方法参数绑定到 JPQL 中的命名参数 :kw
     *
     * 什么时候用 @Query？
     *   - 多表关联查询（JOIN）
     *   - 复杂条件组合（方法名太长了读不懂）
     *   - 需要精确控制 SQL 语句
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.name LIKE %:kw% " +
           "   OR p.description LIKE %:kw%")
    Page<Product> searchByKeyword(@Param("kw") String keyword, Pageable pageable);
}

