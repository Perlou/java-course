# Java 资深工程师 → 架构师 完整学习计划

> **定制对象**：零基础或初级 Java 开发者 → 资深 Java 工程师 → 架构师  
> **学习方式**：基于 Java 17 + Spring 生态的理论与实践结合学习  
> **预计时长**：35-43 周（每周投入 10-15 小时）  
> **当前进度**：🔄 准备开始第 1 阶段

---

## 📊 当前学习状态评估

### 🎯 学习目标

#### 资深工程师目标（Phase 1-14）

1. 掌握 Java 核心语法与面向对象编程
2. 深入理解 JVM 原理与并发编程
3. 熟练使用 Spring 生态开发企业级应用
4. 具备微服务架构设计与实现能力
5. 掌握分布式系统核心技术
6. 为资深 Java 工程师岗位面试做好准备

#### 架构师目标（Phase 15-20）🏗️

7. 掌握系统架构设计方法论（DDD、CQRS、事件溯源）
8. 具备高并发系统设计与优化能力
9. 深入理解分布式存储与数据一致性
10. 建立全链路可观测性工程能力
11. 掌握企业级安全架构设计
12. 具备架构师软技能与技术决策能力

---

## 🗺️ 详细学习计划

### 阶段 1：Java 基础语法 (2 周)

> **目标**：掌握 Java 核心语法与编程基础

#### 第 1 周：基础语法

- [ ] **环境搭建与第一个程序**

  - 创建 `HelloWorld.java`：JDK 安装、IDE 配置
  - 创建 `VariablesDemo.java`：变量与数据类型
  - 创建 `OperatorsDemo.java`：运算符详解

- [ ] **控制流程**
  - 创建 `ConditionDemo.java`：条件语句
  - 创建 `LoopDemo.java`：循环结构
  - 创建 `SwitchDemo.java`：switch 表达式（Java 17）

#### 第 2 周：数组与方法

- [ ] **数组操作**

  - 创建 `ArrayBasics.java`：数组创建与遍历
  - 创建 `ArrayAlgorithms.java`：排序与查找算法
  - 创建 `MultiDimensionalArray.java`：多维数组

- [ ] **方法与递归**

  - 创建 `MethodDemo.java`：方法定义与调用
  - 创建 `RecursionDemo.java`：递归算法

- [ ] **实战项目**：控制台计算器

---

### 阶段 2：面向对象编程 (2 周)

> **目标**：深入理解 OOP 核心概念

#### 第 3 周：类与对象

- [ ] **类的基础**

  - 创建 `ClassBasics.java`：类的定义、属性、方法
  - 创建 `ConstructorDemo.java`：构造方法与重载
  - 创建 `EncapsulationDemo.java`：封装与访问控制

- [ ] **继承与多态**
  - 创建 `InheritanceDemo.java`：继承机制
  - 创建 `PolymorphismDemo.java`：多态与动态绑定
  - 创建 `OverrideDemo.java`：方法重写

#### 第 4 周：高级 OOP

- [ ] **抽象与接口**

  - 创建 `AbstractClassDemo.java`：抽象类
  - 创建 `InterfaceDemo.java`：接口定义与实现
  - 创建 `DefaultMethodDemo.java`：接口默认方法

- [ ] **内部类与枚举**

  - 创建 `InnerClassDemo.java`：内部类类型
  - 创建 `EnumDemo.java`：枚举与枚举方法
  - 创建 `RecordDemo.java`：Record 类（Java 17）

- [ ] **实战项目**：学生管理系统

---

### 阶段 3：集合框架 (1 周)

> **目标**：掌握 Java 集合框架与 Stream API

#### 第 5 周：集合与 Stream

- [ ] **List 与 Set**

  - 创建 `ArrayListDemo.java`：ArrayList 操作
  - 创建 `LinkedListDemo.java`：LinkedList 与 Deque
  - 创建 `HashSetDemo.java`：HashSet 与 TreeSet

- [ ] **Map 集合**

  - 创建 `HashMapDemo.java`：HashMap 原理与使用
  - 创建 `TreeMapDemo.java`：TreeMap 与排序
  - 创建 `ConcurrentMapDemo.java`：线程安全 Map

- [ ] **Stream API**

  - 创建 `StreamBasics.java`：Stream 创建与操作
  - 创建 `StreamAdvanced.java`：Collector 与归约

- [ ] **实战项目**：数据统计分析工具

---

### 阶段 4：IO 与 NIO (1 周)

> **目标**：掌握文件操作与网络编程基础

#### 第 6 周：IO 操作

- [ ] **传统 IO**

  - 创建 `FileIODemo.java`：文件读写
  - 创建 `ByteStreamDemo.java`：字节流操作
  - 创建 `CharStreamDemo.java`：字符流操作

- [ ] **NIO 与序列化**

  - 创建 `NioBasics.java`：Buffer 与 Channel
  - 创建 `NioFileDemo.java`：Files 工具类
  - 创建 `SerializationDemo.java`：对象序列化

- [ ] **实战项目**：文件批量处理工具

---

### 阶段 5：并发编程 (2 周)

> **目标**：掌握 Java 多线程与并发编程

#### 第 7 周：线程基础

- [ ] **线程创建与控制**

  - 创建 `ThreadBasics.java`：创建线程的方式
  - 创建 `ThreadLifecycle.java`：线程生命周期
  - 创建 `ThreadMethods.java`：sleep、join、yield

- [ ] **线程同步**
  - 创建 `SynchronizedDemo.java`：synchronized 关键字
  - 创建 `LockDemo.java`：ReentrantLock
  - 创建 `DeadlockDemo.java`：死锁分析与避免

#### 第 8 周：并发工具

- [ ] **线程池**

  - 创建 `ExecutorDemo.java`：Executor 框架
  - 创建 `ThreadPoolDemo.java`：线程池配置与调优
  - 创建 `FutureDemo.java`：Callable 与 Future

- [ ] **并发工具类**

  - 创建 `CountDownLatchDemo.java`：倒计时门栓
  - 创建 `CyclicBarrierDemo.java`：循环屏障
  - 创建 `SemaphoreDemo.java`：信号量

- [ ] **实战项目**：多线程下载器

---

### 阶段 6：JVM 深入 (2 周)

> **目标**：深入理解 JVM 原理与调优

#### 第 9 周：JVM 内存模型

- [ ] **内存结构**

  - 创建 `MemoryModelDemo.java`：堆、栈、方法区
  - 创建 `HeapDemo.java`：堆内存分析
  - 创建 `StackDemo.java`：栈溢出演示
  - 文档：`docs/JVM_MEMORY_MODEL.md`

- [ ] **垃圾回收**
  - 创建 `GCBasics.java`：GC 算法演示
  - 创建 `GCRootsDemo.java`：GC Roots 分析
  - 创建 `GCTuningDemo.java`：GC 调优参数

#### 第 10 周：类加载与调优

- [ ] **类加载机制**

  - 创建 `ClassLoaderDemo.java`：类加载器
  - 创建 `CustomClassLoader.java`：自定义类加载器
  - 创建 `HotSwapDemo.java`：热加载原理

- [ ] **JVM 监控与调优**

  - 创建 `JvmMonitorDemo.java`：监控工具使用
  - 创建 `MemoryLeakDemo.java`：内存泄漏分析
  - 文档：`docs/JVM_TUNING_GUIDE.md`

- [ ] **实战项目**：JVM 性能分析报告

---

### 阶段 7：设计模式 (2 周)

> **目标**：掌握常用设计模式与 SOLID 原则

#### 第 11 周：创建型与结构型模式

- [ ] **创建型模式**

  - 创建 `SingletonDemo.java`：单例模式（多种实现）
  - 创建 `FactoryDemo.java`：工厂模式
  - 创建 `BuilderDemo.java`：建造者模式
  - 创建 `PrototypeDemo.java`：原型模式

- [ ] **结构型模式**
  - 创建 `ProxyDemo.java`：代理模式（静态/动态）
  - 创建 `DecoratorDemo.java`：装饰器模式
  - 创建 `AdapterDemo.java`：适配器模式
  - 创建 `FacadeDemo.java`：外观模式

#### 第 12 周：行为型模式与原则

- [ ] **行为型模式**

  - 创建 `StrategyDemo.java`：策略模式
  - 创建 `ObserverDemo.java`：观察者模式
  - 创建 `TemplateMethodDemo.java`：模板方法
  - 创建 `ChainOfResponsibilityDemo.java`：责任链模式
  - 创建 `StateDemo.java`：状态模式

- [ ] **SOLID 原则**

  - 创建 `SolidPrinciples.java`：五大原则示例
  - 文档：`docs/DESIGN_PATTERNS.md`

- [ ] **实战项目**：电商系统设计（应用设计模式）

---

### 阶段 8：Spring 框架 (2 周)

> **目标**：深入理解 Spring 核心原理

#### 第 13 周：IoC 与 DI

- [ ] **IoC 容器**

  - 创建 `IocBasics.java`：Bean 定义与获取
  - 创建 `BeanLifecycle.java`：Bean 生命周期
  - 创建 `BeanScope.java`：Bean 作用域
  - 创建 `DependencyInjection.java`：依赖注入方式

- [ ] **注解配置**
  - 创建 `AnnotationConfig.java`：注解配置
  - 创建 `ComponentScan.java`：组件扫描
  - 创建 `Autowired.java`：自动装配

#### 第 14 周：AOP 与 MVC

- [ ] **AOP 编程**

  - 创建 `AopBasics.java`：切面编程基础
  - 创建 `AspectDemo.java`：切点与通知
  - 创建 `TransactionDemo.java`：声明式事务

- [ ] **Spring MVC**

  - 创建 `ControllerDemo.java`：控制器开发
  - 创建 `RestControllerDemo.java`：RESTful API
  - 创建 `ExceptionHandler.java`：异常处理

- [ ] **实战项目**：手写简易 Spring IoC 容器

---

### 阶段 9：Spring Boot (2 周)

> **目标**：掌握 Spring Boot 快速开发

#### 第 15 周：Spring Boot 核心

- [ ] **自动配置**

  - 创建 `AutoConfigDemo.java`：自动配置原理
  - 创建 `StarterDemo.java`：Starter 机制
  - 创建 `ConditionalDemo.java`：条件装配

- [ ] **配置管理**
  - 创建 `ConfigurationDemo.java`：配置文件管理
  - 创建 `ProfileDemo.java`：多环境配置
  - 创建 `ExternalConfigDemo.java`：外部化配置

#### 第 16 周：企业级特性

- [ ] **数据访问**

  - 创建 `JdbcTemplateDemo.java`：JDBC Template
  - 创建 `JpaDemo.java`：Spring Data JPA
  - 创建 `TransactionManagement.java`：事务管理

- [ ] **监控与安全**

  - 创建 `ActuatorDemo.java`：Actuator 监控
  - 创建 `SecurityBasics.java`：Spring Security 基础
  - 创建 `JwtDemo.java`：JWT 认证

- [ ] **实战项目**：RESTful API 后端服务

---

### 阶段 10：微服务架构 (2 周)

> **目标**：掌握 Spring Cloud 微服务开发

#### 第 17 周：服务治理

- [ ] **服务注册与发现**

  - 创建 `NacosDemo`：Nacos 服务注册
  - 创建 `EurekaDemo`：Eureka 服务发现
  - 创建 `ServiceDiscovery.java`：服务发现客户端

- [ ] **服务调用**
  - 创建 `OpenFeignDemo`：声明式调用
  - 创建 `LoadBalancerDemo`：负载均衡
  - 创建 `CircuitBreakerDemo`：熔断降级

#### 第 18 周：网关与配置

- [ ] **API 网关**

  - 创建 `GatewayDemo`：Spring Cloud Gateway
  - 创建 `RouteConfig.java`：路由配置
  - 创建 `FilterDemo.java`：过滤器链

- [ ] **分布式配置**

  - 创建 `ConfigServerDemo`：配置中心
  - 创建 `BusDemo`：消息总线
  - 文档：`docs/MICROSERVICES_ARCH.md`

- [ ] **实战项目**：微服务电商系统

---

### 阶段 11：数据库与 ORM (2 周)

> **目标**：掌握数据库编程与 ORM 框架

#### 第 19 周：MySQL 与 MyBatis

- [ ] **MySQL 进阶**

  - 创建 `IndexDemo.java`：索引原理与优化
  - 创建 `TransactionDemo.java`：事务隔离级别
  - 创建 `LockDemo.java`：锁机制
  - 文档：`docs/MYSQL_OPTIMIZATION.md`

- [ ] **MyBatis**
  - 创建 `MyBatisBasics.java`：基础配置与使用
  - 创建 `DynamicSqlDemo.java`：动态 SQL
  - 创建 `MyBatisPlusDemo.java`：MyBatis-Plus

#### 第 20 周：JPA 与分库分表

- [ ] **Spring Data JPA**

  - 创建 `JpaRepositoryDemo.java`：Repository 模式
  - 创建 `QueryMethodDemo.java`：查询方法
  - 创建 `SpecificationDemo.java`：动态查询

- [ ] **分库分表**

  - 创建 `ShardingDemo.java`：ShardingSphere
  - 创建 `ReadWriteSplitDemo.java`：读写分离

- [ ] **实战项目**：订单管理系统（分库分表）

---

### 阶段 12：分布式系统 (2 周)

> **目标**：掌握分布式系统核心技术

#### 第 21 周：缓存与消息队列

- [ ] **Redis**

  - 创建 `RedisBasics.java`：Redis 数据类型
  - 创建 `RedisCache.java`：缓存策略
  - 创建 `DistributedLock.java`：分布式锁

- [ ] **消息队列**
  - 创建 `RocketMQDemo.java`：RocketMQ 使用
  - 创建 `KafkaDemo.java`：Kafka 消息
  - 创建 `MessageReliability.java`：消息可靠性

#### 第 22 周：分布式事务与协调

- [ ] **分布式事务**

  - 创建 `SeataDemo.java`：Seata AT 模式
  - 创建 `TccDemo.java`：TCC 模式
  - 创建 `SagaDemo.java`：Saga 模式

- [ ] **分布式协调**

  - 创建 `DistributedIdDemo.java`：分布式 ID
  - 创建 `ZookeeperDemo.java`：分布式协调
  - 文档：`docs/DISTRIBUTED_SYSTEMS.md`

- [ ] **实战项目**：秒杀系统

---

### 阶段 13：性能调优 (1 周)

> **目标**：掌握系统性能优化方法

#### 第 23 周：全链路优化

- [ ] **JVM 调优**

  - 创建 `JvmTuning.java`：JVM 参数调优
  - 创建 `GCAnalysis.java`：GC 日志分析
  - 创建 `MemoryAnalysis.java`：内存问题排查

- [ ] **SQL 与缓存优化**

  - 创建 `SqlOptimization.java`：SQL 优化
  - 创建 `CacheStrategy.java`：缓存策略
  - 创建 `ConnectionPoolTuning.java`：连接池调优

- [ ] **实战项目**：性能压测与优化报告

---

### 阶段 14：云原生技术 (1 周)

> **目标**：掌握容器化与云原生开发

#### 第 24 周：Docker 与 Kubernetes

- [ ] **Docker**

  - 创建 `Dockerfile`：镜像构建
  - 创建 `docker-compose.yml`：容器编排
  - 文档：`docs/DOCKER_GUIDE.md`

- [ ] **Kubernetes**

  - 创建 `deployment.yaml`：K8s 部署
  - 创建 `service.yaml`：服务配置
  - 创建 `configmap.yaml`：配置管理
  - 文档：`docs/K8S_GUIDE.md`

- [ ] **实战项目**：K8s 部署微服务应用

---

### 阶段 15：系统架构设计 (2 周) 🏗️

> **目标**：掌握架构设计方法论与核心模式

#### 第 25 周：架构设计原则

- [ ] **架构基础理论**

  - 创建 `ArchitecturePrinciples.java`：CAP、BASE 理论
  - 创建 `SolidInArch.java`：SOLID 在架构中的应用
  - 创建 `TwelveFactorApp.java`：12-Factor 应用原则

- [ ] **架构模式**
  - 创建 `LayeredArchitecture.java`：分层架构
  - 创建 `HexagonalArchitecture.java`：六边形架构
  - 创建 `EventDrivenArchitecture.java`：事件驱动架构

#### 第 26 周：领域驱动设计

- [ ] **DDD 核心概念**

  - 创建 `DomainModel.java`：领域模型设计
  - 创建 `AggregateRoot.java`：聚合根与边界
  - 创建 `DomainEvent.java`：领域事件

- [ ] **CQRS 与事件溯源**

  - 创建 `CqrsDemo.java`：命令查询职责分离
  - 创建 `EventSourcing.java`：事件溯源实现
  - 文档：`docs/DDD_ARCHITECTURE.md`

- [ ] **实战项目**：电商系统领域建模

---

### 阶段 16：高并发架构 (2 周) ⚡

> **目标**：掌握高并发系统设计与优化

#### 第 27 周：流量控制与限流

- [ ] **限流算法**

  - 创建 `TokenBucket.java`：令牌桶算法
  - 创建 `SlidingWindow.java`：滑动窗口算法
  - 创建 `LeakyBucket.java`：漏桶算法

- [ ] **限流实践**
  - 创建 `SentinelDemo.java`：Sentinel 限流
  - 创建 `RateLimiterDemo.java`：Guava RateLimiter
  - 创建 `DistributedRateLimiter.java`：分布式限流

#### 第 28 周：高可用设计

- [ ] **降级与熔断**

  - 创建 `CircuitBreakerPattern.java`：熔断器模式
  - 创建 `FallbackStrategy.java`：降级策略
  - 创建 `Resilience4jDemo.java`：Resilience4j 实践

- [ ] **高可用架构**

  - 创建 `MultiActiveDesign.java`：多活架构
  - 创建 `DisasterRecovery.java`：容灾设计
  - 文档：`docs/HIGH_AVAILABILITY.md`

- [ ] **实战项目**：百万级并发系统设计

---

### 阶段 17：分布式存储架构 (2 周) 💾

> **目标**：掌握分布式存储与数据一致性

#### 第 29 周：分布式数据库

- [ ] **NewSQL 数据库**

  - 创建 `TiDBDemo.java`：TiDB 使用与原理
  - 创建 `CockroachDBDemo.java`：CockroachDB 特性
  - 创建 `VitessDemo.java`：Vitess 分片

- [ ] **一致性算法**
  - 创建 `RaftDemo.java`：Raft 共识算法
  - 创建 `PaxosDemo.java`：Paxos 原理
  - 创建 `ConsistencyModel.java`：一致性模型对比

#### 第 30 周：数据架构

- [ ] **存储架构**

  - 创建 `DataLakeDesign.java`：数据湖架构
  - 创建 `DataWarehouse.java`：数据仓库设计
  - 创建 `LSMTree.java`：LSM-Tree 原理

- [ ] **多数据源架构**

  - 创建 `PolyglotPersistence.java`：多模存储
  - 创建 `CacheAsidePattern.java`：缓存策略
  - 文档：`docs/DISTRIBUTED_STORAGE.md`

- [ ] **实战项目**：多数据源架构设计

---

### 阶段 18：可观测性工程 (2 周) 📊

> **目标**：建立全链路可观测性能力

#### 第 31 周：链路追踪与指标

- [ ] **分布式追踪**

  - 创建 `SkyWalkingDemo.java`：SkyWalking 集成
  - 创建 `JaegerDemo.java`：Jaeger 追踪
  - 创建 `TraceContextPropagation.java`：上下文传播

- [ ] **指标监控**
  - 创建 `PrometheusMetrics.java`：Prometheus 指标
  - 创建 `MicrometerDemo.java`：Micrometer 集成
  - 创建 `GrafanaDashboard.java`：Grafana 可视化

#### 第 32 周：日志与告警

- [ ] **日志系统**

  - 创建 `ELKStackDemo.java`：ELK Stack 集成
  - 创建 `StructuredLogging.java`：结构化日志
  - 创建 `LogAggregation.java`：日志聚合

- [ ] **告警体系**

  - 创建 `AlertRules.java`：告警规则设计
  - 创建 `OnCallSystem.java`：On-Call 体系
  - 文档：`docs/OBSERVABILITY.md`

- [ ] **实战项目**：全链路可观测平台搭建

---

### 阶段 19：安全架构 (1 周) 🔐

> **目标**：掌握企业级安全架构设计

#### 第 33 周：安全架构设计

- [ ] **认证与授权**

  - 创建 `OAuth2InDepth.java`：OAuth2 深入
  - 创建 `OIDCDemo.java`：OpenID Connect
  - 创建 `RBACDesign.java`：RBAC 权限设计

- [ ] **零信任与数据安全**

  - 创建 `ZeroTrustArch.java`：零信任架构
  - 创建 `DataEncryption.java`：数据加密策略
  - 创建 `SecureCoding.java`：安全编码（OWASP Top 10）
  - 文档：`docs/SECURITY_ARCHITECTURE.md`

- [ ] **实战项目**：企业级安全架构设计

---

### 阶段 20：架构师实战 (2 周) 🎯

> **目标**：培养架构师综合能力与软技能

#### 第 34 周：技术决策与管理

- [ ] **技术选型**

  - 创建 `TechSelectionFramework.java`：技术选型框架
  - 创建 `TradeoffAnalysis.java`：权衡分析方法
  - 创建 `POCDesign.java`：概念验证设计

- [ ] **技术债务与演进**
  - 创建 `TechDebtManagement.java`：技术债务管理
  - 创建 `ArchitectureEvolution.java`：架构演进策略
  - 创建 `MigrationPattern.java`：迁移模式

#### 第 35 周：架构评审与综合实战

- [ ] **架构评审**

  - 创建 `ArchReviewProcess.java`：架构评审流程
  - 创建 `ATAMDemo.java`：ATAM 评估方法
  - 创建 `RiskAnalysis.java`：风险分析

- [ ] **综合能力**

  - 创建 `TechnicalWriting.java`：技术文档写作
  - 创建 `TeamTechManagement.java`：团队技术管理
  - 文档：`docs/ARCHITECT_SKILLS.md`

- [ ] **综合实战项目**：从 0 到 1 设计大型分布式系统

---

## 🎓 配套学习资源

### 必读书籍

- [ ] 《Java 核心技术》卷 I & II
- [ ] 《深入理解 Java 虚拟机》- 周志明
- [ ] 《Java 并发编程实战》
- [ ] 《Effective Java》- Joshua Bloch
- [ ] 《Spring 实战》

### 推荐课程

- [ ] **尚硅谷 Java 基础**：入门首选
- [ ] **黑马程序员 JVM**：JVM 深入
- [ ] **周阳 Spring Cloud**：微服务实战

### 关键技术文档

| 阶段     | 文档                  | 必读程度   |
| -------- | --------------------- | ---------- |
| Phase 5  | Java 并发包官方文档   | ⭐⭐⭐⭐⭐ |
| Phase 6  | JVM 规范              | ⭐⭐⭐⭐   |
| Phase 8  | Spring 官方文档       | ⭐⭐⭐⭐⭐ |
| Phase 9  | Spring Boot 官方文档  | ⭐⭐⭐⭐⭐ |
| Phase 10 | Spring Cloud 官方文档 | ⭐⭐⭐⭐   |

---

## 📝 学习方法建议

### 1️⃣ 代码先行，理论跟进

每个概念先运行代码、观察结果，再深入理解原理。

### 2️⃣ 构建个人知识库

- 将所有学习笔记放入 `docs/` 目录
- 定期复盘总结到 `CONCEPTS.md`

### 3️⃣ 项目驱动学习

每个阶段末尾完成对应实战项目，巩固所学。

### 4️⃣ 源码阅读习惯

从 Phase 5 开始，养成阅读源码的习惯（JDK、Spring）。

### 5️⃣ 面试题目积累

- 每个阶段整理常见面试题
- 在 `docs/` 中记录面试要点

---

## 🎯 学习里程碑检查

### 第 6 周后（Phase 1-4 完成）

- [ ] Java 基础语法熟练
- [ ] OOP 思想理解透彻
- [ ] 集合框架原理清楚
- [ ] 完成学生管理系统项目

### 第 14 周后（Phase 5-8 完成）

- [ ] 并发编程核心掌握
- [ ] JVM 原理深入理解
- [ ] 设计模式灵活运用
- [ ] Spring 核心原理清楚
- [ ] 完成手写 IoC 项目

### 第 22 周后（Phase 9-12 完成）

- [ ] Spring Boot 熟练开发
- [ ] 微服务架构设计能力
- [ ] 分布式系统核心技术
- [ ] 完成微服务电商/秒杀项目

### 第 24 周后（资深工程师阶段完成）

- [ ] 性能调优经验积累
- [ ] 云原生技术掌握
- [ ] 具备资深工程师能力

### 第 35 周后（架构师阶段完成）🏗️

- [ ] 架构设计方法论熟练掌握
- [ ] 高并发与高可用设计能力
- [ ] 分布式存储与数据一致性深入理解
- [ ] 全链路可观测性工程能力
- [ ] 安全架构设计能力
- [ ] 架构师软技能与技术决策能力
- [ ] 完成大型系统架构设计项目

---

## 💼 职业发展建议

### 作品集建设

将以下项目放入 GitHub：

1. `java-course`（学习笔记库）
2. 多线程下载器
3. 简易 Spring IoC
4. 微服务电商系统
5. 秒杀系统
6. 电商系统领域建模（DDD）🏗️
7. 百万级并发系统设计 🏗️
8. 全链路可观测平台 🏗️
9. 大型分布式系统架构设计 🏗️

### 技能关键词（简历优化）

完成本计划后，可以突出：

#### 资深工程师技能

- Java 核心与并发编程
- JVM 性能调优
- Spring Boot / Spring Cloud 开发
- 微服务架构设计
- 分布式系统（Redis、MQ、分布式事务）
- MySQL 优化与分库分表
- Docker / Kubernetes 部署

#### 架构师技能 🏗️

- 系统架构设计（DDD、CQRS、事件溯源）
- 高并发架构（限流、熔断、高可用）
- 分布式存储（NewSQL、数据一致性）
- 可观测性工程（追踪、监控、日志）
- 安全架构（OAuth2、零信任）
- 技术决策与架构评审

### 目标岗位

#### 资深工程师阶段（Phase 1-14）

- 高级 Java 开发工程师
- Java 技术专家
- 技术 Leader

#### 架构师阶段（Phase 15-20）🏗️

- 系统架构师
- 首席架构师
- 技术总监
- CTO

---

## 📊 进度追踪

| 周次  | 阶段     | 完成文件数 | 实战项目     | 状态      |
| ----- | -------- | ---------- | ------------ | --------- |
| 1-2   | Phase 1  | 0/10       | 控制台计算器 | ⏳ 待开始 |
| 3-4   | Phase 2  | 0/12       | 学生管理系统 | ⏳ 待开始 |
| 5     | Phase 3  | 0/8        | 数据统计工具 | ⏳ 待开始 |
| 6     | Phase 4  | 0/6        | 文件处理工具 | ⏳ 待开始 |
| 7-8   | Phase 5  | 0/12       | 多线程下载器 | ⏳ 待开始 |
| 9-10  | Phase 6  | 0/10       | JVM 分析报告 | ⏳ 待开始 |
| 11-12 | Phase 7  | 0/15       | 设计模式应用 | ⏳ 待开始 |
| 13-14 | Phase 8  | 0/8        | 手写 IoC     | ⏳ 待开始 |
| 15-16 | Phase 9  | 0/10       | RESTful API  | ⏳ 待开始 |
| 17-18 | Phase 10 | 0/8        | 微服务电商   | ⏳ 待开始 |
| 19-20 | Phase 11 | 0/10       | 订单管理     | ⏳ 待开始 |
| 21-22 | Phase 12 | 0/10       | 秒杀系统     | ⏳ 待开始 |
| 23    | Phase 13 | 0/6        | 性能优化     | ⏳ 待开始 |
| 24    | Phase 14 | 0/6        | K8s 部署     | ⏳ 待开始 |

### 架构师进阶阶段 (Phase 15-20) 🏗️

| 周次  | 阶段     | 完成文件数 | 实战项目         | 状态      |
| ----- | -------- | ---------- | ---------------- | --------- |
| 25-26 | Phase 15 | 0/10       | 电商领域建模     | ⏳ 待开始 |
| 27-28 | Phase 16 | 0/8        | 高并发系统设计   | ⏳ 待开始 |
| 29-30 | Phase 17 | 0/8        | 多数据源架构     | ⏳ 待开始 |
| 31-32 | Phase 18 | 0/8        | 可观测平台       | ⏳ 待开始 |
| 33    | Phase 19 | 0/6        | 安全架构设计     | ⏳ 待开始 |
| 34-35 | Phase 20 | 0/10       | 大型系统架构设计 | ⏳ 待开始 |

---

**Good luck! 🚀**

有任何问题随时在代码注释或 `docs/` 中记录，养成持续学习和总结的习惯。
