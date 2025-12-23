package phase05.ex;

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
                    lock.wait();
                } catch (InterruptedException e) {

                }
            }
        });

        waitingThread.start();
        Thread.sleep(100);
        System.out.println("wait() 中: " + waitingThread.getState());

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
                    Thread.sleep(1000);
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
        Thread.sleep(50);
        blockedThread.start();
        Thread.sleep(50);
        System.out.println("等待锁时: " + blockedThread.getState());

        holdingThread.join();
        blockedThread.join();
    }
}
