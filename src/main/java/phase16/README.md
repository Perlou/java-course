# ⚡ Phase 16: 高并发架构 (2 周)

> **目标**：掌握高并发系统设计与优化  
> **前置要求**：完成 Phase 15（系统架构设计）  
> **预计时长**：2 周（第 27-28 周）  
> **状态**: ✅ 学习资料已创建

---

## 📋 学习目标

完成本阶段后，你将能够：

1. 实现多种限流算法（令牌桶、滑动窗口、漏桶）
2. 设计服务降级与熔断策略
3. 构建高可用系统架构（多活、容灾）
4. 进行流量调度与智能负载均衡

---

## 📚 课程内容

### 核心概念

| 文件                       | 描述         | 知识点           |
| -------------------------- | ------------ | ---------------- |
| [CONCEPT.md](./CONCEPT.md) | 核心概念文档 | 高并发架构全景图 |

### 第 27 周：流量控制与限流

#### 限流算法

| 文件                                       | 主题         | 核心知识点                     |
| ------------------------------------------ | ------------ | ------------------------------ |
| [TokenBucket.java](./TokenBucket.java)     | 令牌桶算法   | 固定速率生成令牌、突发流量处理 |
| [SlidingWindow.java](./SlidingWindow.java) | 滑动窗口算法 | 时间窗口、精确限流             |
| [LeakyBucket.java](./LeakyBucket.java)     | 漏桶算法     | 恒定速率、流量整形             |

#### 分布式限流

| 文件                                                         | 主题       | 核心知识点           |
| ------------------------------------------------------------ | ---------- | -------------------- |
| [DistributedRateLimiter.java](./DistributedRateLimiter.java) | 分布式限流 | Redis 实现、Lua 脚本 |

### 第 28 周：高可用设计

#### 降级与熔断

| 文件                                                       | 主题              | 核心知识点         |
| ---------------------------------------------------------- | ----------------- | ------------------ |
| [CircuitBreakerPattern.java](./CircuitBreakerPattern.java) | 熔断器模式        | 状态机、失败率阈值 |
| [FallbackStrategy.java](./FallbackStrategy.java)           | 降级策略          | 静态降级、缓存降级 |
| [Resilience4jDemo.java](./Resilience4jDemo.java)           | Resilience4j 实践 | 舱壁模式、重试机制 |

#### 高可用架构

| 文件                                               | 主题     | 核心知识点         |
| -------------------------------------------------- | -------- | ------------------ |
| [MultiActiveDesign.java](./MultiActiveDesign.java) | 多活架构 | 同城双活、异地多活 |
| [DisasterRecovery.java](./DisasterRecovery.java)   | 容灾设计 | RTO/RPO、故障转移  |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行课程（按学习顺序）
mvn exec:java -Dexec.mainClass="phase16.TokenBucket"
mvn exec:java -Dexec.mainClass="phase16.SlidingWindow"
mvn exec:java -Dexec.mainClass="phase16.LeakyBucket"
mvn exec:java -Dexec.mainClass="phase16.CircuitBreakerPattern"
mvn exec:java -Dexec.mainClass="phase16.FallbackStrategy"
mvn exec:java -Dexec.mainClass="phase16.Resilience4jDemo"
mvn exec:java -Dexec.mainClass="phase16.MultiActiveDesign"
mvn exec:java -Dexec.mainClass="phase16.DisasterRecovery"
mvn exec:java -Dexec.mainClass="phase16.DistributedRateLimiter"
```

---

## 📖 学习建议

### 学习顺序

```
Week 1: 限流算法与实践
├── Day 1-2: 阅读 CONCEPT.md，理解高并发挑战
├── Day 3-4: TokenBucket.java 令牌桶算法
├── Day 5: SlidingWindow.java 滑动窗口算法
├── Day 6: LeakyBucket.java 漏桶算法
└── Day 7: DistributedRateLimiter.java 分布式限流

Week 2: 熔断降级与高可用
├── Day 1-2: CircuitBreakerPattern.java 熔断器模式
├── Day 3: FallbackStrategy.java 降级策略
├── Day 4: Resilience4jDemo.java 容错框架
├── Day 5: MultiActiveDesign.java 多活架构
├── Day 6: DisasterRecovery.java 容灾设计
└── Day 7: 复习与实践
```

### 核心知识点

| 概念     | 描述                     |
| -------- | ------------------------ |
| 令牌桶   | 控制平均速率，允许突发   |
| 滑动窗口 | 精确限流，解决边界问题   |
| 漏桶     | 恒定速率，流量整形       |
| 熔断器   | 故障隔离，快速失败       |
| 降级策略 | 保障核心功能，牺牲非核心 |
| 舱壁隔离 | 资源隔离，防止雪崩       |
| 多活架构 | 多个数据中心同时服务     |
| RTO/RPO  | 恢复时间目标/恢复点目标  |

---

## 📖 推荐资源

### 必读书籍

- 《高性能 MySQL》
- 《大型网站技术架构》- 李智慧

### 参考资料

- [Alibaba Sentinel 官方文档](https://sentinelguard.io/)
- [Resilience4j 官方文档](https://resilience4j.readme.io/)

---

## ✅ 学习检查点

- [ ] 能够实现令牌桶和滑动窗口限流算法
- [ ] 理解令牌桶与漏桶的区别
- [ ] 掌握熔断器状态转换机制
- [ ] 理解多种降级策略的适用场景
- [ ] 能够设计多活架构方案
- [ ] 理解 RTO 和 RPO 的含义

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 17](../phase17/README.md)
