package phase05.ex;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
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

        ExecutorService cachedPool = Executors.newCachedThreadPool();

        ExecutorService singlePool = Executors.newSingleThreadExecutor();

        ExecutorService callablePool = Executors.newFixedThreadPool(2);

        Callable<Integer> task = () -> {
            System.out.println("计算中...");
            Thread.sleep(1000);
            return 42;
        };

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
    }

}
