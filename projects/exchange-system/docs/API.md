# 📡 TurboX Exchange - API 接口文档

> 版本: 1.0.0
> 更新日期: 2024-12-29
> 状态: 草案

---

## 1. 概述

### 1.1 Base URL

```
https://{host}/api/v1
```

### 1.2 统一响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {},
  "timestamp": 1703836800000
}
```

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| code | int | 状态码，0 表示成功，非 0 表示错误 |
| message | string | 状态描述 |
| data | T | 业务数据（错误时为 null） |
| timestamp | long | 服务器时间戳（毫秒） |

### 1.3 分页

**请求参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| page | int | 否 | 1 | 页码（从 1 开始） |
| size | int | 否 | 20 | 每页数量（最大 100） |

**响应格式**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "total": 1000,
    "page": 1,
    "size": 20,
    "records": []
  },
  "timestamp": 1703836800000
}
```

---

## 2. 认证方式

### 2.1 Bearer Token (JWT)

适用于 Web/App 用户登录后的接口调用。

**请求头**

```
Authorization: Bearer {token}
```

- Access Token 有效期：2 小时
- Refresh Token 有效期：7 天
- Token 刷新：Access Token 过期后使用 Refresh Token 获取新 Token
- 登出后 Token 加入 Redis 黑名单，剩余有效期内不可使用

### 2.2 API Key 签名

适用于程序化交易（API 调用）。

**签名算法：HMAC-SHA256**

签名串构造：

```
签名串 = HTTP_METHOD + REQUEST_PATH + TIMESTAMP + BODY_MD5
Signature = HMAC-SHA256(签名串, API_SECRET)
```

- `HTTP_METHOD`：大写，如 `GET`、`POST`、`DELETE`
- `REQUEST_PATH`：不含域名，如 `/api/v1/order`
- `TIMESTAMP`：Unix 毫秒时间戳
- `BODY_MD5`：请求体 MD5 值（GET 请求为空字符串）

**请求头**

| Header | 说明 |
| ------ | ---- |
| X-API-KEY | API Key |
| X-TIMESTAMP | Unix 毫秒时间戳 |
| X-SIGNATURE | HMAC-SHA256 签名值 |

**防重放**：服务端校验 `X-TIMESTAMP` 与服务器时间差在 ±30 秒内，超出则拒绝。

**签名示例**

```bash
# 构造签名串
STRING_TO_SIGN="POST/api/v1/order1703836800000d41d8cd98f00b204e9800998ecf8427e"

# 计算签名
echo -n "${STRING_TO_SIGN}" | openssl dgst -sha256 -hmac "your_api_secret"
```

---

## 3. 用户模块

### 3.1 用户注册

`POST /api/v1/user/register`

**请求体**

| 字段 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| email | string | 是 | 邮箱地址 |
| password | string | 是 | 密码（8-32 位，含大小写字母和数字） |
| verifyCode | string | 是 | 邮箱验证码 |

**请求示例**

```bash
curl -X POST https://{host}/api/v1/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "MyPassword123",
    "verifyCode": "123456"
  }'
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "userId": 1234567890,
    "email": "user@example.com"
  },
  "timestamp": 1703836800000
}
```

**错误响应**

```json
{
  "code": 20003,
  "message": "EMAIL_EXISTED",
  "data": null,
  "timestamp": 1703836800000
}
```

### 3.2 用户登录

`POST /api/v1/user/login`

**请求体**

| 字段 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| email | string | 是 | 邮箱地址 |
| password | string | 是 | 密码 |
| twoFactorCode | string | 否 | 2FA 验证码（已绑定时必填） |

**请求示例**

```bash
curl -X POST https://{host}/api/v1/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "MyPassword123"
  }'
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 7200
  },
  "timestamp": 1703836800000
}
```

**错误响应**

```json
{
  "code": 20002,
  "message": "PASSWORD_INCORRECT",
  "data": null,
  "timestamp": 1703836800000
}
```

### 3.3 获取用户信息

`GET /api/v1/user/profile`

需要认证。

**请求示例**

```bash
curl https://{host}/api/v1/user/profile \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "userId": 1234567890,
    "email": "user@example.com",
    "nickname": "trader001",
    "status": 1,
    "kycLevel": 1,
    "twoFactorEnabled": true,
    "vipLevel": 0,
    "createdAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": 1703836800000
}
```

---

## 4. 账户模块

### 4.1 获取资产列表

`GET /api/v1/account/assets`

需要认证。返回用户所有币种的余额信息。

**请求示例**

```bash
curl https://{host}/api/v1/account/assets \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "currency": "BTC",
      "available": "1.23456789",
      "frozen": "0.10000000",
      "estimatedUsdt": "85432.10"
    },
    {
      "currency": "USDT",
      "available": "50000.00000000",
      "frozen": "10000.00000000",
      "estimatedUsdt": "60000.00"
    }
  ],
  "timestamp": 1703836800000
}
```

### 4.2 获取充值地址

`GET /api/v1/account/deposit/address`

需要认证。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| currency | string | 是 | 币种（如 BTC, USDT） |
| chain | string | 是 | 链名称（如 BTC, ERC20, TRC20） |

**请求示例**

```bash
curl "https://{host}/api/v1/account/deposit/address?currency=USDT&chain=TRC20" \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "currency": "USDT",
    "chain": "TRC20",
    "address": "TN7Qx2XkquXAf9DYtzPq6i8H9tLMjLGBMm",
    "memo": null,
    "minDeposit": "1"
  },
  "timestamp": 1703836800000
}
```

### 4.3 提现申请

`POST /api/v1/account/withdraw`

需要认证。

**请求体**

| 字段 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| currency | string | 是 | 币种 |
| chain | string | 是 | 链名称 |
| address | string | 是 | 提现地址 |
| amount | string | 是 | 提现数量 |
| twoFactorCode | string | 是 | 2FA 验证码 |

**请求示例**

```bash
curl -X POST https://{host}/api/v1/account/withdraw \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "currency": "USDT",
    "chain": "TRC20",
    "address": "TN7Qx2XkquXAf9DYtzPq6i8H9tLMjLGBMm",
    "amount": "100.00",
    "twoFactorCode": "123456"
  }'
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "withdrawalId": "W20240101000001",
    "currency": "USDT",
    "chain": "TRC20",
    "amount": "100.00",
    "fee": "1.00",
    "status": "PENDING"
  },
  "timestamp": 1703836800000
}
```

**错误响应**

```json
{
  "code": 30001,
  "message": "INSUFFICIENT_BALANCE",
  "data": null,
  "timestamp": 1703836800000
}
```

### 4.4 资金流水查询

`GET /api/v1/account/flows`

需要认证。支持分页。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| currency | string | 否 | 币种筛选 |
| flowType | string | 否 | 流水类型（TRADE/DEPOSIT/WITHDRAW/FREEZE/UNFREEZE） |
| startTime | long | 否 | 开始时间（毫秒时间戳） |
| endTime | long | 否 | 结束时间（毫秒时间戳） |
| page | int | 否 | 页码（默认 1） |
| size | int | 否 | 每页数量（默认 20） |

**请求示例**

```bash
curl "https://{host}/api/v1/account/flows?currency=USDT&page=1&size=20" \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "total": 150,
    "page": 1,
    "size": 20,
    "records": [
      {
        "id": 9876543210,
        "currency": "USDT",
        "changeAmount": "-50000.00000000",
        "balanceAfter": "10000.00000000",
        "flowType": "TRADE",
        "bizId": "TRADE:1234567890:BUYER",
        "createdAt": "2024-01-01T12:00:00Z"
      }
    ]
  },
  "timestamp": 1703836800000
}
```

---

## 5. 交易模块

### 5.1 下单

`POST /api/v1/order`

需要认证。支持限价单、市价单、止盈止损单。

**请求体**

| 字段 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| symbol | string | 是 | 交易对（如 BTCUSDT） |
| side | string | 是 | 买卖方向：BUY / SELL |
| type | string | 是 | 订单类型：LIMIT / MARKET / STOP_LIMIT |
| timeInForce | string | 否 | 有效方式：GTC（默认）/ IOC / FOK |
| price | string | 条件必填 | 委托价格（限价单必填） |
| quantity | string | 条件必填 | 委托数量（限价单和市价卖单必填） |
| quoteAmount | string | 条件必填 | 委托金额（市价买单必填，指定花费的 quote 金额） |
| triggerPrice | string | 条件必填 | 触发价格（止盈止损单必填） |
| clientOrderId | string | 否 | 客户端自定义订单 ID（最长 64 字符） |

**字段校验规则**

- `price` 必须是 `tickSize` 的整数倍（如 BTC/USDT tickSize=0.01，则 65000.015 不合法）
- `quantity` 必须是 `stepSize` 的整数倍（如 BTC/USDT stepSize=0.000001）
- `quantity` 范围：`[minQuantity, maxQuantity]`
- 名义金额（`price × quantity`）不低于 `minNotional`（如 10 USDT）
- 精度参考交易对配置（BTC/USDT：价格 2 位，数量 6 位）

**请求示例**

```bash
# 限价买单
curl -X POST https://{host}/api/v1/order \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "BTCUSDT",
    "side": "BUY",
    "type": "LIMIT",
    "timeInForce": "GTC",
    "price": "65000.00",
    "quantity": "0.001000",
    "clientOrderId": "my-order-001"
  }'
```

```bash
# 市价买单（指定花费金额）
curl -X POST https://{host}/api/v1/order \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "symbol": "BTCUSDT",
    "side": "BUY",
    "type": "MARKET",
    "quoteAmount": "1000.00"
  }'
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "orderId": 1234567890,
    "clientOrderId": "my-order-001",
    "symbol": "BTCUSDT",
    "side": "BUY",
    "type": "LIMIT",
    "timeInForce": "GTC",
    "price": "65000.00",
    "quantity": "0.001000",
    "status": "NEW",
    "createdAt": "2024-01-01T12:00:00Z"
  },
  "timestamp": 1703836800000
}
```

**错误响应示例**

```json
{
  "code": 40003,
  "message": "PRECISION_EXCEEDED",
  "data": null,
  "timestamp": 1703836800000
}
```

### 5.2 撤单

`DELETE /api/v1/order/{orderId}`

需要认证。

**请求示例**

```bash
curl -X DELETE https://{host}/api/v1/order/1234567890 \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "orderId": 1234567890,
    "status": "CANCELLED"
  },
  "timestamp": 1703836800000
}
```

### 5.3 查询当前委托

`GET /api/v1/order/open`

需要认证。支持分页。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| symbol | string | 否 | 交易对筛选 |
| page | int | 否 | 页码（默认 1） |
| size | int | 否 | 每页数量（默认 20） |

**请求示例**

```bash
curl "https://{host}/api/v1/order/open?symbol=BTCUSDT&page=1&size=20" \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "total": 5,
    "page": 1,
    "size": 20,
    "records": [
      {
        "orderId": 1234567890,
        "clientOrderId": "my-order-001",
        "symbol": "BTCUSDT",
        "side": "BUY",
        "type": "LIMIT",
        "timeInForce": "GTC",
        "price": "65000.00",
        "quantity": "0.001000",
        "filledQuantity": "0.000500",
        "filledAmount": "32.50",
        "avgPrice": "65000.00",
        "fee": "0.00000100",
        "feeCurrency": "BTC",
        "status": "PARTIALLY_FILLED",
        "createdAt": "2024-01-01T12:00:00Z",
        "updatedAt": "2024-01-01T12:05:00Z"
      }
    ]
  },
  "timestamp": 1703836800000
}
```

### 5.4 查询历史委托

`GET /api/v1/order/history`

需要认证。支持分页。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| symbol | string | 否 | 交易对筛选 |
| startTime | long | 否 | 开始时间（毫秒时间戳） |
| endTime | long | 否 | 结束时间（毫秒时间戳） |
| page | int | 否 | 页码（默认 1） |
| size | int | 否 | 每页数量（默认 20） |

**请求示例**

```bash
curl "https://{host}/api/v1/order/history?symbol=BTCUSDT&page=1&size=20" \
  -H "Authorization: Bearer {token}"
```

**成功响应**

响应格式同"当前委托"，records 中包含已完成和已撤销的订单。

### 5.5 查询用户成交明细

`GET /api/v1/order/trades`

需要认证。支持分页。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| symbol | string | 否 | 交易对筛选 |
| orderId | long | 否 | 订单 ID 筛选 |
| startTime | long | 否 | 开始时间（毫秒时间戳） |
| endTime | long | 否 | 结束时间（毫秒时间戳） |
| page | int | 否 | 页码（默认 1） |
| size | int | 否 | 每页数量（默认 20） |

**请求示例**

```bash
curl "https://{host}/api/v1/order/trades?symbol=BTCUSDT&page=1&size=20" \
  -H "Authorization: Bearer {token}"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "total": 100,
    "page": 1,
    "size": 20,
    "records": [
      {
        "tradeId": 9876543210,
        "symbol": "BTCUSDT",
        "orderId": 1234567890,
        "price": "65000.00",
        "quantity": "0.000500",
        "amount": "32.50",
        "fee": "0.00000100",
        "feeCurrency": "BTC",
        "isMaker": false,
        "side": "BUY",
        "createdAt": "2024-01-01T12:05:00Z"
      }
    ]
  },
  "timestamp": 1703836800000
}
```

---

## 6. 行情模块

行情接口无需认证。

### 6.1 所有交易对 24h 行情

`GET /api/v1/market/tickers`

**请求示例**

```bash
curl https://{host}/api/v1/market/tickers
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "symbol": "BTCUSDT",
      "lastPrice": "65000.00",
      "priceChange": "1200.00",
      "priceChangePercent": "1.88",
      "high": "66000.00",
      "low": "63500.00",
      "open": "63800.00",
      "volume": "12345.678900",
      "quoteVolume": "802469135.80",
      "timestamp": 1703836800000
    }
  ],
  "timestamp": 1703836800000
}
```

### 6.2 深度数据

`GET /api/v1/market/depth/{symbol}`

**请求参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| symbol | string | 是 | - | 路径参数，交易对 |
| limit | int | 否 | 20 | 档位数量（5/10/20/50） |

**请求示例**

```bash
curl "https://{host}/api/v1/market/depth/BTCUSDT?limit=5"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": {
    "symbol": "BTCUSDT",
    "bids": [
      ["64999.00", "1.234000"],
      ["64998.00", "0.500000"],
      ["64997.00", "2.100000"],
      ["64996.00", "0.800000"],
      ["64995.00", "3.000000"]
    ],
    "asks": [
      ["65001.00", "0.800000"],
      ["65002.00", "1.500000"],
      ["65003.00", "0.300000"],
      ["65004.00", "2.000000"],
      ["65005.00", "1.100000"]
    ],
    "timestamp": 1703836800000
  },
  "timestamp": 1703836800000
}
```

### 6.3 K 线数据

`GET /api/v1/market/klines`

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
| ---- | ---- | ---- | ---- |
| symbol | string | 是 | 交易对 |
| period | string | 是 | K 线周期：1m / 5m / 15m / 1h / 4h / 1d / 1w |
| from | long | 否 | 开始时间（毫秒时间戳） |
| to | long | 否 | 结束时间（毫秒时间戳） |
| limit | int | 否 | 返回数量（默认 500，最大 1500） |

**请求示例**

```bash
curl "https://{host}/api/v1/market/klines?symbol=BTCUSDT&period=1h&limit=100"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "openTime": 1703836800000,
      "open": "64800.00",
      "high": "65200.00",
      "low": "64700.00",
      "close": "65000.00",
      "volume": "123.456789",
      "quoteVolume": "8024691.35",
      "closeTime": 1703840399999,
      "tradeCount": 5678
    }
  ],
  "timestamp": 1703836800000
}
```

### 6.4 最近成交记录

`GET /api/v1/market/trades/{symbol}`

**请求参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| ---- | ---- | ---- | ------ | ---- |
| symbol | string | 是 | - | 路径参数，交易对 |
| limit | int | 否 | 100 | 返回数量（最大 500） |

**请求示例**

```bash
curl "https://{host}/api/v1/market/trades/BTCUSDT?limit=50"
```

**成功响应**

```json
{
  "code": 0,
  "message": "SUCCESS",
  "data": [
    {
      "tradeId": 9876543210,
      "price": "65000.00",
      "quantity": "0.001000",
      "amount": "65.00",
      "buyerIsMaker": false,
      "createdAt": 1703836800000
    }
  ],
  "timestamp": 1703836800000
}
```

---

## 7. WebSocket 协议

### 7.1 连接地址

```
ws://{host}/ws
```

生产环境使用 `wss://` 加密连接。

### 7.2 消息格式

**订阅请求**

```json
{
  "method": "SUBSCRIBE",
  "params": ["ticker.BTCUSDT", "depth.BTCUSDT"]
}
```

**取消订阅**

```json
{
  "method": "UNSUBSCRIBE",
  "params": ["ticker.BTCUSDT"]
}
```

**订阅确认响应**

```json
{
  "method": "SUBSCRIBE",
  "params": ["ticker.BTCUSDT"],
  "code": 0,
  "message": "SUCCESS"
}
```

### 7.3 公共频道

无需认证。

#### 7.3.1 实时行情 — `ticker.{symbol}`

```json
{
  "channel": "ticker.BTCUSDT",
  "data": {
    "symbol": "BTCUSDT",
    "lastPrice": "65000.00",
    "priceChange": "1200.00",
    "priceChangePercent": "1.88",
    "high": "66000.00",
    "low": "63500.00",
    "volume": "12345.678900",
    "quoteVolume": "802469135.80",
    "timestamp": 1703836800000
  }
}
```

#### 7.3.2 深度更新 — `depth.{symbol}`

推送增量更新，客户端本地维护深度快照。首次订阅返回全量快照。

```json
{
  "channel": "depth.BTCUSDT",
  "type": "snapshot",
  "data": {
    "bids": [["64999.00", "1.234000"], ["64998.00", "0.500000"]],
    "asks": [["65001.00", "0.800000"], ["65002.00", "1.500000"]],
    "timestamp": 1703836800000
  }
}
```

```json
{
  "channel": "depth.BTCUSDT",
  "type": "update",
  "data": {
    "bids": [["64999.00", "2.000000"]],
    "asks": [["65001.00", "0.000000"]],
    "timestamp": 1703836800100
  }
}
```

> 数量为 `"0.000000"` 表示该价位已无挂单，客户端应移除该档位。

#### 7.3.3 K 线更新 — `kline.{symbol}.{period}`

```json
{
  "channel": "kline.BTCUSDT.1m",
  "data": {
    "openTime": 1703836800000,
    "open": "64800.00",
    "high": "65200.00",
    "low": "64700.00",
    "close": "65000.00",
    "volume": "12.345678",
    "closed": false
  }
}
```

#### 7.3.4 最近成交 — `trade.{symbol}`

```json
{
  "channel": "trade.BTCUSDT",
  "data": {
    "tradeId": 9876543210,
    "price": "65000.00",
    "quantity": "0.001000",
    "buyerIsMaker": false,
    "timestamp": 1703836800000
  }
}
```

### 7.4 私有频道

需要认证。连接时通过消息进行身份验证：

```json
{
  "method": "AUTH",
  "params": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### 7.4.1 订单更新 — `order.{userId}`

认证成功后自动订阅。

```json
{
  "channel": "order",
  "data": {
    "orderId": 1234567890,
    "symbol": "BTCUSDT",
    "side": "BUY",
    "type": "LIMIT",
    "price": "65000.00",
    "quantity": "0.001000",
    "filledQuantity": "0.000500",
    "status": "PARTIALLY_FILLED",
    "updatedAt": 1703836800000
  }
}
```

#### 7.4.2 余额更新 — `account.{userId}`

认证成功后自动订阅。

```json
{
  "channel": "account",
  "data": {
    "currency": "BTC",
    "available": "1.23456789",
    "frozen": "0.10000000",
    "timestamp": 1703836800000
  }
}
```

### 7.5 心跳机制

- 客户端每 30 秒发送一次 `ping` 帧
- 服务端返回 `pong` 帧
- 服务端 60 秒未收到 `ping` 则断开连接

```json
{"method": "PING"}
```

```json
{"method": "PONG"}
```

### 7.6 重连策略

- 断线后采用指数退避重连：1s → 2s → 4s → 8s → 16s → 30s（上限）
- 重连后需重新订阅频道
- 推荐使用 `reconnecting-websocket` 库实现自动重连

---

## 8. 错误码大全

### 8.1 通用错误码 (1xxxx)

| 错误码 | 名称 | 说明 |
| ------ | ---- | ---- |
| 0 | SUCCESS | 成功 |
| 10001 | PARAM_ERROR | 参数错误 |
| 10002 | UNAUTHORIZED | 未认证（Token 无效或过期） |
| 10003 | FORBIDDEN | 无权限 |
| 10004 | NOT_FOUND | 资源不存在 |
| 10005 | RATE_LIMITED | 请求频率超限 |
| 10006 | INTERNAL_ERROR | 服务器内部错误 |

### 8.2 用户错误码 (2xxxx)

| 错误码 | 名称 | 说明 |
| ------ | ---- | ---- |
| 20001 | USER_NOT_FOUND | 用户不存在 |
| 20002 | PASSWORD_INCORRECT | 密码错误 |
| 20003 | EMAIL_EXISTED | 邮箱已注册 |
| 20004 | PHONE_EXISTED | 手机号已注册 |
| 20005 | VERIFY_CODE_INVALID | 验证码无效或已过期 |
| 20006 | TWO_FACTOR_REQUIRED | 需要 2FA 验证 |
| 20007 | TWO_FACTOR_INVALID | 2FA 验证码错误 |
| 20008 | KYC_REQUIRED | 需要完成 KYC 认证 |
| 20009 | USER_FROZEN | 用户已被冻结 |

### 8.3 账户错误码 (3xxxx)

| 错误码 | 名称 | 说明 |
| ------ | ---- | ---- |
| 30001 | INSUFFICIENT_BALANCE | 余额不足 |
| 30002 | ACCOUNT_FROZEN | 账户已冻结 |
| 30003 | WITHDRAWAL_LIMIT_EXCEEDED | 超出提现限额 |
| 30004 | WITHDRAWAL_ADDRESS_INVALID | 提现地址无效 |
| 30005 | DEPOSIT_DISABLED | 该币种充值已关闭 |
| 30006 | WITHDRAWAL_DISABLED | 该币种提现已关闭 |
| 30007 | AMOUNT_TOO_SMALL | 金额低于最小限制 |

### 8.4 交易错误码 (4xxxx)

| 错误码 | 名称 | 说明 |
| ------ | ---- | ---- |
| 40001 | SYMBOL_SUSPENDED | 交易对已暂停交易 |
| 40002 | PRICE_DEVIATION | 价格偏离市价过大（>10%） |
| 40003 | PRECISION_EXCEEDED | 精度超出交易对配置 |
| 40004 | QUANTITY_TOO_SMALL | 数量低于最小下单量 |
| 40005 | NOTIONAL_TOO_SMALL | 名义金额低于最小要求 |
| 40006 | QUANTITY_TOO_LARGE | 数量超过最大下单量 |
| 40007 | ORDER_NOT_FOUND | 订单不存在 |
| 40008 | ORDER_CANNOT_CANCEL | 订单状态不允许撤销 |
| 40009 | MARKET_NO_LIQUIDITY | 市价单无对手盘 |
| 40010 | CLIENT_ORDER_ID_DUPLICATE | 客户端订单 ID 重复 |

### 8.5 系统错误码 (5xxxx)

| 错误码 | 名称 | 说明 |
| ------ | ---- | ---- |
| 50001 | SERVICE_UNAVAILABLE | 服务暂时不可用 |
| 50002 | TIMEOUT | 请求超时 |
| 50003 | MATCH_ENGINE_UNAVAILABLE | 撮合引擎不可用 |

---

## 9. 限流规则

| 类别 | 限制 | 说明 |
| ---- | ---- | ---- |
| 公开接口 | 100 次/秒/IP | 行情等无需认证的接口 |
| 认证接口 | 50 次/秒/用户 | 需要 Token 的接口 |
| 下单接口 | 10 次/秒/用户 | POST /api/v1/order |
| 撤单接口 | 10 次/秒/用户 | DELETE /api/v1/order/{orderId} |
| WebSocket 连接 | 10 个连接/用户 | 超出后拒绝新连接 |

超限时返回：

```json
{
  "code": 10005,
  "message": "RATE_LIMITED",
  "data": null,
  "timestamp": 1703836800000
}
```

HTTP 状态码返回 `429 Too Many Requests`，响应头包含：

| Header | 说明 |
| ------ | ---- |
| X-RateLimit-Limit | 限流上限 |
| X-RateLimit-Remaining | 剩余次数 |
| X-RateLimit-Reset | 重置时间（Unix 秒） |
