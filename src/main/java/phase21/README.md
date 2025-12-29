# Phase 21: 大数据基础与 Hadoop 生态

> **目标**：掌握大数据基础理论与 Hadoop 核心组件  
> **时长**：2 周  
> **前置要求**：完成 Phase 1-20（Java 架构师基础）

---

## 📚 学习内容

### 第 36 周：大数据基础

#### 大数据概念

- `BigDataIntro.java` - 大数据 5V 特性（Volume、Velocity、Variety、Value、Veracity）
- `DistributedComputing.java` - 分布式计算基础原理
- `DataFormats.java` - 大数据格式（Parquet、ORC、Avro）

#### HDFS 分布式文件系统

- `HdfsBasics.java` - HDFS 架构与原理（NameNode、DataNode）
- `HdfsClient.java` - Java HDFS 客户端操作
- `HdfsHA.java` - HDFS 高可用配置

### 第 37 周：MapReduce 与 YARN

#### MapReduce 编程

- `MapReduceBasics.java` - MapReduce 编程模型
- `WordCount.java` - 经典 WordCount 案例
- `CustomPartitioner.java` - 自定义分区器
- `ChainMapReduce.java` - 链式 MapReduce

#### YARN 资源调度

- `YarnArchitecture.java` - YARN 架构详解（ResourceManager、NodeManager）
- `ResourceScheduler.java` - 资源调度策略（Fair、Capacity）

---

## 🎯 学习目标

完成本阶段后，你应该能够：

1. **理解大数据生态**

   - 解释大数据 5V 特性
   - 对比批处理与流处理的区别
   - 选择合适的数据存储格式

2. **掌握 HDFS**

   - 理解 HDFS 架构与数据块存储原理
   - 使用 Java API 操作 HDFS 文件
   - 配置 HDFS 高可用环境

3. **编写 MapReduce 程序**

   - 实现自定义 Mapper 和 Reducer
   - 优化 MapReduce 作业性能
   - 处理数据倾斜问题

4. **理解 YARN 调度**
   - 解释 YARN 资源管理机制
   - 配置调度器参数

---

## 🛠️ 环境准备

### 本地开发环境

```xml
<!-- pom.xml 依赖 -->
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-client</artifactId>
    <version>3.3.6</version>
</dependency>
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-hdfs</artifactId>
    <version>3.3.6</version>
</dependency>
```

### Docker 快速启动（推荐）

```bash
# 使用 Docker Compose 启动 Hadoop 集群
docker-compose -f docker/hadoop-cluster.yml up -d
```

---

## 📝 实战项目：日志分析系统

### 项目目标

使用 MapReduce 处理 Web 服务器日志，实现：

- PV/UV 统计
- 热门页面 Top N
- 用户访问路径分析

### 项目结构

```
projects/log-analyzer/
├── src/
│   ├── LogParser.java          # 日志解析器
│   ├── PvUvMapper.java         # PV/UV 统计 Mapper
│   ├── PvUvReducer.java        # PV/UV 统计 Reducer
│   └── TopNDriver.java         # TopN 驱动类
├── input/                      # 输入数据
└── output/                     # 输出结果
```

---

## 📖 推荐资源

### 书籍

- 《Hadoop 权威指南》(第 4 版) - Tom White
- 《大数据技术原理与应用》 - 林子雨

### 在线资源

- [Apache Hadoop 官方文档](https://hadoop.apache.org/docs/)
- [HDFS Architecture Guide](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)

---

## ✅ 检查清单

- [ ] 理解大数据 5V 特性
- [ ] 掌握 HDFS 架构原理
- [ ] 能够使用 Java API 操作 HDFS
- [ ] 编写完整的 MapReduce 程序
- [ ] 理解 YARN 资源调度机制
- [ ] 完成日志分析实战项目
