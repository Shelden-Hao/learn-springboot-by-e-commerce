package com.example.demo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单控制器
 * <p>
 * 核心教学点：
 * - 多表关联查询通过 XML Mapper（selectOrderDetail）
 * - 动态 SQL 查询（searchOrders）
 * - 下单事务：扣库存 → 生成订单号 → 插订单 + 订单明细
 */
@SaCheckLogin                  // 类级别：所有方法都需登录（双重保险）
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public OrderController(OrderMapper orderMapper,
                           OrderItemMapper orderItemMapper,
                           ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    /**
     * POST /api/orders — 下单
     * <p>
     * 流程：
     * 1. 校验每个商品是否存在、库存是否充足
     * 2. 计算总金额
     * 3. 生成订单编号
     * 4. 插入 orders 表
     * 5. 批量插入 order_items 表
     * 6. 扣减库存
     * <p>
     * 整个方法包在 @Transactional 中，
     * 第 1~6 步任何一步失败 → 全部回滚
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Order create(@Valid @RequestBody CreateOrderRequest request) {
        // ① 校验商品 & 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new ProductNotFoundException(item.getProductId());
            }
            if (product.getStatus() == ProductStatus.OFF_SHELF) {
                throw new RuntimeException("该商品已下架");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品 [" + product.getName() + "] 库存不足，当前库存: " + product.getStock());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // ② 生成订单编号 ORD + 年月日 + 毫秒
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // ③ 插入订单
        Order order = new Order(orderNo, request.getBuyerName(), totalAmount, OrderStatus.PENDING);
        orderMapper.insert(order);

        // ④ 批量插入订单明细
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            orderItemMapper.insert(new OrderItem(
                    order.getId(),
                    item.getProductId(),
                    item.getQuantity(),
                    product.getPrice()  // 快照当前价格
            ));

            productMapper.updateById(product);
        }

        return order;
    }

    /**
     * GET /api/orders — 分页查询，支持动态条件筛选
     * <p>
     * 调用的 searchOrders() 是 XML 中定义的动态 SQL（<where> + <if>）
     */
    @GetMapping
    public IPage<Order> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) String status) {

        // 注意：这里我们需要手动拼接分页，因为 XML 里的 searchOrders 不支持 Page 对象
        // 示范方案：先查总数，再查当前页
        // 实际项目中更推荐在 XML 里写分页查询，或使用 MyBatis-Plus 分页拦截器
        List<Order> allOrders = orderMapper.searchOrders(buyerName, status);

        // 手动分页（简化版，实际项目用 PageHelper 或 XML 分页更优雅）
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, allOrders.size());
        List<Order> pageContent = allOrders.subList(
                Math.min(fromIndex, allOrders.size()),
                toIndex
        );

        Page<Order> result = new Page<>(page, size, allOrders.size());
        result.setRecords(pageContent);
        return result;
    }

    /**
     * GET /api/orders/{id} — 订单详情（XML 多表 JOIN）
     * <p>
     * 这是本课最核心的接口！
     * 调用的是 OrderMapper.xml 中的 selectOrderDetail，
     * 用 LEFT JOIN 三表联查 + <resultMap> 嵌套映射。
     */
    @GetMapping("/{id}")
    public OrderDetailDTO getDetail(@PathVariable Long id) {
        OrderDetailDTO detail = orderMapper.selectOrderDetail(id);
        if (detail == null) {
            throw new RuntimeException("订单不存在: id=" + id);
        }
        return detail;
    }

    /**
     * 取消订单 + 库存回退
     */
    @PatchMapping("/{id}/cancel")
    @Transactional // 事务
    public Order cancel(@PathVariable Long id) {
        // 查询订单是否存在，校验订单状态是否为 PENDING，不是则抛异常
        Order order = orderMapper.selectById(id);
        if (order == null || order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("该订单不可被取消");
        }
        // 查出该订单所有的 items
        OrderDetailDTO orderDetailDTO = orderMapper.selectOrderDetail(id);
        for (OrderDetailDTO.ItemDTO item : orderDetailDTO.getItems()) {
            Long productId = item.getProductId();
            // 查询到商品
            Product product = productMapper.selectById(productId);
            // 加入库存
            product.setStock(product.getStock() + item.getQuantity());
            // 更新商品信息
            productMapper.updateById(product);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderMapper.updateById(order);
        // 把更新后的 Order 对象返回，前端才知道现在是什么状态
        return order;
    }

    /**
     * 订单确认付款
     */
    @PatchMapping("/{id}/pay")
    @Transactional // 事务
    public Order pay(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("订单不可被支付");
        }
        // 付款逻辑。。。
        order.setStatus(OrderStatus.PAID);
        orderMapper.updateById(order);
        // 真实业务中付款成功后才减库存，下单时不减库存
        OrderDetailDTO orderDetailDTO = orderMapper.selectOrderDetail(id);
        for (OrderDetailDTO.ItemDTO item : orderDetailDTO.getItems()) {
            Long productId = item.getProductId();
            Product product = productMapper.selectById(productId);
            // 扣减库存
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.updateById(product);
        }
        return order;
    }

    @PatchMapping("/{id}/ship")
    @Transactional
    public Order delivery(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("该订单不可发货，当前状态: " + order.getStatus());
        }
        order.setStatus(OrderStatus.SHIPPED);
        orderMapper.updateById(order);
        return order;
    }

    @PatchMapping("/{id}/complete")
    @Transactional
    public Order complete(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("该订单不可收货，当前状态: " + order.getStatus());
        }
        order.setStatus(OrderStatus.COMPLETED);
        orderMapper.updateById(order);
        return order;
    }
}
