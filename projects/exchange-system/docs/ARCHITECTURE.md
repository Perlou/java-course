# 🏗️ TurboX Exchange - 技术架构文档

> 版本: 1.0.0  
> 更新日期: 2024-12-29  
> 状态: 草案

---

## 1. 架构概述

### 1.1 架构目标

| 目标   | 描述                                  |
| ------ | ------------------------------------- |
| 高性能 | 撮合 P99 < 100μs / 5 万+ orders/sec（单交易对），下单全链路 P99 < 50ms |
| 高可用 | 99.9% 可用性，故障自动恢复，撮合 RTO < 30s |
| 可扩展 | 支持水平扩展，弹性伸缩，分库分表      |
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

#### 3.1.1 容灾与恢复

**快照机制**

| 配置项 | 值 |
| ------ | -- |
| 触发频率 | 每 10,000 个事件或每 60 秒（先到者触发） |
| 快照内容 | OrderBook 完整状态 + 当前 Kafka Consumer Offset |
| 序列化格式 | Protocol Buffers（紧凑、向后兼容） |
| 存储位置 | 本地 SSD（主）+ MinIO（备份） |

**WAL（Write-Ahead Log）**

复用 Kafka 作为 WAL，不自建日志：
- 所有进入撮合的订单事件通过 Kafka `order.created` topic 持久化
- 撮合产生的结果通过 `trade.matched` topic 持久化
- Kafka 保留策略：7 天或 100GB（以先到者为准）

**恢复流程**

```
1. 加载最近一次快照（OrderBook + Kafka offset）
2. 从快照记录的 offset 开始重放 Kafka 事件
3. 重放期间所有事件标记 replay=true，下游消费者跳过已处理事件
4. 追上实时位点后切换为正常模式，开始处理新订单
```

**主备方案**

- 每个交易对独立部署：Active + Standby（热备）
- Leader 选举：Redis 分布式锁（Redisson），锁 TTL = 30s，续期间隔 = 10s
- 故障切换：Active 节点心跳丢失 → Standby 获取锁 → 加载快照恢复 → RTO < 30s
- 脑裂防护：获取锁后先校验 Kafka 最新 offset，确保无数据丢失

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

#### 3.2.1 冻结/解冻/结算详细流程

**下单冻结规则**

| 订单类型 | 冻结对象 | 冻结金额 |
| -------- | -------- | -------- |
| 限价买单 | quote 币种（如 USDT） | price × quantity |
| 限价卖单 | base 币种（如 BTC） | quantity |
| 市价买单 | quote 币种（如 USDT） | 用户指定的 quote 金额（如花 50000 USDT 买入） |
| 市价卖单 | base 币种（如 BTC） | quantity |

**结算流程（每笔成交触发）**

以 BTC/USDT 限价买单成交为例（买方为 Taker）：

```
买方结算:
  1. 解冻 USDT: frozen -= matchPrice × matchQty
  2. 扣除 USDT: available 不变（从冻结扣）
  3. 增加 BTC:  available += matchQty - buyerFee
  4. 记录流水: biz_id = "TRADE:{tradeId}:BUYER"

卖方结算:
  1. 解冻 BTC: frozen -= matchQty
  2. 扣除 BTC: available 不变（从冻结扣）
  3. 增加 USDT: available += matchPrice × matchQty - sellerFee
  4. 记录流水: biz_id = "TRADE:{tradeId}:SELLER"

部分成交剩余冻结处理:
  - 限价买单多冻结退回: 若 matchPrice < orderPrice，多冻结部分解冻退回 available
  - 撤单时: 剩余冻结全部解冻回 available
```

**并发安全保障**

- 账户余额更新使用乐观锁（version 字段）
- 数据库 CHECK 约束：`available >= 0 AND frozen >= 0`
- 同一用户的结算事件串行处理（Kafka partition by user_id）

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

### 3.4 下单全链路事件流

#### 3.4.1 完整事件流

```
用户下单 ──▶ [1] Order Service: 参数校验 + 风控检查
           │
           ▼
         [2] Account Service: 冻结资金 (同步 RPC)
           │
           ▼
         [3] Order Service: 写入订单 + Outbox Event (同一事务)
           │
           ▼
         [4] Kafka: order.created ──▶ Match Engine: 撮合
           │
           ▼
         [5] Kafka: trade.matched ──▶ 并行处理:
                                      ├── Account Service: 结算（解冻+扣款+加款）
                                      ├── Order Service: 更新订单状态
                                      ├── Market Service: 更新行情/K线
                                      └── Push Service: WebSocket 推送
```

#### 3.4.2 Kafka Topic 设计

| Topic | Partition Key | 分区数 | 用途 |
| ----- | ------------- | ------ | ---- |
| `order.created` | symbol | 按交易对数 | 新订单 → 撮合引擎 |
| `trade.matched` | symbol | 按交易对数 | 成交结果 → 结算/行情 |
| `order.updated` | user_id | 16 | 订单状态变更 → 推送 |
| `account.updated` | user_id | 16 | 账户余额变更 → 推送 |

#### 3.4.3 同步 vs 异步通信边界

| 调用关系 | 通信方式 | 理由 |
| -------- | -------- | ---- |
| Order → Account（冻结） | 同步 RPC (OpenFeign) | 冻结失败需立即拒单 |
| Order → Match（新订单） | 异步 Kafka | 解耦，撮合引擎独立消费 |
| Match → Account（结算） | 异步 Kafka | 最终一致性，避免阻塞撮合 |
| Match → Market（行情） | 异步 Kafka | 行情容许短暂延迟 |
| Match → Order（状态更新） | 异步 Kafka | 非关键路径 |

#### 3.4.4 幂等性设计

- 每条事件携带全局唯一 `eventId`（Snowflake ID）
- 业务幂等键 `biz_id` 格式约定：

| 场景 | biz_id 格式 | 示例 |
| ---- | ----------- | ---- |
| 下单冻结 | `FREEZE:{orderId}` | `FREEZE:1234567890` |
| 成交结算（买方） | `TRADE:{tradeId}:BUYER` | `TRADE:9876543210:BUYER` |
| 成交结算（卖方） | `TRADE:{tradeId}:SELLER` | `TRADE:9876543210:SELLER` |
| 撤单解冻 | `CANCEL:{orderId}` | `CANCEL:1234567890` |
| 充值入账 | `DEPOSIT:{txHash}:{vout}` | `DEPOSIT:0xabc123:0` |

#### 3.4.5 失败补偿

- **Outbox Pattern**：订单写入与事件写入在同一数据库事务，独立线程扫描发送至 Kafka
- **DLQ（死信队列）**：消费失败超过 3 次 → 进入 `*.dlq` topic → 触发告警 → 人工介入
- **对账任务**：每日凌晨比对未完成状态数据

### 3.5 充提系统设计

#### 3.5.1 WalletService 接口定义

```java
public interface WalletService {

    /**
     * 生成充值地址
     * @param userId 用户ID
     * @param currency 币种
     * @param chain 链名称 (如 "ERC20", "TRC20")
     * @return 充值地址
     */
    String generateDepositAddress(Long userId, String currency, String chain);

    /**
     * 提交提现请求
     * @param request 提现请求（含用户ID、币种、链、地址、数量）
     * @return 提现记录ID
     */
    String submitWithdrawal(WithdrawalRequest request);

    /**
     * 查询充值状态（链上确认进度）
     * @param txHash 交易哈希
     * @return 确认数及状态
     */
    DepositStatus queryDepositStatus(String txHash);

    /**
     * 查询提现状态
     * @param withdrawalId 提现记录ID
     * @return 提现状态
     */
    WithdrawalStatus queryWithdrawalStatus(String withdrawalId);
}
```

#### 3.5.2 Mock 实现策略

```java
@Service
@Profile("mock")
public class MockWalletServiceImpl implements WalletService {
    // generateDepositAddress: 返回固定格式地址 "MOCK_{currency}_{userId}"
    // submitWithdrawal: 立即标记为"已广播"，3秒后异步标记为"已确认"
    // queryDepositStatus: 始终返回"已确认"
    // queryWithdrawalStatus: 始终返回"已完成"
}
```

#### 3.5.3 Profile 切换

```yaml
# application.yml
wallet:
  mode: mock  # mock | real

# 通过 Spring Profile 激活:
# mock 模式: -Dspring.profiles.active=mock
# 真实模式: -Dspring.profiles.active=real (需要对接真实链上节点)
```

#### 3.5.4 充值事件流

```
链上交易确认 → WalletService.queryDepositStatus (轮询/回调)
    → 确认数达标 → 发布 DepositConfirmedEvent
    → Account Service 消费 → 入账 (available += amount)
    → 记录流水: biz_id = "DEPOSIT:{txHash}:{vout}"
```

### 3.6 缓存策略

#### 3.6.1 Redis 缓存分层设计

| 数据类型 | Key Pattern | TTL | 更新方式 |
| -------- | ----------- | --- | -------- |
| 交易对配置 | `config:symbol:{symbol}` | 1h | 后台修改时主动删除 + TTL 兜底 |
| 用户费率 | `config:fee:{userId}` | 30min | 等级变化时主动删除 |
| Ticker | `market:ticker:{symbol}` | 不过期 | 成交后实时覆盖写入 |
| K 线 | `market:kline:{symbol}:{period}` | 按周期（1m→2h, 1d→30d） | 成交后增量更新 |
| 深度 | `market:depth:{symbol}` | 不过期 | 撮合后增量/全量更新 |
| 最近成交 | `market:trades:{symbol}` | 不过期 | LPUSH + LTRIM 保留最近 100 条 |
| Token 黑名单 | `auth:blacklist:{token}` | 等于 Token 剩余有效期 | 登出/修改密码时写入 |
| API 限流 | `ratelimit:{userId}:{endpoint}` | 滑动窗口 | Redis Lua 脚本原子递增 |

#### 3.6.2 一致性原则

- **行情数据**：弱一致性，容许秒级延迟，追求推送吞吐
- **配置数据**：主动删除 + TTL 双保险，变更后最多 TTL 时间内生效
- **资金数据**：**不走缓存**，直接读写 DB，保证强一致性

### 3.7 分布式事务保障

#### 3.7.1 Outbox Pattern 详细实现

```
同一数据库事务中:
  1. INSERT INTO orders (...) VALUES (...)
  2. INSERT INTO outbox_event (event_type, event_key, payload, status) VALUES (...)
  3. COMMIT

独立线程/定时任务 (每 500ms):
  1. SELECT * FROM outbox_event WHERE status = 'PENDING' ORDER BY id LIMIT 100
  2. 逐条发送至 Kafka
  3. 发送成功 → UPDATE outbox_event SET status = 'SENT'
  4. 发送失败 → retry_count++ ，下次继续
  5. retry_count > 5 → status = 'FAILED' → 触发告警
```

#### 3.7.2 消费端幂等

- `account_flow` 表 `UNIQUE KEY uk_biz_id (biz_id)` 保证幂等
- 消费逻辑：先 INSERT 流水，成功则执行余额更新；INSERT 冲突则跳过
- 消费端 ACK 策略：手动提交 offset，确保处理完成后才确认

#### 3.7.3 对账任务

```
每日凌晨 02:00 执行:
  1. 比对 trade.matched 中的 TradeEvent 与 account_flow 中已结算流水
  2. 找出"有成交无结算"的记录 → 自动补偿
  3. 找出"有结算无成交"的异常记录 → 告警人工介入
  4. 生成对账报告，发送至运维群组
```

#### 3.7.4 DLQ 处理

- 消费失败超过 3 次重试 → 消息转入 `{originalTopic}.dlq` 死信队列
- DLQ 消费者：记录日志 + 发送告警（邮件/钉钉）
- 人工介入后通过管理后台工具重放或手动处理

---

## 4. 技术选型

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
| **分片**     | ShardingSphere-JDBC  | 5.x    | 分库分表，JDBC 层透明  |
| **撮合队列** | Disruptor            | 4.0    | 高性能无锁环形队列     |
| **序列化**   | Protocol Buffers     | 3.x    | 快照序列化，紧凑高效   |
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
| **WebSocket** | reconnecting-websocket | -    | 原生 WebSocket + 自动重连，与后端 Spring WebSocket 协议匹配 |
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

**中间件选型**：Apache ShardingSphere-JDBC 5.x
- 以 JDBC 驱动方式嵌入应用，无需独立代理节点
- 对应用透明，MyBatis-Plus 无需改造
- 支持分布式主键、读写分离、数据加密

**分片规则**

| 数据表       | 分库 | 分表 | 分片键          | 分片算法 | 分片数 |
| ------------ | ---- | ---- | --------------- | -------- | ------ |
| user         | 否   | 否   | -               | -        | -      |
| account      | 否   | 否   | -               | -        | -      |
| account_flow | 否   | 是   | user_id         | user_id % 16 | 16 表 |
| orders       | 否   | 是   | user_id         | user_id % 16 | 16 表 |
| trades       | 否   | 是   | symbol          | symbol hash % 8 | 8 表 |
| kline        | 否   | 是   | symbol + period | (symbol + period) hash % 16 | 16 表 |

> 初期不做分库，仅做分表。数据量增长后可通过 ShardingSphere 配置平滑扩展为分库分表。

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
    client_order_id VARCHAR(64),       -- 客户端自定义订单ID
    symbol VARCHAR(20) NOT NULL,
    side TINYINT NOT NULL,             -- 1:买 2:卖
    type TINYINT NOT NULL,             -- 1:限价 2:市价
    time_in_force VARCHAR(10) DEFAULT 'GTC',  -- GTC/IOC/FOK
    price DECIMAL(32,16),
    quantity DECIMAL(32,16) NOT NULL,
    filled_quantity DECIMAL(32,16) DEFAULT 0,
    filled_amount DECIMAL(32,16) DEFAULT 0,   -- 累计成交额
    avg_price DECIMAL(32,16) DEFAULT 0,       -- 平均成交价
    fee DECIMAL(32,16) DEFAULT 0,             -- 累计手续费
    fee_currency VARCHAR(10),                 -- 手续费币种
    trigger_price DECIMAL(32,16),             -- 止盈止损触发价
    source VARCHAR(20) DEFAULT 'API',         -- API/WEB/APP
    status TINYINT NOT NULL,           -- 1:未成交 2:部分成交 3:全部成交 4:已撤销 5:部分撤销
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_status (user_id, status),
    INDEX idx_symbol_status (symbol, status),
    INDEX idx_client_order (user_id, client_order_id)
);

-- 成交表
CREATE TABLE trades (
    id BIGINT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    taker_order_id BIGINT NOT NULL,    -- Taker 订单ID
    maker_order_id BIGINT NOT NULL,    -- Maker 订单ID
    buy_order_id BIGINT NOT NULL,
    sell_order_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    price DECIMAL(32,16) NOT NULL,
    quantity DECIMAL(32,16) NOT NULL,
    amount DECIMAL(32,16) NOT NULL,    -- 成交额 = price × quantity
    buyer_fee DECIMAL(32,16) NOT NULL,
    seller_fee DECIMAL(32,16) NOT NULL,
    buyer_is_maker TINYINT NOT NULL,   -- 1:买方为Maker 0:卖方为Maker
    created_at DATETIME NOT NULL,
    INDEX idx_symbol_time (symbol, created_at),
    INDEX idx_buyer (buyer_id, created_at),
    INDEX idx_seller (seller_id, created_at)
);

-- 交易对配置表
CREATE TABLE symbol_config (
    id BIGINT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL UNIQUE,      -- 交易对名称 (如 BTC/USDT)
    base_currency VARCHAR(10) NOT NULL,      -- 基础币种 (如 BTC)
    quote_currency VARCHAR(10) NOT NULL,     -- 计价币种 (如 USDT)
    price_precision INT NOT NULL,            -- 价格精度
    quantity_precision INT NOT NULL,         -- 数量精度
    min_quantity DECIMAL(32,16) NOT NULL,    -- 最小下单量
    max_quantity DECIMAL(32,16) NOT NULL,    -- 最大下单量
    min_notional DECIMAL(32,16) NOT NULL,    -- 最小名义金额
    tick_size DECIMAL(32,16) NOT NULL,       -- 价格最小变动
    step_size DECIMAL(32,16) NOT NULL,       -- 数量最小变动
    maker_fee_rate DECIMAL(10,6) NOT NULL,   -- Maker 费率
    taker_fee_rate DECIMAL(10,6) NOT NULL,   -- Taker 费率
    status TINYINT DEFAULT 1,                -- 1:交易中 2:暂停 3:下架
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 币种配置表
CREATE TABLE currency_config (
    id BIGINT PRIMARY KEY,
    currency VARCHAR(10) NOT NULL,           -- 币种名称
    chain VARCHAR(20) NOT NULL,              -- 链名称 (如 BTC, ERC20, TRC20)
    confirmations INT NOT NULL,              -- 确认数
    min_deposit DECIMAL(32,16) NOT NULL,     -- 最小充值
    min_withdrawal DECIMAL(32,16) NOT NULL,  -- 最小提现
    withdrawal_fee DECIMAL(32,16) NOT NULL,  -- 提现手续费
    audit_threshold DECIMAL(32,16) NOT NULL, -- 大额审核阈值
    deposit_enabled TINYINT DEFAULT 1,       -- 充值开关
    withdrawal_enabled TINYINT DEFAULT 1,    -- 提现开关
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_currency_chain (currency, chain)
);

-- 本地事务消息表 (Outbox Pattern)
CREATE TABLE outbox_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type VARCHAR(50) NOT NULL,         -- 事件类型 (如 ORDER_CREATED, TRADE_MATCHED)
    event_key VARCHAR(64) NOT NULL,          -- 分区键 (如 symbol 或 user_id)
    payload TEXT NOT NULL,                   -- 事件 JSON 内容
    status VARCHAR(10) DEFAULT 'PENDING',    -- PENDING / SENT / FAILED
    retry_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_status_created (status, created_at)
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

---

## 12. 测试策略

### 12.1 测试金字塔

| 层级 | 覆盖率目标 | 工具 | 说明 |
| ---- | ---------- | ---- | ---- |
| 单元测试 | 撮合核心 >90%, 业务服务 >70% | JUnit 5 + Mockito | 纯逻辑测试，不依赖外部服务 |
| 集成测试 | 关键路径覆盖 | Testcontainers (MySQL, Redis, Kafka) | 启动真实中间件容器测试 |
| 性能测试 | 撮合 P99/吞吐达标 | JMH (Java Microbenchmark Harness) | 撮合引擎微基准测试 |
| 端到端测试 | 核心交易流程 | Docker Compose + REST Assured | 全服务启动，模拟真实下单→成交→结算 |

### 12.2 关键测试场景

| # | 场景 | 测试类型 | 验证点 |
| - | ---- | -------- | ------ |
| 1 | 限价单完整生命周期 | 集成 | 下单→冻结→撮合→成交→结算→余额正确 |
| 2 | 市价单无对手盘 | 单元 | 拒绝下单，不冻结资金 |
| 3 | 部分成交 + 撤单 | 集成 | 部分结算正确，剩余冻结解冻 |
| 4 | 自成交预防 | 单元 | 同一用户买卖不成交 |
| 5 | IOC/FOK 订单行为 | 单元 | IOC 撤未成交部分，FOK 全部或拒绝 |
| 6 | 并发余额扣减 | 集成 | 乐观锁重试，余额不为负 |
| 7 | 撮合引擎快照恢复 | 集成 | 快照+重放后订单簿状态一致 |
| 8 | Outbox 消息投递 | 集成 | 事务提交后消息最终到达 Kafka |
