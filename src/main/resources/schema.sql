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
