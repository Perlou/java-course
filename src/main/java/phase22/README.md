# Phase 22: Spark 生态与实时计算

> **目标**：掌握 Spark 生态系统与实时流处理  
> **时长**：2 周  
> **前置要求**：完成 Phase 21（Hadoop 生态基础）

---

## 📚 学习内容

### 第 38 周：Spark Core 与 SparkSQL

#### Spark 基础

- `SparkBasics.java` - Spark 架构与 RDD 原理
- `RddOperations.java` - RDD 转换与行动算子
- `SparkDAG.java` - DAG 执行引擎原理

#### SparkSQL

- `SparkSqlBasics.java` - DataFrame/Dataset API
- `SparkUdf.java` - 自定义 UDF 函数
- `SparkCatalyst.java` - Catalyst 优化器原理

### 第 39 周：Spark Streaming

#### 实时流处理

- `SparkStreamingDemo.java` - DStream 编程
- `StructuredStreaming.java` - 结构化流处理
- `KafkaIntegration.java` - Kafka 与 Spark 集成
- `WindowOperations.java` - 窗口操作

#### Spark 调优

- `SparkTuning.java` - Spark 性能调优
- `SparkMemory.java` - 内存管理与数据倾斜处理

---

## 🎯 学习目标

1. **理解 Spark 架构**：Driver、Executor、Cluster Manager
2. **掌握 RDD 编程**：转换算子、行动算子、持久化
3. **熟练使用 SparkSQL**：DataFrame API、SQL 查询
4. **实现流处理应用**：Structured Streaming 编程

---

## 🛠️ 环境配置

```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.12</artifactId>
    <version>3.5.0</version>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.12</artifactId>
    <version>3.5.0</version>
</dependency>
```

---

## 📝 实战项目：实时数据分析平台

电商用户行为实时分析系统，实现：

- 实时 PV/UV 统计
- 热门商品 TopN
- 用户行为路径分析
