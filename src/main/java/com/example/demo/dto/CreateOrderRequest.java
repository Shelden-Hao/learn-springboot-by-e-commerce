package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 创建订单请求
 */
public class CreateOrderRequest {

    @NotBlank(message = "买家姓名不能为空")
    private String buyerName;

    @NotEmpty(message = "订单明细不能为空")
    @Valid  // 嵌套校验：对 items 列表里每个元素也做校验
    private List<OrderItemRequest> items;

    // ===== 内嵌 DTO =====
    public static class OrderItemRequest {
        @NotNull(message = "商品 ID 不能为空")
        private Long productId;

        @Positive(message = "购买数量必须大于 0")
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // ===== Getter / Setter =====

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
