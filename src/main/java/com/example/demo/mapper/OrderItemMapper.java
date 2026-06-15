package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper — 纯 BaseMapper，不需要自定义方法
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
