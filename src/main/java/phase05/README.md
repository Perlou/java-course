# Phase 5: 并发编程

> **目标**：掌握 Java 多线程与并发编程  
> **预计时长**：2 周  
> **前置条件**：Phase 4 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解线程的创建和生命周期
2. 掌握 synchronized 和 Lock 同步机制
3. 熟练使用线程池
4. 理解 JUC 并发工具类
5. 能够分析和解决并发问题

---

## 📚 核心概念

### 线程创建方式

```java
// 1. 继承 Thread
class MyThread extends Thread {
    public void run() { ... }
}

// 2. 实现 Runnable (推荐)
Thread t = new Thread(() -> { ... });

// 3. 实现 Callable (有返回值)
Callable<Integer> task = () -> 42;

// 4. 线程池 (最佳实践)
ExecutorService pool = Executors.newFixedThreadPool(10);
```

### 同步机制

```
┌─────────────────────────────────────┐
│           同步机制                  │
├─────────────────────────────────────┤
│  synchronized   - 内置锁            │
│  ReentrantLock  - 显式锁            │
│  volatile       - 可见性保证        │
│  Atomic*        - 原子类            │
└─────────────────────────────────────┘
```

### 并发工具

```
CountDownLatch  - 一个等待多个
CyclicBarrier   - 多个互相等待
Semaphore       - 限制并发数
CompletableFuture - 异步编程
```

---

## 📁 文件列表

| #   | 文件                                                   | 描述            | 知识点                     |
| --- | ------------------------------------------------------ | --------------- | -------------------------- |
| 1   | [ThreadBasics.java](./ThreadBasics.java)               | 线程基础        | Thread, Runnable, 守护线程 |
| 2   | [ThreadLifecycle.java](./ThreadLifecycle.java)         | 线程生命周期    | 6 种状态, 中断机制         |
| 3   | [SynchronizedDemo.java](./SynchronizedDemo.java)       | synchronized    | 对象锁, 类锁, 死锁         |
| 4   | [LockDemo.java](./LockDemo.java)                       | Lock 接口       | ReentrantLock, Condition   |
| 5   | [ThreadPoolDemo.java](./ThreadPoolDemo.java)           | 线程池          | Executor, 自定义线程池     |
| 6   | [ConcurrentToolsDemo.java](./ConcurrentToolsDemo.java) | JUC 工具类      | Latch, Barrier, Semaphore  |
| 7   | [Downloader.java](./Downloader.java)                   | 🎯 **实战项目** | 多线程下载器               |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行示例
mvn exec:java -Dexec.mainClass="phase05.ThreadBasics"
mvn exec:java -Dexec.mainClass="phase05.ThreadPoolDemo"
mvn exec:java -Dexec.mainClass="phase05.ConcurrentToolsDemo"

# 运行实战项目
mvn exec:java -Dexec.mainClass="phase05.Downloader"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: ThreadBasics + ThreadLifecycle
2. **Day 3-4**: SynchronizedDemo (重点)
3. **Day 5-6**: LockDemo
4. **Day 7-8**: ThreadPoolDemo (重点)
5. **Day 9-10**: ConcurrentToolsDemo
6. **Day 11-14**: Downloader 项目 + 练习

### 学习方法

1. 运行代码，观察多线程执行效果
2. 多次运行，观察结果的不确定性
3. 使用 jstack 分析线程状态
4. 完成每个文件末尾的练习题

---

## ✅ 完成检查

- [ ] 理解线程的创建方式和生命周期
- [ ] 理解线程安全问题的根源
- [ ] 能够正确使用 synchronized
- [ ] 能够使用 Lock 进行更灵活的同步
- [ ] 能够配置合适的线程池
- [ ] 掌握 CountDownLatch/CyclicBarrier/Semaphore
- [ ] 完成多线程下载器项目

---

## 🎯 实战项目: 多线程下载器

`Downloader.java` 是本阶段的综合项目，功能包括：

- 单线程下载
- 多线程分块下载
- 批量并行下载
- 进度条显示
- 下载统计

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase05.Downloader"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 6: 数据库与 JDBC](../phase06/README.md)
