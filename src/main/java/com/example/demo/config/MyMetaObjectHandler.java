package com.example.demo.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充处理器 — 替代 JPA 的 @PrePersist
 *
 * 当 @TableField(fill = FieldFill.INSERT) 的字段值为 null 时，
 * 在 INSERT 前自动调用此方法填入值
 *
 * 前端类比：类似 React Hook 的 useEffect——在数据写入前的生命周期节点自动执行
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 如果 createdAt 没传值，自动填入当前时间
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 暂不需要更新时间——后续有需求再加
    }
}
