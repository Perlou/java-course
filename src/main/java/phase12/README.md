# Phase 12: 分布式系统

> **目标**：掌握分布式系统核心技术  
> **预计时长**：2 周  
> **前置条件**：Phase 11 完成

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 熟练使用 Redis
2. 掌握消息队列
3. 理解分布式事务
4. 实现分布式锁

---

## 📚 核心概念

### Redis

- 数据类型
- 缓存策略
- 分布式锁

### 消息队列

- RocketMQ / Kafka
- 消息可靠性
- 顺序消息

### 分布式事务

- 2PC / 3PC
- TCC
- Saga

---

## 📁 文件列表

| 文件                      | 描述             | 状态 |
| ------------------------- | ---------------- | ---- |
| `RedisBasics.java`        | Redis 数据类型   | ⏳   |
| `RedisCache.java`         | 缓存策略         | ⏳   |
| `DistributedLock.java`    | 分布式锁         | ⏳   |
| `RocketMQDemo.java`       | RocketMQ 使用    | ⏳   |
| `KafkaDemo.java`          | Kafka 消息       | ⏳   |
| `MessageReliability.java` | 消息可靠性       | ⏳   |
| `SeataDemo.java`          | Seata 分布式事务 | ⏳   |
| `DistributedIdDemo.java`  | 分布式 ID        | ⏳   |

---

## ✅ 完成检查

- [ ] 能够设计缓存方案
- [ ] 理解消息队列使用场景
- [ ] 完成秒杀系统项目
