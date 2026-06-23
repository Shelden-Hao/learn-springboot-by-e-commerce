package com.example.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Product;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Component: 告诉 Spring 自动 new 这个对象
 * ① 扫描到 @Component → "我得造一个 ScheduledTaskConfig 对象"
 * ② 看构造函数 → "哦它需要 OrderMapper，我去找个 OrderMapper 来"
 * ③ 把 OrderMapper 注入进去，并自己实例化对象 → 对象造好了，放在容器里待命
 * ④ 方法上有 @Scheduled → 注册到定时任务调度器
 */

@Component
public class ScheduledTaskConfig {
    private OrderMapper orderMapper;
    private ProductMapper productMapper;
    private OrderItemMapper orderItemMapper;

    public ScheduledTaskConfig(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * 定期查询过期订单
     * 每 30 秒执行一次：扫描超过 5 分钟未支付的订单，自动取消
     * <p>
     * fixedRate = 30000 → 两次执行间隔固定 30 秒（单位：毫秒）
     */
    @Transactional
    @Scheduled(fixedRate = 30000)
    public void autoCancelExpiredOrders() {
        // 构造查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        /**
         * .eq(字段, 值) → field = value（等于）
         * .lt(字段, 值) → field < value（less than，小于）
         * .le(字段, 值) → field <= value
         * .gt(字段, 值) → field > value（greater than）
         * .ge(字段, 值) → field >= value
         */

        queryWrapper.eq(Order::getStatus, OrderStatus.PENDING)
                .lt(Order::getCreatedAt, LocalDateTime.now().minusMinutes(5));

        // 执行查询过期订单
        List<Order> expiredOrders = orderMapper.selectList(queryWrapper);

        if (expiredOrders.isEmpty()) {
            return;
        }
        for (Order order : expiredOrders) {
            // 把订单改为取消
            order.setStatus(OrderStatus.CANCELLED);
            orderMapper.updateById(order);

            // 恢复库存：查出该订单所有明细，把数量加回商品
            LambdaQueryWrapper<OrderItem> itemQuery = new LambdaQueryWrapper<>(); // 必须分步写指定泛型，否则后面查询类型无法自动推断出
            itemQuery.eq(OrderItem::getOrderId, order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectList(
                    itemQuery
            );
            for (OrderItem orderItem : orderItems) {
                Product product = productMapper.selectById(orderItem.getProductId());
                if (product != null) {
                    product.setStock(product.getStock() + orderItem.getQuantity());
                    productMapper.updateById(product);
                }
            }
        }

        System.out.println("⏰ 已自动取消 " + expiredOrders.size() + " 笔超时未支付订单");
    }
}
