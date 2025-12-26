# Phase 8: Spring 框架核心 - 核心概念

> Spring 是 Java 企业级开发的事实标准

---

## 🎯 Spring 全景图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Spring 生态系统                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│                      Spring Boot                                    │
│                   (快速启动/自动配置)                                │
│                          │                                          │
│  ┌─────────────┬─────────┴─────────┬─────────────┐                 │
│  │             │                   │             │                 │
│  ↓             ↓                   ↓             ↓                 │
│  Spring     Spring     Spring     Spring     Spring                │
│  MVC        Data       Security   Cloud      Batch                 │
│  Web框架    数据访问    安全框架    微服务     批处理                │
│                                                                     │
│                          │                                          │
│                          ↓                                          │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                   Spring Framework                           │   │
│  │  ┌───────────────┬───────────────┬───────────────────────┐  │   │
│  │  │    IoC/DI     │     AOP       │      其他模块          │  │   │
│  │  │  控制反转     │   面向切面     │    事务/JDBC/...      │  │   │
│  │  └───────────────┴───────────────┴───────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 IoC 控制反转

### 什么是 IoC？

```
┌─────────────────────────────────────────────────────────────────────┐
│                     控制反转 (IoC)                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  传统方式:                                                          │
│  ──────────────────────────────────────────────────────────────    │
│  对象自己创建依赖                                                   │
│                                                                     │
│  public class UserService {                                         │
│      private UserDao userDao = new UserDao();  // 自己创建          │
│  }                                                                  │
│                                                                     │
│  IoC 方式:                                                          │
│  ──────────────────────────────────────────────────────────────    │
│  由容器创建并注入依赖                                               │
│                                                                     │
│  public class UserService {                                         │
│      @Autowired                                                     │
│      private UserDao userDao;  // 容器注入                          │
│  }                                                                  │
│                                                                     │
│  好处:                                                              │
│  • 解耦: 不依赖具体实现                                             │
│  • 可测试: 容易 Mock 依赖                                           │
│  • 可配置: 运行时切换实现                                           │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 依赖注入方式

```java
// 1. 构造器注入 (推荐)
@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// 2. Setter 注入
@Service
public class UserService {
    private UserRepository repository;

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}

// 3. 字段注入 (不推荐)
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
}
```

---

## 🫘 Bean 生命周期

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Bean 生命周期                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. 实例化 (Instantiation)                                         │
│     └── 创建 Bean 对象                                             │
│              ↓                                                      │
│  2. 属性注入 (Populate Properties)                                 │
│     └── 注入依赖                                                    │
│              ↓                                                      │
│  3. BeanNameAware.setBeanName()                                    │
│              ↓                                                      │
│  4. BeanFactoryAware.setBeanFactory()                              │
│              ↓                                                      │
│  5. ApplicationContextAware.setApplicationContext()                 │
│              ↓                                                      │
│  6. BeanPostProcessor.postProcessBeforeInitialization()            │
│              ↓                                                      │
│  7. @PostConstruct / InitializingBean.afterPropertiesSet()         │
│              ↓                                                      │
│  8. BeanPostProcessor.postProcessAfterInitialization()             │
│              ↓                                                      │
│  9. Bean 就绪，可以使用                                             │
│              ↓                                                      │
│  10. @PreDestroy / DisposableBean.destroy()                        │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Bean 作用域

| 作用域    | 描述                       |
| --------- | -------------------------- |
| singleton | 默认，全局唯一实例         |
| prototype | 每次获取创建新实例         |
| request   | 每个 HTTP 请求一个实例     |
| session   | 每个 HTTP Session 一个实例 |

---

## ✂️ AOP 面向切面

### AOP 概念

```
┌─────────────────────────────────────────────────────────────────────┐
│                        AOP 核心概念                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  切面 (Aspect)                                                      │
│  └── 横切关注点的模块化 (如日志、事务)                              │
│                                                                     │
│  切入点 (Pointcut)                                                  │
│  └── 定义在哪些方法上执行                                           │
│                                                                     │
│  通知 (Advice)                                                      │
│  └── 在切入点执行的代码                                             │
│                                                                     │
│  通知类型:                                                          │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ @Before        方法执行前                                     │  │
│  │ @After         方法执行后 (无论是否异常)                      │  │
│  │ @AfterReturning 方法正常返回后                                │  │
│  │ @AfterThrowing  方法抛出异常后                                │  │
│  │ @Around        环绕通知 (最强大)                              │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### AOP 示例

```java
@Aspect
@Component
public class LogAspect {

    // 切入点定义
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    // 环绕通知
    @Around("serviceLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().getName();
        log.info("方法开始: {}", method);

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();  // 执行目标方法
        long elapsed = System.currentTimeMillis() - start;

        log.info("方法结束: {}, 耗时: {}ms", method, elapsed);
        return result;
    }
}
```

---

## 🌐 Spring MVC

### 请求处理流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Spring MVC 请求流程                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  客户端请求                                                         │
│      ↓                                                              │
│  DispatcherServlet (前端控制器)                                     │
│      ↓                                                              │
│  HandlerMapping (查找处理器)                                        │
│      ↓                                                              │
│  Controller (处理请求)                                              │
│      ↓                                                              │
│  ViewResolver (解析视图) / ResponseBody                             │
│      ↓                                                              │
│  返回响应                                                           │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 常用注解

```java
@RestController     // @Controller + @ResponseBody
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) { }

    @PostMapping
    public User create(@RequestBody User user) { }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) { }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { }
}
```

---

## 💳 事务管理

```java
@Service
public class OrderService {

    @Transactional
    public void createOrder(Order order) {
        // 扣减库存
        inventoryService.deduct(order.getItems());
        // 保存订单
        orderRepository.save(order);
        // 扣减余额
        accountService.deduct(order.getUserId(), order.getAmount());
    }
}
```

### @Transactional 属性

| 属性          | 说明             |
| ------------- | ---------------- |
| propagation   | 事务传播行为     |
| isolation     | 隔离级别         |
| rollbackFor   | 触发回滚的异常   |
| noRollbackFor | 不触发回滚的异常 |
| timeout       | 超时时间         |
| readOnly      | 只读事务         |

### 事务失效场景

```
⚠️ @Transactional 失效的常见原因:

1. 方法不是 public
2. 同类中的方法内部调用
3. 异常被 catch 吞掉
4. 抛出的是非 RuntimeException
5. 数据库引擎不支持事务 (MyISAM)
```

---

## 📖 学习要点

```
✅ 理解 IoC 和 DI 的概念
✅ 掌握 Bean 的生命周期
✅ 理解 AOP 的原理和应用
✅ 熟练使用 Spring MVC
✅ 掌握声明式事务管理
```

---

> 继续学习 Spring Boot: [Phase 9 README](../phase09/README.md)
