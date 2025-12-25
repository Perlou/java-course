# Phase 12: 分布式系统 - 学习总结

> **完成状态**: ✅ 已完成  
> **学习时间**: 2025 年 12 月

---

## 🎯 学习目标达成

| 目标                          | 状态 |
| ----------------------------- | ---- |
| 熟练使用 Redis 五种数据类型   | ✅   |
| 掌握缓存设计 (穿透/击穿/雪崩) | ✅   |
| 实现分布式锁 (Redisson)       | ✅   |
| 理解 RocketMQ 消息队列        | ✅   |
| 掌握 Kafka 分区消费           | ✅   |
| 保证消息可靠性和幂等性        | ✅   |
| 理解分布式事务 (Seata)        | ✅   |
| 实现分布式 ID 生成            | ✅   |
| 完成秒杀系统项目              | ✅   |

---

## 📚 核心知识点

### 1. Redis 数据类型 ([RedisBasics.java](./RedisBasics.java))

| 类型   | 命令               | 应用场景         |
| ------ | ------------------ | ---------------- |
| String | SET/GET/INCR       | 缓存、计数器     |
| Hash   | HSET/HGET/HGETALL  | 对象存储、购物车 |
| List   | LPUSH/RPUSH/LPOP   | 消息队列、时间线 |
| Set    | SADD/SINTER/SUNION | 去重、共同好友   |
| ZSet   | ZADD/ZRANGE/ZRANK  | 排行榜、延迟队列 |

---

### 2. 缓存问题 ([RedisCacheDemo.java](./RedisCacheDemo.java))

```
┌─────────────────┬──────────────────────────────────────────┐
│     问题        │             解决方案                      │
├─────────────────┼──────────────────────────────────────────┤
│ 缓存穿透        │ 布隆过滤器 / 缓存空值                    │
│ 缓存击穿        │ 互斥锁 / 逻辑过期                        │
│ 缓存雪崩        │ 随机过期 / 多级缓存 / 高可用             │
│ 一致性问题      │ 先更新数据库，再删除缓存                 │
└─────────────────┴──────────────────────────────────────────┘
```

---

### 3. 分布式锁 ([DistributedLock.java](./DistributedLock.java))

**Redis 分布式锁:**

```
SET lock:key value NX EX 30
```

**Redisson 推荐:**

```java
RLock lock = redisson.getLock("lock:key");
if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
    try {
        // 业务逻辑
    } finally {
        lock.unlock();
    }
}
```

Features: ✅ 自动续期 ✅ 可重入 ✅ 公平锁 ✅ 红锁

---

### 4. RocketMQ ([RocketMQDemo.java](./RocketMQDemo.java))

**消息类型:**

- 普通消息: syncSend
- 延迟消息: level 参数
- 顺序消息: syncSendOrderly
- 事务消息: TransactionListener

**核心概念:** Topic → MessageQueue → Consumer Group

---

### 5. Kafka ([KafkaDemo.java](./KafkaDemo.java))

```
┌─────────────────────────────────────────────────────────┐
│  Topic: orders                                          │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐          │
│  │ Partition0 │ │ Partition1 │ │ Partition2 │          │
│  │  Broker1   │ │  Broker2   │ │  Broker3   │          │
│  └────────────┘ └────────────┘ └────────────┘          │
│       ↑              ↑              ↑                   │
│       └────── Consumer Group ───────┘                   │
└─────────────────────────────────────────────────────────┘
```

**关键:** 相同 Key → 同一分区 → 保证顺序

---

### 6. 消息可靠性 ([MessageReliability.java](./MessageReliability.java))

| 环节   | 保证方案                     |
| ------ | ---------------------------- |
| 生产者 | 同步发送 + 重试 + 本地消息表 |
| Broker | 同步刷盘 + 多副本            |
| 消费者 | 手动确认 + 幂等设计          |

**幂等方案:** 唯一 ID 去重 / 业务唯一键 / 状态机

---

### 7. Seata 分布式事务 ([SeataDemo.java](./SeataDemo.java))

**AT 模式 (推荐):**

```java
@GlobalTransactional
public void createOrder(OrderDTO dto) {
    orderMapper.insert(order);     // 本地事务
    storageClient.deduct(...);     // 远程调用
    accountClient.debit(...);      // 远程调用
    // 任何失败自动回滚
}
```

**原理:** undo_log 记录修改前数据，失败时反向补偿

---

### 8. 分布式 ID ([DistributedIdDemo.java](./DistributedIdDemo.java))

**雪花算法 (64 位):**

```
┌───┬─────────────┬──────────┬────────────┐
│ 0 │ 41位时间戳  │ 10位机器 │ 12位序列号 │
└───┴─────────────┴──────────┴────────────┘
```

**常用工具:** Hutool IdUtil / MyBatis-Plus ASSIGN_ID

---

## 🎯 实战项目: 秒杀系统

[SeckillProject.java](./SeckillProject.java) 综合运用所有技术：

### 核心流程

```
用户请求 → Gateway限流 → Redis预扣库存(Lua) → MQ异步 → 创建订单
```

### 防超卖方案

```lua
-- Lua 脚本原子操作
local stock = redis.call('GET', KEYS[1])
if stock <= 0 then return 0 end
redis.call('DECR', KEYS[1])
return 1
```

### 技术栈

| 组件     | 作用                     |
| -------- | ------------------------ |
| Redis    | 库存预热、预扣、分布式锁 |
| RocketMQ | 异步下单削峰             |
| Sentinel | 限流降级                 |
| MySQL    | 数据持久化               |

---

## 🔑 关键收获

1. **Redis** 是分布式系统的核心基础设施
2. **Lua 脚本** 保证 Redis 操作原子性
3. **MQ 异步** 用于削峰和解耦
4. **幂等设计** 是消息消费的必备
5. **Seata AT** 无侵入解决分布式事务
6. **雪花算法** 生成趋势递增的分布式 ID

---

## 🛠 环境准备

```bash
# Redis
docker run -d --name redis -p 6379:6379 redis:7

# RocketMQ
docker run -d --name rmqnamesrv -p 9876:9876 apache/rocketmq:5.1.0 sh mqnamesrv

# Seata Server
docker run -d --name seata-server -p 8091:8091 seataio/seata-server
```

---

## 📈 进阶方向

- [ ] 学习 Redis Cluster 集群模式
- [ ] 深入 Kafka Streams 流处理
- [ ] 了解 Service Mesh (Istio)
- [ ] 进入 Phase 13: 性能调优

---

> 📝 Phase 12 完成！接下来进入 [Phase 13: 性能调优](../phase13/README.md)
