package phase16;

/**
 * 令牌桶算法 (Token Bucket Algorithm)
 * 
 * 核心思想：
 * 1. 以固定速率向桶中添加令牌
 * 2. 请求需要获取令牌才能被处理
 * 3. 桶有容量限制，满了之后新令牌被丢弃
 * 4. 允许一定程度的突发流量（桶内累积的令牌）
 * 
 * 优点：
 * - 可以处理突发流量
 * - 平滑的限流效果
 * 
 * 缺点：
 * - 实现相对复杂
 * - 需要精确的时间控制
 * 
 * 应用场景：API限流、接口保护
 */
public class TokenBucket {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          ⚡ Phase 16: 令牌桶算法 (Token Bucket)               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // 1. 基础令牌桶
        System.out.println("\n📌 1. 基础令牌桶实现\n");
        demonstrateBasicTokenBucket();

        // 2. 突发流量处理
        System.out.println("\n📌 2. 突发流量处理能力\n");
        demonstrateBurstHandling();

        // 3. 令牌桶 vs 固定窗口对比
        System.out.println("\n📌 3. 令牌桶 vs 固定窗口对比\n");
        compareWithFixedWindow();

        // 4. 实际应用示例
        System.out.println("\n📌 4. API 限流实战示例\n");
        demonstrateApiRateLimiting();
    }

    // ==================== 1. 基础令牌桶实现 ====================

    /**
     * 简单令牌桶实现
     * 
     * 核心参数:
     * - capacity: 桶容量（最大令牌数）
     * - refillRate: 令牌生成速率（每秒生成的令牌数）
     * - tokens: 当前令牌数
     * - lastRefillTime: 上次填充时间
     */
    static class SimpleTokenBucket {
        private final long capacity; // 桶容量
        private final double refillRate; // 每秒生成的令牌数
        private double tokens; // 当前令牌数
        private long lastRefillTime; // 上次填充时间

        public SimpleTokenBucket(long capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity; // 初始时桶是满的
            this.lastRefillTime = System.nanoTime();
        }

        /**
         * 尝试获取指定数量的令牌
         * 
         * @param numTokens 需要的令牌数
         * @return true 如果成功获取，false 如果令牌不足
         */
        public synchronized boolean tryAcquire(int numTokens) {
            // 1. 先补充令牌
            refill();

            // 2. 检查是否有足够的令牌
            if (tokens >= numTokens) {
                tokens -= numTokens;
                return true;
            }
            return false;
        }

        /**
         * 根据时间流逝补充令牌
         */
        private void refill() {
            long now = System.nanoTime();
            // 计算时间间隔（秒）
            double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
            // 计算应该添加的令牌数
            double tokensToAdd = elapsedSeconds * refillRate;
            // 更新令牌数，不超过容量
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }

        /**
         * 获取当前令牌数
         */
        public synchronized double getAvailableTokens() {
            refill();
            return tokens;
        }
    }

    private static void demonstrateBasicTokenBucket() {
        // 创建：容量10个令牌，每秒生成2个令牌
        SimpleTokenBucket bucket = new SimpleTokenBucket(10, 2);

        System.out.println("令牌桶配置: 容量=10, 生成速率=2/秒");
        System.out.println("初始令牌数: " + bucket.getAvailableTokens());
        System.out.println();

        // 模拟请求
        System.out.println("模拟连续请求:");
        for (int i = 1; i <= 15; i++) {
            boolean acquired = bucket.tryAcquire(1);
            System.out.printf("  请求 %2d: %s (剩余令牌: %.1f)%n",
                    i,
                    acquired ? "✅ 通过" : "❌ 拒绝",
                    bucket.getAvailableTokens());
        }

        // 等待一段时间让令牌恢复
        System.out.println("\n等待 2 秒让令牌恢复...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("恢复后令牌数: " + String.format("%.1f", bucket.getAvailableTokens()));
    }

    // ==================== 2. 突发流量处理 ====================

    private static void demonstrateBurstHandling() {
        System.out.println("令牌桶的突发处理能力:");
        System.out.println();

        /*
         * 令牌桶允许突发流量的原因：
         * - 桶内可以累积令牌（最多到容量上限）
         * - 低流量时期，令牌逐渐累积
         * - 突发时期，可以一次性消费累积的令牌
         * 
         * 示例：容量100，速率10/秒
         * - 等待10秒，积累100个令牌
         * - 突发100个请求，全部通过
         * - 之后恢复为10/秒的稳定速率
         */

        System.out.println("场景：API 限流保护");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();
        System.out.println("配置: 容量=100 令牌, 速率=10 令牌/秒");
        System.out.println();

        // 令牌桶算法示意
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│                 令牌桶示意图                      │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│                                                 │");
        System.out.println("│     [令牌生成器] ──→ 10个/秒                    │");
        System.out.println("│          │                                      │");
        System.out.println("│          ↓                                      │");
        System.out.println("│    ┌───────────┐                                │");
        System.out.println("│    │  ● ● ● ●  │  容量: 100                     │");
        System.out.println("│    │  ● ● ● ●  │                                │");
        System.out.println("│    │  ● ● ● ●  │  当前: 100 (满)                │");
        System.out.println("│    └─────┬─────┘                                │");
        System.out.println("│          │                                      │");
        System.out.println("│          ↓ 获取令牌                             │");
        System.out.println("│    [请求处理器]                                  │");
        System.out.println("│                                                 │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("场景模拟:");
        System.out.println("  T=0s:  桶满100令牌");
        System.out.println("  T=0s:  突发50个请求 → 全部通过 (剩余50令牌)");
        System.out.println("  T=0.1s: 再来20个请求 → 全部通过 (剩余30+1=31令牌)");
        System.out.println("  T=1s:  再来50个请求 → 41个通过 (31+10-50=-9，部分拒绝)");
        System.out.println();

        System.out.println("💡 关键特性:");
        System.out.println("   - 累积令牌能力让系统可以处理突发流量");
        System.out.println("   - 长期来看，仍然保持平均速率限制");
        System.out.println("   - 非常适合 API 调用、登录限流等场景");
    }

    // ==================== 3. 对比固定窗口 ====================

    /**
     * 固定窗口计数器（用于对比）
     */
    static class FixedWindowCounter {
        private final int limit;
        private final long windowSizeMs;
        private int count;
        private long windowStart;

        public FixedWindowCounter(int limit, long windowSizeMs) {
            this.limit = limit;
            this.windowSizeMs = windowSizeMs;
            this.windowStart = System.currentTimeMillis();
            this.count = 0;
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowSizeMs) {
                // 进入新窗口
                windowStart = now;
                count = 0;
            }

            if (count < limit) {
                count++;
                return true;
            }
            return false;
        }
    }

    private static void compareWithFixedWindow() {
        System.out.println("令牌桶 vs 固定窗口计数器");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 对比维度          │  固定窗口         │  令牌桶              │");
        System.out.println("├───────────────────┼──────────────────┼─────────────────────┤");
        System.out.println("│ 实现复杂度         │  简单             │  中等               │");
        System.out.println("├───────────────────┼──────────────────┼─────────────────────┤");
        System.out.println("│ 限流精确度         │  存在边界问题     │  精确               │");
        System.out.println("├───────────────────┼──────────────────┼─────────────────────┤");
        System.out.println("│ 突发处理           │  窗口内可突发     │  累积令牌可突发     │");
        System.out.println("├───────────────────┼──────────────────┼─────────────────────┤");
        System.out.println("│ 流量平滑           │  不平滑           │  相对平滑           │");
        System.out.println("├───────────────────┼──────────────────┼─────────────────────┤");
        System.out.println("│ 内存占用           │  最小             │  最小               │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        // 演示固定窗口的边界问题
        System.out.println("固定窗口的边界问题 (Critical Section Problem):");
        System.out.println();
        System.out.println("假设限制: 100 请求/秒");
        System.out.println();
        System.out.println("  |<─── 窗口1(0-1秒) ───>|<─── 窗口2(1-2秒) ───>|");
        System.out.println("  │                      │                      │");
        System.out.println("  │         ...    ●●●●●●│●●●●●● ...           │");
        System.out.println("  │              ↑       │       ↑              │");
        System.out.println("  │         0.9s: 100个  │  1.1s: 100个         │");
        System.out.println("  │              请求    │   请求               │");
        System.out.println();
        System.out.println("  问题: 0.9s~1.1s 这 0.2 秒内实际通过了 200 个请求！");
        System.out.println("       这是期望限制的 2 倍");
        System.out.println();
        System.out.println("  💡 令牌桶不会有这个问题，因为令牌是持续消耗的");
    }

    // ==================== 4. API 限流实战 ====================

    /**
     * 线程安全的令牌桶实现（适合生产使用）
     */
    static class ThreadSafeTokenBucket {
        private final long capacity;
        private final double refillRate;
        private volatile double tokens;
        private volatile long lastRefillNanoTime;
        private final Object lock = new Object();

        public ThreadSafeTokenBucket(long capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefillNanoTime = System.nanoTime();
        }

        public boolean tryAcquire() {
            return tryAcquire(1);
        }

        public boolean tryAcquire(int numTokens) {
            synchronized (lock) {
                refill();
                if (tokens >= numTokens) {
                    tokens -= numTokens;
                    return true;
                }
                return false;
            }
        }

        /**
         * 阻塞等待获取令牌
         */
        public void acquire() throws InterruptedException {
            acquire(1);
        }

        public void acquire(int numTokens) throws InterruptedException {
            while (true) {
                synchronized (lock) {
                    refill();
                    if (tokens >= numTokens) {
                        tokens -= numTokens;
                        return;
                    }
                    // 计算需要等待的时间
                    double tokensNeeded = numTokens - tokens;
                    long waitMs = (long) Math.ceil(tokensNeeded / refillRate * 1000);
                    lock.wait(Math.max(1, waitMs));
                }
            }
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanoTime) / 1_000_000_000.0;
            double tokensToAdd = elapsedSeconds * refillRate;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillNanoTime = now;
        }

        public double getTokens() {
            synchronized (lock) {
                refill();
                return tokens;
            }
        }
    }

    /**
     * API 限流器示例
     */
    static class ApiRateLimiter {
        private final ThreadSafeTokenBucket bucket;
        private final String apiName;

        public ApiRateLimiter(String apiName, int qps) {
            this.apiName = apiName;
            // 容量设为 QPS 的 2 倍，允许一定突发
            this.bucket = new ThreadSafeTokenBucket(qps * 2, qps);
        }

        public ApiResponse call(String request) {
            if (bucket.tryAcquire()) {
                // 模拟 API 调用
                return new ApiResponse(true, "Success: " + request);
            } else {
                return new ApiResponse(false, "Rate limited: " + apiName);
            }
        }
    }

    static class ApiResponse {
        final boolean success;
        final String message;

        ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    private static void demonstrateApiRateLimiting() {
        // 创建限流器: 限制 5 QPS
        ApiRateLimiter limiter = new ApiRateLimiter("/api/user", 5);

        System.out.println("API 限流配置: /api/user, 限制 5 QPS");
        System.out.println();

        // 模拟 10 个并发请求
        System.out.println("模拟 10 个快速请求:");
        int success = 0, failed = 0;

        for (int i = 1; i <= 10; i++) {
            ApiResponse response = limiter.call("request-" + i);
            if (response.success) {
                success++;
                System.out.println("  请求" + i + ": ✅ " + response.message);
            } else {
                failed++;
                System.out.println("  请求" + i + ": ❌ " + response.message);
            }
        }

        System.out.println();
        System.out.println("结果统计: 成功=" + success + ", 限流=" + failed);
        System.out.println();

        System.out.println("💡 最佳实践:");
        System.out.println("   1. 合理设置容量，允许一定的突发流量");
        System.out.println("   2. 返回友好的限流响应 (HTTP 429 Too Many Requests)");
        System.out.println("   3. 在响应头中告知客户端限流策略");
        System.out.println("   4. 对不同的 API 设置不同的限流阈值");
        System.out.println("   5. 考虑使用 Redis 实现分布式限流");
    }
}
