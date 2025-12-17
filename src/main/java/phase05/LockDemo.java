package phase05;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Phase 5 - Lesson 4: Lock 接口与 ReentrantLock
 * 
 * 🎯 学习目标:
 * 1. 理解 Lock 接口与 synchronized 的区别
 * 2. 掌握 ReentrantLock 的使用
 * 3. 学会使用 Condition 进行线程通信
 * 4. 了解 ReadWriteLock 读写锁
 */
public class LockDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(60));
        System.out.println("Phase 5 - Lesson 4: Lock 接口与 ReentrantLock");
        System.out.println("=".repeat(60));

        // 1. Lock vs synchronized
        System.out.println("\n【1. Lock vs synchronized】");
        System.out.println("""
                | 特性         | synchronized    | Lock             |
                |--------------|-----------------|------------------|
                | 获取锁       | 自动            | 手动 lock()      |
                | 释放锁       | 自动            | 手动 unlock()    |
                | 可中断       | 否              | lockInterruptibly|
                | 超时获取     | 否              | tryLock(time)    |
                | 公平锁       | 否              | 可选             |
                | 多条件变量   | 否              | Condition        |
                | 读写分离     | 否              | ReadWriteLock    |
                """);

        // 2. ReentrantLock 基本使用
        System.out.println("【2. ReentrantLock 基本使用】");

        Lock lock = new ReentrantLock();
        int[] count = { 0 }; // 使用数组使 lambda 可以修改

        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                lock.lock(); // 获取锁
                try {
                    count[0]++;
                } finally {
                    lock.unlock(); // 必须在 finally 中释放!
                }
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("ReentrantLock 结果: " + count[0] + " (期望 20000)");

        // 3. tryLock - 尝试获取锁
        System.out.println("\n【3. tryLock - 非阻塞获取锁】");

        Lock tryLockDemo = new ReentrantLock();

        Thread holder = new Thread(() -> {
            tryLockDemo.lock();
            try {
                System.out.println("持有锁 2 秒...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                tryLockDemo.unlock();
                System.out.println("释放锁");
            }
        });

        Thread tryThread = new Thread(() -> {
            // 尝试立即获取
            if (tryLockDemo.tryLock()) {
                try {
                    System.out.println("立即获得锁");
                } finally {
                    tryLockDemo.unlock();
                }
            } else {
                System.out.println("立即获取失败，尝试等待...");
                try {
                    // 尝试等待获取
                    if (tryLockDemo.tryLock(3, TimeUnit.SECONDS)) {
                        try {
                            System.out.println("等待后获得锁");
                        } finally {
                            tryLockDemo.unlock();
                        }
                    } else {
                        System.out.println("超时未获得锁");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.start();
        Thread.sleep(100); // 确保 holder 先获得锁
        tryThread.start();

        holder.join();
        tryThread.join();

        // 4. lockInterruptibly - 可中断获取锁
        System.out.println("\n【4. lockInterruptibly - 可中断获取锁】");

        Lock interruptLock = new ReentrantLock();

        Thread lockHolder = new Thread(() -> {
            interruptLock.lock();
            try {
                System.out.println("锁持有者: 持有锁 5 秒");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                interruptLock.unlock();
            }
        });

        Thread interruptibleThread = new Thread(() -> {
            try {
                System.out.println("尝试获取锁 (可中断)...");
                interruptLock.lockInterruptibly();
                try {
                    System.out.println("获得锁");
                } finally {
                    interruptLock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("被中断，放弃获取锁");
            }
        });

        lockHolder.start();
        Thread.sleep(100);
        interruptibleThread.start();
        Thread.sleep(500);

        System.out.println("主线程: 中断等待锁的线程");
        interruptibleThread.interrupt();

        interruptibleThread.join();
        lockHolder.interrupt(); // 提前结束
        lockHolder.join(1000);

        // 5. 公平锁 vs 非公平锁
        System.out.println("\n【5. 公平锁 vs 非公平锁】");

        ReentrantLock fairLock = new ReentrantLock(true); // 公平锁
        ReentrantLock unfairLock = new ReentrantLock(false); // 非公平锁 (默认)

        System.out.println("fairLock.isFair(): " + fairLock.isFair());
        System.out.println("unfairLock.isFair(): " + unfairLock.isFair());

        System.out.println("""

                公平锁:
                - 按照请求顺序分配锁
                - 避免线程饥饿
                - 性能较低

                非公平锁 (默认):
                - 不保证顺序
                - 可能导致某些线程等待时间长
                - 吞吐量更高
                """);

        // 6. Condition - 条件变量
        System.out.println("【6. Condition - 条件变量】");

        Lock conditionLock = new ReentrantLock();
        Condition notEmpty = conditionLock.newCondition();
        Condition notFull = conditionLock.newCondition();

        int[] buffer = new int[1];
        boolean[] hasData = { false };

        // 生产者
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                conditionLock.lock();
                try {
                    while (hasData[0]) {
                        System.out.println("生产者: 等待消费...");
                        notFull.await(); // 等待不满
                    }
                    buffer[0] = i;
                    hasData[0] = true;
                    System.out.println("生产者: 生产 " + i);
                    notEmpty.signal(); // 通知不空
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    conditionLock.unlock();
                }
            }
        }, "Producer");

        // 消费者
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                conditionLock.lock();
                try {
                    while (!hasData[0]) {
                        System.out.println("消费者: 等待生产...");
                        notEmpty.await(); // 等待不空
                    }
                    System.out.println("消费者: 消费 " + buffer[0]);
                    hasData[0] = false;
                    notFull.signal(); // 通知不满
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    conditionLock.unlock();
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // 7. ReadWriteLock 读写锁
        System.out.println("\n【7. ReadWriteLock 读写锁】");

        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        int[] data = { 0 };

        Runnable reader = () -> {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() +
                        " 读取: " + data[0]);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        };

        Runnable writer = () -> {
            writeLock.lock();
            try {
                data[0]++;
                System.out.println(Thread.currentThread().getName() +
                        " 写入: " + data[0]);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        };

        // 启动多个读者和写者
        Thread r1 = new Thread(reader, "Reader-1");
        Thread r2 = new Thread(reader, "Reader-2");
        Thread w1 = new Thread(writer, "Writer-1");
        Thread r3 = new Thread(reader, "Reader-3");

        r1.start();
        r2.start();
        w1.start();
        r3.start();

        r1.join();
        r2.join();
        w1.join();
        r3.join();

        System.out.println("""

                读写锁规则:
                - 读-读: 不互斥，可并发
                - 读-写: 互斥
                - 写-写: 互斥

                适用场景: 读多写少
                """);

        // 8. Lock 最佳实践
        System.out.println("【8. Lock 最佳实践】");
        System.out.println("""
                ✅ 正确用法:

                lock.lock();
                try {
                    // 临界区代码
                } finally {
                    lock.unlock();  // 必须在 finally 中!
                }

                ✅ tryLock 用法:

                if (lock.tryLock()) {
                    try {
                        // 临界区
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // 获取失败的处理
                }

                ❌ 错误用法:
                - 忘记 unlock
                - unlock 不在 finally 中
                - 在 try 中 lock (可能导致锁未释放)
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Lock 比 synchronized 更灵活");
        System.out.println("💡 必须在 finally 中 unlock");
        System.out.println("💡 读写锁适合读多写少场景");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. ReentrantLock:
 * - lock(): 获取锁
 * - unlock(): 释放锁
 * - tryLock(): 尝试获取锁
 * - lockInterruptibly(): 可中断获取
 * 
 * 2. Condition:
 * - await(): 等待条件
 * - signal(): 唤醒一个
 * - signalAll(): 唤醒所有
 * 
 * 3. ReadWriteLock:
 * - readLock(): 获取读锁
 * - writeLock(): 获取写锁
 * - 读读不互斥，读写/写写互斥
 * 
 * 4. Lock vs synchronized:
 * - Lock 需手动释放
 * - Lock 支持超时、中断
 * - Lock 支持公平锁
 * - Lock 支持多条件变量
 * 
 * 🏃 练习:
 * 1. 用 ReentrantLock 实现生产者-消费者
 * 2. 实现一个带超时的资源获取
 * 3. 用读写锁实现缓存
 */
