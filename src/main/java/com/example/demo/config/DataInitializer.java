package com.example.demo.config;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 数据初始化器 — 项目启动时自动插入测试数据
 *
 * CommandLineRunner：Spring Boot 启动完成后，自动执行 run() 方法
 * 非常适合在开发阶段插入种子数据（seed data）
 *
 * 前端类比：类似 npm 脚本里的 "seed" 命令，
 *           在 dev 环境启动时自动造一些初始数据
 */
@Component  // 标记这个类为 Spring 管理的 Bean
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    /**
     * 构造方法注入 — 依赖注入的推荐方式
     *
     * Spring 会自动找到 ProductRepository 的实现并传进来
     * 你不需要自己 new，Spring 已经帮你创建好了
     *
     * 前端类比：就像 React 的 useContext / provide-inject，
     *           你声明需要什么，框架帮你注入
     */
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // 如果数据库已经有数据了，就别重复插入
        if (productRepository.count() > 0) {
            return;
        }

        System.out.println("📦 正在初始化商品数据...");

        // 批量插入 15 条商品，让分页效果更直观
        Object[][] seedData = {
            {"机械键盘 K8 Pro",      "499.00",  "75% 布局 · Gasket 结构 · 热插拔 · RGB 背光",           "https://img.example.com/kb-k8pro.jpg",      120},
            {"27寸 4K 显示器",       "2499.00", "IPS 面板 · 3840×2160 · Type-C 65W 反向充电",             "https://img.example.com/monitor-27-4k.jpg",   45},
            {"人体工学椅 Ergo",      "1899.00", "网布透气 · 4D 扶手 · 135° 后仰 · 10年质保",              "https://img.example.com/chair-ergo.jpg",      30},
            {"无线鼠标 MX Master",   "699.00",  "电磁滚轮 · USB-C 快充 · 跨设备 Flow",                    "https://img.example.com/mouse-mx.jpg",        200},
            {"USB-C 扩展坞",        "349.00",  "7合1 · HDMI 4K@60Hz · PD 100W · SD/TF 读卡",             "https://img.example.com/dock-usbc.jpg",       85},
            {"机械键盘 K2",         "399.00",  "84键 紧凑布局 · 蓝牙5.1 · 4000mAh 电池",                 "https://img.example.com/kb-k2.jpg",           60},
            {"降噪耳机 Pro",        "1299.00", "自适应 ANC · 40h 续航 · LDAC · 多设备连接",              "https://img.example.com/headphone-pro.jpg",   95},
            {"34寸 曲面带鱼屏",     "3299.00", "3440×1440 · 165Hz · 1ms · HDR400",                       "https://img.example.com/monitor-34.jpg",       20},
            {"笔记本支架",          "159.00",  "全铝合金 · 6档高度 · 360° 旋转 · 折叠便携",               "https://img.example.com/stand-laptop.jpg",    150},
            {"桌面充电站",          "259.00",  "GaN 氮化镓 · 100W · 3C1A · 数显屏幕",                    "https://img.example.com/charger-desk.jpg",    110},
            {"屏幕挂灯 Pro",        "199.00",  "非对称光路 · 无极调色温 · 无线遥控",                      "https://img.example.com/light-bar.jpg",        78},
            {"电竞椅 Titan",        "2899.00", "4D 扶手 · 180° 平躺 · 冷凝胶头枕 · 承重 150kg",          "https://img.example.com/chair-titan.jpg",      15},
            {"电动升降桌",          "1699.00", "双电机 · 承重 120kg · 4 档记忆 · 遇阻回弹",               "https://img.example.com/desk-elevate.jpg",     25},
            {"挂灯 Air",            "129.00",  "重力感应调节 · Ra95 高显色 · USB 供电",                   "https://img.example.com/light-air.jpg",       200},
            {"显示器支架",          "449.00",  "气压弹簧 · 3-9kg 承重 · 360° 旋转 · 桌夹/穿孔双安装",     "https://img.example.com/arm-monitor.jpg",      55},
        };

        for (Object[] row : seedData) {
            productRepository.save(new Product(
                    (String) row[0],
                    new BigDecimal((String) row[1]),
                    (String) row[2],
                    (String) row[3],
                    (Integer) row[4]
            ));
        }

        System.out.println("✅ 商品数据初始化完成，共插入 " + productRepository.count() + " 条记录");
    }
}
