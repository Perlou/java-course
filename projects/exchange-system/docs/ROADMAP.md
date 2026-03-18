# 📅 TurboX Exchange - 开发进度表

> 版本: 1.0.0
> 更新日期: 2026-03-18
> 状态: 执行中

---

## 1. 项目总览

### 1.1 基本信息

| 项目 | 说明 |
| ---- | ---- |
| 总阶段数 | 10 个阶段（P1 - P10） |
| 预估总工期 | 10 - 12 周 |
| Task 总数 | 112 个 |
| 核心服务数 | 9 个微服务 + 1 个前端 |

### 1.2 核心依赖链

```
P1 项目骨架
 ├── P2 用户服务 + 网关
 │    └── P3 账户服务
 │         └── P4 订单服务
 │              └── P5 撮合引擎
 │                   └── P6 全链路联调 ──┬── P7 行情服务
 │                                       ├── P8 风控 + 管理后台
 │                                       └── P9 前端 MVP
 │                                            └── P10 增强 + 优化 + 测试
```

### 1.3 阶段与 PRD 优先级映射

| 阶段 | 目标 | PRD 阶段 | ARCH 里程碑 | 预估工期 |
| ---- | ---- | -------- | ----------- | -------- |
| P1 | 项目骨架 + 基础设施 | MVP 前置 | M1 前半 | 3-4 天 |
| P2 | 用户服务 + 网关 | MVP | M1 后半 | 4-5 天 |
| P3 | 账户服务 | MVP | M2 前半 | 5-6 天 |
| P4 | 订单服务 | MVP | M2 后半 | 5-7 天 |
| P5 | 撮合引擎 MVP | MVP | M3 | 7-10 天 |
| P6 | 全链路联调 | MVP 验收 | M3 验收 | 3-4 天 |
| P7 | 行情服务 + WebSocket | MVP | M4 | 5-7 天 |
| P8 | 风控 + 管理后台 | 完善 | M6 | 7-10 天 |
| P9 | 前端 MVP | MVP/完善 | M5 | 7-10 天 |
| P10 | 增强 + 优化 + 测试 | 优化 | M7 + M8 | 7-10 天 |

---

## 2. 阶段明细

### P1: 项目骨架 + 基础设施

**阶段目标**: 搭建 Maven 多模块工程、exchange-common 公共基础类、Docker 中间件环境、数据库建表。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 1.1 | Maven 多模块父 POM + 各子模块 POM | 全局 | P0 | 低 | - | ⬜ |
| 1.2 | exchange-common 统一响应体 `Result<T>` | common | P0 | 低 | 1.1 | ⬜ |
| 1.3 | exchange-common 错误码枚举（5 大类 25+ 错误码） | common | P0 | 低 | 1.1 | ⬜ |
| 1.4 | exchange-common 业务枚举（OrderSide / OrderType / OrderStatus / FlowType 等 14 个） | common | P0 | 低 | 1.1 | ⬜ |
| 1.5 | exchange-common 分页 `PageRequest` / `PageResult` | common | P0 | 低 | 1.2 | ⬜ |
| 1.6 | exchange-common Snowflake ID 生成器 | common | P0 | 中 | 1.1 | ⬜ |
| 1.7 | exchange-common BigDecimal 精度工具类（RoundingMode.DOWN + 手续费向上取整） | common | P0 | 中 | 1.1 | ⬜ |
| 1.8 | exchange-common Kafka 事件 DTO（OrderCreatedEvent / TradeMatchedEvent / OrderUpdatedEvent / AccountUpdatedEvent） | common | P0 | 中 | 1.4 | ⬜ |
| 1.9 | docker-compose.infra.yml（MySQL 8.0 / Redis 7.0 / Kafka + ZooKeeper / Nacos / MinIO） | docker | P0 | 中 | - | ⬜ |
| 1.10 | 建表 SQL 脚本（9 张表: user / account / account_flow / orders / trades / kline / symbol_config / currency_config / outbox_event） | scripts | P0 | 中 | 1.9 | ⬜ |
| 1.11 | 初始数据 SQL（symbol_config: BTC/USDT + ETH/USDT，currency_config: BTC + ETH + USDT） | scripts | P0 | 低 | 1.10 | ⬜ |

**验收标准**:
- [ ] `mvn clean install` 全模块编译通过
- [ ] `docker-compose up` 所有中间件健康（MySQL / Redis / Kafka / Nacos / MinIO）
- [ ] 建表 SQL 和初始数据执行成功
- [ ] Result / 错误码 / 枚举 / Snowflake / BigDecimal 工具类单元测试通过

---

### P2: 用户服务 + 网关

**阶段目标**: 实现用户注册/登录/信息查询，搭建 API 网关（JWT 验证、签名校验、限流）。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 2.1 | exchange-user Spring Boot 启动 + Nacos 注册 | user | P0 | 低 | P1 | ⬜ |
| 2.2 | User Entity + MyBatis-Plus Mapper + Service 基础 CRUD | user | P0 | 低 | 2.1 | ⬜ |
| 2.3 | 用户注册 API `POST /api/v1/user/register`（邮箱 + BCrypt 密码 + 验证码） | user | P0 | 中 | 2.2 | ⬜ |
| 2.4 | 用户登录 API `POST /api/v1/user/login`（JWT Access/Refresh Token 生成） | user | P0 | 中 | 2.2 | ⬜ |
| 2.5 | 获取用户信息 API `GET /api/v1/user/profile` | user | P0 | 低 | 2.4 | ⬜ |
| 2.6 | Token 刷新 + Redis Token 黑名单（登出/修改密码时写入） | user | P0 | 中 | 2.4 | ⬜ |
| 2.7 | 忘记密码 API（邮箱验证码校验 → 重置密码） | user | P0 | 中 | 2.3 | ⬜ |
| 2.8 | 修改密码 API（原密码验证 → BCrypt 新密码 → Token 黑名单失效） | user | P0 | 中 | 2.6 | ⬜ |
| 2.9 | exchange-gateway Spring Cloud Gateway 启动 + 路由配置（转发至各服务） | gateway | P0 | 中 | P1 | ⬜ |
| 2.10 | JWT Token 验证全局过滤器（解析 Token → 注入用户上下文 → 白名单放行） | gateway | P0 | 中 | 2.4, 2.9 | ⬜ |
| 2.11 | API Key 签名验证过滤器（HMAC-SHA256 + 防重放 ±30s） | gateway | P1 | 中 | 2.9 | ⬜ |
| 2.12 | Redis Lua 限流过滤器（滑动窗口: 公开 100/s/IP, 认证 50/s/用户, 下单 10/s/用户） | gateway | P0 | 高 | 2.9 | ⬜ |
| 2.13 | 用户服务 + 网关联调验证（注册→登录→携带 Token 访问→限流） | user + gateway | P0 | 低 | 2.1-2.12 | ⬜ |

**验收标准**:
- [ ] 用户注册/登录/查询 API 正常工作
- [ ] Token 刷新和黑名单机制正确
- [ ] 网关路由转发正常，JWT 过滤器拦截无效 Token
- [ ] 限流在超限时返回 429 + RATE_LIMITED

---

### P3: 账户服务

**阶段目标**: 实现资产管理、冻结/解冻/结算、充提（Mock）、资金流水查询，保证幂等和乐观锁并发安全。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 3.1 | exchange-account Spring Boot 启动 + Nacos 注册 | account | P0 | 低 | P1 | ⬜ |
| 3.2 | Account / AccountFlow Entity + MyBatis-Plus Mapper | account | P0 | 低 | 3.1 | ⬜ |
| 3.3 | ShardingSphere-JDBC 配置（account_flow 分表: user_id % 16） | account | P0 | 高 | 3.2 | ⬜ |
| 3.4 | 获取资产列表 API `GET /api/v1/account/assets` | account | P0 | 低 | 3.2 | ⬜ |
| 3.5 | 冻结/解冻资金内部 RPC 接口（供 Order Service 通过 OpenFeign 同步调用） | account | P0 | 高 | 3.3 | ⬜ |
| 3.6 | 结算接口（Kafka consumer `trade.matched` → 解冻+扣款+加款，同事务写 account_flow） | account | P0 | 高 | 3.5 | ⬜ |
| 3.7 | 乐观锁（version 字段）+ 幂等性（biz_id UNIQUE KEY）实现 | account | P0 | 高 | 3.5 | ⬜ |
| 3.8 | 充值地址 API `GET /api/v1/account/deposit/address`（MockWalletService `@Profile("mock")`） | account | P0 | 中 | 3.2 | ⬜ |
| 3.9 | 提现申请 API `POST /api/v1/account/withdraw`（余额校验 + 限额检查 + 扣款） | account | P0 | 中 | 3.5 | ⬜ |
| 3.10 | 资金流水查询 API `GET /api/v1/account/flows`（分页 + 按币种/类型/时间筛选） | account | P0 | 中 | 3.3 | ⬜ |

**验收标准**:
- [ ] 资产列表、充值地址、提现申请、流水查询 API 正常工作
- [ ] 冻结/解冻/结算内部接口正确处理 biz_id 幂等
- [ ] 乐观锁在并发场景下正确重试，余额不为负
- [ ] ShardingSphere 分表路由正确（account_flow 按 user_id % 16 分表）

**关键测试里程碑**: 测试场景 #6（并发余额扣减）在此阶段编写

---

### P4: 订单服务

**阶段目标**: 实现下单（含参数校验 + Account 冻结调用）、撤单、委托查询、Outbox Pattern 可靠事件投递。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 4.1 | exchange-order Spring Boot 启动 + Nacos 注册 | order | P0 | 低 | P1 | ⬜ |
| 4.2 | Order / Trade / OutboxEvent Entity + MyBatis-Plus Mapper | order | P0 | 低 | 4.1 | ⬜ |
| 4.3 | ShardingSphere-JDBC 配置（orders: user_id % 16, trades: symbol hash % 8） | order | P0 | 高 | 4.2 | ⬜ |
| 4.4 | 下单 API `POST /api/v1/order`（参数校验: tickSize/stepSize/minQuantity/minNotional + symbol_config 查询 + Account 冻结 RPC） | order | P0 | 高 | 4.3, P3 | ⬜ |
| 4.5 | Outbox Pattern 实现（同事务写 orders + outbox_event） | order | P0 | 高 | 4.4 | ⬜ |
| 4.6 | Outbox 扫描投递线程（500ms 周期扫描 PENDING → Kafka `order.created` 发送 → 标记 SENT / FAILED） | order | P0 | 高 | 4.5 | ⬜ |
| 4.7 | 撤单 API `DELETE /api/v1/order/{orderId}`（状态校验 + Account 解冻 RPC + 发布 order.updated） | order | P0 | 中 | 4.4 | ⬜ |
| 4.8 | 当前委托查询 API `GET /api/v1/order/open`（分页 + 按交易对筛选） | order | P0 | 低 | 4.3 | ⬜ |
| 4.9 | 历史委托查询 API `GET /api/v1/order/history`（分页 + 按交易对/时间筛选） | order | P0 | 低 | 4.3 | ⬜ |
| 4.10 | 成交明细查询 API `GET /api/v1/order/trades`（分页 + 按交易对/订单/时间筛选） | order | P0 | 低 | 4.3 | ⬜ |
| 4.11 | 订单状态更新（Kafka consumer `trade.matched` → 更新 filled_quantity / avg_price / fee / status） | order | P0 | 中 | 4.3 | ⬜ |

**验收标准**:
- [ ] 下单 API 参数校验完整（精度、数量、金额、交易对状态）
- [ ] Outbox 事件在事务提交后被投递到 Kafka `order.created`
- [ ] 撤单正确解冻冻结资金
- [ ] 查询 API 分页、筛选正常工作
- [ ] ShardingSphere 分表路由正确

**关键测试里程碑**: 测试场景 #8（Outbox 消息投递）在此阶段编写

---

### P5: 撮合引擎 MVP

**阶段目标**: 实现内存撮合引擎核心（OrderBook + 限价/市价撮合 + Disruptor 队列 + 快照恢复），撮合核心单测覆盖率 >90%。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 5.1 | exchange-match 独立进程启动 + Kafka Consumer 配置（消费 `order.created`） | match | P0 | 中 | P1 | ⬜ |
| 5.2 | OrderBook 数据结构（TreeMap 双向订单簿: bids 降序 / asks 升序 + HashMap 订单索引） | match | P0 | 高 | 5.1 | ⬜ |
| 5.3 | 限价单撮合逻辑（价格优先 → 时间优先 → 部分成交支持） | match | P0 | 高 | 5.2 | ⬜ |
| 5.4 | 市价单撮合逻辑（买单按 quoteAmount 消耗 / 卖单按 quantity 消耗 / 无对手盘拒绝） | match | P0 | 高 | 5.3 | ⬜ |
| 5.5 | 自成交预防（同一 userId 买卖不成交，passive reject 后到达的订单） | match | P0 | 中 | 5.3 | ⬜ |
| 5.6 | Disruptor 无锁队列接入（Kafka consumer → Disruptor RingBuffer → 单线程撮合） | match | P0 | 高 | 5.3 | ⬜ |
| 5.7 | 成交事件发布（撮合结果 → Kafka `trade.matched` producer，partition by symbol） | match | P0 | 中 | 5.6 | ⬜ |
| 5.8 | Protobuf Schema 定义 + OrderBook 快照序列化/反序列化 | match | P0 | 高 | 5.2 | ⬜ |
| 5.9 | 快照触发机制（每 10,000 个事件或每 60 秒 → 写本地 SSD + 上传 MinIO） | match | P0 | 中 | 5.8 | ⬜ |
| 5.10 | 快照恢复 + Kafka 事件重放（加载快照 → 从 offset 重放 → replay=true 标记） | match | P0 | 高 | 5.9 | ⬜ |
| 5.11 | 撮合核心单元测试（覆盖率 >90%: 限价/市价/部分成交/自成交/空订单簿等） | match | P0 | 高 | 5.1-5.10 | ⬜ |

**验收标准**:
- [ ] 限价单撮合正确（价格/时间优先，部分成交）
- [ ] 市价单撮合正确（无对手盘时拒绝）
- [ ] 自成交预防生效
- [ ] Disruptor 单线程处理无锁无竞争
- [ ] 快照保存和恢复后订单簿状态一致
- [ ] 撮合核心单元测试覆盖率 > 90%

**关键测试里程碑**:
- 测试场景 #2（市价单无对手盘）
- 测试场景 #4（自成交预防）
- 测试场景 #5（IOC/FOK 基础行为）
- 测试场景 #7（撮合引擎快照恢复）

---

### P6: 全链路联调

**阶段目标**: 打通 Order → Account → Match → Settlement 完整事件链路，验证端到端交易流程正确性。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 6.1 | Order → Account 冻结同步 RPC 调用联调 | order + account | P0 | 中 | P3, P4 | ⬜ |
| 6.2 | Order → Kafka `order.created` → Match 撮合联调 | order + match | P0 | 中 | P4, P5 | ⬜ |
| 6.3 | Match → Kafka `trade.matched` → Account 结算联调（解冻+扣款+加款+流水） | match + account | P0 | 高 | P3, P5 | ⬜ |
| 6.4 | Match → Kafka `trade.matched` → Order 状态更新联调 | match + order | P0 | 中 | P4, P5 | ⬜ |
| 6.5 | 限价单完整生命周期端到端验证（下单→冻结→撮合→成交→结算→余额正确） | 全链路 | P0 | 高 | 6.1-6.4 | ⬜ |
| 6.6 | 部分成交 + 撤单端到端验证（部分结算正确 + 剩余冻结解冻） | 全链路 | P0 | 高 | 6.5 | ⬜ |
| 6.7 | 幂等性验证（Kafka 重复消息 → biz_id 去重 → 余额不重复变动） | account | P0 | 中 | 6.3 | ⬜ |
| 6.8 | DLQ 死信队列验证（消费失败 3 次 → 转入 `*.dlq` → 告警触发） | 全链路 | P1 | 中 | 6.3 | ⬜ |

**验收标准**:
- [ ] 限价买单/卖单下单→成交→结算→余额全流程正确
- [ ] 部分成交后撤单，剩余冻结正确解冻
- [ ] 重复消息不会导致重复结算
- [ ] 消费失败的消息正确进入 DLQ

**关键测试里程碑**:
- 测试场景 #1（限价单完整生命周期）
- 测试场景 #3（部分成交 + 撤单）

---

### P7: 行情服务 + WebSocket

**阶段目标**: 实现行情计算（Ticker / K 线 / 深度）、REST 查询接口、WebSocket 实时推送。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 7.1 | exchange-market Spring Boot 启动 + Nacos 注册 | market | P0 | 低 | P1 | ⬜ |
| 7.2 | Kline Entity + MyBatis-Plus Mapper + ShardingSphere 分表（(symbol+period) hash % 16） | market | P0 | 中 | 7.1 | ⬜ |
| 7.3 | Ticker 计算（Kafka consumer `trade.matched` → 计算 24h 开高低收量 → Redis `market:ticker:{symbol}`） | market | P0 | 中 | 7.1, P6 | ⬜ |
| 7.4 | K 线聚合（成交事件 → 多周期 1m/5m/15m/1h/4h/1d/1w 聚合 → Redis + DB 持久化） | market | P0 | 高 | 7.2 | ⬜ |
| 7.5 | 深度快照（从撮合引擎接收 depth 数据 → Redis `market:depth:{symbol}`） | market | P0 | 中 | P6 | ⬜ |
| 7.6 | 行情 REST API（`GET tickers` / `GET depth/{symbol}` / `GET klines` / `GET trades/{symbol}`） | market | P0 | 中 | 7.3-7.5 | ⬜ |
| 7.7 | WebSocket Server 搭建 + 订阅/取消订阅协议（SUBSCRIBE / UNSUBSCRIBE） | market | P0 | 高 | 7.1 | ⬜ |
| 7.8 | 公共频道推送（ticker.{symbol} / depth.{symbol} / kline.{symbol}.{period} / trade.{symbol}） | market | P0 | 高 | 7.3-7.7 | ⬜ |
| 7.9 | 私有频道推送（order / account，AUTH 认证 → 自动订阅 → 推送 order.updated / account.updated） | market | P1 | 中 | 7.7 | ⬜ |
| 7.10 | 心跳机制（PING/PONG，60s 无心跳断开）+ 连接数限制（10 连接/用户） | market | P0 | 中 | 7.7 | ⬜ |

**验收标准**:
- [ ] 行情 REST API 返回正确的 Ticker / 深度 / K 线 / 成交数据
- [ ] WebSocket 连接后订阅频道，实时收到行情推送
- [ ] 私有频道认证后推送订单和余额更新
- [ ] 心跳机制和连接数限制正常工作
- [ ] 行情端到端延迟 < 100ms

---

### P8: 风控 + 管理后台

**阶段目标**: 实现交易/账户风控规则，搭建管理后台（用户管理、交易管理、财务管理、系统管理）。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 8.1 | exchange-risk 风控规则引擎框架（内部服务，供 Order Service 调用） | risk | P0 | 中 | P1 | ⬜ |
| 8.2 | 价格保护规则（偏离市价 >10% 拒绝，返回 PRICE_DEVIATION） | risk | P0 | 中 | 8.1 | ⬜ |
| 8.3 | Redis Lua 下单频率限制（10 次/秒/用户） | risk | P0 | 中 | 8.1 | ⬜ |
| 8.4 | 单笔/日累计金额限制 | risk | P0 | 中 | 8.1 | ⬜ |
| 8.5 | 大额提现审核触发（超过 audit_threshold → 标记待审核） | risk | P0 | 中 | 8.1, P3 | ⬜ |
| 8.6 | 登录风控（异地登录 IP 检测 + 新设备验证 + 异常登录告警） | risk | P0 | 中 | 8.1, P2 | ⬜ |
| 8.7 | exchange-admin Spring Boot 启动 + Nacos 注册 | admin | P0 | 低 | P1 | ⬜ |
| 8.8 | 管理员 RBAC 权限体系（角色: 超级管理员 / 运营 / 客服 / 财务） | admin | P0 | 高 | 8.7 | ⬜ |
| 8.9 | 用户管理 API（用户列表/详情/冻结解冻） | admin | P0 | 中 | 8.7 | ⬜ |
| 8.10 | KYC 审核 API（审核列表/通过/拒绝） | admin | P1 | 中 | 8.7 | ⬜ |
| 8.11 | 交易对配置 API（增删改查 + 上下架 + 缓存失效通知） | admin | P0 | 中 | 8.7 | ⬜ |
| 8.12 | 手续费配置 API（VIP 阶梯费率设置） | admin | P0 | 低 | 8.11 | ⬜ |
| 8.13 | 订单/成交查询 API（跨服务查询: 调用 Order Service） | admin | P0 | 中 | 8.7, P4 | ⬜ |
| 8.14 | 充提审核 API（大额提现审批/拒绝） | admin | P0 | 中 | 8.7, P3 | ⬜ |
| 8.15 | 资产统计 + 手续费收入报表 | admin | P1 | 中 | 8.7 | ⬜ |
| 8.16 | 操作日志审计 + 系统全局参数配置 | admin | P0 | 中 | 8.7 | ⬜ |

**验收标准**:
- [ ] 风控规则正确拦截（价格偏离/频率超限/金额超限）
- [ ] 管理后台 RBAC 权限控制生效
- [ ] 交易对配置变更后缓存正确失效
- [ ] 充提审核流程完整（发起→审核→执行）
- [ ] 操作日志完整记录管理员操作

---

### P9: 前端 MVP

**阶段目标**: 实现交易平台 Web 端 MVP（注册登录、交易页面、资产管理、行情展示）。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 9.1 | Next.js 14 项目初始化（TypeScript + Tailwind CSS + Radix UI + Zustand） | web | P0 | 中 | - | ⬜ |
| 9.2 | 全局布局 + 路由结构（Header / Sidebar / 页面骨架） | web | P0 | 中 | 9.1 | ⬜ |
| 9.3 | 注册/登录页面 + Token 管理（Zustand 持久化 + Refresh Token 自动刷新） | web | P0 | 中 | 9.1, P2 | ⬜ |
| 9.4 | 资产总览页面（各币种余额列表 + USDT 估值） | web | P0 | 中 | 9.3, P3 | ⬜ |
| 9.5 | 交易页面布局（左侧订单簿 + 中间 K 线图 + 右侧下单面板 + 底部成交记录） | web | P0 | 高 | 9.2 | ⬜ |
| 9.6 | TradingView K 线图表集成（多周期切换 + 实时更新） | web | P0 | 高 | 9.5, P7 | ⬜ |
| 9.7 | WebSocket 实时数据接入（reconnecting-websocket: ticker / depth / kline / trade） | web | P0 | 高 | 9.5, P7 | ⬜ |
| 9.8 | 下单/撤单表单 + 当前委托列表 | web | P0 | 中 | 9.5, P4 | ⬜ |
| 9.9 | 历史委托 + 成交明细查询页面 | web | P0 | 中 | 9.3, P4 | ⬜ |
| 9.10 | 充值/提现页面（生成充值地址 + 提现表单 + 充提记录） | web | P0 | 中 | 9.3, P3 | ⬜ |

**验收标准**:
- [ ] 注册→登录→交易→查询完整用户流程可走通
- [ ] TradingView K 线图表实时更新
- [ ] WebSocket 推送数据实时展示（订单簿/成交记录/行情）
- [ ] 下单/撤单操作正常，当前委托实时更新
- [ ] 充值地址生成和提现表单正常工作

---

### P10: 增强 + 优化 + 测试

**阶段目标**: 完善功能（2FA / IOC/FOK / 止盈止损 / VIP费率）、性能优化、完整测试、部署流水线。

| # | Task | 服务 | 优先级 | 复杂度 | 前置依赖 | 状态 |
| - | ---- | ---- | ------ | ------ | -------- | ---- |
| 10.1 | 2FA 双因素认证（Google Authenticator TOTP 绑定 + 登录/提现时校验） | user | P1 | 中 | P2 | ⬜ |
| 10.2 | 止盈止损单实现（trigger_price 触发监控 → 触发后转为限价单） | order + match | P1 | 高 | P6 | ⬜ |
| 10.3 | IOC/FOK 订单完整支持（IOC: 立即成交→剩余撤销 / FOK: 全部成交或全部拒绝） | match | P1 | 中 | P5 | ⬜ |
| 10.4 | VIP 阶梯费率实现（30 天交易量统计 → 自动升降级 → 费率生效） | order + account | P1 | 中 | P6 | ⬜ |
| 10.5 | 撮合引擎主备（Redis 分布式锁选主 Redisson，TTL=30s，续期=10s，故障 RTO < 30s） | match | P1 | 高 | P5 | ⬜ |
| 10.6 | 撮合性能优化 + JMH 基准测试（目标: P99 < 100μs, 吞吐 > 50,000 orders/sec） | match | P1 | 高 | P5 | ⬜ |
| 10.7 | 集成测试（Testcontainers: MySQL + Redis + Kafka 容器化测试环境） | 全服务 | P0 | 高 | P6 | ⬜ |
| 10.8 | 端到端测试（Docker Compose 全服务启动 + REST Assured 自动化） | 全服务 | P1 | 高 | P6 | ⬜ |
| 10.9 | 可观测性（Prometheus 指标采集 + Grafana 面板 + Jaeger 链路追踪） | 全服务 | P1 | 中 | P6 | ⬜ |
| 10.10 | 对账任务（每日凌晨比对 trade.matched vs account_flow → 补偿/告警） | account | P1 | 中 | P6 | ⬜ |
| 10.11 | Kubernetes 部署配置（各服务 Deployment + Service + ConfigMap + Ingress） | 运维 | P1 | 中 | P6 | ⬜ |
| 10.12 | CI/CD 流水线（GitHub Actions: build → test → Docker image → deploy） | 运维 | P1 | 中 | 10.11 | ⬜ |

**验收标准**:
- [ ] 2FA 绑定和验证流程完整
- [ ] IOC/FOK/止盈止损订单行为正确
- [ ] VIP 费率按交易量自动升降级
- [ ] 撮合引擎 JMH 基准: P99 < 100μs, 吞吐 > 50,000 orders/sec
- [ ] 集成测试和端到端测试全部通过
- [ ] Prometheus + Grafana 监控面板正常展示
- [ ] CI/CD 流水线自动构建、测试、部署

---

## 3. 服务复杂度与工作量估算

| 服务 | 复杂度 | API 数 | 表数 | 分片表 | Kafka 主题 | 核心难点 |
| ---- | ------ | ------ | ---- | ------ | ---------- | -------- |
| exchange-common | 中 | 0 | 0 | 0 | 0 | BigDecimal 精度工具 / Snowflake ID / Protobuf Schema |
| exchange-gateway | 中 | 0 (透传) | 0 | 0 | 0 | JWT 验证 / HMAC-SHA256 签名 / Redis Lua 限流 |
| exchange-user | 中 | 3 | 1 | 0 | 0 | Spring Security / BCrypt / JWT / 2FA TOTP |
| exchange-account | 高 | 4 | 2 | 1 (account_flow × 16) | 2 (消费 trade.matched / 生产 account.updated) | 乐观锁 / biz_id 幂等 / 冻结结算 / MockWalletService |
| exchange-order | 高 | 5 | 3 | 2 (orders × 16, trades × 8) | 4 (生产 order.created + order.updated / 消费 trade.matched) | Outbox Pattern / 状态机 / 参数校验 / ShardingSphere |
| exchange-match | 极高 | 0 | 0 | 0 | 2 (消费 order.created / 生产 trade.matched) | Disruptor / TreeMap 订单簿 / Protobuf 快照恢复 / 主备选主 |
| exchange-market | 高 | 4 + WebSocket | 1 | 1 (kline × 16) | 3 (消费 trade.matched + order.updated + account.updated) | K 线多周期聚合 / WebSocket 推送 / 深度增量更新 |
| exchange-risk | 中 | 0 (内部) | 0 | 0 | 0 | 规则引擎 / Redis Lua 限流 / 价格保护 |
| exchange-admin | 中 | ~15 | 2 | 0 | 0 | RBAC 权限 / 跨服务查询 / 缓存失效 / 审核流程 |
| exchange-web | 高 | - | - | - | - | TradingView 集成 / WebSocket 实时数据 / 复杂交易表单 |

---

## 4. 关键测试里程碑

来自 ARCHITECTURE.md Section 12.2 的 8 个关键测试场景，标注执行时间点：

| # | 测试场景 | 测试类型 | 验证点 | 执行阶段 |
| - | -------- | -------- | ------ | -------- |
| 1 | 限价单完整生命周期 | 集成 | 下单→冻结→撮合→成交→结算→余额正确 | **P6** |
| 2 | 市价单无对手盘 | 单元 | 拒绝下单，不冻结资金 | **P5** |
| 3 | 部分成交 + 撤单 | 集成 | 部分结算正确，剩余冻结解冻 | **P6** |
| 4 | 自成交预防 | 单元 | 同一用户买卖不成交 | **P5** |
| 5 | IOC/FOK 订单行为 | 单元 | IOC 撤未成交部分，FOK 全部或拒绝 | **P5** (基础) / **P10** (完整) |
| 6 | 并发余额扣减 | 集成 | 乐观锁重试，余额不为负 | **P3** |
| 7 | 撮合引擎快照恢复 | 集成 | 快照+重放后订单簿状态一致 | **P5** |
| 8 | Outbox 消息投递 | 集成 | 事务提交后消息最终到达 Kafka | **P4** |

---

## 5. 进度追踪表

| 阶段 | 目标 | Task 总数 | 已完成 | 进度 | 状态 |
| ---- | ---- | --------- | ------ | ---- | ---- |
| P1 | 项目骨架 + 基础设施 | 11 | 0 | 0% | ⬜ 未开始 |
| P2 | 用户服务 + 网关 | 13 | 0 | 0% | ⬜ 未开始 |
| P3 | 账户服务 | 10 | 0 | 0% | ⬜ 未开始 |
| P4 | 订单服务 | 11 | 0 | 0% | ⬜ 未开始 |
| P5 | 撮合引擎 MVP | 11 | 0 | 0% | ⬜ 未开始 |
| P6 | 全链路联调 | 8 | 0 | 0% | ⬜ 未开始 |
| P7 | 行情服务 + WebSocket | 10 | 0 | 0% | ⬜ 未开始 |
| P8 | 风控 + 管理后台 | 16 | 0 | 0% | ⬜ 未开始 |
| P9 | 前端 MVP | 10 | 0 | 0% | ⬜ 未开始 |
| P10 | 增强 + 优化 + 测试 | 12 | 0 | 0% | ⬜ 未开始 |
| **合计** | | **112** | **0** | **0%** | |

**状态图标说明**: ⬜ 未开始 / 🔧 进行中 / ✅ 已完成
