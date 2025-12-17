# Phase 5 学习总结: 并发编程 ✅

> **完成日期**: 2025 年 12 月 16 日  
> **学习时长**: 约 2 周  
> **状态**: 已完成 🎉

---

## 📚 知识点掌握情况

### 1. 线程基础 ✅

- [x] 理解进程与线程的区别
- [x] 掌握三种创建线程的方式
- [x] 理解守护线程的特性

**创建线程的方式**:

```java
// 1. 继承 Thread 类
class MyThread extends Thread {
    public void run() { ... }
}

// 2. 实现 Runnable 接口 (推荐)
Thread t = new Thread(() -> {
    System.out.println("执行中");
});

// 3. 实现 Callable 接口 (有返回值)
Callable<Integer> task = () -> 42;
Future<Integer> future = executor.submit(task);
```

| 方式        | 优点               | 缺点           |
| ----------- | ------------------ | -------------- |
| 继承 Thread | 简单直接           | 无法继承其他类 |
| Runnable    | 解耦，可继承       | 无返回值       |
| Callable    | 有返回值，可抛异常 | 稍复杂         |

### 2. 线程生命周期 ✅

- [x] 理解线程的六种状态
- [x] 掌握状态之间的转换
- [x] 理解线程中断机制

**六种状态 (Thread.State)**:

```
NEW         → 新建，尚未启动
RUNNABLE    → 可运行（就绪/运行中）
BLOCKED     → 阻塞，等待监视器锁
WAITING     → 无限期等待
TIMED_WAITING → 计时等待
TERMINATED  → 已终止
```

**状态转换**:

```
    NEW ──start()──> RUNNABLE <──────┐
                        │            │
              ┌─────────┼─────────┐  │
              ▼         ▼         ▼  │
         BLOCKED    WAITING   TIMED_WAITING
              │         │         │
              └─────────┴─────────┘
                        │
                        ▼
                   TERMINATED
```

**中断机制**:

```java
// 发送中断信号
thread.interrupt();

// 检查中断标志
while (!Thread.currentThread().isInterrupted()) {
    // 工作
}

// 处理 InterruptedException
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();  // 恢复中断状态
}
```

### 3. synchronized 同步 ✅

- [x] 理解线程安全问题的根源
- [x] 掌握 synchronized 的三种使用方式
- [x] 理解可重入性
- [x] 能够识别和避免死锁

**三种使用方式**:

```java
// 1. 同步方法 (锁 this)
public synchronized void method() { }

// 2. 同步代码块 (锁指定对象)
synchronized (lock) { }

// 3. 静态同步方法 (锁 Class 对象)
public static synchronized void staticMethod() { }
```

**对象锁 vs 类锁**:
| 类型 | 锁对象 | 影响范围 |
|------|-------|---------|
| 对象锁 | this 或指定对象 | 同一实例 |
| 类锁 | Class 对象 | 所有实例 |

**避免死锁**:

1. 固定加锁顺序
2. 使用超时机制
3. 避免嵌套锁
4. 减少锁持有时间

### 4. Lock 接口 ✅

- [x] 理解 Lock 与 synchronized 的区别
- [x] 掌握 ReentrantLock 的使用
- [x] 学会使用 Condition 进行线程通信
- [x] 了解 ReadWriteLock 读写锁

```java
Lock lock = new ReentrantLock();

lock.lock();
try {
    // 临界区
} finally {
    lock.unlock();  // 必须在 finally 中!
}
```

**Lock vs synchronized**:
| 特性 | synchronized | Lock |
|------|-------------|------|
| 获取/释放 | 自动 | 手动 |
| 可中断 | 否 | lockInterruptibly |
| 超时获取 | 否 | tryLock(time) |
| 公平锁 | 否 | 可选 |
| 多条件变量 | 否 | Condition |

**Condition 使用**:

```java
Condition condition = lock.newCondition();

// 等待
condition.await();

// 唤醒
condition.signal();
condition.signalAll();
```

### 5. 线程池 ✅

- [x] 理解线程池的作用和优势
- [x] 掌握 ExecutorService 的使用
- [x] 了解常见线程池的特点
- [x] 学会自定义线程池

**常见线程池**:
| 类型 | 特点 | 适用场景 |
|------|------|---------|
| FixedThreadPool | 固定大小 | 稳定并发 |
| CachedThreadPool | 按需创建 | 短期异步任务 |
| SingleThreadExecutor | 单线程 | 顺序执行 |
| ScheduledThreadPool | 定时任务 | 周期性任务 |

**自定义线程池 (推荐)**:

```java
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    2,                      // corePoolSize
    4,                      // maximumPoolSize
    60, TimeUnit.SECONDS,   // keepAliveTime
    new LinkedBlockingQueue<>(10),  // workQueue
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略
);
```

**任务执行流程**:

```
任务提交 → 核心线程未满? → 创建核心线程执行
              ↓否
         队列未满? → 加入队列等待
              ↓否
         最大线程未满? → 创建临时线程执行
              ↓否
         执行拒绝策略
```

### 6. JUC 并发工具 ✅

- [x] 掌握 CountDownLatch
- [x] 掌握 CyclicBarrier
- [x] 掌握 Semaphore
- [x] 了解 CompletableFuture

**CountDownLatch - 倒计时门栓**:

```java
CountDownLatch latch = new CountDownLatch(3);

// 子线程完成后
latch.countDown();

// 主线程等待
latch.await();
```

**CyclicBarrier - 循环屏障**:

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("所有线程到达!");
});

// 各线程等待
barrier.await();  // 可重用
```

**Semaphore - 信号量**:

```java
Semaphore semaphore = new Semaphore(3);  // 最多3个并发

semaphore.acquire();  // 获取许可
try {
    // 临界区
} finally {
    semaphore.release();  // 释放许可
}
```

**对比**:
| 工具 | 用途 | 可重用 |
|------|------|--------|
| CountDownLatch | 一个等待多个 | 否 |
| CyclicBarrier | 多个互相等待 | 是 |
| Semaphore | 限制并发数 | 是 |

**CompletableFuture - 异步编程**:

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenApply(String::toUpperCase);

String result = future.join();  // "HELLO WORLD"
```

---

## 🎯 实战项目: 多线程下载器

成功完成了 `Downloader.java` 项目，功能包括:

| 功能           | 使用技术                        |
| -------------- | ------------------------------- |
| 单线程下载     | InputStream, OutputStream       |
| 多线程分块下载 | RandomAccessFile, 分块策略      |
| 批量并行下载   | ExecutorService, CountDownLatch |
| 进度显示       | AtomicLong, 进度条              |
| 下载统计       | 并发计数                        |

---

## 📊 学习文件清单

| #   | 文件                       | 状态 | 核心知识点                 |
| --- | -------------------------- | ---- | -------------------------- |
| 1   | `ThreadBasics.java`        | ✅   | Thread, Runnable, 守护线程 |
| 2   | `ThreadLifecycle.java`     | ✅   | 6 种状态, 中断机制         |
| 3   | `SynchronizedDemo.java`    | ✅   | 对象锁, 类锁, 死锁         |
| 4   | `LockDemo.java`            | ✅   | ReentrantLock, Condition   |
| 5   | `ThreadPoolDemo.java`      | ✅   | Executor, 自定义线程池     |
| 6   | `ConcurrentToolsDemo.java` | ✅   | Latch, Barrier, Semaphore  |
| 7   | `Downloader.java`          | ✅   | **综合项目**               |

---

## 💡 重点心得

### 并发编程核心原则

```
┌─────────────────────────────────────────┐
│           并发三大特性                  │
├─────────────┬───────────────────────────┤
│   原子性    │ 操作不可分割              │
│   可见性    │ 修改对其他线程立即可见    │
│   有序性    │ 指令执行顺序符合预期      │
└─────────────┴───────────────────────────┘
```

### 最佳实践

1. **优先使用线程池**

   - 避免手动创建线程
   - 使用自定义 ThreadPoolExecutor

2. **正确使用锁**

   - unlock 必须在 finally 中
   - 避免嵌套锁
   - 固定加锁顺序

3. **选择合适的同步工具**

   - 简单计数: AtomicInteger
   - 互斥访问: synchronized/Lock
   - 限流: Semaphore
   - 等待完成: CountDownLatch

4. **线程安全设计**
   - 不可变对象天然线程安全
   - 无状态类天然线程安全
   - 使用并发集合代替同步集合

### 常见陷阱

- ❌ 忘记在 finally 中 unlock
- ❌ 多个锁顺序不一致导致死锁
- ❌ 忽略 InterruptedException
- ❌ 使用无界队列导致 OOM
- ❌ 线程池不关闭

---

## 🚀 下一步计划

Phase 5 已完成！准备进入 **[Phase 6: 数据库与 JDBC](../phase06/README.md)**

Phase 6 将学习:

- JDBC 基础
- 连接池 (HikariCP)
- 事务管理
- SQL 注入防护
- ORM 框架简介

---

> 📝 _本总结由学习过程自动生成于 2025-12-16_
