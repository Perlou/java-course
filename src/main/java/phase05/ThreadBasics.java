package phase05;

/**
 * Phase 5 - Lesson 1: 线程基础
 * 
 * 🎯 学习目标:
 * 1. 理解进程与线程的区别
 * 2. 掌握创建线程的三种方式
 * 3. 理解线程的基本概念
 * 4. 了解守护线程
 */
public class ThreadBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 5 - Lesson 1: 线程基础");
        System.out.println("=".repeat(60));

        // 1. 进程与线程
        System.out.println("\n【1. 进程与线程】");
        System.out.println("""
                进程 (Process):
                - 操作系统分配资源的基本单位
                - 每个进程有独立的内存空间
                - 进程间通信需要特殊机制 (IPC)

                线程 (Thread):
                - CPU 调度的基本单位
                - 同一进程的线程共享内存空间
                - 线程间通信更方便，但需要同步

                Java 线程:
                - 每个 Java 程序至少有一个主线程 (main)
                - JVM 还有 GC 线程、Finalizer 线程等
                """);

        // 当前线程信息
        Thread currentThread = Thread.currentThread();
        System.out.println("当前线程: " + currentThread.getName());
        System.out.println("线程 ID: " + currentThread.threadId());
        System.out.println("线程优先级: " + currentThread.getPriority());
        System.out.println("是否守护线程: " + currentThread.isDaemon());

        // 2. 方式一: 继承 Thread 类
        System.out.println("\n【2. 方式一: 继承 Thread 类】");

        class MyThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println(getName() + " 执行: " + i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        MyThread thread1 = new MyThread();
        thread1.setName("Thread-A");
        thread1.start(); // 启动线程，不是 run()!

        MyThread thread2 = new MyThread();
        thread2.setName("Thread-B");
        thread2.start();

        // 等待线程完成
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread 方式完成");

        // 3. 方式二: 实现 Runnable 接口
        System.out.println("\n【3. 方式二: 实现 Runnable 接口】");

        class MyRunnable implements Runnable {
            private String name;

            public MyRunnable(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println(name + " 执行: " + i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Thread t1 = new Thread(new MyRunnable("Runnable-A"));
        Thread t2 = new Thread(new MyRunnable("Runnable-B"));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Runnable 方式完成");

        // 4. 方式三: Lambda 表达式 (推荐)
        System.out.println("\n【4. 方式三: Lambda 表达式 (推荐)】");

        Thread lambdaThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Lambda-A 执行: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Lambda-Thread-A");

        Thread lambdaThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Lambda-B 执行: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Lambda-Thread-B");

        lambdaThread1.start();
        lambdaThread2.start();

        try {
            lambdaThread1.join();
            lambdaThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Lambda 方式完成");

        // 5. start() vs run()
        System.out.println("\n【5. start() vs run()】");

        Thread demoThread = new Thread(() -> {
            System.out.println("  执行线程: " + Thread.currentThread().getName());
        }, "DemoThread");

        System.out.println("调用 run() - 同步执行:");
        demoThread.run(); // 在主线程中执行

        Thread demoThread2 = new Thread(() -> {
            System.out.println("  执行线程: " + Thread.currentThread().getName());
        }, "DemoThread2");

        System.out.println("调用 start() - 异步执行:");
        demoThread2.start(); // 创建新线程执行

        try {
            demoThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 6. 线程命名
        System.out.println("\n【6. 线程命名】");

        Thread namedThread = new Thread(() -> {
            System.out.println("线程名: " + Thread.currentThread().getName());
        });
        namedThread.setName("CustomNamedThread");
        namedThread.start();

        try {
            namedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 7. 线程优先级
        System.out.println("\n【7. 线程优先级】");
        System.out.println("优先级范围: " + Thread.MIN_PRIORITY + " - " + Thread.MAX_PRIORITY);
        System.out.println("默认优先级: " + Thread.NORM_PRIORITY);

        Thread lowPriority = new Thread(() -> {
            System.out.println("低优先级线程执行");
        });
        lowPriority.setPriority(Thread.MIN_PRIORITY);

        Thread highPriority = new Thread(() -> {
            System.out.println("高优先级线程执行");
        });
        highPriority.setPriority(Thread.MAX_PRIORITY);

        lowPriority.start();
        highPriority.start();

        try {
            lowPriority.join();
            highPriority.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("⚠️ 优先级只是建议，不保证执行顺序");

        // 8. 守护线程
        System.out.println("\n【8. 守护线程】");
        System.out.println("""
                守护线程 (Daemon Thread):
                - 为其他线程提供服务的线程
                - 例如: GC、Finalizer
                - 当所有非守护线程结束时，JVM 退出
                - 守护线程会被强制终止
                """);

        Thread daemonThread = new Thread(() -> {
            while (true) {
                System.out.println("守护线程运行中...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        daemonThread.setDaemon(true); // 必须在 start() 前设置
        daemonThread.start();

        // 主线程稍等一下就结束
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程即将结束，守护线程也会停止");

        // 9. 三种创建方式对比
        System.out.println("\n【9. 三种创建方式对比】");
        System.out.println("""
                | 方式          | 优点                       | 缺点                    |
                |---------------|----------------------------|-------------------------|
                | 继承 Thread   | 简单直接                    | 无法继承其他类           |
                | 实现 Runnable | 可继承其他类，解耦          | 无返回值                 |
                | Lambda        | 代码简洁，函数式风格        | 无返回值                 |
                | Callable      | 有返回值，可抛异常          | 稍复杂（需要Future）     |

                推荐: Lambda > Runnable > Thread
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 start() 启动新线程，run() 只是普通方法调用");
        System.out.println("💡 优先使用 Runnable/Lambda 而非继承 Thread");
        System.out.println("💡 守护线程在所有用户线程结束后自动停止");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 创建线程的方式:
 * - 继承 Thread 类，重写 run()
 * - 实现 Runnable 接口
 * - Lambda 表达式 (推荐)
 * - 实现 Callable 接口 (有返回值)
 * 
 * 2. Thread 关键方法:
 * - start(): 启动线程
 * - run(): 线程执行体
 * - join(): 等待线程完成
 * - setName()/getName(): 线程命名
 * - setPriority(): 设置优先级
 * - setDaemon(): 设置为守护线程
 * 
 * 3. 守护线程:
 * - 为用户线程提供服务
 * - JVM 在所有用户线程结束后退出
 * - 必须在 start() 前设置
 * 
 * 🏃 练习:
 * 1. 创建 10 个线程，每个打印自己的编号
 * 2. 实现一个简单的守护线程日志记录器
 * 3. 比较 start() 和 run() 的执行效果
 */
