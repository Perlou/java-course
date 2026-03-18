# TurboX Exchange - Codex 项目指令

## 项目概述

TurboX Exchange 是一个准生产级分布式数字资产现货交易平台，采用微服务架构 + 事件驱动设计。

- **定位**: 准生产级，仅现货交易
- **阶段**: 设计文档已完善，代码尚未开始

## 核心文档

- `docs/PRD.md` — 产品需求文档（功能需求、手续费体系、精度规范、错误场景）
- `docs/ARCHITECTURE.md` — 技术架构文档（核心设计、事件流、数据库、测试策略）
- `README.md` — 项目说明

## 技术栈

### 后端

- Java 21 + Spring Boot 3.2 + Spring Cloud 2023
- MySQL 8.0 + ShardingSphere-JDBC 5.x（分库分表）
- Redis 7.0（缓存/锁/限流）
- Apache Kafka（事件驱动）
- LMAX Disruptor（撮合队列）
- Protocol Buffers（快照序列化）
- MyBatis-Plus（ORM）
- Nacos（注册中心 + 配置中心）

### 前端

- Next.js 14 + TypeScript
- Zustand（状态管理）
- reconnecting-websocket（原生 WebSocket，非 Socket.io）
- TradingView（K 线图表）

## 服务划分

| 服务 | 职责 |
|------|------|
| `exchange-gateway` | API 网关 |
| `exchange-user` | 用户/认证/KYC |
| `exchange-account` | 资金/充提/流水 |
| `exchange-order` | 订单/委托/撤单 |
| `exchange-match` | 撮合引擎 |
| `exchange-market` | 行情/K线/WebSocket |
| `exchange-risk` | 风控 |
| `exchange-admin` | 管理后台 |
| `exchange-common` | 公共模块 |

## 关键设计约束

### 精度

- 所有金额/价格/数量使用 `BigDecimal`，严禁 `double`/`float`
- 舍入: `RoundingMode.DOWN`（截断）
- 手续费向上取整（对平台有利）

### 事件驱动 + 最终一致性

- 同步: Order → Account（冻结）使用 OpenFeign
- 异步: Order → Match, Match → Account/Market/Order 使用 Kafka
- 可靠投递: Outbox Pattern（同事务写 orders + outbox_event）
- 消费幂等: `account_flow.biz_id` UNIQUE KEY

### biz_id 格式约定

- 下单冻结: `FREEZE:{orderId}`
- 成交结算: `TRADE:{tradeId}:BUYER` / `TRADE:{tradeId}:SELLER`
- 撤单解冻: `CANCEL:{orderId}`
- 充值入账: `DEPOSIT:{txHash}:{vout}`

### Kafka Topic

- `order.created` — partition by symbol
- `trade.matched` — partition by symbol
- `order.updated` — partition by user_id
- `account.updated` — partition by user_id

### 撮合引擎容灾

- 快照: 10,000 事件或 60 秒触发，Protobuf 序列化
- WAL: 复用 Kafka，不自建
- 恢复: 快照 + Kafka 事件重放
- 主备: Active + Standby per symbol，Redis 分布式锁选主

### 充提

- 接口抽象: `WalletService` 接口
- Mock 实现: `@Profile("mock")`，开发阶段使用
- 真实实现: `@Profile("real")`，需对接链上节点

### 分库分表 (ShardingSphere-JDBC)

- `account_flow`: user_id % 16
- `orders`: user_id % 16
- `trades`: symbol hash % 8
- `kline`: (symbol + period) hash % 16

## 开发规范

### 代码风格

- Google Java Style Guide + Checkstyle
- 测试覆盖率: 撮合核心 >90%, 业务 >70%

### Git 提交

```
格式: <type>(<scope>): <subject>
类型: feat / fix / docs / refactor / test / chore
示例: feat(match): 实现限价单撮合逻辑
```

### 测试

- 单元: JUnit 5 + Mockito
- 集成: Testcontainers (MySQL, Redis, Kafka)
- 性能: JMH
- 端到端: Docker Compose + REST Assured

## 手续费

- Maker 0.1% / Taker 0.2%（VIP0 基础费率）
- 买单从 base 币种扣，卖单从 quote 币种扣
- VIP 阶梯: VIP0-VIP3，按 30 天交易量

## 性能目标

- 撮合纯延迟: P99 < 100μs（单交易对）
- 撮合吞吐: > 50,000 orders/sec（单交易对）
- 下单全链路: P99 < 50ms
- 行情端到端: < 100ms
