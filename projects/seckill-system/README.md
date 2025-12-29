# 🛒 商城系统 - Phase 11 + 12 + 13 综合实战项目

> **Phase 11**: 订单管理 - 用户、商品、订单、报表  
> **Phase 12**: 秒杀系统 - Redis + RabbitMQ + 分布式锁  
> **Phase 13**: 性能调优 - JVM 监控、Caffeine 缓存、连接池优化

---

## 📋 项目概述

这是一个完整的商城系统，综合运用了 Phase 11-13 的技术栈：

| 阶段     | 功能模块            | 技术要点                      |
| -------- | ------------------- | ----------------------------- |
| Phase 11 | 用户/商品/订单/报表 | MyBatis-Plus、事务、乐观锁    |
| Phase 12 | 秒杀抢购            | Redis Lua 脚本、RabbitMQ 异步 |
| Phase 13 | 性能监控            | JVM 监控、Caffeine、HikariCP  |

---

## 🛠️ 技术栈

| 技术             | 用途                |
| ---------------- | ------------------- |
| Spring Boot 3.2  | 应用框架            |
| MyBatis-Plus     | ORM 增强            |
| MySQL + HikariCP | 数据库 + 连接池优化 |
| Redis + Redisson | 缓存 + 分布式锁     |
| RabbitMQ         | 消息队列            |
| Caffeine         | 本地缓存            |
| Spring Actuator  | 监控端点            |
| Swagger          | API 文档            |

---

## 📁 项目结构

```
seckill-system/
├── src/main/java/com/example/seckill/
│   ├── controller/
│   │   ├── UserController.java       # 用户接口
│   │   ├── ProductController.java    # 商品接口
│   │   ├── OrderController.java      # 订单接口
│   │   ├── ReportController.java     # 报表接口
│   │   ├── SeckillController.java    # 秒杀接口
│   │   └── MonitorController.java    # 监控接口
│   ├── service/
│   ├── entity/
│   ├── mapper/
│   ├── dto/
│   ├── monitor/                      # Phase 13: 性能监控
│   ├── mq/                           # Phase 12: 消息队列
│   ├── config/
│   └── common/
├── src/main/resources/
│   ├── application.yml
│   ├── db/migration/                 # Flyway 迁移
│   │   ├── V1__Init_seckill_tables.sql
│   │   └── V2__Add_mall_tables.sql
│   └── lua/seckill.lua
├── frontend/                         # React 前端
└── docker-compose.yml
```

---

## 🚀 快速开始

```bash
cd /Users/perlou/Desktop/personal/java-course/projects/seckill-system

# 启动基础设施
docker-compose up -d

# 启动应用
mvn spring-boot:run
```

**访问地址：**

- 前端页面: http://localhost:8080
- Swagger 文档: http://localhost:8080/swagger-ui.html

---

## 📡 API 接口汇总

### 用户模块 `/api/users`

```bash
POST /api/users/register?username=test&password=123456  # 注册
POST /api/users/login?username=admin&password=123456    # 登录
```

### 商品模块 `/api/products`

```bash
GET  /api/products           # 商品列表
GET  /api/products/{id}      # 商品详情
POST /api/products           # 创建商品
```

### 订单模块 `/api/orders`

```bash
POST /api/orders             # 创建订单
POST /api/orders/{id}/pay    # 支付订单
POST /api/orders/{id}/cancel # 取消订单
GET  /api/orders/user/{uid}  # 用户订单
```

### 报表模块 `/api/reports`

```bash
GET /api/reports/sales?startDate=2024-01-01&endDate=2024-12-31
GET /api/reports/top-products?days=30&limit=10
```

### 秒杀模块 `/api/seckill`

```bash
GET  /api/seckill/goods      # 秒杀商品列表
POST /api/seckill/do?userId=1&goodsId=1  # 秒杀抢购
```

### 监控模块 `/api/monitor`

```bash
GET /api/monitor/dashboard   # 综合监控面板
GET /api/monitor/jvm         # JVM 信息
GET /api/monitor/cache/stats # 缓存统计
GET /api/monitor/pool/datasource  # 连接池状态
```

---

## 📚 学习要点

### Phase 11: 数据库与 ORM

- Flyway 数据库版本控制
- MyBatis-Plus CRUD
- 乐观锁防超卖
- 事务管理

### Phase 12: 高并发

- Redis Lua 脚本原子操作
- RabbitMQ 异步下单
- 分布式锁

### Phase 13: 性能调优

- JVM 内存/GC 监控
- Caffeine 多级缓存
- HikariCP 连接池优化
- 线程池配置

---

## 🔗 相关文档

- [Phase 11 - 数据库与 ORM](../../src/main/java/phase11/README.md)
- [Phase 12 - Redis 缓存](../../src/main/java/phase12/README.md)
- [Phase 13 - 性能调优](../../src/main/java/phase13/README.md)
