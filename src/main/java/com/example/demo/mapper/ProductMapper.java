package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper — MyBatis-Plus 版的数据访问层
 *
 * JPA 对比：
 *   JpaRepository<Product, Long>  →  BaseMapper<Product>
 *   @Repository                     →  @Mapper
 *   findAll()                       →  selectList(null)
 *   findById(id)                    →  selectById(id)
 *   save(entity)                    →  insert(entity) / updateById(entity)
 *   deleteById(id)                  →  deleteById(id)
 *
 * BaseMapper 内置方法比 JPA 更丰富：
 *   selectList(Wrapper)    — 条件查询
 *   selectPage(page, null) — 分页查询
 *   selectCount(Wrapper)   — 统计条数
 *   insert(entity)         — 插入
 *   updateById(entity)     — 根据 ID 更新
 *   deleteBatchIds(ids)    — 批量删除
 *   ...还有 20+ 个
 *
 * 前端类比：axios 封装，你调方法就行，底层 SQL 自动生成
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    // 这里不需要写任何方法！BaseMapper 已经提供了完整的 CRUD。
    //
    // 需要自定义查询时，可以写方法 + 注解 SQL，或 XML 文件：
    //
    //   @Select("SELECT * FROM products WHERE name LIKE CONCAT('%', #{kw}, '%')")
    //   List<Product> searchByName(@Param("kw") String keyword);
    //
    // 更推荐的做法是用 QueryWrapper / LambdaQueryWrapper（在 Controller 中构建），
    // 无需写一行 SQL。
}
