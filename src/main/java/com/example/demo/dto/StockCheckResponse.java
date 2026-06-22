package com.example.demo.dto;

public class StockCheckResponse {
    private Long productId;
    private String productName;
    private Long requestedQty;
    private Boolean available;
    private Integer currentStock;

    public StockCheckResponse(Long productId, String productName, Long requestedQty, Boolean available, Integer currentStock) {
        this.productId = productId;
        this.productName = productName;
        this.requestedQty = requestedQty;
        this.available = available;
        this.currentStock = currentStock;
    }

    // 必须要写 getter
    // Jackson 序列化 JSON 靠的是 getter 方法，你现在只有构造器 + 字段，缺了 getter，接口返回会是空 {}。
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Long getRequestedQty() { return requestedQty; }
    public Boolean getAvailable() { return available; }
    public Integer getCurrentStock() { return currentStock; }

}
