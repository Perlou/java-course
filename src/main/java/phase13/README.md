# Phase 13: 性能调优

> **目标**：掌握系统性能优化方法  
> **预计时长**：1 周  
> **前置条件**：Phase 12 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 进行 JVM 性能调优
2. 分析和优化 GC 问题
3. 排查内存泄漏和 OOM
4. 优化 SQL 查询
5. 设计缓存策略
6. 配置连接池和线程池

---

## 📚 核心概念

### 性能调优金字塔

```
                    ┌─────────┐
                    │  架构   │  ← 分布式、微服务
                   ─┴─────────┴─
                  ┌─────────────┐
                  │   代码优化   │  ← 算法、并发
                 ─┴─────────────┴─
                ┌─────────────────┐
                │    SQL 优化     │  ← 索引、执行计划
               ─┴─────────────────┴─
              ┌─────────────────────┐
              │     JVM 调优        │  ← GC、内存
              └─────────────────────┘
```

### 技术栈

| 技术     | 用途               |
| -------- | ------------------ |
| JVM 参数 | 堆内存、GC 收集器  |
| GC 日志  | 性能分析、问题定位 |
| MAT      | 内存分析           |
| Arthas   | 在线诊断           |
| EXPLAIN  | SQL 执行计划       |
| HikariCP | 数据库连接池       |
| Caffeine | 本地缓存           |

---

## 📁 文件列表

| #   | 文件                                                     | 描述         | 知识点                 |
| --- | -------------------------------------------------------- | ------------ | ---------------------- |
| 0   | [CONCEPT.md](./CONCEPT.md)                               | 核心概念     | 性能指标、调优方法论   |
| 1   | [JvmTuning.java](./JvmTuning.java)                       | JVM 调优     | 参数配置、GC 选择      |
| 2   | [GCAnalysis.java](./GCAnalysis.java)                     | GC 日志分析  | 日志格式、问题分析     |
| 3   | [MemoryAnalysis.java](./MemoryAnalysis.java)             | 内存问题排查 | OOM 类型、泄漏检测     |
| 4   | [SqlOptimization.java](./SqlOptimization.java)           | SQL 优化     | EXPLAIN、索引、分页    |
| 5   | [CacheStrategy.java](./CacheStrategy.java)               | 缓存策略     | 穿透/击穿/雪崩、一致性 |
| 6   | [ConnectionPoolTuning.java](./ConnectionPoolTuning.java) | 连接池调优   | HikariCP、线程池       |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行课程
mvn exec:java -Dexec.mainClass="phase13.JvmTuning"
mvn exec:java -Dexec.mainClass="phase13.GCAnalysis"
mvn exec:java -Dexec.mainClass="phase13.MemoryAnalysis"
mvn exec:java -Dexec.mainClass="phase13.SqlOptimization"
mvn exec:java -Dexec.mainClass="phase13.CacheStrategy"
mvn exec:java -Dexec.mainClass="phase13.ConnectionPoolTuning"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1**: 阅读 CONCEPT.md，理解性能调优全景图
2. **Day 2**: JVM 调优 - 参数配置与 GC 选择
3. **Day 3**: GC 日志分析 - 学会读懂 GC 日志
4. **Day 4**: 内存问题排查 - OOM 分析与解决
5. **Day 5**: SQL 优化 - EXPLAIN 与索引设计
6. **Day 6**: 缓存策略 - 解决缓存问题
7. **Day 7**: 连接池调优 - 配置最佳参数

### 实践建议

```bash
# 1. 使用 GC 日志运行程序
java -Xlog:gc* -Xms256m -Xmx256m phase13.GCAnalysis

# 2. 使用 Arthas 进行在线诊断
curl -O https://arthas.aliyun.com/arthas-boot.jar
java -jar arthas-boot.jar

# 3. 压测工具
# JMeter: https://jmeter.apache.org/
# wrk: brew install wrk
```

---

## ✅ 完成检查

- [ ] 理解 JVM 内存模型和 GC 原理
- [ ] 能够读懂并分析 GC 日志
- [ ] 能够排查内存泄漏问题
- [ ] 熟练使用 EXPLAIN 分析 SQL
- [ ] 理解缓存穿透/击穿/雪崩及解决方案
- [ ] 能够配置数据库连接池和线程池

---

## 🛠️ 常用工具

| 类别     | 工具                 | 用途         |
| -------- | -------------------- | ------------ |
| JVM 监控 | jstat, jmap, jstack  | 命令行监控   |
| 可视化   | VisualVM, JConsole   | 图形化监控   |
| 内存分析 | Eclipse MAT          | 堆转储分析   |
| 在线诊断 | Arthas               | 生产环境诊断 |
| 压测     | JMeter, wrk, Gatling | 性能测试     |

---

> 📝 完成本阶段后，请更新 `LEARNING_PLAN.md`，然后进入 [Phase 14](../phase14/README.md)
