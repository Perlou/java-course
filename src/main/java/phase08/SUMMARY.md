# Phase 8: Spring 框架 - 学习总结

> **完成状态**: ✅ 已完成  
> **学习时间**: 2025 年 12 月

---

## 🎯 学习目标达成

| 目标                      | 状态 |
| ------------------------- | ---- |
| 理解 IoC 和 DI 原理       | ✅   |
| 掌握 Spring Bean 生命周期 | ✅   |
| 理解 AOP 面向切面编程     | ✅   |
| 熟悉 Spring MVC 开发      | ✅   |
| 掌握声明式事务管理        | ✅   |
| 完成手写 IoC 容器项目     | ✅   |

---

## 📚 核心知识点

### 1. IoC 与依赖注入 ([IocBasics.java](./IocBasics.java))

```
传统方式 (紧耦合)        IoC 方式 (解耦)
────────────────────    ────────────────────
自己 new 依赖对象   →   容器注入依赖对象
难以测试           →   便于 Mock 测试
更换实现需改代码   →   只需修改配置
```

**三种依赖注入方式:**

| 方式        | 推荐度    | 说明                 |
| ----------- | --------- | -------------------- |
| 构造器注入  | ✅ 推荐   | 依赖不可变，便于测试 |
| Setter 注入 | ⚪ 可选   | 适合可选依赖         |
| 字段注入    | ❌ 不推荐 | 隐藏依赖，难以测试   |

---

### 2. Bean 生命周期 ([BeanLifecycle.java](./BeanLifecycle.java))

```
实例化 → 属性注入 → Aware回调 → 前置处理 → 初始化 → 后置处理 → 使用 → 销毁
  │         │          │           │         │          │
  new     @Autowired  感知容器   BeanPost  @Post     AOP代理
                      信息      Processor Construct  在此创建
```

**Bean 作用域:**

| 作用域      | 说明                            |
| ----------- | ------------------------------- |
| `singleton` | 单例 (默认)，容器只创建一个实例 |
| `prototype` | 原型，每次请求创建新实例        |
| `request`   | 每个 HTTP 请求一个实例          |
| `session`   | 每个 Session 一个实例           |

---

### 3. AOP 面向切面编程 ([AopBasics.java](./AopBasics.java))

**核心术语:**

| 术语       | 说明                       |
| ---------- | -------------------------- |
| Aspect     | 切面，包含切点和通知       |
| Pointcut   | 切点表达式，匹配方法       |
| Advice     | 通知 (Before/After/Around) |
| Join Point | 连接点，程序执行的点       |

**Advice 执行顺序:**

```
@Before ──▶ [方法执行] ──▶ @AfterReturning
    │                           │
    └────── @Around ────────────┘
               │
         (异常) ▼
        @AfterThrowing

@After: 无论成功或异常都执行 (finally)
```

**实现原理:**

- **JDK 动态代理**: 有接口时使用
- **CGLIB 代理**: 无接口时使用

---

### 4. Spring MVC ([SpringMvcBasics.java](./SpringMvcBasics.java))

**MVC 架构:**

```
┌─────────┐    ┌─────────────┐    ┌─────────┐
│  View   │◀───│ Controller  │───▶│  Model  │
│  视图   │    │   控制器    │    │  模型   │
└─────────┘    └─────────────┘    └─────────┘
  展示数据        处理请求         业务数据
```

**常用注解:**

| 注解              | 说明                   |
| ----------------- | ---------------------- |
| `@RestController` | REST 控制器            |
| `@GetMapping`     | GET 请求映射           |
| `@PostMapping`    | POST 请求映射          |
| `@PathVariable`   | 路径变量 `/users/{id}` |
| `@RequestBody`    | 请求体 JSON → 对象     |

**RESTful 设计:**

- 资源用名词: `/users`, `/orders`
- HTTP 动词表示操作: GET/POST/PUT/DELETE
- 合理使用状态码: 200/201/204/404/500

---

### 5. 声明式事务 ([TransactionDemo.java](./TransactionDemo.java))

**ACID 特性:**

| 特性        | 说明                       |
| ----------- | -------------------------- |
| Atomicity   | 原子性：全部成功或全部回滚 |
| Consistency | 一致性：事务前后数据一致   |
| Isolation   | 隔离性：事务间相互隔离     |
| Durability  | 持久性：提交后永久保存     |

**事务传播行为:**

| 传播行为       | 说明                       |
| -------------- | -------------------------- |
| `REQUIRED`     | 默认，有事务加入，没有新建 |
| `REQUIRES_NEW` | 总是新建，适合日志记录     |
| `NESTED`       | 嵌套事务，使用保存点       |

**⚠️ @Transactional 失效场景:**

1. ❌ 非 public 方法
2. ❌ 同类自调用（不走代理）
3. ❌ 异常被 catch 吞掉
4. ❌ 非 RuntimeException（需 rollbackFor）
5. ❌ 非 Spring 管理的对象

---

## 🎯 实战项目: Mini Spring

[MiniSpring.java](./MiniSpring.java) 手写实现了简易 IoC 容器：

| 功能      | 实现                |
| --------- | ------------------- |
| Bean 容器 | Map 存储和管理 Bean |
| 单例管理  | 单例 Bean 缓存      |
| 依赖注入  | @Autowired 字段注入 |
| 生命周期  | @PostConstruct 回调 |

---

## 🔑 关键收获

1. **IoC 核心思想**: 对象创建和依赖管理交给容器，实现解耦
2. **DI 最佳实践**: 优先使用构造器注入
3. **AOP 应用场景**: 日志、事务、权限、缓存
4. **事务原理**: @Transactional 基于 AOP 代理实现
5. **Spring MVC 流程**: DispatcherServlet → HandlerMapping → Controller → ViewResolver

---

## 📈 进阶方向

- [ ] 使用 Spring Initializr 创建实际项目
- [ ] 学习 Spring Boot 自动配置原理
- [ ] 完成 Phase 9: Spring Boot Framework

---

> 📝 Phase 8 完成！接下来进入 [Phase 9: Spring Boot Framework](../phase09/README.md)
