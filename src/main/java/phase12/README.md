# Phase 12: 分布式系统

> **目标**：掌握分布式系统核心技术  
> **预计时长**：2 周  
> **前置条件**：Phase 11 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 熟练使用 Redis 数据类型和命令
2. 掌握缓存设计策略 (穿透、击穿、雪崩)
3. 实现分布式锁 (Redisson)
4. 理解消息队列 (RocketMQ/Kafka)
5. 掌握消息可靠性保证
6. 理解分布式事务方案 (Seata)
7. 实现分布式 ID 生成

---

## 📚 核心概念

### 分布式系统挑战

```
┌─────────────────────────────────────────────────────────┐
│                 分布式系统核心问题                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  数据一致性                                             │
│  ├── 缓存与数据库一致性                                │
│  ├── 分布式事务                                        │
│  └── 消息最终一致性                                    │
│                                                         │
│  高可用                                                │
│  ├── Redis 集群                                        │
│  ├── MQ 集群                                           │
│  └── 故障转移                                          │
│                                                         │
│  并发控制                                               │
│  ├── 分布式锁                                          │
│  ├── 分布式 ID                                         │
│  └── 幂等性设计                                        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 技术栈

| 技术     | 用途                   |
| -------- | ---------------------- |
| Redis    | 缓存、分布式锁、计数器 |
| RocketMQ | 消息队列、异步解耦     |
| Kafka    | 日志收集、流处理       |
| Seata    | 分布式事务             |

---

## 📁 文件列表

| #   | 文件                                                 | 描述            | 知识点                        |
| --- | ---------------------------------------------------- | --------------- | ----------------------------- |
| 1   | [RedisBasics.java](./RedisBasics.java)               | Redis 数据类型  | String, Hash, List, Set, ZSet |
| 2   | [RedisCacheDemo.java](./RedisCacheDemo.java)         | 缓存策略        | 穿透, 击穿, 雪崩, 一致性      |
| 3   | [DistributedLock.java](./DistributedLock.java)       | 分布式锁        | Redis, Redisson, 红锁         |
| 4   | [RocketMQDemo.java](./RocketMQDemo.java)             | RocketMQ 入门   | 生产者, 消费者, 消息类型      |
| 5   | [KafkaDemo.java](./KafkaDemo.java)                   | Kafka 使用      | 分区, 消费组, 偏移量          |
| 6   | [MessageReliability.java](./MessageReliability.java) | 消息可靠性      | 幂等, 重试, 死信队列          |
| 7   | [SeataDemo.java](./SeataDemo.java)                   | 分布式事务      | AT, TCC, Saga 模式            |
| 8   | [DistributedIdDemo.java](./DistributedIdDemo.java)   | 分布式 ID       | UUID, 雪花算法, Leaf          |
| 9   | [SeckillProject.java](./SeckillProject.java)         | 🎯 **实战项目** | 秒杀系统                      |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行课程
mvn exec:java -Dexec.mainClass="phase12.RedisBasics"
mvn exec:java -Dexec.mainClass="phase12.DistributedLock"
mvn exec:java -Dexec.mainClass="phase12.RocketMQDemo"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: Redis 数据类型和命令
2. **Day 3-4**: 缓存策略设计
3. **Day 5-6**: 分布式锁实现
4. **Day 7-8**: RocketMQ/Kafka 消息队列
5. **Day 9-10**: 消息可靠性保证
6. **Day 11-12**: 分布式事务
7. **Day 13-14**: 实战 - 秒杀系统

### 环境准备

```bash
# 启动 Redis
docker run -d --name redis -p 6379:6379 redis:7

# 启动 RocketMQ (NameServer + Broker)
docker run -d --name rmqnamesrv -p 9876:9876 apache/rocketmq:5.1.0 sh mqnamesrv
docker run -d --name rmqbroker -p 10911:10911 -p 10909:10909 \
  --link rmqnamesrv:namesrv \
  -e "NAMESRV_ADDR=namesrv:9876" \
  apache/rocketmq:5.1.0 sh mqbroker

# 启动 Kafka
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 \
  bitnami/kafka:latest
```

---

## ✅ 完成检查

- [ ] 熟练使用 Redis 五种数据类型
- [ ] 能够设计缓存方案
- [ ] 实现分布式锁
- [ ] 理解消息队列使用场景
- [ ] 保证消息可靠性
- [ ] 理解分布式事务方案
- [ ] 完成秒杀系统项目

---

## 🎯 实战项目: 秒杀系统

综合运用 Redis、消息队列、分布式锁：

**核心功能:**

- 商品秒杀
- 库存预扣
- 订单异步处理
- 防止超卖

**技术要点:**

- Redis 预热库存
- 分布式锁防超卖
- MQ 异步下单
- 限流降级

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 13: 性能调优](../phase13/README.md)
