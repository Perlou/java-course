# Phase 8: Spring 框架

> **目标**：深入理解 Spring 核心原理  
> **预计时长**：2 周  
> **前置条件**：Phase 7 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解 IoC 和 DI 原理
2. 掌握 Spring Bean 生命周期
3. 理解 AOP 面向切面编程
4. 熟悉 Spring MVC 开发
5. 掌握声明式事务管理
6. 能够手写简易 IoC 容器

---

## 📚 核心概念

### IoC 容器

```
┌─────────────────────────────────────────────────────────┐
│                    Spring 容器                          │
│  ┌─────────────────────────────────────────────────┐   │
│  │                 Bean 工厂                        │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐            │   │
│  │  │ Bean A  │ │ Bean B  │ │ Bean C  │ ...        │   │
│  │  └─────────┘ └─────────┘ └─────────┘            │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  职责: 创建 Bean → 注入依赖 → 管理生命周期             │
└─────────────────────────────────────────────────────────┘
```

### AOP 切面

| 术语     | 说明                       |
| -------- | -------------------------- |
| Aspect   | 切面模块                   |
| Pointcut | 切点表达式                 |
| Advice   | 通知 (Before/After/Around) |

---

## 📁 文件列表

| #   | 文件                                           | 描述            | 知识点                   |
| --- | ---------------------------------------------- | --------------- | ------------------------ |
| 1   | [IocBasics.java](./IocBasics.java)             | IoC 与 DI       | 控制反转, 依赖注入       |
| 2   | [BeanLifecycle.java](./BeanLifecycle.java)     | Bean 生命周期   | 生命周期, 作用域         |
| 3   | [AopBasics.java](./AopBasics.java)             | AOP 基础        | 切面, 通知, 代理         |
| 4   | [SpringMvcBasics.java](./SpringMvcBasics.java) | Spring MVC      | 控制器, 注解, RESTful    |
| 5   | [TransactionDemo.java](./TransactionDemo.java) | 声明式事务      | @Transactional, 传播行为 |
| 6   | [MiniSpring.java](./MiniSpring.java)           | 🎯 **实战项目** | 手写 IoC 容器            |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行 IoC 基础
mvn exec:java -Dexec.mainClass="phase08.IocBasics"

# 运行 Bean 生命周期
mvn exec:java -Dexec.mainClass="phase08.BeanLifecycle"

# 运行 AOP 基础
mvn exec:java -Dexec.mainClass="phase08.AopBasics"

# 运行实战项目 - 手写 IoC
mvn exec:java -Dexec.mainClass="phase08.MiniSpring"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: IocBasics - IoC 和依赖注入原理
2. **Day 3-4**: BeanLifecycle - Bean 生命周期和作用域
3. **Day 5-6**: AopBasics - AOP 面向切面编程 (重点)
4. **Day 7-8**: SpringMvcBasics - MVC 和 RESTful
5. **Day 9-10**: TransactionDemo - 事务管理
6. **Day 11-14**: MiniSpring 项目实战

### 进阶学习

本阶段是理论基础，建议后续：

1. 使用 [Spring Initializr](https://start.spring.io) 创建项目
2. 完成一个完整的 Spring Boot 应用
3. 学习 Spring Boot 自动配置原理

---

## ✅ 完成检查

- [ ] 理解 IoC 控制反转的概念
- [ ] 理解依赖注入的三种方式
- [ ] 掌握 Bean 生命周期各阶段
- [ ] 理解 AOP 的核心术语
- [ ] 掌握 @Transactional 使用和失效场景
- [ ] 完成手写 IoC 容器项目

---

## 🎯 实战项目: 手写 Mini Spring

`MiniSpring.java` 是本阶段的综合项目，实现了一个简易 IoC 容器：

| 功能      | 实现                |
| --------- | ------------------- |
| Bean 容器 | 存储和管理 Bean     |
| 单例管理  | 单例 Bean 缓存      |
| 依赖注入  | @Autowired 字段注入 |
| 生命周期  | @PostConstruct 回调 |

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase08.MiniSpring"
```

---

## 📚 Spring 常用注解速查

```java
// 组件扫描
@Component, @Service, @Repository, @Controller

// 依赖注入
@Autowired, @Qualifier, @Value

// 生命周期
@PostConstruct, @PreDestroy

// 作用域
@Scope("prototype")

// 事务
@Transactional(rollbackFor = Exception.class)

// AOP
@Aspect, @Before, @After, @Around

// MVC
@RestController, @RequestMapping, @GetMapping
@PathVariable, @RequestParam, @RequestBody
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 9: 数据库与 JDBC](../phase09/README.md)
