# Phase 11: 数据库与 ORM

> **目标**：掌握数据库编程与 ORM 框架  
> **预计时长**：2 周  
> **前置条件**：Phase 10 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 深入理解 MySQL 索引原理 (B+ 树)
2. 掌握事务隔离级别和锁机制
3. 熟练使用 MyBatis 和动态 SQL
4. 掌握 MyBatis-Plus 快速开发
5. 深入理解 JPA Repository 模式
6. 了解分库分表方案 (ShardingSphere)
7. 能够进行 SQL 性能优化

---

## 📚 核心概念

### MySQL 进阶

```
┌─────────────────────────────────────────────────────────┐
│                   MySQL 知识体系                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  索引原理                                               │
│  ├── B+ 树结构                                         │
│  ├── 聚簇索引 vs 非聚簇索引                            │
│  └── 索引优化策略                                      │
│                                                         │
│  事务与锁                                               │
│  ├── ACID 特性                                         │
│  ├── 隔离级别 (RU/RC/RR/Serializable)                  │
│  ├── 行锁、表锁、间隙锁                                │
│  └── MVCC 多版本并发控制                               │
│                                                         │
│  SQL 优化                                               │
│  ├── EXPLAIN 执行计划                                  │
│  ├── 慢查询分析                                        │
│  └── 索引优化                                          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### ORM 框架对比

| 框架          | 特点             | 适用场景             |
| ------------- | ---------------- | -------------------- |
| JDBC          | 原生、灵活       | 简单查询、学习原理   |
| MyBatis       | 半自动、SQL 控制 | 复杂 SQL、性能要求高 |
| MyBatis-Plus  | 增强版、开箱即用 | 快速开发、CRUD 密集  |
| JPA/Hibernate | 全自动、ORM 映射 | 领域驱动、简单 CRUD  |

---

## 📁 文件列表

| #   | 文件                                                     | 描述            | 知识点                    |
| --- | -------------------------------------------------------- | --------------- | ------------------------- |
| 1   | [MysqlIndexDemo.java](./MysqlIndexDemo.java)             | 索引原理与优化  | B+ 树, 索引类型, 优化策略 |
| 2   | [TransactionIsolation.java](./TransactionIsolation.java) | 事务隔离级别    | ACID, 隔离级别, 并发问题  |
| 3   | [LockMechanism.java](./LockMechanism.java)               | 锁机制详解      | 行锁, 表锁, 间隙锁, MVCC  |
| 4   | [MyBatisBasics.java](./MyBatisBasics.java)               | MyBatis 基础    | 配置, Mapper, 结果映射    |
| 5   | [DynamicSqlDemo.java](./DynamicSqlDemo.java)             | 动态 SQL        | if, choose, foreach, trim |
| 6   | [MyBatisPlusDemo.java](./MyBatisPlusDemo.java)           | MyBatis-Plus    | 代码生成, CRUD, 分页      |
| 7   | [JpaAdvanced.java](./JpaAdvanced.java)                   | JPA 进阶        | 查询方法, @Query, 审计    |
| 8   | [ShardingDemo.java](./ShardingDemo.java)                 | 分库分表        | ShardingSphere, 分片策略  |
| 9   | [SqlOptimization.java](./SqlOptimization.java)           | SQL 优化实战    | EXPLAIN, 慢查询, 调优     |
| 10  | [OrmProject.java](./OrmProject.java)                     | 🎯 **实战项目** | 订单管理系统              |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行课程
mvn exec:java -Dexec.mainClass="phase11.MysqlIndexDemo"
mvn exec:java -Dexec.mainClass="phase11.MyBatisBasics"
mvn exec:java -Dexec.mainClass="phase11.SqlOptimization"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: MySQL 索引原理
2. **Day 3-4**: 事务隔离级别和锁机制
3. **Day 5-6**: MyBatis 基础和动态 SQL
4. **Day 7-8**: MyBatis-Plus 快速开发
5. **Day 9-10**: JPA 进阶和分库分表
6. **Day 11-14**: 实战项目 - 订单管理系统

### 环境准备

```bash
# 启动 MySQL
docker run -d --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=demo \
  mysql:8.0

# 连接数据库
mysql -h localhost -P 3306 -u root -p123456
```

---

## ✅ 完成检查

- [ ] 理解 B+ 树索引原理
- [ ] 掌握事务隔离级别
- [ ] 能够分析和优化 SQL
- [ ] 熟练使用 MyBatis 动态 SQL
- [ ] 掌握 MyBatis-Plus 快速开发
- [ ] 理解分库分表概念
- [ ] 完成订单管理系统项目

---

## 🎯 实战项目: 订单管理系统

`OrmProject.java` 是本阶段的综合实战指南：

**功能模块:**

- 用户管理
- 商品管理
- 订单 CRUD
- 库存管理
- 报表统计

**技术选型:**

- 数据访问: MyBatis-Plus / JPA
- 连接池: HikariCP
- 分页: PageHelper / Page
- 缓存: Redis

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 12: 数据库进阶](../phase12/README.md)
