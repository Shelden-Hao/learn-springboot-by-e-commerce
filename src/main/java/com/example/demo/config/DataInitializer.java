package com.example.demo.config;

import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public DataInitializer(ProductMapper productMapper,
                           OrderMapper orderMapper,
                           OrderItemMapper orderItemMapper) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public void run(String... args) {
        if (productMapper.selectCount(null) > 0) {
            return;
        }

        System.out.println("📦 正在初始化数据...");

        // ===== 商品 =====
        Object[][] seedData = {
            {"机械键盘 K8 Pro",      "499.00",  "75% 布局 · Gasket 结构 · 热插拔 · RGB 背光",           "https://img.example.com/kb-k8pro.jpg",      120, ProductStatus.ON_SALE},
            {"27寸 4K 显示器",       "2499.00", "IPS 面板 · 3840×2160 · Type-C 65W 反向充电",             "https://img.example.com/monitor-27-4k.jpg",   45, ProductStatus.ON_SALE},
            {"人体工学椅 Ergo",      "1899.00", "网布透气 · 4D 扶手 · 135° 后仰 · 10年质保",              "https://img.example.com/chair-ergo.jpg",      30, ProductStatus.ON_SALE},
            {"无线鼠标 MX Master",   "699.00",  "电磁滚轮 · USB-C 快充 · 跨设备 Flow",                    "https://img.example.com/mouse-mx.jpg",        200, ProductStatus.ON_SALE},
            {"USB-C 扩展坞",        "349.00",  "7合1 · HDMI 4K@60Hz · PD 100W · SD/TF 读卡",             "https://img.example.com/dock-usbc.jpg",       85, ProductStatus.ON_SALE},
            {"机械键盘 K2",         "399.00",  "84键 紧凑布局 · 蓝牙5.1 · 4000mAh 电池",                 "https://img.example.com/kb-k2.jpg",           60, ProductStatus.ON_SALE},
            {"降噪耳机 Pro",        "1299.00", "自适应 ANC · 40h 续航 · LDAC · 多设备连接",              "https://img.example.com/headphone-pro.jpg",   95, ProductStatus.ON_SALE},
            {"34寸 曲面带鱼屏",     "3299.00", "3440×1440 · 165Hz · 1ms · HDR400",                       "https://img.example.com/monitor-34.jpg",       20, ProductStatus.ON_SALE},
            {"笔记本支架",          "159.00",  "全铝合金 · 6档高度 · 360° 旋转 · 折叠便携",               "https://img.example.com/stand-laptop.jpg",    150, ProductStatus.ON_SALE},
            {"桌面充电站",          "259.00",  "GaN 氮化镓 · 100W · 3C1A · 数显屏幕",                    "https://img.example.com/charger-desk.jpg",    110, ProductStatus.ON_SALE},
            {"屏幕挂灯 Pro",        "199.00",  "非对称光路 · 无极调色温 · 无线遥控",                      "https://img.example.com/light-bar.jpg",        78, ProductStatus.ON_SALE},
            {"电竞椅 Titan",        "2899.00", "4D 扶手 · 180° 平躺 · 冷凝胶头枕 · 承重 150kg",          "https://img.example.com/chair-titan.jpg",      15, ProductStatus.OFF_SHELF},
            {"电动升降桌",          "1699.00", "双电机 · 承重 120kg · 4 档记忆 · 遇阻回弹",               "https://img.example.com/desk-elevate.jpg",     25, ProductStatus.ON_SALE},
            {"挂灯 Air",            "129.00",  "重力感应调节 · Ra95 高显色 · USB 供电",                   "https://img.example.com/light-air.jpg",       200, ProductStatus.OFF_SHELF},
            {"显示器支架",          "449.00",  "气压弹簧 · 3-9kg 承重 · 360° 旋转 · 桌夹/穿孔双安装",     "https://img.example.com/arm-monitor.jpg",      55, ProductStatus.ON_SALE},
        };

        for (Object[] row : seedData) {
            productMapper.insert(new Product(
                    (String) row[0],
                    new BigDecimal((String) row[1]),
                    (String) row[2],
                    (String) row[3],
                    (Integer) row[4],
                    (ProductStatus) row[5]
            ));
        }

        System.out.println("  ✅ 商品 x" + productMapper.selectCount(null));

        // ===== 订单（演示数据，方便测试多表联查） =====
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // 订单1：小明 买了 1把键盘 + 1个鼠标
        Order o1 = new Order("ORD" + ts + "001", "小明",
                new BigDecimal("1198.00"), OrderStatus.PAID);
        orderMapper.insert(o1);
        orderItemMapper.insert(new OrderItem(o1.getId(), 1L, 1, new BigDecimal("499.00")));  // 键盘
        orderItemMapper.insert(new OrderItem(o1.getId(), 4L, 1, new BigDecimal("699.00")));  // 鼠标

        // 订单2：小红 买了 1台显示器
        Order o2 = new Order("ORD" + ts + "002", "小红",
                new BigDecimal("2499.00"), OrderStatus.SHIPPED);
        orderMapper.insert(o2);
        orderItemMapper.insert(new OrderItem(o2.getId(), 2L, 1, new BigDecimal("2499.00")));

        // 订单3：小明 又买了 1个扩展坞 + 2把 K2 键盘
        Order o3 = new Order("ORD" + ts + "003", "小明",
                new BigDecimal("1147.00"), OrderStatus.PENDING);
        orderMapper.insert(o3);
        orderItemMapper.insert(new OrderItem(o3.getId(), 5L, 1, new BigDecimal("349.00")));
        orderItemMapper.insert(new OrderItem(o3.getId(), 6L, 2, new BigDecimal("399.00")));

        System.out.println("  ✅ 订单 x" + orderMapper.selectCount(null));
        System.out.println("✅ 全部数据初始化完成");
    }
}
