package phase05;

/**
 * Phase 5 - Lesson 2: 线程生命周期
 * 
 * 🎯 学习目标:
 * 1. 理解线程的六种状态
 * 2. 掌握状态之间的转换
 * 3. 了解如何观察线程状态
 * 4. 理解线程中断机制
 */
public class ThreadLifecycle {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(60));
        System.out.println("Phase 5 - Lesson 2: 线程生命周期");
        System.out.println("=".repeat(60));

        // 1. 线程的六种状态
        System.out.println("\n【1. 线程的六种状态 (Thread.State)】");
        System.out.println("""
                NEW         - 新建，尚未启动
                RUNNABLE    - 可运行（包括运行中和就绪）
                BLOCKED     - 阻塞，等待监视器锁
                WAITING     - 等待，无限期等待
                TIMED_WAITING - 计时等待，有超时时间
                TERMINATED  - 终止，执行完成
                """);

        // 打印所有状态
        System.out.println("Java 定义的所有线程状态:");
        for (Thread.State state : Thread.State.values()) {
            System.out.println("  " + state);
        }

        // 2. NEW 状态
        System.out.println("\n【2. NEW 状态】");

        Thread newThread = new Thread(() -> {
            System.out.println("线程执行中");
        });

        System.out.println("创建后，start() 前: " + newThread.getState());

        // 3. RUNNABLE 状态
        System.out.println("\n【3. RUNNABLE 状态】");

        Thread runnableThread = new Thread(() -> {
            // 忙循环
            for (int i = 0; i < 100000; i++) {
                Math.sqrt(i);
            }
        });

        runnableThread.start();
        // 立即检查状态
        System.out.println("start() 后: " + runnableThread.getState());
        runnableThread.join();

        // 4. TIMED_WAITING 状态
        System.out.println("\n【4. TIMED_WAITING 状态】");

        Thread timedWaitingThread = new Thread(() -> {
            try {
                Thread.sleep(2000); // 计时等待
            } catch (InterruptedException e) {
                System.out.println("被中断");
            }
        });

        timedWaitingThread.start();
        Thread.sleep(100); // 给线程时间进入 sleep
        System.out.println("sleep() 中: " + timedWaitingThread.getState());
        timedWaitingThread.interrupt(); // 中断它
        timedWaitingThread.join();

        // 5. WAITING 状态
        System.out.println("\n【5. WAITING 状态】");

        Object lock = new Object();
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait(); // 无限期等待
                } catch (InterruptedException e) {
                    System.out.println("被唤醒/中断");
                }
            }
        });

        waitingThread.start();
        Thread.sleep(100);
        System.out.println("wait() 中: " + waitingThread.getState());

        // 唤醒它
        synchronized (lock) {
            lock.notify();
        }
        waitingThread.join();

        // 6. BLOCKED 状态
        System.out.println("\n【6. BLOCKED 状态】");

        Object blockLock = new Object();

        Thread holdingThread = new Thread(() -> {
            synchronized (blockLock) {
                try {
                    Thread.sleep(1000); // 持有锁 1 秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "HoldingThread");

        Thread blockedThread = new Thread(() -> {
            synchronized (blockLock) { // 尝试获取锁
                System.out.println("获得锁");
            }
        }, "BlockedThread");

        holdingThread.start();
        Thread.sleep(50); // 确保 holdingThread 先获得锁
        blockedThread.start();
        Thread.sleep(50); // 给 blockedThread 时间尝试获取锁

        System.out.println("等待锁时: " + blockedThread.getState());

        holdingThread.join();
        blockedThread.join();

        // 7. TERMINATED 状态
        System.out.println("\n【7. TERMINATED 状态】");

        Thread terminatedThread = new Thread(() -> {
            System.out.println("线程执行完成");
        });

        terminatedThread.start();
        terminatedThread.join();
        System.out.println("执行完成后: " + terminatedThread.getState());

        // 8. 状态转换图
        System.out.println("\n【8. 状态转换图】");
        System.out.println("""
                   ┌─────────────────────────────────────────────┐
                   │                                             │
                   │    NEW ──start()──> RUNNABLE <──────┐       │
                   │                        │            │       │
                   │              ┌─────────┼─────────┐  │       │
                   │              │         │         │  │       │
                   │              ▼         ▼         ▼  │       │
                   │         BLOCKED    WAITING    TIMED_│       │
                   │              │         │      WAITING       │
                   │              │         │         │  │       │
                   │              └─────────┴─────────┘  │       │
                   │                        │            │       │
                   │                        └────────────┘       │
                   │                        │                    │
                   │                        ▼                    │
                   │                   TERMINATED                │
                   └─────────────────────────────────────────────┘

                转换条件:
                - NEW → RUNNABLE: start()
                - RUNNABLE → BLOCKED: 等待 synchronized 锁
                - RUNNABLE → WAITING: wait(), join()
                - RUNNABLE → TIMED_WAITING: sleep(ms), wait(ms), join(ms)
                - BLOCKED/WAITING/TIMED_WAITING → RUNNABLE: 获得锁/被唤醒/超时
                - RUNNABLE → TERMINATED: run() 执行完成或异常
                """);

        // 9. 线程中断
        System.out.println("【9. 线程中断机制】");

        Thread interruptDemo = new Thread(() -> {
            System.out.println("线程开始");
            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                if (count % 100000000 == 0) {
                    System.out.println("工作中... " + count);
                }
            }
            System.out.println("检测到中断，优雅退出");
        });

        interruptDemo.start();
        Thread.sleep(100);
        interruptDemo.interrupt(); // 发送中断信号
        interruptDemo.join();

        // 10. 中断阻塞线程
        System.out.println("\n【10. 中断阻塞线程】");

        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("开始睡眠 10 秒...");
                Thread.sleep(10000);
                System.out.println("睡眠完成");
            } catch (InterruptedException e) {
                System.out.println("睡眠被中断! isInterrupted = " +
                        Thread.currentThread().isInterrupted());
                // 注意: 捕获异常后中断标志被清除
            }
        });

        sleepingThread.start();
        Thread.sleep(500);
        System.out.println("中断睡眠中的线程...");
        sleepingThread.interrupt();
        sleepingThread.join();

        // 11. 正确处理中断
        System.out.println("\n【11. 正确处理中断】");
        System.out.println("""
                正确处理中断的方式:

                1. 检查中断标志:
                   while (!Thread.currentThread().isInterrupted()) {
                       // 工作
                   }

                2. 处理 InterruptedException:
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       // 选项1: 恢复中断状态
                       Thread.currentThread().interrupt();
                       // 选项2: 直接结束
                       return;
                   }

                ⚠️ 不要忽略中断异常!
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 线程有 6 种状态，可通过 getState() 获取");
        System.out.println("💡 interrupt() 只是设置中断标志，需要线程配合检查");
        System.out.println("💡 sleep/wait 中被中断会抛出 InterruptedException");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 线程六种状态:
 * - NEW: 创建未启动
 * - RUNNABLE: 可运行
 * - BLOCKED: 等待锁
 * - WAITING: 无限等待
 * - TIMED_WAITING: 计时等待
 * - TERMINATED: 已终止
 * 
 * 2. 状态转换:
 * - start() 使线程从 NEW 变为 RUNNABLE
 * - synchronized 可能使线程进入 BLOCKED
 * - wait()/join() 使线程进入 WAITING
 * - sleep(ms) 使线程进入 TIMED_WAITING
 * 
 * 3. 中断机制:
 * - interrupt(): 设置中断标志
 * - isInterrupted(): 检查中断标志
 * - Thread.interrupted(): 检查并清除中断标志
 * 
 * 🏃 练习:
 * 1. 创建一个线程并观察其完整生命周期
 * 2. 实现一个可以被优雅中断的任务
 * 3. 模拟 BLOCKED 状态并观察
 */
