# 🏗️ TurboX Exchange - 技术架构文档

> 版本: 1.0.0  
> 更新日期: 2024-12-29  
> 状态: 草案

---

## 1. 架构概述

### 1.1 架构目标

| 目标   | 描述                                  |
| ------ | ------------------------------------- |
| 高性能 | 撮合引擎 10 万+ TPS，API 响应 < 100ms |
| 高可用 | 99.9% 可用性，故障自动恢复            |
| 可扩展 | 支持水平扩展，弹性伸缩                |
| 安全性 | 资金安全，操作可审计                  |
| 可维护 | 模块化设计，易于迭代                  |

### 1.2 架构风格

- **微服务架构**：按业务领域拆分，独立部署
- **事件驱动架构**：核心业务通过事件解耦
- **CQRS 模式**：读写分离，优化性能
- **DDD 领域驱动**：核心域采用 DDD 设计

---

## 2. 系统架构

### 2.1 整体架构图

```
                                    ┌─────────────────────────────────────┐
                                    │          用户端 (Web/App)            │
                                    └─────────────────┬───────────────────┘
                                                      │
                                    ┌─────────────────▼───────────────────┐
                                    │           CDN / 负载均衡             │
                                    └─────────────────┬───────────────────┘
                                                      │
┌─────────────────────────────────────────────────────▼────────────────────────────────────────┐
│                                        API Gateway                                            │
│                            (Spring Cloud Gateway + Rate Limiting)                             │
└──────┬──────────────┬──────────────┬──────────────┬──────────────┬──────────────┬───────────┘
       │              │              │              │              │              │
       ▼              ▼              ▼              ▼              ▼              ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   User      │ │   Account   │ │   Order     │ │   Market    │ │   Risk      │ │   Admin     │
│   Service   │ │   Service   │ │   Service   │ │   Service   │ │   Service   │ │   Service   │
│   用户服务  │ │   账户服务  │ │   订单服务  │ │   行情服务  │ │   风控服务  │ │   管理服务  │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │              │              │              │              │              │
       └──────────────┴──────────────┼──────────────┴──────────────┴──────────────┘
                                     │
                      ┌──────────────▼──────────────┐
                      │       Message Queue          │
                      │         (Kafka)              │
                      └──────────────┬──────────────┘
                                     │
                      ┌──────────────▼──────────────┐
                      │       Match Engine           │
                      │       撮合引擎服务           │
                      │    (独立进程/高性能)         │
                      └──────────────┬──────────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   MySQL     │ │   Redis     │ │   Kafka     │ │    ES       │ │   MinIO     │
│   主数据    │ │   缓存/锁   │ │   消息队列  │ │   日志/搜索 │ │   对象存储  │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

### 2.2 服务划分

| 服务               | 职责                           | 核心技术                |
| ------------------ | ------------------------------ | ----------------------- |
| `exchange-gateway` | API 网关，路由、限流、认证     | Spring Cloud Gateway    |
| `exchange-user`    | 用户注册登录、安全认证、KYC    | Spring Security, OAuth2 |
| `exchange-account` | 资产管理、充提、流水           | 分布式事务, 幂等        |
| `exchange-order`   | 订单管理、委托、撤单           | DDD, CQRS               |
| `exchange-match`   | 撮合引擎、订单簿               | 无锁队列, 内存计算      |
| `exchange-market`  | 行情计算、K 线、WebSocket 推送 | Redis, WebSocket        |
| `exchange-risk`    | 风控规则、异常检测             | 规则引擎                |
| `exchange-admin`   | 管理后台 API                   | Spring MVC              |
| `exchange-common`  | 公共模块、工具类               | -                       |

---

## 3. 核心设计

### 3.1 撮合引擎设计

撮合引擎是交易所的核心，采用内存撮合 + 持久化的方式：

```
                    ┌─────────────────────────────────┐
                    │          Order Queue            │
                    │         (Disruptor)             │
                    └─────────────┬───────────────────┘
                                  │
                    ┌─────────────▼───────────────────┐
                    │        Sequencer                │
                    │      (单线程定序)               │
                    └─────────────┬───────────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Order Book     │    │   Match Core    │    │   Event         │
│  BTC/USDT       │    │   撮合核心      │    │   Publisher     │
│  ├─ Buy Orders  │ ◀──│   价格/时间优先 │──▶ │   事件发布      │
│  └─ Sell Orders │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                      │
                                  ┌───────────────────┼───────────────────┐
                                  ▼                   ▼                   ▼
                           ┌───────────┐       ┌───────────┐       ┌───────────┐
                           │ 成交事件  │       │ 行情更新  │       │ 订单状态  │
                           │ TradeEvent│       │ MarketData│       │ OrderEvent│
                           └───────────┘       └───────────┘       └───────────┘
```

**技术选型**：

- **Disruptor**：高性能无锁队列，处理订单输入
- **TreeMap**：红黑树实现订单簿，O(log n) 插入删除
- **单线程撮合**：避免锁竞争，保证确定性

**订单簿数据结构**：

```java
public class OrderBook {
    // 买单: 价格从高到低 (TreeMap 降序)
    private TreeMap<BigDecimal, LinkedList<Order>> bids;

    // 卖单: 价格从低到高 (TreeMap 升序)
    private TreeMap<BigDecimal, LinkedList<Order>> asks;

    // 订单索引: orderId -> Order
    private Map<Long, Order> orderIndex;
}
```

### 3.2 资金账户设计

资金操作必须保证原子性和幂等性：

```
┌──────────────────────────────────────────────────────────────────┐
│                        Account Service                            │
├──────────────────────────────────────────────────────────────────┤
│                                                                    │
│   ┌────────────────┐                                              │
│   │ 资金操作请求   │                                              │
│   └───────┬────────┘                                              │
│           │                                                        │
│           ▼                                                        │
│   ┌────────────────┐                                              │
│   │ 幂等性检查     │ ── 检查 request_id 是否已处理               │
│   └───────┬────────┘                                              │
│           │                                                        │
│           ▼                                                        │
│   ┌────────────────┐                                              │
│   │ 余额校验       │ ── 检查可用余额是否充足                      │
│   └───────┬────────┘                                              │
│           │                                                        │
│           ▼                                                        │
│   ┌────────────────┐    ┌────────────────┐                        │
│   │ 更新余额       │ ── │ 记录流水       │  (同一事务)            │
│   └───────┬────────┘    └────────────────┘                        │
│           │                                                        │
│           ▼                                                        │
│   ┌────────────────┐                                              │
│   │ 发布事件       │ ── 资金变动事件                              │
│   └────────────────┘                                              │
│                                                                    │
└──────────────────────────────────────────────────────────────────┘
```

**账户表设计**：

```sql
CREATE TABLE account (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    available DECIMAL(32,16) NOT NULL DEFAULT 0,  -- 可用余额
    frozen DECIMAL(32,16) NOT NULL DEFAULT 0,     -- 冻结余额
    version INT NOT NULL DEFAULT 0,               -- 乐观锁版本
    UNIQUE KEY uk_user_currency (user_id, currency)
);

CREATE TABLE account_flow (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    change_amount DECIMAL(32,16) NOT NULL,
    balance_after DECIMAL(32,16) NOT NULL,
    flow_type VARCHAR(20) NOT NULL,  -- TRADE/DEPOSIT/WITHDRAW/FREEZE/UNFREEZE
    biz_id VARCHAR(64) NOT NULL,     -- 业务ID (幂等)
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_biz_id (biz_id)
);
```

### 3.3 行情系统设计

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Market Service                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ┌─────────────────┐                                                   │
│   │ Trade Events    │ ◀── 来自撮合引擎的成交事件                        │
│   │ (Kafka Consumer)│                                                   │
│   └────────┬────────┘                                                   │
│            │                                                             │
│   ┌────────▼────────────────────────────────────────────────┐           │
│   │                    Market Processor                      │           │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐      │           │
│   │  │ Ticker 计算 │  │ K 线聚合    │  │ 深度快照    │      │           │
│   │  │ 最新价/量   │  │ 1m/5m/1h/1d │  │ 买卖盘口    │      │           │
│   │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘      │           │
│   └─────────┼────────────────┼────────────────┼─────────────┘           │
│             │                │                │                          │
│             ▼                ▼                ▼                          │
│   ┌─────────────────────────────────────────────────────────┐           │
│   │                      Redis Cache                         │           │
│   │  • market:ticker:{symbol}                                │           │
│   │  • market:kline:{symbol}:{period}                        │           │
│   │  • market:depth:{symbol}                                 │           │
│   └──────────────────────────┬──────────────────────────────┘           │
│                              │                                           │
│   ┌──────────────────────────▼──────────────────────────────┐           │
│   │                  WebSocket Server                        │           │
│   │  • /ws/ticker     实时行情                               │           │
│   │  • /ws/kline      K线更新                                │           │
│   │  • /ws/depth      深度更新                               │           │
│   │  • /ws/trade      最新成交                               │           │
│   └─────────────────────────────────────────────────────────┘           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 技术选型

### 4.1 后端技术栈

| 类别         | 技术                 | 版本   | 选型理由               |
| ------------ | -------------------- | ------ | ---------------------- |
| **语言**     | Java                 | 21     | LTS 版本，虚拟线程支持 |
| **框架**     | Spring Boot          | 3.2    | 主流框架，生态完善     |
| **微服务**   | Spring Cloud         | 2023.0 | 服务治理               |
| **网关**     | Spring Cloud Gateway | 4.1    | 响应式网关             |
| **注册中心** | Nacos                | 2.3    | 服务注册发现+配置中心  |
| **RPC**      | OpenFeign            | -      | 声明式 HTTP 客户端     |
| **数据库**   | MySQL                | 8.0    | 主数据存储             |
| **缓存**     | Redis                | 7.0    | 缓存、分布式锁、计数器 |
| **消息队列** | Kafka                | 3.6    | 高吞吐事件驱动         |
| **搜索**     | Elasticsearch        | 8.x    | 日志、订单搜索         |
| **ORM**      | MyBatis-Plus         | 3.5    | SQL 灵活，代码生成     |
| **连接池**   | HikariCP             | 5.x    | 高性能连接池           |
| **JSON**     | Jackson              | 2.16   | JSON 序列化            |
| **API 文档** | SpringDoc            | 2.3    | OpenAPI 3.0            |
| **安全**     | Spring Security      | 6.2    | 认证授权               |
| **JWT**      | jjwt                 | 0.12   | Token 处理             |
| **验证**     | Hibernate Validator  | 8.0    | 参数校验               |
| **工具**     | Lombok               | 1.18   | 代码简化               |
| **日志**     | Logback              | 1.4    | 日志框架               |

### 4.2 前端技术栈

| 类别          | 技术             | 版本 | 选型理由            |
| ------------- | ---------------- | ---- | ------------------- |
| **框架**      | Next.js          | 14   | React 全栈框架，SSR |
| **语言**      | TypeScript       | 5.x  | 类型安全            |
| **状态**      | Zustand          | 4.x  | 轻量状态管理        |
| **请求**      | TanStack Query   | 5.x  | 数据获取缓存        |
| **样式**      | Tailwind CSS     | 3.x  | 原子化 CSS          |
| **组件**      | Radix UI         | -    | 无样式组件库        |
| **图表**      | TradingView      | -    | 专业 K 线图表       |
| **WebSocket** | Socket.io-client | 4.x  | 实时通信            |
| **表单**      | React Hook Form  | 7.x  | 表单处理            |
| **验证**      | Zod              | 3.x  | 类型验证            |

### 4.3 基础设施

| 类别         | 技术           | 用途         |
| ------------ | -------------- | ------------ |
| **容器**     | Docker         | 容器化部署   |
| **编排**     | Docker Compose | 本地开发     |
| **K8s**      | Kubernetes     | 生产编排     |
| **监控**     | Prometheus     | 指标采集     |
| **可视化**   | Grafana        | 监控面板     |
| **链路追踪** | Jaeger         | 分布式追踪   |
| **日志**     | ELK Stack      | 日志平台     |
| **CI/CD**    | GitHub Actions | 自动化流水线 |

---

## 5. 数据库设计

### 5.1 分库分表策略

| 数据表       | 分库 | 分表 | 分片键          |
| ------------ | ---- | ---- | --------------- |
| user         | 否   | 否   | -               |
| account      | 否   | 否   | -               |
| account_flow | 是   | 是   | user_id         |
| orders       | 是   | 是   | user_id         |
| trades       | 是   | 是   | symbol          |
| kline        | 是   | 是   | symbol + period |

### 5.2 核心表结构

```sql
-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    status TINYINT DEFAULT 1,  -- 1:正常 2:冻结
    kyc_level TINYINT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side TINYINT NOT NULL,       -- 1:买 2:卖
    type TINYINT NOT NULL,       -- 1:限价 2:市价
    price DECIMAL(32,16),
    quantity DECIMAL(32,16) NOT NULL,
    filled_quantity DECIMAL(32,16) DEFAULT 0,
    status TINYINT NOT NULL,     -- 1:未成交 2:部分成交 3:全部成交 4:已撤销
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_status (user_id, status),
    INDEX idx_symbol_status (symbol, status)
);

-- 成交表
CREATE TABLE trades (
    id BIGINT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    buy_order_id BIGINT NOT NULL,
    sell_order_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    price DECIMAL(32,16) NOT NULL,
    quantity DECIMAL(32,16) NOT NULL,
    buyer_fee DECIMAL(32,16) NOT NULL,
    seller_fee DECIMAL(32,16) NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_symbol_time (symbol, created_at),
    INDEX idx_buyer (buyer_id, created_at),
    INDEX idx_seller (seller_id, created_at)
);
```

---

## 6. API 设计

### 6.1 API 规范

- **RESTful 风格**
- **统一响应格式**：`{ code, message, data, timestamp }`
- **版本控制**：`/api/v1/...`
- **认证方式**：Bearer Token (JWT)
- **签名验证**：API Key + Secret + Timestamp + Signature

### 6.2 核心 API 列表

| 模块     | 接口                              | 方法   | 描述         |
| -------- | --------------------------------- | ------ | ------------ |
| **用户** | `/api/v1/user/register`           | POST   | 用户注册     |
|          | `/api/v1/user/login`              | POST   | 用户登录     |
|          | `/api/v1/user/profile`            | GET    | 获取用户信息 |
| **账户** | `/api/v1/account/assets`          | GET    | 资产列表     |
|          | `/api/v1/account/deposit/address` | GET    | 充值地址     |
|          | `/api/v1/account/withdraw`        | POST   | 提现申请     |
| **交易** | `/api/v1/order`                   | POST   | 下单         |
|          | `/api/v1/order/{orderId}`         | DELETE | 撤单         |
|          | `/api/v1/order/open`              | GET    | 当前委托     |
|          | `/api/v1/order/history`           | GET    | 历史委托     |
| **行情** | `/api/v1/market/tickers`          | GET    | 行情列表     |
|          | `/api/v1/market/depth/{symbol}`   | GET    | 深度数据     |
|          | `/api/v1/market/klines`           | GET    | K 线数据     |
|          | `/api/v1/market/trades/{symbol}`  | GET    | 成交记录     |

---

## 7. 安全设计

### 7.1 认证授权

```
┌─────────────────────────────────────────────────────────────┐
│                     认证流程                                 │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  登录请求 ──▶ 验证密码 ──▶ 检查 2FA ──▶ 生成 Token          │
│                                                              │
│  API 请求 ──▶ Gateway ──▶ Token 验证 ──▶ 权限检查 ──▶ 服务  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 API 签名

```
签名算法: HMAC-SHA256

签名串 = HTTP_METHOD + REQUEST_PATH + TIMESTAMP + BODY_MD5
Signature = HMAC-SHA256(签名串, API_SECRET)

请求头:
X-API-KEY: <api_key>
X-TIMESTAMP: <timestamp>
X-SIGNATURE: <signature>
```

### 7.3 安全措施清单

| 类别     | 措施                        |
| -------- | --------------------------- |
| 传输安全 | 全站 HTTPS，TLS 1.3         |
| 密码安全 | BCrypt 加密，盐值哈希       |
| 会话安全 | JWT 短期有效，Refresh Token |
| 接口安全 | 限流、防重放、签名验证      |
| 数据安全 | 敏感数据加密存储            |
| 审计日志 | 关键操作留痕                |

---

## 8. 可观测性

### 8.1 监控指标

| 类型     | 指标                     |
| -------- | ------------------------ |
| 业务指标 | 订单量、成交量、用户活跃 |
| 性能指标 | QPS、RT、错误率          |
| 资源指标 | CPU、内存、磁盘、网络    |
| JVM 指标 | GC、堆内存、线程数       |

### 8.2 告警规则

| 告警       | 条件         | 级别 |
| ---------- | ------------ | ---- |
| 服务不可用 | 健康检查失败 | P0   |
| 高错误率   | 错误率 > 5%  | P1   |
| 高延迟     | P99 > 1s     | P1   |
| 撮合积压   | 队列 > 10000 | P1   |

---

## 9. 部署架构

### 9.1 开发环境

```yaml
# docker-compose.dev.yml
services:
  mysql:
    image: mysql:8.0
  redis:
    image: redis:7.0
  kafka:
    image: confluentinc/cp-kafka:7.5.0
  nacos:
    image: nacos/nacos-server:v2.3.0
```

### 9.2 生产环境

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│   │   Gateway   │  │   Gateway   │  │   Gateway   │        │
│   │   (Pod)     │  │   (Pod)     │  │   (Pod)     │        │
│   └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                              │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│   │ User Svc x3 │  │ Order Svc x3│  │ Market Svc x3│       │
│   └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                              │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│   │ Account x3  │  │ Match x1*   │  │ Risk Svc x2 │        │
│   └─────────────┘  └─────────────┘  └─────────────┘        │
│                    * 每交易对一个                            │
│                                                              │
│   ┌───────────────────────────────────────────────────┐     │
│   │              StatefulSet: MySQL/Redis/Kafka        │     │
│   └───────────────────────────────────────────────────┘     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 10. 开发规范

### 10.1 代码规范

- 遵循 Google Java Style Guide
- 使用 Checkstyle 检查
- 单元测试覆盖率 > 70%
- 核心逻辑必须有单元测试

### 10.2 Git 规范

```
提交格式: <type>(<scope>): <subject>

类型:
- feat: 新功能
- fix: 修复
- docs: 文档
- refactor: 重构
- test: 测试
- chore: 杂项

示例:
feat(order): 支持止盈止损订单
fix(account): 修复余额扣减精度问题
```

### 10.3 分支策略

```
main          ─────●─────●─────●───────
                   │     │     │
release/1.0  ──────●─────│─────│───────
                         │     │
develop       ───●───●───●───●─●───●───
                 │       │
feature/xxx  ────●───────│─────────────
                         │
hotfix/xxx   ────────────●─────────────
```

---

## 11. 里程碑

| 阶段 | 目标                   | 预计时间 |
| ---- | ---------------------- | -------- |
| M1   | 基础框架搭建、用户服务 | 1 周     |
| M2   | 账户服务、订单服务     | 2 周     |
| M3   | 撮合引擎 MVP           | 2 周     |
| M4   | 行情服务、WebSocket    | 1 周     |
| M5   | 前端 MVP               | 2 周     |
| M6   | 风控、管理后台         | 2 周     |
| M7   | 可观测性、优化         | 1 周     |
| M8   | 测试、文档、部署       | 1 周     |

**总计预估**: 10-12 周
