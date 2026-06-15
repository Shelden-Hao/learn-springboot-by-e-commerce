-- 建表：product 表
-- MyBatis-Plus 不像 JPA 那样自动建表，需要手动写 SQL（或配 DDL 自动执行）
-- 这是真实项目的标准做法——DDL 由开发人员精确控制
CREATE TABLE IF NOT EXISTS products (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(200)   NOT NULL COMMENT '商品名称',
    price        DECIMAL(10,2)  NOT NULL COMMENT '商品价格',
    description  VARCHAR(1000)  DEFAULT '' COMMENT '商品描述',
    image_url    VARCHAR(500)   DEFAULT '' COMMENT '图片链接',
    stock        INT            NOT NULL DEFAULT 0 COMMENT '库存',
    status       VARCHAR(20)    NOT NULL DEFAULT 'ON_SALE' COMMENT '状态: ON_SALE-在售, OFF_SHELF-下架',
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 订单表：记录每次下单的概要信息
CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no     VARCHAR(32)    NOT NULL COMMENT '订单编号（业务号，如 ORD202606150001）',
    buyer_name   VARCHAR(50)    NOT NULL COMMENT '买家姓名',
    total_amount DECIMAL(10,2)  NOT NULL COMMENT '订单总金额',
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING-待付款, PAID-已付款, SHIPPED-已发货, COMPLETED-已完成, CANCELLED-已取消',
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 订单明细表：一笔订单可以包含多个商品（一对多关系）
CREATE TABLE IF NOT EXISTS order_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT         NOT NULL COMMENT '所属订单 ID（外键 → orders.id）',
    product_id   BIGINT         NOT NULL COMMENT '商品 ID（外键 → products.id）',
    quantity     INT            NOT NULL COMMENT '购买数量',
    price        DECIMAL(10,2)  NOT NULL COMMENT '下单时的商品单价（快照，防止后续改价影响历史订单）'
);
