# Java 核心概念汇总

> 本文档汇总 Java 的核心概念，随学习进度持续更新。

---

## 目录

1. [Java 基础](#1-java-基础)
2. [面向对象编程](#2-面向对象编程)
3. [集合框架](#3-集合框架)
4. [并发编程](#4-并发编程)
5. [JVM 原理](#5-jvm-原理)
6. [设计模式](#6-设计模式)
7. [Spring 框架](#7-spring-框架)
8. [微服务架构](#8-微服务架构)
9. [分布式系统](#9-分布式系统)
10. [性能调优](#10-性能调优)

---

## 1. Java 基础

### 1.1 数据类型

Java 有 8 种基本数据类型：

```
整型: byte (1), short (2), int (4), long (8)
浮点型: float (4), double (8)
字符型: char (2)
布尔型: boolean
```

### 1.2 Java 17 新特性

```java
// Record 类
public record Person(String name, int age) {}

// 密封类
public sealed class Shape permits Circle, Rectangle {}

// 模式匹配
if (obj instanceof String s) {
    System.out.println(s.length());
}

// Switch 表达式
String result = switch (day) {
    case MONDAY, FRIDAY -> "Work";
    case SATURDAY, SUNDAY -> "Rest";
    default -> "Unknown";
};
```

---

## 2. 面向对象编程

### 2.1 OOP 四大特性

| 特性     | 说明             | 关键字/实现            |
| -------- | ---------------- | ---------------------- |
| **封装** | 隐藏实现细节     | private, getter/setter |
| **继承** | 代码复用         | extends                |
| **多态** | 同一接口不同实现 | 重写, 接口             |
| **抽象** | 定义模板         | abstract, interface    |

### 2.2 接口 vs 抽象类

```java
// 接口 - 定义契约
public interface Flyable {
    void fly();
    default void land() { /* 默认实现 */ }
}

// 抽象类 - 定义模板
public abstract class Animal {
    protected String name;
    public abstract void sound();
    public void eat() { /* 通用实现 */ }
}
```

**选择原则**：

- 接口：定义能力（can-do）
- 抽象类：定义是什么（is-a）

---

## 3. 集合框架

### 3.1 集合体系

```
Collection
├── List (有序，可重复)
│   ├── ArrayList  - 数组实现，查询快
│   ├── LinkedList - 链表实现，增删快
│   └── Vector     - 线程安全
├── Set (无序，不重复)
│   ├── HashSet    - 哈希表实现
│   ├── LinkedHashSet - 保持插入顺序
│   └── TreeSet    - 排序
└── Queue (队列)
    ├── LinkedList
    └── PriorityQueue

Map (键值对)
├── HashMap        - 哈希表实现
├── LinkedHashMap  - 保持插入顺序
├── TreeMap        - 排序
└── ConcurrentHashMap - 线程安全
```

### 3.2 HashMap 原理

```
结构: 数组 + 链表 + 红黑树 (JDK 8+)

put 流程:
1. 计算 key 的 hash 值
2. 定位数组下标
3. 处理哈希冲突（链表/红黑树）
4. 扩容检查（负载因子 0.75）

红黑树转换:
- 链表长度 > 8 且数组长度 >= 64 → 红黑树
- 红黑树节点 < 6 → 链表
```

---

## 4. 并发编程

### 4.1 线程状态

```
NEW → RUNNABLE ←→ BLOCKED/WAITING/TIMED_WAITING → TERMINATED

关键方法:
- start()    - 启动线程
- sleep()    - 休眠（不释放锁）
- wait()     - 等待（释放锁）
- notify()   - 唤醒
- join()     - 等待线程结束
```

### 4.2 synchronized vs Lock

| 特性     | synchronized | Lock |
| -------- | ------------ | ---- |
| 加锁方式 | 隐式         | 显式 |
| 可中断   | 否           | 是   |
| 超时获取 | 否           | 是   |
| 公平锁   | 否           | 可选 |
| 条件变量 | 1 个         | 多个 |

### 4.3 线程池参数

```java
ThreadPoolExecutor(
    int corePoolSize,      // 核心线程数
    int maximumPoolSize,   // 最大线程数
    long keepAliveTime,    // 空闲线程存活时间
    TimeUnit unit,         // 时间单位
    BlockingQueue<Runnable> workQueue,  // 任务队列
    ThreadFactory threadFactory,         // 线程工厂
    RejectedExecutionHandler handler     // 拒绝策略
)

拒绝策略:
- AbortPolicy        - 抛异常
- CallerRunsPolicy   - 调用者执行
- DiscardPolicy      - 静默丢弃
- DiscardOldestPolicy - 丢弃最旧任务
```

---

## 5. JVM 原理

### 5.1 内存模型

```
┌───────────────────────────────────────┐
│          运行时数据区                   │
├─────────────────┬─────────────────────┤
│   线程共享       │     线程私有          │
├─────────────────┼─────────────────────┤
│ 堆 (Heap)       │ 虚拟机栈 (VM Stack) │
│ 方法区 (Method) │ 本地方法栈          │
│                 │ 程序计数器          │
└─────────────────┴─────────────────────┘
```

### 5.2 垃圾回收

```
GC 算法:
- 标记-清除: 产生碎片
- 复制算法: 空间换时间
- 标记-整理: 移动对象

分代收集:
- 新生代: Eden + S0 + S1 (复制算法)
- 老年代: 标记-整理

常用收集器:
- G1: 低延迟，大堆推荐
- ZGC: 超低延迟
- Parallel: 高吞吐量
```

---

## 6. 设计模式

### 6.1 常用模式

| 类型   | 模式     | 场景         |
| ------ | -------- | ------------ |
| 创建型 | 单例     | 全局唯一对象 |
| 创建型 | 工厂     | 创建复杂对象 |
| 创建型 | 建造者   | 构建复杂对象 |
| 结构型 | 代理     | 增强功能     |
| 结构型 | 装饰器   | 动态添加功能 |
| 行为型 | 策略     | 算法切换     |
| 行为型 | 观察者   | 事件通知     |
| 行为型 | 模板方法 | 流程复用     |

### 6.2 SOLID 原则

- **S**ingle Responsibility - 单一职责
- **O**pen/Closed - 开闭原则
- **L**iskov Substitution - 里氏替换
- **I**nterface Segregation - 接口隔离
- **D**ependency Inversion - 依赖倒置

---

## 7. Spring 框架

### 7.1 IoC 与 DI

```java
// 控制反转: 对象创建交给容器
// 依赖注入: 容器自动注入依赖

@Component
public class UserService {
    @Autowired  // 字段注入
    private UserRepository repository;

    // 构造器注入（推荐）
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 7.2 AOP 概念

```
切面 (Aspect)    - 横切关注点的模块化
切点 (Pointcut)  - 定义拦截位置
通知 (Advice)    - 拦截后的动作
  - Before      - 前置通知
  - After       - 后置通知
  - Around      - 环绕通知
  - AfterReturning - 返回通知
  - AfterThrowing  - 异常通知
```

---

## 8. 微服务架构

_（Phase 10 学习后填充）_

---

## 9. 分布式系统

_（Phase 12 学习后填充）_

---

## 10. 性能调优

_（Phase 13 学习后填充）_

---

> 📝 **注意**: 本文档将随着学习进度持续更新，每完成一个阶段都会补充相应的概念内容。
