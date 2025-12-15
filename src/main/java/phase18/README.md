# 📊 Phase 18: 可观测性工程 (2 周)

> **目标**：建立全链路可观测性能力  
> **前置要求**：完成 Phase 17（分布式存储架构）  
> **预计时长**：2 周（第 31-32 周）

---

## 📋 学习目标

完成本阶段后，你将能够：

1. 实现分布式链路追踪（SkyWalking、Jaeger）
2. 构建指标监控体系（Prometheus、Grafana）
3. 设计结构化日志系统（ELK Stack）
4. 建立告警与 On-Call 体系

---

## 📚 课程内容

### 第 31 周：链路追踪与指标

#### 分布式追踪

| 文件                           | 主题            | 核心知识点                    |
| ------------------------------ | --------------- | ----------------------------- |
| `SkyWalkingDemo.java`          | SkyWalking 集成 | Java Agent、自动探针          |
| `JaegerDemo.java`              | Jaeger 追踪     | OpenTracing、采样策略         |
| `TraceContextPropagation.java` | 上下文传播      | W3C Trace Context、跨服务传递 |

#### 指标监控

| 文件                     | 主题            | 核心知识点                |
| ------------------------ | --------------- | ------------------------- |
| `PrometheusMetrics.java` | Prometheus 指标 | Counter、Gauge、Histogram |
| `MicrometerDemo.java`    | Micrometer 集成 | 多后端适配、维度指标      |
| `GrafanaDashboard.java`  | Grafana 可视化  | Dashboard 设计、告警配置  |

### 第 32 周：日志与告警

#### 日志系统

| 文件                     | 主题           | 核心知识点                      |
| ------------------------ | -------------- | ------------------------------- |
| `ELKStackDemo.java`      | ELK Stack 集成 | Elasticsearch、Logstash、Kibana |
| `StructuredLogging.java` | 结构化日志     | JSON 日志、上下文传递           |
| `LogAggregation.java`    | 日志聚合       | Fluentd、日志采集               |

#### 告警体系

| 文件                | 主题         | 核心知识点           |
| ------------------- | ------------ | -------------------- |
| `AlertRules.java`   | 告警规则设计 | 阈值告警、异常检测   |
| `OnCallSystem.java` | On-Call 体系 | 值班轮转、escalation |

---

## 🎯 实战项目：全链路可观测平台

### 项目目标

搭建一个完整的可观测性平台，包括：

- 分布式链路追踪系统
- 实时指标监控 Dashboard
- 集中式日志平台
- 智能告警系统

### 技术栈

- SkyWalking / Jaeger
- Prometheus + Grafana
- ELK Stack / Loki

---

## 📖 推荐资源

### 必读

- 《分布式系统可观测性》
- [OpenTelemetry 官方文档](https://opentelemetry.io/)

### 参考

- [SkyWalking 官方文档](https://skywalking.apache.org/)
- [Prometheus 官方文档](https://prometheus.io/)

---

## ✅ 学习检查点

- [ ] 能够配置和使用 SkyWalking 进行链路追踪
- [ ] 掌握 Prometheus 指标类型与 PromQL
- [ ] 理解 ELK Stack 各组件职责
- [ ] 能够设计有效的告警规则
- [ ] 完成全链路可观测平台项目
