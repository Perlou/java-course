package phase16;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 漏桶算法 (Leaky Bucket Algorithm)
 * 
 * 核心思想：
 * 1. 请求先进入桶中排队
 * 2. 以固定速率从桶中流出处理
 * 3. 桶满时，新请求被丢弃
 * 4. 实现流量整形，消除突发
 * 
 * 与令牌桶的区别：
 * - 令牌桶：控制平均速率，允许突发
 * - 漏桶：严格控制速率，完全消除突发
 */
public class LeakyBucket {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           ⚡ Phase 16: 漏桶算法 (Leaky Bucket)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        System.out.println("\n📌 1. 漏桶原理图解\n");
        explainLeakyBucketPrinciple();

        System.out.println("\n📌 2. 漏桶 vs 令牌桶对比\n");
        compareWithTokenBucket();

        System.out.println("\n📌 3. 漏桶实现演示\n");
        demonstrateLeakyBucket();
    }

    // ==================== 1. 漏桶原理 ====================

    private static void explainLeakyBucketPrinciple() {
        System.out.println("漏桶算法示意图:");
        System.out.println();
        System.out.println("        ●●●●●●●●● 突发请求 (不规则流入)");
        System.out.println("            ↓ ↓ ↓");
        System.out.println("        ┌─────────────┐");
        System.out.println("        │   ● ● ● ●  │  ← 请求在桶中排队");
        System.out.println("        │   ● ● ● ●  │    容量有限");
        System.out.println("        │   ● ● ● ●  │");
        System.out.println("        └──────┬──────┘");
        System.out.println("               │");
        System.out.println("               ↓ 恒定速率流出");
        System.out.println("           ● ─ ● ─ ● ─ ● ─ ●");
        System.out.println("           │   │   │   │   │");
        System.out.println("         0ms 100ms 200ms 300ms 400ms");
        System.out.println();
        System.out.println("💡 关键特性:");
        System.out.println("   - 入队：突发的，请求随时可能到来");
        System.out.println("   - 出队：恒定的，固定间隔处理一个请求");
        System.out.println("   - 桶满：新请求被丢弃");
    }

    // ==================== 2. 与令牌桶对比 ====================

    private static void compareWithTokenBucket() {
        System.out.println("┌────────────┬────────────────────┬────────────────────┐");
        System.out.println("│   特性      │     令牌桶          │      漏桶          │");
        System.out.println("├────────────┼────────────────────┼────────────────────┤");
        System.out.println("│ 突发流量    │ ✅ 允许(消耗令牌)  │ ❌ 不允许          │");
        System.out.println("├────────────┼────────────────────┼────────────────────┤");
        System.out.println("│ 流量整形    │ ❌ 不整形          │ ✅ 完全整形        │");
        System.out.println("├────────────┼────────────────────┼────────────────────┤");
        System.out.println("│ 请求等待    │ ❌ 直接拒绝或等待  │ ✅ 排队等待处理    │");
        System.out.println("├────────────┼────────────────────┼────────────────────┤");
        System.out.println("│ 适用场景    │ API限流、登录保护  │ 消息队列、流量整形 │");
        System.out.println("└────────────┴────────────────────┴────────────────────┘");
    }

    // ==================== 3. 漏桶实现 ====================

    static class SimpleLeakyBucket {
        private final int capacity;
        private final long leakIntervalMs; // 每次漏出的间隔
        private final BlockingQueue<Runnable> bucket;
        private volatile boolean running = true;

        public SimpleLeakyBucket(int capacity, int ratePerSecond) {
            this.capacity = capacity;
            this.leakIntervalMs = 1000 / ratePerSecond;
            this.bucket = new LinkedBlockingQueue<>(capacity);
            startLeaking();
        }

        private void startLeaking() {
            Thread leaker = new Thread(() -> {
                while (running) {
                    try {
                        Runnable task = bucket.poll(leakIntervalMs, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            task.run();
                        }
                        Thread.sleep(leakIntervalMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            leaker.setDaemon(true);
            leaker.start();
        }

        public boolean trySubmit(Runnable task) {
            return bucket.offer(task);
        }

        public int getQueueSize() {
            return bucket.size();
        }

        public void stop() {
            running = false;
        }
    }

    private static void demonstrateLeakyBucket() throws InterruptedException {
        // 容量5, 速率2/秒
        SimpleLeakyBucket bucket = new SimpleLeakyBucket(5, 2);

        System.out.println("漏桶配置: 容量=5, 漏出速率=2/秒");
        System.out.println();

        // 突发10个请求
        System.out.println("突发提交 10 个任务:");
        int accepted = 0, rejected = 0;

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            boolean ok = bucket.trySubmit(() -> System.out.println("    🔄 处理任务 " + taskId));

            if (ok) {
                accepted++;
                System.out.printf("  任务 %2d: ✅ 入队 (队列: %d)%n", i, bucket.getQueueSize());
            } else {
                rejected++;
                System.out.printf("  任务 %2d: ❌ 丢弃 (队列已满)%n", i);
            }
        }

        System.out.println();
        System.out.println("统计: 入队=" + accepted + ", 丢弃=" + rejected);
        System.out.println();
        System.out.println("等待任务处理 (恒定速率)...");
        Thread.sleep(3000);

        bucket.stop();
        System.out.println();
        System.out.println("💡 应用场景: 消息队列消费、任务调度、网络流量整形");
    }
}
