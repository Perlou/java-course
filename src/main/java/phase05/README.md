# Phase 5: 并发编程

> **目标**：掌握 Java 多线程与并发编程  
> **预计时长**：2 周  
> **前置条件**：Phase 4 完成

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解线程的创建和生命周期
2. 掌握线程同步机制
3. 熟练使用线程池
4. 理解 JUC 并发工具类
5. 能够分析和解决并发问题

---

## 📚 核心概念

### 线程创建方式

1. 继承 Thread 类
2. 实现 Runnable 接口
3. 实现 Callable 接口
4. 使用线程池

### 同步机制

- synchronized 关键字
- ReentrantLock
- volatile 关键字
- 原子类

---

## 📁 文件列表

| 文件                      | 描述                | 状态 |
| ------------------------- | ------------------- | ---- |
| `ThreadBasics.java`       | 创建线程的方式      | ⏳   |
| `ThreadLifecycle.java`    | 线程生命周期        | ⏳   |
| `ThreadMethods.java`      | sleep、join、yield  | ⏳   |
| `SynchronizedDemo.java`   | synchronized 关键字 | ⏳   |
| `LockDemo.java`           | ReentrantLock       | ⏳   |
| `DeadlockDemo.java`       | 死锁分析与避免      | ⏳   |
| `ExecutorDemo.java`       | Executor 框架       | ⏳   |
| `ThreadPoolDemo.java`     | 线程池配置与调优    | ⏳   |
| `FutureDemo.java`         | Callable 与 Future  | ⏳   |
| `CountDownLatchDemo.java` | 倒计时门栓          | ⏳   |
| `CyclicBarrierDemo.java`  | 循环屏障            | ⏳   |
| `SemaphoreDemo.java`      | 信号量              | ⏳   |

---

## ✅ 完成检查

- [ ] 理解线程安全问题
- [ ] 能够正确使用锁机制
- [ ] 能够配置合适的线程池
- [ ] 完成多线程下载器项目
