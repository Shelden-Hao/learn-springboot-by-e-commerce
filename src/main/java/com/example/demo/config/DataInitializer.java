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

        productRepository.save(new Product(
                "机械键盘 K8 Pro",
                new BigDecimal("499.00"),
                "75% 布局 · Gasket 结构 · 热插拔 · RGB 背光",
                "https://img.example.com/kb-k8pro.jpg",
                120
        ));

        productRepository.save(new Product(
                "27寸 4K 显示器",
                new BigDecimal("2499.00"),
                "IPS 面板 · 3840×2160 · Type-C 65W 反向充电",
                "https://img.example.com/monitor-27-4k.jpg",
                45
        ));

        productRepository.save(new Product(
                "人体工学椅 Ergo",
                new BigDecimal("1899.00"),
                "网布透气 · 4D 扶手 · 135° 后仰 · 10年质保",
                "https://img.example.com/chair-ergo.jpg",
                30
        ));

        System.out.println("✅ 商品数据初始化完成，共插入 " + productRepository.count() + " 条记录");
    }
}
