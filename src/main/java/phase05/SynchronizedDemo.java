package phase05;

/**
 * Phase 5 - Lesson 3: synchronized 同步机制
 * 
 * 🎯 学习目标:
 * 1. 理解线程安全问题
 * 2. 掌握 synchronized 的使用方式
 * 3. 理解对象锁和类锁
 * 4. 了解可重入特性
 */
public class SynchronizedDemo {

    // 共享变量
    private int count = 0;
    private static int staticCount = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(60));
        System.out.println("Phase 5 - Lesson 3: synchronized 同步机制");
        System.out.println("=".repeat(60));

        // 1. 线程安全问题演示
        System.out.println("\n【1. 线程安全问题演示】");

        class UnsafeCounter {
            private int count = 0;

            public void increment() {
                count++; // 非原子操作: 读取 -> 加1 -> 写入
            }

            public int getCount() {
                return count;
            }
        }

        UnsafeCounter unsafe = new UnsafeCounter();

        Thread[] unsafeThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    unsafe.increment();
                }
            });
            unsafeThreads[i].start();
        }

        for (Thread t : unsafeThreads) {
            t.join();
        }

        System.out.println("期望值: " + (10 * 10000));
        System.out.println("实际值: " + unsafe.getCount());
        System.out.println("❌ 出现了数据丢失!");

        // 2. synchronized 方法
        System.out.println("\n【2. synchronized 方法】");

        class SafeCounter {
            private int count = 0;

            public synchronized void increment() {
                count++;
            }

            public synchronized int getCount() {
                return count;
            }
        }

        SafeCounter safe = new SafeCounter();

        Thread[] safeThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            safeThreads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    safe.increment();
                }
            });
            safeThreads[i].start();
        }

        for (Thread t : safeThreads) {
            t.join();
        }

        System.out.println("期望值: " + (10 * 10000));
        System.out.println("实际值: " + safe.getCount());
        System.out.println("✅ 结果正确!");

        // 3. synchronized 代码块
        System.out.println("\n【3. synchronized 代码块】");

        class BlockCounter {
            private int count = 0;
            private final Object lock = new Object(); // 锁对象

            public void increment() {
                // 只同步必要的部分，提高性能
                synchronized (lock) {
                    count++;
                }
            }

            public int getCount() {
                synchronized (lock) {
                    return count;
                }
            }
        }

        BlockCounter block = new BlockCounter();

        Thread[] blockThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            blockThreads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    block.increment();
                }
            });
            blockThreads[i].start();
        }

        for (Thread t : blockThreads) {
            t.join();
        }

        System.out.println("synchronized 代码块结果: " + block.getCount());

        // 4. 对象锁 vs 类锁
        System.out.println("\n【4. 对象锁 vs 类锁】");
        System.out.println("""
                对象锁 (实例锁):
                - synchronized(this) 或 synchronized 实例方法
                - 锁住的是当前对象实例
                - 不同实例之间互不影响

                类锁:
                - synchronized(Class.class) 或 synchronized static 方法
                - 锁住的是类的 Class 对象
                - 所有实例共享同一把锁
                """);

        SynchronizedDemo demo = new SynchronizedDemo();

        // 对象锁示例
        Thread objThread1 = new Thread(() -> {
            demo.incrementInstance();
        });
        Thread objThread2 = new Thread(() -> {
            demo.incrementInstance();
        });

        // 类锁示例
        Thread classThread1 = new Thread(() -> {
            SynchronizedDemo.incrementStatic();
        });
        Thread classThread2 = new Thread(() -> {
            SynchronizedDemo.incrementStatic();
        });

        objThread1.start();
        objThread2.start();
        classThread1.start();
        classThread2.start();

        objThread1.join();
        objThread2.join();
        classThread1.join();
        classThread2.join();

        // 5. synchronized 锁对象选择
        System.out.println("\n【5. synchronized 锁对象选择】");
        System.out.println("""
                锁对象选择原则:

                ✅ 推荐:
                - private final Object lock = new Object();
                - 使用不可变的私有对象作为锁

                ⚠️ 不推荐:
                - synchronized(this): 可能被外部锁住
                - synchronized(string): String 可能被常量池共享
                - synchronized(Integer): 包装类可能被缓存
                """);

        // 6. 可重入性
        System.out.println("【6. 可重入性】");

        class ReentrantTest {
            public synchronized void methodA() {
                System.out.println("methodA 获得锁");
                methodB(); // 可以再次获得同一把锁
            }

            public synchronized void methodB() {
                System.out.println("methodB 获得锁 (重入)");
            }
        }

        ReentrantTest reentrant = new ReentrantTest();
        new Thread(() -> reentrant.methodA()).start();

        Thread.sleep(100);

        System.out.println("""

                可重入锁:
                - 同一个线程可以多次获得同一把锁
                - synchronized 是可重入的
                - 避免自己把自己锁死
                """);

        // 7. 死锁演示
        System.out.println("【7. 死锁演示】");

        Object lockA = new Object();
        Object lockB = new Object();

        Thread deadThread1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread1 获得 lockA");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                System.out.println("Thread1 尝试获得 lockB...");
                synchronized (lockB) {
                    System.out.println("Thread1 获得 lockB");
                }
            }
        }, "DeadThread1");

        Thread deadThread2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread2 获得 lockB");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                System.out.println("Thread2 尝试获得 lockA...");
                synchronized (lockA) {
                    System.out.println("Thread2 获得 lockA");
                }
            }
        }, "DeadThread2");

        System.out.println("⚠️ 以下可能发生死锁 (3秒后强制继续):");
        deadThread1.start();
        deadThread2.start();

        // 等待最多 3 秒
        deadThread1.join(3000);
        deadThread2.join(3000);

        if (deadThread1.isAlive() || deadThread2.isAlive()) {
            System.out.println("❗ 检测到死锁! 线程卡住了");
        }

        // 8. 避免死锁
        System.out.println("\n【8. 避免死锁的方法】");
        System.out.println("""
                死锁发生的四个条件:
                1. 互斥: 资源不能共享
                2. 持有并等待: 持有资源同时等待其他资源
                3. 不可剥夺: 资源只能由持有者释放
                4. 循环等待: 形成等待环路

                避免死锁:
                1. 固定加锁顺序
                2. 使用超时机制 (tryLock)
                3. 使用 Lock 的 lockInterruptibly
                4. 避免嵌套锁
                """);

        // 正确的加锁顺序
        System.out.println("正确方式: 固定加锁顺序");

        Thread safeThread1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("SafeThread1 获得 lockA");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                synchronized (lockB) {
                    System.out.println("SafeThread1 获得 lockB");
                }
            }
        });

        Thread safeThread2 = new Thread(() -> {
            synchronized (lockA) { // 先获取 lockA
                System.out.println("SafeThread2 获得 lockA");
                synchronized (lockB) {
                    System.out.println("SafeThread2 获得 lockB");
                }
            }
        });

        safeThread1.start();
        safeThread2.start();
        safeThread1.join();
        safeThread2.join();

        System.out.println("✅ 固定顺序，无死锁");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 synchronized 保证原子性、可见性、有序性");
        System.out.println("💡 锁对象应该是 private final");
        System.out.println("💡 避免死锁: 固定加锁顺序");
        System.out.println("=".repeat(60));
    }

    // 实例方法锁 - 锁 this
    public synchronized void incrementInstance() {
        count++;
        System.out.println("实例方法, count = " + count);
    }

    // 静态方法锁 - 锁 Class 对象
    public static synchronized void incrementStatic() {
        staticCount++;
        System.out.println("静态方法, staticCount = " + staticCount);
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. synchronized 使用方式:
 * - 同步方法: public synchronized void method()
 * - 同步代码块: synchronized(lock) { }
 * - 静态同步方法: public static synchronized void method()
 * 
 * 2. 对象锁 vs 类锁:
 * - 对象锁: synchronized(this) 或实例方法
 * - 类锁: synchronized(Class.class) 或静态方法
 * 
 * 3. synchronized 特性:
 * - 原子性: 操作不可分割
 * - 可见性: 修改对其他线程可见
 * - 有序性: 防止指令重排
 * - 可重入: 同一线程可重复获得
 * 
 * 4. 死锁预防:
 * - 固定加锁顺序
 * - 使用超时机制
 * - 减少锁的持有时间
 * 
 * 🏃 练习:
 * 1. 实现一个线程安全的单例模式
 * 2. 故意制造一个死锁并用 jstack 分析
 * 3. 比较 synchronized 方法和代码块的性能
 */
