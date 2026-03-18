# 📊 TurboX Exchange - 数据字典

> 版本: 1.0.0
> 更新日期: 2024-12-29
> 状态: 草案

---

## 1. 概述

| 项目 | 说明 |
| ---- | ---- |
| 数据库 | MySQL 8.0 |
| 分库分表中间件 | ShardingSphere-JDBC 5.x |
| 主键策略 | Snowflake ID（BIGINT） |
| 金额精度 | DECIMAL(32,16) |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |

**精度规范**

- 所有金额、价格、数量字段使用 `DECIMAL(32,16)`，严禁 `double`/`float`
- 费率字段使用 `DECIMAL(10,6)`
- 应用层使用 `BigDecimal`，舍入模式 `RoundingMode.DOWN`（截断）
- 手续费向上取整至最小精度（对平台有利）

---

## 2. 表清单与分片规则

| 表名 | 所属服务 | 分片键 | 分片算法 | 分片数 | 说明 |
| ---- | -------- | ------ | -------- | ------ | ---- |
| user | exchange-user | - | 不分片 | - | 用户表 |
| account | exchange-account | - | 不分片 | - | 账户表 |
| account_flow | exchange-account | user_id | user_id % 16 | 16 表 | 资金流水表 |
| orders | exchange-order | user_id | user_id % 16 | 16 表 | 订单表 |
| trades | exchange-order | symbol | symbol hash % 8 | 8 表 | 成交表 |
| kline | exchange-market | symbol + period | (symbol + period) hash % 16 | 16 表 | K 线数据表 |
| symbol_config | exchange-admin | - | 不分片 | - | 交易对配置表 |
| currency_config | exchange-admin | - | 不分片 | - | 币种配置表 |
| outbox_event | exchange-order | - | 不分片 | - | 事件发件箱表 |

> 初期仅做分表不做分库。数据量增长后可通过 ShardingSphere 配置平滑扩展为分库分表。

---

## 3. 各表详细字段说明

### 3.1 user — 用户表

所属服务：exchange-user

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    status TINYINT DEFAULT 1,
    kyc_level TINYINT DEFAULT 0,
    two_factor_enabled TINYINT DEFAULT 0,
    vip_level TINYINT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 用户 ID（Snowflake） |
| email | VARCHAR(100) | 是 | NULL | 邮箱地址（UNIQUE） |
| phone | VARCHAR(20) | 是 | NULL | 手机号（UNIQUE） |
| password_hash | VARCHAR(255) | 否 | - | BCrypt 加密后的密码 |
| nickname | VARCHAR(50) | 是 | NULL | 昵称 |
| status | TINYINT | 否 | 1 | 用户状态，见枚举 4.1 |
| kyc_level | TINYINT | 否 | 0 | KYC 认证等级，见枚举 4.2 |
| two_factor_enabled | TINYINT | 否 | 0 | 是否启用 2FA（0=未启用, 1=已启用） |
| vip_level | TINYINT | 否 | 0 | VIP 等级，见枚举 4.14 |
| created_at | DATETIME | 否 | - | 创建时间 |
| updated_at | DATETIME | 否 | - | 更新时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| email | UNIQUE | email | 邮箱唯一索引 |
| phone | UNIQUE | phone | 手机号唯一索引 |

---

### 3.2 account — 账户表

所属服务：exchange-account

```sql
CREATE TABLE account (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    available DECIMAL(32,16) NOT NULL DEFAULT 0,
    frozen DECIMAL(32,16) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_currency (user_id, currency)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 账户 ID（Snowflake） |
| user_id | BIGINT | 否 | - | 用户 ID |
| currency | VARCHAR(10) | 否 | - | 币种（如 BTC, USDT） |
| available | DECIMAL(32,16) | 否 | 0 | 可用余额 |
| frozen | DECIMAL(32,16) | 否 | 0 | 冻结余额 |
| version | INT | 否 | 0 | 乐观锁版本号 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| uk_user_currency | UNIQUE | (user_id, currency) | 每个用户每个币种唯一一条账户记录 |

**约束**

- `available >= 0`：可用余额不能为负
- `frozen >= 0`：冻结余额不能为负
- `version` 用于乐观锁控制并发更新

---

### 3.3 account_flow — 资金流水表

所属服务：exchange-account | 分片：user_id % 16

```sql
CREATE TABLE account_flow (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    change_amount DECIMAL(32,16) NOT NULL,
    balance_after DECIMAL(32,16) NOT NULL,
    flow_type VARCHAR(20) NOT NULL,
    biz_id VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_biz_id (biz_id)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 流水 ID（Snowflake） |
| user_id | BIGINT | 否 | - | 用户 ID（分片键） |
| currency | VARCHAR(10) | 否 | - | 币种 |
| change_amount | DECIMAL(32,16) | 否 | - | 变动金额（正数增加，负数减少） |
| balance_after | DECIMAL(32,16) | 否 | - | 变动后余额 |
| flow_type | VARCHAR(20) | 否 | - | 流水类型，见枚举 4.11 |
| biz_id | VARCHAR(64) | 否 | - | 业务 ID（幂等键） |
| created_at | DATETIME | 否 | - | 创建时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| uk_biz_id | UNIQUE | biz_id | 幂等性保证，防止重复结算 |

**biz_id 格式约定**

| 场景 | 格式 | 示例 |
| ---- | ---- | ---- |
| 下单冻结 | `FREEZE:{orderId}` | `FREEZE:1234567890` |
| 成交结算（买方） | `TRADE:{tradeId}:BUYER` | `TRADE:9876543210:BUYER` |
| 成交结算（卖方） | `TRADE:{tradeId}:SELLER` | `TRADE:9876543210:SELLER` |
| 撤单解冻 | `CANCEL:{orderId}` | `CANCEL:1234567890` |
| 充值入账 | `DEPOSIT:{txHash}:{vout}` | `DEPOSIT:0xabc123:0` |

---

### 3.4 orders — 订单表

所属服务：exchange-order | 分片：user_id % 16

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    client_order_id VARCHAR(64),
    symbol VARCHAR(20) NOT NULL,
    side TINYINT NOT NULL,
    type TINYINT NOT NULL,
    time_in_force VARCHAR(10) DEFAULT 'GTC',
    price DECIMAL(32,16),
    quantity DECIMAL(32,16) NOT NULL,
    filled_quantity DECIMAL(32,16) DEFAULT 0,
    filled_amount DECIMAL(32,16) DEFAULT 0,
    avg_price DECIMAL(32,16) DEFAULT 0,
    fee DECIMAL(32,16) DEFAULT 0,
    fee_currency VARCHAR(10),
    trigger_price DECIMAL(32,16),
    source VARCHAR(20) DEFAULT 'API',
    status TINYINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_status (user_id, status),
    INDEX idx_symbol_status (symbol, status),
    INDEX idx_client_order (user_id, client_order_id)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 订单 ID（Snowflake） |
| user_id | BIGINT | 否 | - | 用户 ID（分片键） |
| client_order_id | VARCHAR(64) | 是 | NULL | 客户端自定义订单 ID |
| symbol | VARCHAR(20) | 否 | - | 交易对（如 BTCUSDT） |
| side | TINYINT | 否 | - | 买卖方向，见枚举 4.3 |
| type | TINYINT | 否 | - | 订单类型，见枚举 4.4 |
| time_in_force | VARCHAR(10) | 否 | GTC | 有效方式，见枚举 4.5 |
| price | DECIMAL(32,16) | 是 | NULL | 委托价格（市价单为空） |
| quantity | DECIMAL(32,16) | 否 | - | 委托数量 |
| filled_quantity | DECIMAL(32,16) | 否 | 0 | 已成交数量 |
| filled_amount | DECIMAL(32,16) | 否 | 0 | 累计成交额（price × quantity 之和） |
| avg_price | DECIMAL(32,16) | 否 | 0 | 平均成交价 |
| fee | DECIMAL(32,16) | 否 | 0 | 累计手续费 |
| fee_currency | VARCHAR(10) | 是 | NULL | 手续费币种（买单=base，卖单=quote） |
| trigger_price | DECIMAL(32,16) | 是 | NULL | 止盈止损触发价格 |
| source | VARCHAR(20) | 否 | API | 订单来源，见枚举 4.7 |
| status | TINYINT | 否 | - | 订单状态，见枚举 4.6 |
| created_at | DATETIME | 否 | - | 创建时间 |
| updated_at | DATETIME | 否 | - | 更新时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| idx_user_status | 普通 | (user_id, status) | 按用户查询指定状态的订单 |
| idx_symbol_status | 普通 | (symbol, status) | 按交易对查询指定状态的订单 |
| idx_client_order | 普通 | (user_id, client_order_id) | 按客户端订单 ID 查询 |

---

### 3.5 trades — 成交表

所属服务：exchange-order | 分片：symbol hash % 8

```sql
CREATE TABLE trades (
    id BIGINT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    taker_order_id BIGINT NOT NULL,
    maker_order_id BIGINT NOT NULL,
    buy_order_id BIGINT NOT NULL,
    sell_order_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    price DECIMAL(32,16) NOT NULL,
    quantity DECIMAL(32,16) NOT NULL,
    amount DECIMAL(32,16) NOT NULL,
    buyer_fee DECIMAL(32,16) NOT NULL,
    seller_fee DECIMAL(32,16) NOT NULL,
    buyer_is_maker TINYINT NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_symbol_time (symbol, created_at),
    INDEX idx_buyer (buyer_id, created_at),
    INDEX idx_seller (seller_id, created_at)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 成交 ID（Snowflake） |
| symbol | VARCHAR(20) | 否 | - | 交易对（分片键） |
| taker_order_id | BIGINT | 否 | - | Taker 订单 ID |
| maker_order_id | BIGINT | 否 | - | Maker 订单 ID |
| buy_order_id | BIGINT | 否 | - | 买方订单 ID |
| sell_order_id | BIGINT | 否 | - | 卖方订单 ID |
| buyer_id | BIGINT | 否 | - | 买方用户 ID |
| seller_id | BIGINT | 否 | - | 卖方用户 ID |
| price | DECIMAL(32,16) | 否 | - | 成交价格 |
| quantity | DECIMAL(32,16) | 否 | - | 成交数量 |
| amount | DECIMAL(32,16) | 否 | - | 成交额（price × quantity） |
| buyer_fee | DECIMAL(32,16) | 否 | - | 买方手续费 |
| seller_fee | DECIMAL(32,16) | 否 | - | 卖方手续费 |
| buyer_is_maker | TINYINT | 否 | - | 买方是否为 Maker，见枚举 4.8 |
| created_at | DATETIME | 否 | - | 成交时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| idx_symbol_time | 普通 | (symbol, created_at) | 按交易对和时间查询成交 |
| idx_buyer | 普通 | (buyer_id, created_at) | 按买方查询成交 |
| idx_seller | 普通 | (seller_id, created_at) | 按卖方查询成交 |

---

### 3.6 symbol_config — 交易对配置表

所属服务：exchange-admin

```sql
CREATE TABLE symbol_config (
    id BIGINT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL UNIQUE,
    base_currency VARCHAR(10) NOT NULL,
    quote_currency VARCHAR(10) NOT NULL,
    price_precision INT NOT NULL,
    quantity_precision INT NOT NULL,
    min_quantity DECIMAL(32,16) NOT NULL,
    max_quantity DECIMAL(32,16) NOT NULL,
    min_notional DECIMAL(32,16) NOT NULL,
    tick_size DECIMAL(32,16) NOT NULL,
    step_size DECIMAL(32,16) NOT NULL,
    maker_fee_rate DECIMAL(10,6) NOT NULL,
    taker_fee_rate DECIMAL(10,6) NOT NULL,
    status TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 配置 ID（Snowflake） |
| symbol | VARCHAR(20) | 否 | - | 交易对名称（如 BTCUSDT，UNIQUE） |
| base_currency | VARCHAR(10) | 否 | - | 基础币种（如 BTC） |
| quote_currency | VARCHAR(10) | 否 | - | 计价币种（如 USDT） |
| price_precision | INT | 否 | - | 价格小数位数 |
| quantity_precision | INT | 否 | - | 数量小数位数 |
| min_quantity | DECIMAL(32,16) | 否 | - | 最小下单量 |
| max_quantity | DECIMAL(32,16) | 否 | - | 最大下单量 |
| min_notional | DECIMAL(32,16) | 否 | - | 最小名义金额（price × quantity） |
| tick_size | DECIMAL(32,16) | 否 | - | 价格最小变动单位 |
| step_size | DECIMAL(32,16) | 否 | - | 数量最小变动单位 |
| maker_fee_rate | DECIMAL(10,6) | 否 | - | Maker 基础费率（如 0.001000 = 0.1%） |
| taker_fee_rate | DECIMAL(10,6) | 否 | - | Taker 基础费率（如 0.002000 = 0.2%） |
| status | TINYINT | 否 | 1 | 交易对状态，见枚举 4.9 |
| sort_order | INT | 否 | 0 | 前端展示排序 |
| created_at | DATETIME | 否 | - | 创建时间 |
| updated_at | DATETIME | 否 | - | 更新时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| symbol | UNIQUE | symbol | 交易对名称唯一 |

---

### 3.7 currency_config — 币种配置表

所属服务：exchange-admin

```sql
CREATE TABLE currency_config (
    id BIGINT PRIMARY KEY,
    currency VARCHAR(10) NOT NULL,
    chain VARCHAR(20) NOT NULL,
    confirmations INT NOT NULL,
    min_deposit DECIMAL(32,16) NOT NULL,
    min_withdrawal DECIMAL(32,16) NOT NULL,
    withdrawal_fee DECIMAL(32,16) NOT NULL,
    audit_threshold DECIMAL(32,16) NOT NULL,
    deposit_enabled TINYINT DEFAULT 1,
    withdrawal_enabled TINYINT DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_currency_chain (currency, chain)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 配置 ID（Snowflake） |
| currency | VARCHAR(10) | 否 | - | 币种名称（如 BTC, USDT） |
| chain | VARCHAR(20) | 否 | - | 链名称（如 BTC, ERC20, TRC20） |
| confirmations | INT | 否 | - | 充值确认数 |
| min_deposit | DECIMAL(32,16) | 否 | - | 最小充值金额 |
| min_withdrawal | DECIMAL(32,16) | 否 | - | 最小提现金额 |
| withdrawal_fee | DECIMAL(32,16) | 否 | - | 提现手续费 |
| audit_threshold | DECIMAL(32,16) | 否 | - | 大额提现审核阈值 |
| deposit_enabled | TINYINT | 否 | 1 | 充值开关，见枚举 4.10 |
| withdrawal_enabled | TINYINT | 否 | 1 | 提现开关（0=关闭, 1=开启） |
| created_at | DATETIME | 否 | - | 创建时间 |
| updated_at | DATETIME | 否 | - | 更新时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| uk_currency_chain | UNIQUE | (currency, chain) | 每个币种在每条链上唯一配置 |

---

### 3.8 kline — K 线数据表

所属服务：exchange-market | 分片：(symbol + period) hash % 16

```sql
CREATE TABLE kline (
    id BIGINT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    period VARCHAR(10) NOT NULL,
    open_time BIGINT NOT NULL,
    open_price DECIMAL(32,16) NOT NULL,
    high_price DECIMAL(32,16) NOT NULL,
    low_price DECIMAL(32,16) NOT NULL,
    close_price DECIMAL(32,16) NOT NULL,
    volume DECIMAL(32,16) NOT NULL DEFAULT 0,
    quote_volume DECIMAL(32,16) NOT NULL DEFAULT 0,
    trade_count INT NOT NULL DEFAULT 0,
    close_time BIGINT NOT NULL,
    UNIQUE KEY uk_symbol_period_time (symbol, period, open_time)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | - | 记录 ID（Snowflake） |
| symbol | VARCHAR(20) | 否 | - | 交易对（分片键之一） |
| period | VARCHAR(10) | 否 | - | K 线周期（1m/5m/15m/1h/4h/1d/1w，分片键之一） |
| open_time | BIGINT | 否 | - | 开盘时间（毫秒时间戳） |
| open_price | DECIMAL(32,16) | 否 | - | 开盘价 |
| high_price | DECIMAL(32,16) | 否 | - | 最高价 |
| low_price | DECIMAL(32,16) | 否 | - | 最低价 |
| close_price | DECIMAL(32,16) | 否 | - | 收盘价 |
| volume | DECIMAL(32,16) | 否 | 0 | 成交量（base 币种） |
| quote_volume | DECIMAL(32,16) | 否 | 0 | 成交额（quote 币种） |
| trade_count | INT | 否 | 0 | 成交笔数 |
| close_time | BIGINT | 否 | - | 收盘时间（毫秒时间戳） |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键 |
| uk_symbol_period_time | UNIQUE | (symbol, period, open_time) | 每个交易对每个周期每个时间点唯一 |

---

### 3.9 outbox_event — 事件发件箱表

所属服务：exchange-order

```sql
CREATE TABLE outbox_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type VARCHAR(50) NOT NULL,
    event_key VARCHAR(64) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(10) DEFAULT 'PENDING',
    retry_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_status_created (status, created_at)
);
```

**字段说明**

| 字段 | 类型 | 可空 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| id | BIGINT | 否 | AUTO_INCREMENT | 事件 ID（自增） |
| event_type | VARCHAR(50) | 否 | - | 事件类型，见枚举 4.13 |
| event_key | VARCHAR(64) | 否 | - | Kafka 分区键（symbol 或 user_id） |
| payload | TEXT | 否 | - | 事件 JSON 内容 |
| status | VARCHAR(10) | 否 | PENDING | 投递状态，见枚举 4.12 |
| retry_count | INT | 否 | 0 | 重试次数（超过 5 次标记为 FAILED） |
| created_at | DATETIME | 否 | - | 创建时间 |
| updated_at | DATETIME | 否 | - | 更新时间 |

**索引**

| 索引名 | 类型 | 字段 | 说明 |
| ------ | ---- | ---- | ---- |
| PRIMARY | 主键 | id | 主键（自增） |
| idx_status_created | 普通 | (status, created_at) | 扫描待发送事件（status=PENDING） |

**Outbox 投递流程**

1. 订单写入与事件写入在同一数据库事务中（保证原子性）
2. 独立线程每 500ms 扫描 `status = 'PENDING'` 的事件，LIMIT 100
3. 逐条发送至 Kafka，成功后更新 `status = 'SENT'`
4. 发送失败则 `retry_count++`，下次继续
5. `retry_count > 5` 则 `status = 'FAILED'`，触发告警

---

## 4. 枚举值速查表

### 4.1 user.status — 用户状态

| 值 | 名称 | 说明 |
| -- | ---- | ---- |
| 1 | 正常 | 正常使用 |
| 2 | 冻结 | 账户被冻结，禁止登录和交易 |

### 4.2 user.kyc_level — KYC 认证等级

| 值 | 名称 | 日提现限额（USDT 等值） |
| -- | ---- | ---------------------- |
| 0 | 未认证 | 禁止提现 |
| 1 | 基础认证 | 10,000 USDT |
| 2 | 高级认证 | 1,000,000 USDT |

### 4.3 orders.side — 买卖方向

| 值 | 名称 | 说明 |
| -- | ---- | ---- |
| 1 | 买 (BUY) | 买入 base 币种 |
| 2 | 卖 (SELL) | 卖出 base 币种 |

### 4.4 orders.type — 订单类型

| 值 | 名称 | 说明 |
| -- | ---- | ---- |
| 1 | 限价 (LIMIT) | 指定价格委托 |
| 2 | 市价 (MARKET) | 以市场最优价成交 |
| 3 | 止盈止损 (STOP_LIMIT) | 触发价达到后按限价委托 |

### 4.5 orders.time_in_force — 有效方式

| 值 | 名称 | 说明 |
| -- | ---- | ---- |
| GTC | Good Till Cancel | 持续有效直到成交或手动撤单 |
| IOC | Immediate or Cancel | 立即成交，未成交部分立即撤销 |
| FOK | Fill or Kill | 全部成交或全部撤销 |

### 4.6 orders.status — 订单状态

| 值 | 名称 | 说明 |
| -- | ---- | ---- |
| 1 | 未成交 (NEW) | 订单已接受，等待撮合 |
| 2 | 部分成交 (PARTIALLY_FILLED) | 订单部分成交 |
| 3 | 全部成交 (FILLED) | 订单全部成交 |
| 4 | 已撤销 (CANCELLED) | 订单被撤销（未成交） |
| 5 | 部分撤销 (PARTIALLY_CANCELLED) | 订单部分成交后剩余部分被撤销 |

### 4.7 orders.source — 订单来源

| 值 | 说明 |
| -- | ---- |
| API | 通过 API Key 下单 |
| WEB | 通过 Web 页面下单 |
| APP | 通过移动 App 下单 |

### 4.8 trades.buyer_is_maker — 买方是否为 Maker

| 值 | 说明 |
| -- | ---- |
| 0 | 卖方为 Maker（买方主动吃单） |
| 1 | 买方为 Maker（卖方主动吃单） |

### 4.9 symbol_config.status — 交易对状态

| 值 | 名称 | 说明 |
| -- | ---- | ---- |
| 1 | 交易中 | 正常交易 |
| 2 | 暂停 | 暂停交易（不接受新订单，已有订单保留） |
| 3 | 下架 | 已下架（不可交易） |

### 4.10 currency_config.deposit_enabled / withdrawal_enabled — 充提开关

| 值 | 说明 |
| -- | ---- |
| 0 | 关闭 |
| 1 | 开启 |

### 4.11 account_flow.flow_type — 流水类型

| 值 | 说明 |
| -- | ---- |
| TRADE | 交易结算（成交扣款/入账） |
| DEPOSIT | 充值入账 |
| WITHDRAW | 提现扣款 |
| FREEZE | 下单冻结 |
| UNFREEZE | 撤单解冻 |

### 4.12 outbox_event.status — 事件投递状态

| 值 | 说明 |
| -- | ---- |
| PENDING | 待发送 |
| SENT | 已成功发送至 Kafka |
| FAILED | 发送失败（重试超过 5 次） |

### 4.13 outbox_event.event_type — 事件类型

| 值 | 对应 Kafka Topic | 说明 |
| -- | ---------------- | ---- |
| ORDER_CREATED | order.created | 新订单创建，发往撮合引擎 |
| TRADE_MATCHED | trade.matched | 撮合成交，发往结算/行情 |
| ORDER_UPDATED | order.updated | 订单状态变更，发往推送 |
| ACCOUNT_UPDATED | account.updated | 账户余额变更，发往推送 |

### 4.14 user.vip_level — VIP 等级

| 值 | 等级 | 30 天交易量（USDT 等值） | Maker 费率 | Taker 费率 |
| -- | ---- | ---------------------- | ---------- | ---------- |
| 0 | VIP0 | < 100,000 | 0.10% | 0.20% |
| 1 | VIP1 | ≥ 100,000 | 0.08% | 0.18% |
| 2 | VIP2 | ≥ 1,000,000 | 0.06% | 0.15% |
| 3 | VIP3 | ≥ 10,000,000 | 0.04% | 0.10% |

---

## 5. 表间关系说明

- **user → account**：一个用户拥有多个币种的账户（1:N），通过 `account.user_id` 关联
- **user → orders**：一个用户可以创建多个订单（1:N），通过 `orders.user_id` 关联
- **user → account_flow**：一个用户拥有多条资金流水（1:N），通过 `account_flow.user_id` 关联
- **orders → trades**：一个订单可以产生多笔成交（1:N），通过 `trades.buy_order_id` 或 `trades.sell_order_id` 关联
- **symbol_config → orders**：交易对配置定义了订单的精度、限额等校验规则，通过 `orders.symbol` 引用 `symbol_config.symbol`
- **symbol_config → trades**：成交记录关联交易对，通过 `trades.symbol` 引用 `symbol_config.symbol`
- **currency_config → account**：币种配置定义了充提规则，通过 `account.currency` 引用 `currency_config.currency`
- **orders → outbox_event**：订单创建时同事务写入发件箱，确保事件可靠投递
