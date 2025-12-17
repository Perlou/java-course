package phase05;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Phase 5 - Lesson 5: 线程池与 Executor 框架
 * 
 * 🎯 学习目标:
 * 1. 理解线程池的作用和优势
 * 2. 掌握 ExecutorService 的使用
 * 3. 了解常见线程池的特点
 * 4. 学会自定义线程池
 */
public class ThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=".repeat(60));
        System.out.println("Phase 5 - Lesson 5: 线程池与 Executor 框架");
        System.out.println("=".repeat(60));

        // 1. 为什么需要线程池
        System.out.println("\n【1. 为什么需要线程池】");
        System.out.println("""
                问题: 每次创建新线程
                - 创建和销毁线程开销大
                - 线程数量无限制，可能耗尽资源
                - 难以管理和监控

                线程池优势:
                - 重用线程，减少创建销毁开销
                - 控制并发数量
                - 统一管理，方便监控
                - 提供任务队列
                """);

        // 2. Executor 框架
        System.out.println("【2. Executor 框架】");
        System.out.println("""
                Executor (接口)
                └── ExecutorService (接口)
                    ├── ThreadPoolExecutor (核心实现)
                    └── ScheduledExecutorService (定时任务)

                Executors (工厂类) - 创建线程池
                """);

        // 3. 固定大小线程池
        System.out.println("【3. FixedThreadPool - 固定大小线程池】");

        ExecutorService fixedPool = Executors.newFixedThreadPool(3);

        System.out.println("提交 5 个任务到 3 线程池:");
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            fixedPool.execute(() -> {
                System.out.println("任务 " + taskId + " 执行于 " +
                        Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        fixedPool.shutdown();
        fixedPool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("FixedThreadPool 完成");

        // 4. 缓存线程池
        System.out.println("\n【4. CachedThreadPool - 缓存线程池】");
        System.out.println("特点: 按需创建，空闲60秒后回收");

        ExecutorService cachedPool = Executors.newCachedThreadPool();

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            cachedPool.execute(() -> {
                System.out.println("任务 " + taskId + " 执行于 " +
                        Thread.currentThread().getName());
            });
        }

        cachedPool.shutdown();
        cachedPool.awaitTermination(5, TimeUnit.SECONDS);

        // 5. 单线程池
        System.out.println("\n【5. SingleThreadExecutor - 单线程池】");
        System.out.println("特点: 保证任务按顺序执行");

        ExecutorService singlePool = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            singlePool.execute(() -> {
                System.out.println("任务 " + taskId + " 顺序执行");
            });
        }

        singlePool.shutdown();
        singlePool.awaitTermination(5, TimeUnit.SECONDS);

        // 6. Callable 与 Future
        System.out.println("\n【6. Callable 与 Future - 有返回值的任务】");

        ExecutorService callablePool = Executors.newFixedThreadPool(2);

        // Callable 可以返回结果和抛出异常
        Callable<Integer> task = () -> {
            System.out.println("计算中...");
            Thread.sleep(1000);
            return 42;
        };

        Future<Integer> future = callablePool.submit(task);

        System.out.println("任务已提交，做其他事...");
        System.out.println("isDone: " + future.isDone());

        // 阻塞获取结果
        Integer result = future.get();
        System.out.println("结果: " + result);
        System.out.println("isDone: " + future.isDone());

        callablePool.shutdown();

        // 7. 批量提交任务
        System.out.println("\n【7. invokeAll - 批量提交任务】");

        ExecutorService batchPool = Executors.newFixedThreadPool(3);

        var tasks = java.util.List.of(
                (Callable<String>) () -> {
                    Thread.sleep(500);
                    return "结果A";
                },
                (Callable<String>) () -> {
                    Thread.sleep(300);
                    return "结果B";
                },
                (Callable<String>) () -> {
                    Thread.sleep(400);
                    return "结果C";
                });

        // invokeAll 等待所有完成
        var futures = batchPool.invokeAll(tasks);

        for (Future<String> f : futures) {
            System.out.println("结果: " + f.get());
        }

        // invokeAny 返回最快完成的一个
        System.out.println("\ninvokeAny - 返回最快的结果:");
        String fastest = batchPool.invokeAny(tasks);
        System.out.println("最快结果: " + fastest);

        batchPool.shutdown();

        // 8. 自定义线程池 (推荐)
        System.out.println("\n【8. 自定义 ThreadPoolExecutor】");

        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
                2, // corePoolSize: 核心线程数
                4, // maximumPoolSize: 最大线程数
                60, // keepAliveTime: 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(10), // 工作队列
                Executors.defaultThreadFactory(), // 线程工厂
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
        );

        System.out.println("核心线程数: " + customPool.getCorePoolSize());
        System.out.println("最大线程数: " + customPool.getMaximumPoolSize());
        System.out.println("队列容量: 10");

        // 提交任务
        for (int i = 0; i < 15; i++) {
            final int taskId = i;
            try {
                customPool.execute(() -> {
                    System.out.println("任务 " + taskId + " 执行");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                });
            } catch (RejectedExecutionException e) {
                System.out.println("任务 " + taskId + " 被拒绝!");
            }
        }

        customPool.shutdown();
        customPool.awaitTermination(10, TimeUnit.SECONDS);

        // 9. 线程池参数详解
        System.out.println("\n【9. 线程池参数详解】");
        System.out.println("""
                ThreadPoolExecutor 参数:

                1. corePoolSize: 核心线程数
                   - 即使空闲也不会被回收 (除非设置 allowCoreThreadTimeOut)

                2. maximumPoolSize: 最大线程数
                   - 任务太多时可以临时增加的线程上限

                3. keepAliveTime: 非核心线程空闲存活时间
                   - 超时后被回收

                4. workQueue: 工作队列
                   - LinkedBlockingQueue: 无界/有界队列
                   - ArrayBlockingQueue: 有界队列
                   - SynchronousQueue: 不存储，直接交付

                5. threadFactory: 线程工厂
                   - 自定义线程名、优先级等

                6. handler: 拒绝策略
                   - AbortPolicy: 抛异常 (默认)
                   - CallerRunsPolicy: 调用者线程执行
                   - DiscardPolicy: 静默丢弃
                   - DiscardOldestPolicy: 丢弃最旧任务
                """);

        // 10. 任务执行流程
        System.out.println("【10. 任务执行流程】");
        System.out.println("""
                提交任务后的处理流程:

                  任务提交
                     │
                     ▼
                ┌──线程数 < corePoolSize?──┐
                │        是                │否
                │    创建核心线程          │
                │        执行              ▼
                │                   ┌──队列未满?──┐
                │                   │    是      │否
                │                   │ 加入队列   │
                │                   │            ▼
                │                   │   ┌──线程数 < max?──┐
                │                   │   │     是        │否
                │                   │   │ 创建临时线程  │执行拒绝策略
                │                   │   │     执行      │
                └───────────────────┴───┴───────────────┘
                """);

        // 11. 优雅关闭线程池
        System.out.println("【11. 优雅关闭线程池】");

        ExecutorService poolToClose = Executors.newFixedThreadPool(2);

        poolToClose.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println("长任务完成");
        });

        // 优雅关闭
        poolToClose.shutdown(); // 不再接受新任务
        System.out.println("isShutdown: " + poolToClose.isShutdown());

        if (!poolToClose.awaitTermination(3, TimeUnit.SECONDS)) {
            System.out.println("超时，强制关闭...");
            poolToClose.shutdownNow(); // 强制终止
        }

        System.out.println("isTerminated: " + poolToClose.isTerminated());

        System.out.println("""

                关闭方法:
                - shutdown(): 不再接受新任务，等待已有任务完成
                - shutdownNow(): 尝试终止所有任务，返回未执行的任务
                - awaitTermination(): 等待终止
                """);

        // 12. 定时任务线程池
        System.out.println("【12. ScheduledThreadPool - 定时任务】");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // 延迟执行
        scheduler.schedule(() -> {
            System.out.println("延迟 1 秒执行");
        }, 1, TimeUnit.SECONDS);

        // 固定速率执行 (演示2次后取消)
        var scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            System.out.println("每 500ms 执行一次: " + System.currentTimeMillis());
        }, 0, 500, TimeUnit.MILLISECONDS);

        Thread.sleep(1500);
        scheduledFuture.cancel(false);

        scheduler.shutdown();
        scheduler.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 优先使用自定义 ThreadPoolExecutor 而非 Executors");
        System.out.println("💡 根据任务类型选择合适的线程池参数");
        System.out.println("💡 始终优雅关闭线程池");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 常用线程池:
 * - FixedThreadPool: 固定大小
 * - CachedThreadPool: 按需创建
 * - SingleThreadExecutor: 单线程
 * - ScheduledThreadPool: 定时任务
 * 
 * 2. ExecutorService 方法:
 * - execute(): 执行 Runnable
 * - submit(): 提交 Callable，返回 Future
 * - invokeAll(): 批量执行，等待全部完成
 * - invokeAny(): 返回最快完成的结果
 * - shutdown(): 优雅关闭
 * 
 * 3. ThreadPoolExecutor 参数:
 * - corePoolSize: 核心线程
 * - maximumPoolSize: 最大线程
 * - keepAliveTime: 存活时间
 * - workQueue: 任务队列
 * - handler: 拒绝策略
 * 
 * 4. 最佳实践:
 * - 避免使用无界队列
 * - 设置合理的拒绝策略
 * - 给线程池起有意义的名字
 * 
 * 🏃 练习:
 * 1. 创建一个处理 HTTP 请求的线程池
 * 2. 实现带超时的批量任务执行
 * 3. 用定时任务实现简单的定时器
 */
