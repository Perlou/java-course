package phase16.ex;

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

        // // 1. 基础令牌桶
        // System.out.println("\n📌 1. 基础令牌桶实现\n");
        // demonstrateBasicTokenBucket();

        // // 2. 突发流量处理
        // System.out.println("\n📌 2. 突发流量处理能力\n");
        // demonstrateBurstHandling();

        // // 3. 令牌桶 vs 固定窗口对比
        // System.out.println("\n📌 3. 令牌桶 vs 固定窗口对比\n");
        // compareWithFixedWindow();

        // 4. 实际应用示例
        System.out.println("\n📌 4. API 限流实战示例\n");
        demonstrateApiRateLimiting();
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
     * API 限流
     */
    static class ApiRateLimiter {
        private final ThreadSafeTokenBucket bucket;
        private final String apiName;

        public ApiRateLimiter(String apiName, int qps) {
            this.apiName = apiName;
            // 2倍
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