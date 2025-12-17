package phase05;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

/**
 * Phase 5 - Lesson 6: JUC 并发工具类
 * 
 * 🎯 学习目标:
 * 1. 掌握 CountDownLatch 的使用
 * 2. 掌握 CyclicBarrier 的使用
 * 3. 掌握 Semaphore 信号量
 * 4. 了解其他常用并发工具
 */
public class ConcurrentToolsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(60));
        System.out.println("Phase 5 - Lesson 6: JUC 并发工具类");
        System.out.println("=".repeat(60));

        // 1. CountDownLatch - 倒计时门栓
        System.out.println("\n【1. CountDownLatch - 倒计时门栓】");
        System.out.println("""
                用途: 一个线程等待多个线程完成
                特点: 一次性，计数归零后不能重用
                场景: 主线程等待多个子任务完成
                """);

        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            new Thread(() -> {
                try {
                    System.out.println("任务 " + taskId + " 开始执行");
                    Thread.sleep(500 + (int) (Math.random() * 1000));
                    System.out.println("任务 " + taskId + " 完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown(); // 计数减1
                }
            }).start();
        }

        System.out.println("主线程等待所有任务完成...");
        latch.await(); // 等待计数归零
        System.out.println("✅ 所有任务完成!");

        // CountDownLatch 应用: 模拟服务启动
        System.out.println("\n应用: 模拟服务启动");

        CountDownLatch servicesLatch = new CountDownLatch(3);
        String[] services = { "数据库", "缓存", "消息队列" };

        for (String service : services) {
            new Thread(() -> {
                System.out.println(service + " 启动中...");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }
                System.out.println(service + " ✓ 启动完成");
                servicesLatch.countDown();
            }).start();
        }

        servicesLatch.await();
        System.out.println("🚀 所有服务就绪，应用启动!");

        // 2. CyclicBarrier - 循环屏障
        System.out.println("\n【2. CyclicBarrier - 循环屏障】");
        System.out.println("""
                用途: 多个线程互相等待，全部到达屏障后一起继续
                特点: 可重用，支持回调
                场景: 多阶段并行计算，每阶段同步
                """);

        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
            System.out.println(">>> 所有线程到达屏障，执行回调 <<<");
        });

        for (int i = 0; i < parties; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    System.out.println("线程 " + id + " 第一阶段完成");
                    barrier.await(); // 等待其他线程

                    System.out.println("线程 " + id + " 第二阶段完成");
                    barrier.await(); // 屏障可重用

                    System.out.println("线程 " + id + " 全部完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Worker-" + i).start();
        }

        Thread.sleep(1000);

        // CountDownLatch vs CyclicBarrier
        System.out.println("\nCountDownLatch vs CyclicBarrier:");
        System.out.println("""
                | 特性     | CountDownLatch   | CyclicBarrier    |
                |----------|------------------|------------------|
                | 等待方式 | 一个等待多个     | 多个互相等待     |
                | 可重用   | 否               | 是               |
                | 回调     | 无               | 有               |
                | 计数     | 减少 (countDown) | 增加 (await)     |
                """);

        // 3. Semaphore - 信号量
        System.out.println("【3. Semaphore - 信号量】");
        System.out.println("""
                用途: 控制同时访问资源的线程数
                特点: 可用于限流
                场景: 数据库连接池、限流器
                """);

        // 模拟只有2个停车位
        Semaphore parkingLot = new Semaphore(2);

        for (int i = 0; i < 5; i++) {
            final int carId = i;
            new Thread(() -> {
                try {
                    System.out.println("车 " + carId + " 尝试进入停车场...");
                    parkingLot.acquire(); // 获取许可
                    System.out.println("车 " + carId + " 进入 (剩余车位: " +
                            parkingLot.availablePermits() + ")");
                    Thread.sleep(1000);
                    System.out.println("车 " + carId + " 离开");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    parkingLot.release(); // 释放许可
                }
            }).start();
        }

        Thread.sleep(4000);

        // 4. Exchanger - 交换器
        System.out.println("\n【4. Exchanger - 交换器】");
        System.out.println("用途: 两个线程之间交换数据");

        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                String myData = "数据A";
                System.out.println("线程1 准备交换: " + myData);
                String received = exchanger.exchange(myData);
                System.out.println("线程1 收到: " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                String myData = "数据B";
                System.out.println("线程2 准备交换: " + myData);
                String received = exchanger.exchange(myData);
                System.out.println("线程2 收到: " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(500);

        // 5. CompletableFuture - 异步编程
        System.out.println("\n【5. CompletableFuture - 异步编程】");

        // 异步执行
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务1 执行中...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            return "结果1";
        });

        // 链式处理
        CompletableFuture<String> result = future1
                .thenApply(s -> {
                    System.out.println("转换: " + s);
                    return s + " -> 转换后";
                })
                .thenApply(String::toUpperCase);

        System.out.println("最终结果: " + result.join());

        // 组合多个 Future
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
            return "Hello";
        });

        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            return "World";
        });

        // 合并两个结果
        CompletableFuture<String> combined = futureA.thenCombine(futureB,
                (a, b) -> a + " " + b);
        System.out.println("组合结果: " + combined.join());

        // 等待任意一个完成
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureA, futureB);
        System.out.println("最快结果: " + anyOf.join());

        System.out.println("""

                CompletableFuture 常用方法:
                - supplyAsync(): 异步执行有返回值任务
                - runAsync(): 异步执行无返回值任务
                - thenApply(): 转换结果
                - thenAccept(): 消费结果
                - thenCombine(): 合并两个结果
                - allOf(): 等待所有完成
                - anyOf(): 等待任意一个完成
                """);

        // 6. 原子类
        System.out.println("【6. 原子类】");

        java.util.concurrent.atomic.AtomicInteger atomicInt = new java.util.concurrent.atomic.AtomicInteger(0);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicInt.incrementAndGet(); // 原子递增
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("AtomicInteger 结果: " + atomicInt.get());

        System.out.println("""

                常用原子类:
                - AtomicInteger, AtomicLong, AtomicBoolean
                - AtomicReference<T>
                - AtomicIntegerArray
                - LongAdder (高并发累加器)

                优势: 无锁，使用 CAS 实现
                """);

        // 7. 并发集合
        System.out.println("【7. 并发集合】");
        System.out.println("""
                线程安全的集合类:

                ConcurrentHashMap
                - 分段锁 (JDK 7) / CAS + synchronized (JDK 8+)
                - 高并发读写

                CopyOnWriteArrayList
                - 写时复制
                - 适合读多写少

                ConcurrentLinkedQueue
                - 无锁队列

                BlockingQueue
                - ArrayBlockingQueue: 有界阻塞队列
                - LinkedBlockingQueue: 可选有界
                - PriorityBlockingQueue: 优先级队列
                - SynchronousQueue: 不存储，直接交付
                """);

        // BlockingQueue 示例
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        // 生产者
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.put(i); // 满时阻塞
                    System.out.println("生产: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 消费者
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(200);
                    Integer item = queue.take(); // 空时阻塞
                    System.out.println("消费: " + item);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(3000);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 CountDownLatch: 一个等待多个");
        System.out.println("💡 CyclicBarrier: 多个互相等待，可重用");
        System.out.println("💡 Semaphore: 控制并发数量");
        System.out.println("💡 CompletableFuture: 强大的异步编程工具");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. CountDownLatch:
 * - countDown(): 计数减1
 * - await(): 等待计数归零
 * - 一次性使用
 * 
 * 2. CyclicBarrier:
 * - await(): 等待所有线程到达
 * - 可重用，支持回调
 * 
 * 3. Semaphore:
 * - acquire(): 获取许可
 * - release(): 释放许可
 * - 控制并发访问数
 * 
 * 4. CompletableFuture:
 * - 异步编程利器
 * - 支持链式操作
 * - 可组合多个异步任务
 * 
 * 5. 原子类:
 * - 无锁，CAS 实现
 * - 适合简单的原子操作
 * 
 * 🏃 练习:
 * 1. 用 CountDownLatch 实现并发请求测试
 * 2. 用 Semaphore 实现简单的限流器
 * 3. 用 CompletableFuture 实现并行调用多个 API
 */
