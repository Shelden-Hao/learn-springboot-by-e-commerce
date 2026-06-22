package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情 DTO — 多表 JOIN 查询的结果容器
 *
 * 包含三张表的数据：
 *   orders + order_items + products
 *
 * 这个类不是 @TableName 实体——它不映射单张表，
 * 而是 MyBatis XML 中 ResultMap 的映射目标。
 *
 * 前端类比：GraphQL 的嵌套查询结果 / TypeScript 的嵌套 interface
 */
public class OrderDetailDTO {

    // ===== 来自 orders 表 =====
    private Long orderId;
    private String orderNo;
    private String buyerName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

    // ===== 来自 order_items + products 表（一对多嵌套） =====
    private List<ItemDTO> items; // 一个订单下的多个产品

    // ===== 内嵌 DTO：订单明细 + 商品信息 =====
    public static class ItemDTO {
        private Long itemId;
        private Long productId;
        private String productName;    // 来自 products 表
        private BigDecimal price;      // 下单时快照价格
        private Integer quantity; // 当前订单下某个产品的数量

        // Getter / Setter
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // ===== Getter / Setter =====

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }
}
