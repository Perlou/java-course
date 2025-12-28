# 💾 Phase 17: 分布式存储架构 (2 周)

> **目标**：掌握分布式存储与数据一致性  
> **前置要求**：完成 Phase 16（高并发架构）  
> **预计时长**：2 周（第 29-30 周）  
> **状态**: ✅ 学习资料已创建

---

## 📋 学习目标

完成本阶段后，你将能够：

1. 理解和使用 NewSQL 数据库（TiDB、CockroachDB）
2. 掌握分布式一致性算法（Raft、Paxos）
3. 理解 LSM-Tree 存储引擎原理
4. 设计数据湖与多数据源架构
5. 实现高效的缓存策略

---

## 📚 课程内容

### 核心概念

| 文件                       | 描述         | 知识点           |
| -------------------------- | ------------ | ---------------- |
| [CONCEPT.md](./CONCEPT.md) | 核心概念文档 | 分布式存储全景图 |

### 第 29 周：分布式数据库与一致性

#### 一致性与共识

| 文件                                             | 主题           | 核心知识点                 |
| ------------------------------------------------ | -------------- | -------------------------- |
| [RaftDemo.java](./RaftDemo.java)                 | Raft 共识算法  | Leader 选举、日志复制      |
| [ConsistencyModel.java](./ConsistencyModel.java) | 一致性模型对比 | 强一致、最终一致、因果一致 |

#### NewSQL 数据库

| 文件                             | 主题            | 核心知识点           |
| -------------------------------- | --------------- | -------------------- |
| [TiDBDemo.java](./TiDBDemo.java) | TiDB 使用与原理 | 分布式事务、水平扩展 |

### 第 30 周：存储架构与缓存

#### 存储引擎

| 文件                           | 主题          | 核心知识点         |
| ------------------------------ | ------------- | ------------------ |
| [LSMTree.java](./LSMTree.java) | LSM-Tree 原理 | 写优化、Compaction |

#### 数据架构

| 文件                                                   | 主题       | 核心知识点         |
| ------------------------------------------------------ | ---------- | ------------------ |
| [DataLakeDesign.java](./DataLakeDesign.java)           | 数据湖架构 | 湖仓一体、数据分层 |
| [PolyglotPersistence.java](./PolyglotPersistence.java) | 多模存储   | 存储选型、混合架构 |

#### 缓存策略

| 文件                                               | 主题     | 核心知识点                 |
| -------------------------------------------------- | -------- | -------------------------- |
| [CacheAsidePattern.java](./CacheAsidePattern.java) | 缓存策略 | Cache-Aside、Write-Through |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
javac -d target/classes src/main/java/phase17/*.java

# 运行课程（按学习顺序）
java -cp target/classes phase17.RaftDemo
java -cp target/classes phase17.ConsistencyModel
java -cp target/classes phase17.TiDBDemo
java -cp target/classes phase17.LSMTree
java -cp target/classes phase17.CacheAsidePattern
java -cp target/classes phase17.DataLakeDesign
java -cp target/classes phase17.PolyglotPersistence
```

---

## 📖 学习建议

### 学习顺序

```
Week 1: 分布式数据库与一致性
├── Day 1-2: CONCEPT.md + Raft 共识算法
├── Day 3-4: 一致性模型对比
├── Day 5-6: TiDB 架构与使用
└── Day 7: 复习与总结

Week 2: 存储架构与缓存
├── Day 1-2: LSM-Tree 存储引擎
├── Day 3-4: 数据湖架构设计
├── Day 5: 多模存储选型
├── Day 6: 缓存策略实践
└── Day 7: 综合练习
```

### 核心知识点

| 概念        | 描述                         |
| ----------- | ---------------------------- |
| Raft        | 易理解的分布式共识算法       |
| 线性一致性  | 最强一致性，像单机一样       |
| 最终一致性  | 允许短暂不一致，提升可用性   |
| LSM-Tree    | 写优化存储结构，顺序写入     |
| 数据湖      | 存储原始数据，Schema-on-Read |
| Cache-Aside | 先读缓存，未命中读数据库回填 |

---

## 📖 推荐资源

### 必读

- 《Designing Data-Intensive Applications》- Martin Kleppmann
- TiDB 官方文档

### 论文

- [Raft: In Search of an Understandable Consensus Algorithm](https://raft.github.io/raft.pdf)

---

## ✅ 学习检查点

- [ ] 能够解释 Raft 算法的核心流程
- [ ] 理解 TiDB 的分布式事务实现
- [ ] 掌握不同一致性模型的适用场景
- [ ] 理解 LSM-Tree 的写入和读取流程
- [ ] 能够设计多数据源架构方案
- [ ] 掌握 Cache-Aside 缓存策略

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 18](../phase18/README.md)
