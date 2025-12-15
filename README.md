# ☕ Java 资深工程师 → 架构师 进阶课程

> **定制对象**：零基础或初级 Java 开发者 → 资深 Java 工程师 → 架构师  
> **学习方式**：基于 Java 17 + Spring 生态的理论与实践结合学习  
> **预计时长**：35-43 周（每周投入 10-15 小时）

---

## 🚀 快速开始

### 1. 环境准备

```bash
# 检查 Java 版本（需要 Java 17+）
java -version

# 检查 Maven 版本
mvn -version

# 如果还没安装，请先安装：
# macOS: brew install openjdk@17 maven
# Ubuntu: sudo apt install openjdk-17-jdk maven

# 克隆项目并进入目录
cd java-course

# 编译项目
mvn compile
```

### 2. 按阶段学习

所有代码已按学习阶段组织，详见下方 [🎓 按阶段学习](#-按阶段学习) 部分。

```bash
# 运行第一个示例：Hello World
mvn exec:java -Dexec.mainClass="phase01.HelloWorld"

# 或直接运行编译后的类
java -cp target/classes phase01.HelloWorld
```

---

## 📂 项目结构

```
java-course/
├── README.md                    # 课程介绍与快速开始
├── ROADMAP.md                   # 学习路线图（可视化）
├── LEARNING_PLAN.md             # 详细学习计划
├── CONCEPTS.md                  # 核心概念汇总文档
├── pom.xml                      # Maven 配置
├── src/
│   └── main/java/
│       ├── phase01/             # 第1阶段：Java 基础语法
│       ├── phase02/             # 第2阶段：面向对象编程
│       ├── phase03/             # 第3阶段：集合框架
│       ├── phase04/             # 第4阶段：IO 与 NIO
│       ├── phase05/             # 第5阶段：并发编程
│       ├── phase06/             # 第6阶段：JVM 深入
│       ├── phase07/             # 第7阶段：设计模式
│       ├── phase08/             # 第8阶段：Spring 框架
│       ├── phase09/             # 第9阶段：Spring Boot
│       ├── phase10/             # 第10阶段：微服务架构
│       ├── phase11/             # 第11阶段：数据库与 ORM
│       ├── phase12/             # 第12阶段：分布式系统
│       ├── phase13/             # 第13阶段：性能调优
│       ├── phase14/             # 第14阶段：云原生技术
│       ├── phase15/             # 第15阶段：系统架构设计 🏗️
│       ├── phase16/             # 第16阶段：高并发架构 ⚡
│       ├── phase17/             # 第17阶段：分布式存储架构 💾
│       ├── phase18/             # 第18阶段：可观测性工程 📊
│       ├── phase19/             # 第19阶段：安全架构 🔐
│       ├── phase20/             # 第20阶段：架构师实战 🎯
│       └── utils/               # 工具类
├── docs/                        # 学习笔记与文档
└── projects/                    # 实战项目
```

**重要文档**：

- 📖 [ROADMAP.md](./ROADMAP.md) - 学习路线图
- 📝 [CONCEPTS.md](./CONCEPTS.md) - 核心概念文档
- 🗺️ [LEARNING_PLAN.md](./LEARNING_PLAN.md) - 完整学习计划

---

## 🎓 按阶段学习

每个阶段目录都包含独立的 README.md，详细说明该阶段的学习目标、核心概念和运行方式。

### 第一部分：Java 基础夯实 (6-8 周)

| 阶段    | 主题          | 运行示例                                                  |
| ------- | ------------- | --------------------------------------------------------- |
| Phase 1 | Java 基础语法 | `mvn exec:java -Dexec.mainClass="phase01.HelloWorld"`     |
| Phase 2 | 面向对象编程  | `mvn exec:java -Dexec.mainClass="phase02.OopBasics"`      |
| Phase 3 | 集合框架      | `mvn exec:java -Dexec.mainClass="phase03.CollectionDemo"` |
| Phase 4 | IO 与 NIO     | `mvn exec:java -Dexec.mainClass="phase04.FileIODemo"`     |

### 第二部分：Java 核心进阶 (8-10 周)

| 阶段    | 主题        | 运行示例                                                 |
| ------- | ----------- | -------------------------------------------------------- |
| Phase 5 | 并发编程    | `mvn exec:java -Dexec.mainClass="phase05.ThreadDemo"`    |
| Phase 6 | JVM 深入    | `mvn exec:java -Dexec.mainClass="phase06.MemoryDemo"`    |
| Phase 7 | 设计模式    | `mvn exec:java -Dexec.mainClass="phase07.SingletonDemo"` |
| Phase 8 | Spring 框架 | 见阶段 README                                            |

### 第三部分：企业级开发 (10-12 周)

| 阶段     | 主题         | 说明                 |
| -------- | ------------ | -------------------- |
| Phase 9  | Spring Boot  | Spring Boot 应用开发 |
| Phase 10 | 微服务架构   | Spring Cloud 微服务  |
| Phase 11 | 数据库与 ORM | MySQL、MyBatis、JPA  |
| Phase 12 | 分布式系统   | Redis、消息队列      |
| Phase 13 | 性能调优     | JVM & SQL 调优       |
| Phase 14 | 云原生技术   | Docker、Kubernetes   |

### 第四部分：架构师进阶 (11-13 周) 🏗️

| 阶段     | 主题           | 说明                                 |
| -------- | -------------- | ------------------------------------ |
| Phase 15 | 系统架构设计   | DDD、CQRS、事件溯源、架构模式        |
| Phase 16 | 高并发架构     | 限流降级、高可用设计、流量调度       |
| Phase 17 | 分布式存储架构 | NewSQL、分布式数据库、数据一致性     |
| Phase 18 | 可观测性工程   | 链路追踪、监控告警、日志系统         |
| Phase 19 | 安全架构       | OAuth2/OIDC、零信任架构、安全编码    |
| Phase 20 | 架构师实战     | 技术决策、架构评审、综合大型系统设计 |

---

## 🛠️ 技术栈

### 基础技术栈

- **Java 17 LTS**
- **Maven 3.8+**
- **Spring Boot 3.x**
- **Spring Cloud 2023.x**
- **MySQL 8.0 / Redis 7.x**
- **Docker / Kubernetes**

### 架构师进阶技术栈 🏗️

- **分布式数据库**: TiDB、CockroachDB
- **可观测性**: SkyWalking、Prometheus、Grafana、ELK Stack
- **安全**: OAuth2、OIDC、Vault
- **高可用**: Sentinel、Resilience4j
- **架构设计**: DDD、CQRS、Event Sourcing

---

## 📈 学习进度追踪

### 资深工程师阶段 (Phase 1-14)

| 阶段     | 主题          | 文件数 | 状态      |
| -------- | ------------- | ------ | --------- |
| Phase 1  | Java 基础语法 | 0/10   | ⏳ 待开始 |
| Phase 2  | 面向对象编程  | 0/12   | ⏳ 待开始 |
| Phase 3  | 集合框架      | 0/8    | ⏳ 待开始 |
| Phase 4  | IO 与 NIO     | 0/6    | ⏳ 待开始 |
| Phase 5  | 并发编程      | 0/12   | ⏳ 待开始 |
| Phase 6  | JVM 深入      | 0/10   | ⏳ 待开始 |
| Phase 7  | 设计模式      | 0/15   | ⏳ 待开始 |
| Phase 8  | Spring 框架   | 0/8    | ⏳ 待开始 |
| Phase 9  | Spring Boot   | 0/10   | ⏳ 待开始 |
| Phase 10 | 微服务架构    | 0/8    | ⏳ 待开始 |
| Phase 11 | 数据库与 ORM  | 0/10   | ⏳ 待开始 |
| Phase 12 | 分布式系统    | 0/10   | ⏳ 待开始 |
| Phase 13 | 性能调优      | 0/6    | ⏳ 待开始 |
| Phase 14 | 云原生技术    | 0/6    | ⏳ 待开始 |

### 架构师进阶阶段 (Phase 15-20) 🏗️

| 阶段     | 主题           | 文件数 | 状态      |
| -------- | -------------- | ------ | --------- |
| Phase 15 | 系统架构设计   | 0/10   | ⏳ 待开始 |
| Phase 16 | 高并发架构     | 0/8    | ⏳ 待开始 |
| Phase 17 | 分布式存储架构 | 0/8    | ⏳ 待开始 |
| Phase 18 | 可观测性工程   | 0/8    | ⏳ 待开始 |
| Phase 19 | 安全架构       | 0/6    | ⏳ 待开始 |
| Phase 20 | 架构师实战     | 0/10   | ⏳ 待开始 |

---

## 💼 职业发展路径

完成本课程后，您将具备以下岗位的核心能力：

| 阶段完成   | 目标岗位                             |
| ---------- | ------------------------------------ |
| Phase 1-8  | 中级 Java 开发工程师                 |
| Phase 1-14 | 高级/资深 Java 工程师                |
| Phase 1-20 | **系统架构师 / 技术专家 / 技术总监** |

---

**Good luck! 🚀**

有任何问题随时在代码注释或 `docs/` 中记录，养成持续学习和总结的习惯。
