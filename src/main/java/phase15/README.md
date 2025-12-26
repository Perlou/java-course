# 🏗️ Phase 15: 系统架构设计 (2 周)

> **目标**：掌握架构设计方法论与核心模式  
> **前置要求**：完成 Phase 1-14（资深工程师阶段）  
> **预计时长**：2 周（第 25-26 周）  
> **状态**: ✅ 学习资料已创建

---

## 📋 学习目标

完成本阶段后，你将能够：

1. 理解并应用架构设计核心原则（CAP、BASE、12-Factor）
2. 选择合适的架构模式（分层、六边形、事件驱动）
3. 运用领域驱动设计（DDD）进行系统建模
4. 实现 CQRS 与事件溯源架构

---

## 📚 课程内容

### 核心概念

| 文件                       | 描述         | 知识点         |
| -------------------------- | ------------ | -------------- |
| [CONCEPT.md](./CONCEPT.md) | 核心概念文档 | 架构设计全景图 |

### 第 25 周：架构设计原则

| 文件                                                         | 主题     | 核心知识点                   |
| ------------------------------------------------------------ | -------- | ---------------------------- |
| [ArchitecturePrinciples.java](./ArchitecturePrinciples.java) | 架构原则 | CAP/BASE 理论、12-Factor App |
| [ArchitecturePatterns.java](./ArchitecturePatterns.java)     | 架构模式 | 分层、六边形、事件驱动架构   |

### 第 26 周：领域驱动设计

| 文件                                                 | 主题      | 核心知识点                    |
| ---------------------------------------------------- | --------- | ----------------------------- |
| [DomainDrivenDesign.java](./DomainDrivenDesign.java) | DDD 设计  | 战略/战术设计、聚合、领域事件 |
| [CqrsEventSourcing.java](./CqrsEventSourcing.java)   | CQRS & ES | 命令查询分离、事件溯源        |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行课程
mvn exec:java -Dexec.mainClass="phase15.ArchitecturePrinciples"
mvn exec:java -Dexec.mainClass="phase15.ArchitecturePatterns"
mvn exec:java -Dexec.mainClass="phase15.DomainDrivenDesign"
mvn exec:java -Dexec.mainClass="phase15.CqrsEventSourcing"
```

---

## 📖 学习建议

### 学习顺序

```
Week 1: 架构原则与模式
├── Day 1-2: 阅读 CONCEPT.md，理解架构全景图
├── Day 3-4: CAP/BASE 理论，12-Factor App
└── Day 5-7: 架构模式（分层、六边形、事件驱动）

Week 2: DDD 与高级模式
├── Day 1-2: DDD 战略设计（限界上下文、子域）
├── Day 3-4: DDD 战术设计（实体、值对象、聚合）
└── Day 5-7: CQRS 与事件溯源
```

### 核心知识点

| 概念       | 描述                           |
| ---------- | ------------------------------ |
| CAP 定理   | 一致性、可用性、分区容错三选二 |
| BASE 理论  | 基本可用、软状态、最终一致     |
| 六边形架构 | 端口与适配器，领域核心隔离     |
| 限界上下文 | DDD 战略设计核心，划分系统边界 |
| 聚合       | DDD 战术设计核心，一致性边界   |
| CQRS       | 读写分离，独立优化             |
| 事件溯源   | 存储事件序列而非状态           |

---

## 📖 推荐资源

### 必读书籍

- 《领域驱动设计》- Eric Evans
- 《实现领域驱动设计》- Vaughn Vernon
- 《架构整洁之道》- Robert C. Martin

### 参考文档

- [Martin Fowler's Architecture Articles](https://martinfowler.com/architecture/)
- [The Twelve-Factor App](https://12factor.net/)
- [Microsoft CQRS Pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs)

---

## ✅ 学习检查点

完成本阶段后，请确认：

- [ ] 能够解释 CAP 定理并举例说明权衡场景
- [ ] 理解 12-Factor App 各项原则
- [ ] 理解并能应用六边形架构设计服务
- [ ] 能够识别并划分限界上下文
- [ ] 掌握实体、值对象、聚合的区别
- [ ] 理解 CQRS 与事件溯源的适用场景

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 16](../phase16/README.md)
