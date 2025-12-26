# Phase 5: 多线程与并发 - 核心概念

> 并发是提升程序性能的关键，也是最容易出错的领域

---

## 🎯 并发编程全景图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Java 并发编程                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  基础概念                                                           │
│  ├── 进程 vs 线程                                                  │
│  ├── 并发 vs 并行                                                  │
│  └── 线程生命周期                                                  │
│                                                                     │
│  线程创建                                                           │
│  ├── 继承 Thread                                                   │
│  ├── 实现 Runnable                                                 │
│  └── 实现 Callable (有返回值)                                      │
│                                                                     │
│  线程安全                                                           │
│  ├── synchronized 关键字                                           │
│  ├── Lock 接口 (ReentrantLock)                                     │
│  ├── volatile 关键字                                               │
│  └── 原子类 (AtomicInteger...)                                     │
│                                                                     │
│  线程协作                                                           │
│  ├── wait/notify                                                   │
│  ├── Condition                                                     │
│  └── CountDownLatch, CyclicBarrier, Semaphore                      │
│                                                                     │
│  线程池                                                             │
│  ├── ThreadPoolExecutor                                            │
│  ├── Executors 工厂类                                              │
│  └── Future/CompletableFuture                                      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 线程生命周期

```
┌─────────────────────────────────────────────────────────────────────┐
│                        线程状态转换                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│                    ┌─────────┐                                     │
│                    │   NEW   │  ← new Thread()                     │
│                    └────┬────┘                                     │
│                         │ start()                                  │
│                         ↓                                          │
│                    ┌─────────┐                                     │
│       ┌───────────→│ RUNNABLE│←───────────┐                       │
│       │            └────┬────┘            │                       │
│       │                 │                 │                       │
│       │    ┌────────────┼────────────┐    │                       │
│       │    ↓            ↓            ↓    │                       │
│  ┌─────────┐      ┌─────────┐    ┌─────────┐                      │
│  │ BLOCKED │      │ WAITING │    │ TIMED   │                      │
│  │(等待锁) │      │(无限等待)│    │ WAITING │                      │
│  └─────────┘      └─────────┘    │(超时等待)│                      │
│       │                          └─────────┘                      │
│       └──────────────────────────────┘                            │
│                         │                                          │
│                         ↓                                          │
│                    ┌─────────┐                                     │
│                    │TERMINATED│  ← 执行完毕                         │
│                    └─────────┘                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ 线程创建方式

```java
// 1. 继承 Thread
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running");
    }
}
new MyThread().start();

// 2. 实现 Runnable (推荐)
Runnable task = () -> System.out.println("Task running");
new Thread(task).start();

// 3. 实现 Callable (有返回值)
Callable<Integer> callable = () -> {
    return 42;
};
FutureTask<Integer> future = new FutureTask<>(callable);
new Thread(future).start();
Integer result = future.get();  // 阻塞获取结果
```

---

## 🔒 线程安全

### synchronized

```java
// 同步方法
public synchronized void method() {
    // 锁对象是 this
}

// 同步代码块
public void method() {
    synchronized (lock) {
        // 临界区
    }
}

// 静态同步方法
public static synchronized void method() {
    // 锁对象是 Class 对象
}
```

### Lock

```java
private final Lock lock = new ReentrantLock();

public void method() {
    lock.lock();  // 加锁
    try {
        // 临界区
    } finally {
        lock.unlock();  // 必须在 finally 中释放锁
    }
}
```

### synchronized vs Lock

| 特性     | synchronized | Lock                |
| -------- | ------------ | ------------------- |
| 使用方式 | 关键字       | 接口                |
| 锁释放   | 自动释放     | 手动 unlock         |
| 可中断   | 不可中断     | lockInterruptibly() |
| 超时获取 | 不支持       | tryLock(timeout)    |
| 公平锁   | 非公平       | 可选公平/非公平     |
| 多条件   | 一个         | 多个 Condition      |

---

## ⚡ volatile 与原子类

```
┌─────────────────────────────────────────────────────────────────────┐
│                        volatile 关键字                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  作用:                                                              │
│  1. 可见性 - 一个线程修改后，其他线程立即可见                        │
│  2. 禁止指令重排序                                                  │
│                                                                     │
│  ⚠️ volatile 不保证原子性！                                        │
│  count++ 不是原子操作，volatile 无法保证线程安全                     │
│                                                                     │
│  适用场景:                                                          │
│  • 状态标志 (boolean flag)                                         │
│  • 双重检查锁定 (单例模式)                                          │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                        原子类                                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  AtomicInteger count = new AtomicInteger(0);                        │
│                                                                     │
│  count.incrementAndGet();  // ++count                               │
│  count.getAndIncrement();  // count++                               │
│  count.addAndGet(5);       // count += 5                            │
│  count.compareAndSet(expected, update);  // CAS 操作                │
│                                                                     │
│  基于 CAS (Compare-And-Swap) 实现，无锁线程安全                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🏊 线程池

### 核心参数

```java
ThreadPoolExecutor(
    int corePoolSize,      // 核心线程数
    int maximumPoolSize,   // 最大线程数
    long keepAliveTime,    // 空闲线程存活时间
    TimeUnit unit,         // 时间单位
    BlockingQueue<Runnable> workQueue,  // 任务队列
    ThreadFactory threadFactory,        // 线程工厂
    RejectedExecutionHandler handler    // 拒绝策略
)
```

### 工作流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                      线程池工作流程                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  任务提交                                                           │
│      │                                                              │
│      ↓                                                              │
│  ┌─────────────────┐                                               │
│  │ 核心线程数已满？ │                                               │
│  └────────┬────────┘                                               │
│     否    │    是                                                   │
│      ↓    ↓                                                         │
│   创建核心  ┌─────────────────┐                                     │
│   线程执行  │  任务队列已满？  │                                     │
│            └────────┬────────┘                                     │
│              否     │    是                                         │
│               ↓     ↓                                              │
│           加入队列  ┌─────────────────┐                             │
│                    │ 最大线程数已满？ │                             │
│                    └────────┬────────┘                             │
│                      否     │    是                                 │
│                       ↓     ↓                                       │
│                   创建非核心  执行拒绝策略                           │
│                   线程执行                                          │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 拒绝策略

| 策略                | 描述            |
| ------------------- | --------------- |
| AbortPolicy         | 抛出异常 (默认) |
| CallerRunsPolicy    | 调用者线程执行  |
| DiscardPolicy       | 静默丢弃        |
| DiscardOldestPolicy | 丢弃最老任务    |

---

## 🤝 线程协作工具

```java
// CountDownLatch - 等待多个任务完成
CountDownLatch latch = new CountDownLatch(3);
// 每个任务完成后: latch.countDown();
latch.await();  // 等待计数归零

// CyclicBarrier - 多个线程互相等待
CyclicBarrier barrier = new CyclicBarrier(3);
// 每个线程到达后: barrier.await();
// 所有线程到齐后继续

// Semaphore - 限制并发数
Semaphore semaphore = new Semaphore(3);
semaphore.acquire();  // 获取许可
try {
    // 限流代码
} finally {
    semaphore.release();  // 释放许可
}
```

---

## 📖 学习要点

```
✅ 理解线程的生命周期
✅ 掌握 synchronized 和 Lock 的使用
✅ 理解 volatile 的作用和限制
✅ 熟练配置和使用线程池
✅ 掌握常用的并发工具类
```

---

> 接下来学习 JVM 原理：[Phase 6 README](../phase06/README.md)
