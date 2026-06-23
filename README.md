# learn-springboot-by-e-commerce

> 从零基础到学完 Java Spring Boot 单体架构项目的全过程记录。

一个基于 Spring Boot 3.5 + MyBatis-Plus + MySQL + Redis + Sa-Token 的电商 API 项目，从最基础的 Product CRUD 起步，逐步集成企业级技术栈。

---

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.5.15 |
| ORM | MyBatis-Plus | 3.5.12 |
| 数据库 | MySQL | 8.0 (Docker) |
| 缓存 | Redis + Spring Cache | 7-alpine (Docker) |
| 认证 | Sa-Token | 1.44.0 |
| 密码加密 | jBCrypt | 0.4 |
| 校验 | Jakarta Validation | — |
| AOP | Spring AOP | — |
| 构建 | Maven Wrapper | 3.9.16 |
| 语言 | Java | 17 |

---

## 项目结构

```
demo
├── docker-compose.yml          # MySQL + Redis 容器编排
├── src/main
│   ├── java/com/example/demo
│   │   ├── DemoApplication.java          # 启动入口
│   │   ├── config/                       # 配置类
│   │   │   ├── CacheConfig.java          # Redis 缓存配置
│   │   │   ├── DataInitializer.java      # 演示数据初始化
│   │   │   ├── MyBatisPlusConfig.java    # MyBatis-Plus 分页插件
│   │   │   ├── MyMetaObjectHandler.java  # 自动填充创建时间
│   │   │   ├── RequestLogAspect.java     # AOP 请求日志切面
│   │   │   ├── SaTokenConfig.java        # Sa-Token 路由拦截
│   │   │   ├── ScheduledTaskConfig.java  # 定时任务：自动取消过期订单
│   │   │   ├── SchedulingConfig.java     # 开启定时任务
│   │   │   ├── StpInterfaceImpl.java     # Sa-Token 角色权限实现
│   │   │   └── WebConfig.java            # 静态资源映射
│   │   ├── controller/                   # 控制器（三层：Product / Order / Auth）
│   │   ├── dto/                          # 数据传输对象
│   │   ├── exception/                    # 自定义异常 + 全局异常处理器
│   │   ├── mapper/                       # MyBatis-Plus Mapper 接口
│   │   └── model/                        # 实体 + 枚举
│   └── resources
│       ├── application.yaml              # 公共配置
│       ├── application-dev.yaml          # 开发环境
│       ├── application-prod.yaml         # 生产环境
│       ├── mapper/OrderMapper.xml        # MyBatis XML 多表联查
│       └── schema.sql                    # 建表 DDL（Docker 自动执行）
└── pom.xml                               # Maven 依赖
```

---

## API 接口一览

### 认证 (AuthController)
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/login` | 登录 | 公开 |
| POST | `/api/auth/logout` | 登出 | 需登录 |
| GET | `/api/auth/me` | 当前用户信息 | 需登录 |

### 商品 (ProductController)
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/products` | 分页列表 | 公开 |
| GET | `/api/products/search` | 关键词搜索 + 价格区间 | 公开 |
| GET | `/api/products/{id}` | 商品详情（缓存） | 公开 |
| GET | `/api/products/{id}/check-stock` | 库存检查 | 公开 |
| POST | `/api/products` | 新增商品 | 需登录 |
| POST | `/api/products/{id}/upload-image` | 上传图片 | 需登录 |
| PUT | `/api/products/{id}` | 更新商品 | 需登录 |
| PATCH | `/api/products/{id}/status` | 上下架 | 需登录 |
| DELETE | `/api/products/{id}` | 删除商品 | 管理员 |

### 订单 (OrderController)
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/orders` | 分页列表 + 筛选 | 需登录 |
| GET | `/api/orders/{id}` | 订单详情（XML JOIN） | 需登录 |
| GET | `/api/orders/export` | 导出 CSV | 需登录 |
| POST | `/api/orders` | 创建订单 | 需登录 |
| PATCH | `/api/orders/{id}/pay` | 付款 | 需登录 |
| PATCH | `/api/orders/{id}/cancel` | 取消（恢复库存） | 需登录 |
| PATCH | `/api/orders/{id}/ship` | 发货 | 需登录 |
| PATCH | `/api/orders/{id}/complete` | 确认收货 | 需登录 |

---

## 学习路线（从零到上线）

### 第 1 阶段：起步 — RESTful CRUD
- Spring Boot 项目初始化 + Maven 依赖管理
- `@RestController` / `@RequestMapping` 路由映射
- `@GetMapping` / `@PostMapping` / `@PutMapping` / `@PatchMapping` / `@DeleteMapping`
- HTTP 状态码：200 / 201 / 204 / 400 / 404 / 500
- 手动实现 Product CRUD（内存模拟 → 数据库）

### 第 2 阶段：持久层 — MyBatis-Plus
- BaseMapper 替代手写 SQL
- `LambdaQueryWrapper` 类型安全查询
- `Page<T>` + `IPage<T>` 分页
- `@TableName` / `@TableId` / `@TableField` 实体映射
- Java 枚举 + MyBatis-Plus `@EnumValue` 自动转换
- MyBatis XML 多表 JOIN + `<resultMap>` 嵌套映射
- 动态 SQL（`<where>` + `<if>`）

### 第 3 阶段：基础设施 — Docker
- `docker-compose.yml` 编排 MySQL 8.0 + Redis 7
- `initdb.d` 自动执行 DDL
- 数据持久化 + 中文编码配置
- JDBC 连接参数：`characterEncoding` / `serverTimezone`

### 第 4 阶段：认证授权 — Sa-Token
- `StpUtil.login()` / `logout()` 会话管理
- `SaInterceptor` 路由拦截
- `@SaCheckLogin` / `@SaCheckRole` 注解鉴权
- `StpInterface` 角色权限接口实现
- BCrypt 密码加密（`hashpw` / `checkpw`）
- RBAC 角色模型：USER / ADMIN

### 第 5 阶段：缓存 — Spring Cache + Redis
- `@Cacheable` 缓存查询结果
- `@CacheEvict` 写操作清除缓存
- `RedisCacheManager` 配置 TTL
- Jackson JSON 序列化（`JavaTimeModule` + `DefaultTyping`）

### 第 6 阶段：业务增强
- `@Valid` + Jakarta Validation 参数校验
- `@RestControllerAdvice` 全局异常处理
- `@Transactional` 事务管理
- DTO 模式分离入参出参
- 订单状态机：PENDING → PAID → SHIPPED → COMPLETED / CANCELLED
- 库存扣减与回滚

### 第 7 阶段：进阶特性
- `@Scheduled` 定时任务 — 自动取消过期订单
- CSV 文件导出 + HTTP 文件下载（`Content-Disposition`）
- `MultipartFile` 图片上传 + 静态资源映射
- BOM 头解决 Excel 中文乱码

### 第 8 阶段：工程化
- 多环境配置（`application-dev.yaml` / `application-prod.yaml`）
- `@EnableScheduling` 开启定时任务
- Spring AOP 切面自动记录请求耗时
- 项目目录分层规范

---

## 快速开始

### 1. 启动基础设施
```bash
docker compose up -d
```

### 2. 启动应用
```bash
# Windows PowerShell
$env:JAVA_HOME = "C:\Users\admin\.jdks\openjdk-26.0.1"
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### 3. 测试
```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 登录（获取 Token）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 查询商品（公开）
curl http://localhost:8080/api/products?page=1&size=10

# 创建订单（需 Token）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "satoken: <your-token>" \
  -d '{"buyerName":"小明","items":[{"productId":2,"quantity":1}]}'
```

### 4. 浏览器访问
- Swagger UI：`http://localhost:8080/swagger-ui.html`（可选安装）

### 5. 预设账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | ADMIN |
| test | 123456 | USER |

---

## 踩坑记录

| 问题 | 原因 | 解决 |
|------|------|------|
| `PaginationInnerInterceptor` 找不到 | MyBatis-Plus 3.5.9+ 移到了 jsqlparser 模块 | 添加 `mybatis-plus-jsqlparser` 依赖 |
| MySQL 中文乱码 | JDBC URL 用了 `utf8mb4` | 改为 `UTF-8`（JDBC 标准写法） |
| Redis 缓存反序列化报 LinkedHashMap | Jackson 没写 `@class` 类型信息 | `activateDefaultTyping(NON_FINAL)` |
| LocalDateTime 序列化失败 | Jackson 默认不支持 Java 8 时间 | 添加 `jackson-datatype-jsr310` + `JavaTimeModule` |
| `@SaCheckRole` 不生效 | 没实现 `StpInterface` | 新建 `StpInterfaceImpl` 返回角色列表 |
| `LambdaQueryWrapper` 编译报错 | 嵌套在方法参数里泛型推断失败 | 单独声明变量 |
| 图片上传路径错误 | `./uploads` 是 Tomcat 临时目录 | 用 `Paths.get("./uploads").toAbsolutePath()` |
| `@Configurable` ≠ `@Configuration` | 前者是 AspectJ 织入用的 | 改成 `@Configuration` |
| 多环境数据库连不上 | application-dev.yaml 写错了数据库名 | 改成跟 docker-compose.yml 一致 |

---

## 项目仓库

https://github.com/Shelden-Hao/learn-springboot-by-e-commerce
