# 💾 Phase 17: 分布式存储架构 (2 周)

> **目标**：掌握分布式存储与数据一致性  
> **前置要求**：完成 Phase 16（高并发架构）  
> **预计时长**：2 周（第 29-30 周）

---

## 📋 学习目标

完成本阶段后，你将能够：

1. 理解和使用 NewSQL 数据库（TiDB、CockroachDB）
2. 掌握分布式一致性算法（Raft、Paxos）
3. 设计数据湖与数据仓库架构
4. 实现多数据源架构与缓存策略

---

## 📚 课程内容

### 第 29 周：分布式数据库

#### NewSQL 数据库

| 文件                   | 主题             | 核心知识点           |
| ---------------------- | ---------------- | -------------------- |
| `TiDBDemo.java`        | TiDB 使用与原理  | 分布式事务、水平扩展 |
| `CockroachDBDemo.java` | CockroachDB 特性 | 强一致性、地理分布   |
| `VitessDemo.java`      | Vitess 分片      | 分片策略、MySQL 扩展 |

#### 一致性算法

| 文件                    | 主题           | 核心知识点                 |
| ----------------------- | -------------- | -------------------------- |
| `RaftDemo.java`         | Raft 共识算法  | Leader 选举、日志复制      |
| `PaxosDemo.java`        | Paxos 原理     | 提案接受、多数派           |
| `ConsistencyModel.java` | 一致性模型对比 | 强一致、最终一致、因果一致 |

### 第 30 周：数据架构

#### 存储架构

| 文件                  | 主题          | 核心知识点          |
| --------------------- | ------------- | ------------------- |
| `DataLakeDesign.java` | 数据湖架构    | 湖仓一体、数据分层  |
| `DataWarehouse.java`  | 数据仓库设计  | 星型/雪花模型、OLAP |
| `LSMTree.java`        | LSM-Tree 原理 | 写优化、Compaction  |

#### 多数据源架构

| 文件                       | 主题     | 核心知识点                 |
| -------------------------- | -------- | -------------------------- |
| `PolyglotPersistence.java` | 多模存储 | 不同存储引擎选型           |
| `CacheAsidePattern.java`   | 缓存策略 | Cache-Aside、Write-Through |

---

## 🎯 实战项目：多数据源架构设计

### 项目目标

设计并实现一个多数据源架构系统，包括：

- 关系型数据库与 NoSQL 混合使用
- 缓存层设计与一致性保证
- 读写分离与分片策略
- 数据同步与迁移方案

### 技术栈

- TiDB / MySQL
- Redis / MongoDB
- DataX / Canal（数据同步）

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
- [ ] 能够设计多数据源架构方案
- [ ] 完成多数据源架构设计项目
