package phase05.ex;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        int[] count = { 0 };

        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                lock.lock();
                try {
                    count[0]++;
                } finally {
                    lock.unlock();
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

        // Thread holder = new Thread(() -> {
        // tryLockDemo.lock();
        // try {
        // Thread.sleep(2000);
        // }
        // });

        // 6. Condition - 条件变量
        System.out.println("【6. Condition - 条件变量】");

    }
}
