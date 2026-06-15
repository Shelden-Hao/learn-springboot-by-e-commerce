package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper — 演示 MyBatis 的两种查询方式
 *
 * 1. BaseMapper 内置方法：insert / selectById / selectList / selectPage 等（零 SQL）
 * 2. XML 映射文件：多表 JOIN / 动态 SQL / 聚合查询（手写 SQL，精确控制）
 *
 * 注意：这里的自定义方法声明和 OrderMapper.xml 中的 SQL 一一对应
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * OrderMapper.java（接口）              OrderMapper.xml（SQL）
     * ┌──────────────────────┐      ┌─────────────────────────┐
     * │ @Mapper              │      │ <mapper namespace="...   │
     * │ interface OrderMapper│      │   OrderMapper">          │
     * │                      │      │                         │
     * │ OrderDetailDTO       │      │ <select id="selectOrder  │
     * │   selectOrderDetail( │ ←──→ │   Detail" resultMap="..">│
     * │     Long orderId);   │      │   SELECT ... FROM ...    │
     * │                      │      │ </select>               │
     * └──────────────────────┘      └─────────────────────────┘
     *         ↑                            ↑
     *    方法名 = "selectOrderDetail"  →  id="selectOrderDetail"
     */

    /**
     * 多表 JOIN：查订单详情（含明细 + 商品信息）
     *
     * SQL 写在 OrderMapper.xml 中，用 <resultMap> 做嵌套映射
     *
     * @param orderId 订单 ID
     * @return 订单详情 DTO（含商品明细列表）
     */
    OrderDetailDTO selectOrderDetail(@Param("orderId") Long orderId);

    /**
     * 动态 SQL 查询：按条件搜索订单列表
     *
     * 使用 <where> + <if> 实现条件动态拼接
     * 不传 buyerName → 忽略该条件；传了 → 加上 LIKE
     *
     * @param buyerName 买家姓名（可选，模糊匹配）
     * @param status    订单状态（可选）
     * @return 订单列表
     */
    List<Order> searchOrders(@Param("buyerName") String buyerName,
                             @Param("status") String status);
}
