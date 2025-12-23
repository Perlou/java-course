package phase05.ex;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

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
                    Thread.sleep(500 + (int) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
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
            ;
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
                String received = exchanger.exchange(myData);
            } catch (InterruptedException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }).start();
    }
}
