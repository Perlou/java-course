# Phase 23: Flink 实时计算 🌊

> **目标**：掌握 Flink 流批一体架构与状态管理  
> **时长**：2 周  
> **前置要求**：完成 Phase 22（Spark 基础）

---

## 📚 学习内容

### 第 40 周：Flink 基础

| 文件                 | 主题           | 核心概念                              |
| -------------------- | -------------- | ------------------------------------- |
| `FlinkBasics.java`   | Flink 架构     | JobManager、TaskManager、Slot、算子链 |
| `DataStreamApi.java` | DataStream API | Source、Transformation、Sink、分区    |
| `FlinkWindow.java`   | 窗口机制       | 滚动/滑动/会话窗口、窗口函数          |
| `Watermark.java`     | 水印与事件时间 | 时间语义、Watermark 策略、迟到数据    |
| `FlinkState.java`    | 状态编程       | Keyed/Operator State、TTL、广播状态   |
| `Checkpoint.java`    | 检查点机制     | Barrier、状态后端、故障恢复           |

### 第 41 周：Flink 高级

| 文件                  | 主题         | 核心概念                      |
| --------------------- | ------------ | ----------------------------- |
| `FlinkSqlBasics.java` | Flink SQL    | DDL/DML、窗口 TVF、Join、UDF  |
| `FlinkCep.java`       | 复杂事件处理 | Pattern、量词、条件、超时     |
| `FlinkCdc.java`       | CDC 数据同步 | MySQL CDC、增量快照、整库同步 |

---

## 🎯 学习目标

1. 理解流批一体架构设计理念
2. 掌握 DataStream API 编程模型
3. 深入理解窗口与 Watermark 机制
4. 掌握状态管理与 Checkpoint 容错
5. 使用 Flink SQL 和 CEP 解决业务问题
6. 了解 CDC 实时数据同步方案

---

## 🔄 Flink vs Spark 对比

| 特性         | Flink                | Spark Streaming |
| ------------ | -------------------- | --------------- |
| **处理模型** | 真正的流处理（逐条） | 微批处理        |
| **延迟**     | 毫秒级               | 秒级            |
| **状态管理** | 原生一等公民         | 有限支持        |
| **精确一次** | 端到端支持           | 需额外配置      |
| **窗口支持** | 丰富类型             | 基于时间        |
| **事件时间** | 完善的水印机制       | 有限支持        |
| **适用场景** | 实时计算首选         | 准实时/批处理   |

---

## 📖 学习路线

```
┌─────────────────────────────────────────────────────────────┐
│  Week 40: Flink 基础                                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  FlinkBasics ──► DataStreamApi ──► FlinkWindow             │
│       │                                 │                   │
│       └──────────► Watermark ◄──────────┘                   │
│                       │                                     │
│                       ▼                                     │
│                 FlinkState ──► Checkpoint                   │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│  Week 41: Flink 高级                                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  FlinkSqlBasics ──► FlinkCep ──► FlinkCdc                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ 环境准备

> ⚠️ Flink 1.18+ 需要 Java 11 或 Java 17

### Maven 依赖

```xml
<!-- Flink 核心 -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-streaming-java</artifactId>
    <version>1.18.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-clients</artifactId>
    <version>1.18.0</version>
</dependency>

<!-- Flink SQL -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-table-api-java-bridge</artifactId>
    <version>1.18.0</version>
</dependency>

<!-- Flink CEP -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-cep</artifactId>
    <version>1.18.0</version>
</dependency>

<!-- Flink CDC (MySQL) -->
<dependency>
    <groupId>com.ververica</groupId>
    <artifactId>flink-sql-connector-mysql-cdc</artifactId>
    <version>3.0.0</version>
</dependency>
```

---

## 📝 实战项目：实时风控系统

**项目目标**：构建一个实时风险控制系统

**核心功能**：

1. 实时交易流处理 (DataStream API)
2. 复杂事件模式检测 (CEP)
3. 实时规则引擎 (Broadcast State)
4. 风险评分计算 (Window Aggregation)

**技术栈**：

- Flink 1.18
- Kafka (数据源)
- MySQL (规则存储)
- Redis (风控结果缓存)

---

## 📌 核心概念速查

### 时间语义

- **Event Time**: 事件发生时间（业务时间）
- **Processing Time**: 处理时间（系统时间）
- **Watermark**: 事件时间进度标记

### 窗口类型

- **Tumbling**: 滚动窗口，不重叠
- **Sliding**: 滑动窗口，可重叠
- **Session**: 会话窗口，按活动间隙

### 状态类型

- **ValueState**: 单值
- **ListState**: 列表
- **MapState**: 键值对
- **ReducingState**: 聚合值

### 容错语义

- **At-Most-Once**: 最多一次
- **At-Least-Once**: 至少一次
- **Exactly-Once**: 精确一次 ✓

---

> 💡 **学习建议**：
>
> 1. 先理解 Flink 核心架构和 DataStream API
> 2. 重点掌握窗口和 Watermark 机制
> 3. 状态管理和 Checkpoint 是生产必备
> 4. Flink SQL 和 CEP 根据业务场景选用

---

> 📚 下一阶段: [Phase 24 - 数据仓库与数据治理](../phase24/README.md)
