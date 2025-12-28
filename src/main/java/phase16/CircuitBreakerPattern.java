package phase16;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 熔断器模式 (Circuit Breaker Pattern)
 * 
 * 核心思想：
 * 当服务调用失败率超过阈值时，熔断器打开，后续请求快速失败
 * 避免故障传播，保护系统整体可用性
 * 
 * 状态转换：
 * CLOSED → [失败率超阈值] → OPEN → [超时] → HALF_OPEN
 * HALF_OPEN → [测试成功] → CLOSED
 * HALF_OPEN → [测试失败] → OPEN
 */
public class CircuitBreakerPattern {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ⚡ Phase 16: 熔断器模式 (Circuit Breaker)             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        System.out.println("\n📌 1. 熔断器状态机\n");
        explainCircuitBreakerStates();

        System.out.println("\n📌 2. 熔断器实现\n");
        demonstrateCircuitBreaker();

        System.out.println("\n📌 3. 熔断器配置建议\n");
        showConfigurationTips();
    }

    // ==================== 1. 状态机说明 ====================

    private static void explainCircuitBreakerStates() {
        System.out.println("熔断器三态模型:");
        System.out.println();
        System.out.println("  ┌─────────┐  失败率>50%  ┌─────────┐  超时30s  ┌───────────┐");
        System.out.println("  │ CLOSED  │ ──────────→ │  OPEN   │ ───────→ │ HALF_OPEN │");
        System.out.println("  │ (正常)  │             │ (熔断)  │           │ (半开)    │");
        System.out.println("  └─────────┘             └─────────┘           └───────────┘");
        System.out.println("       ↑                                              │");
        System.out.println("       │                    测试失败                   │");
        System.out.println("       │        ←─────────────────────────────────────┘");
        System.out.println("       │                                              │");
        System.out.println("       └──────────────── 测试成功 ────────────────────┘");
        System.out.println();
        System.out.println("CLOSED:    正常状态，请求正常执行");
        System.out.println("OPEN:      熔断状态，请求快速失败");
        System.out.println("HALF_OPEN: 半开状态，允许少量请求测试");
    }

    // ==================== 2. 熔断器实现 ====================

    enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    static class SimpleCircuitBreaker {
        private final int failureThreshold; // 失败阈值
        private final long openTimeoutMs; // 熔断超时
        private final int halfOpenMaxCalls; // 半开测试次数

        private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicInteger halfOpenCalls = new AtomicInteger(0);
        private volatile long openedAt = 0;

        public SimpleCircuitBreaker(int failureThreshold, long openTimeoutMs, int halfOpenMaxCalls) {
            this.failureThreshold = failureThreshold;
            this.openTimeoutMs = openTimeoutMs;
            this.halfOpenMaxCalls = halfOpenMaxCalls;
        }

        public <T> T execute(Supplier<T> action, Supplier<T> fallback) {
            if (!allowRequest()) {
                System.out.println("    [熔断器] 状态=OPEN, 执行降级");
                return fallback.get();
            }

            try {
                T result = action.get();
                onSuccess();
                return result;
            } catch (Exception e) {
                onFailure();
                return fallback.get();
            }
        }

        private boolean allowRequest() {
            State current = state.get();

            if (current == State.CLOSED)
                return true;

            if (current == State.OPEN) {
                if (System.currentTimeMillis() - openedAt >= openTimeoutMs) {
                    if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                        halfOpenCalls.set(0);
                        System.out.println("    [熔断器] OPEN → HALF_OPEN");
                    }
                    return true;
                }
                return false;
            }

            // HALF_OPEN: 限制测试请求数
            return halfOpenCalls.incrementAndGet() <= halfOpenMaxCalls;
        }

        private void onSuccess() {
            State current = state.get();
            if (current == State.HALF_OPEN) {
                if (successCount.incrementAndGet() >= halfOpenMaxCalls) {
                    state.set(State.CLOSED);
                    failureCount.set(0);
                    successCount.set(0);
                    System.out.println("    [熔断器] HALF_OPEN → CLOSED (恢复)");
                }
            }
        }

        private void onFailure() {
            State current = state.get();
            if (current == State.CLOSED) {
                if (failureCount.incrementAndGet() >= failureThreshold) {
                    state.set(State.OPEN);
                    openedAt = System.currentTimeMillis();
                    System.out.println("    [熔断器] CLOSED → OPEN (熔断!)");
                }
            } else if (current == State.HALF_OPEN) {
                state.set(State.OPEN);
                openedAt = System.currentTimeMillis();
                System.out.println("    [熔断器] HALF_OPEN → OPEN (测试失败)");
            }
        }

        public State getState() {
            return state.get();
        }
    }

    private static void demonstrateCircuitBreaker() throws InterruptedException {
        // 3次失败熔断，2秒超时，2次测试
        SimpleCircuitBreaker breaker = new SimpleCircuitBreaker(3, 2000, 2);

        System.out.println("熔断器配置: 失败阈值=3, 熔断超时=2s, 测试次数=2");
        System.out.println();

        // 模拟不稳定服务
        AtomicInteger callCount = new AtomicInteger(0);
        Supplier<String> unstableService = () -> {
            int n = callCount.incrementAndGet();
            if (n <= 3 || (n >= 8 && n <= 9)) { // 前3次和第8-9次失败
                throw new RuntimeException("Service Error");
            }
            return "Success #" + n;
        };
        Supplier<String> fallback = () -> "Fallback Response";

        // 执行请求
        for (int i = 1; i <= 12; i++) {
            String result = breaker.execute(unstableService, fallback);
            System.out.printf("请求 %2d: %s (状态: %s)%n", i, result, breaker.getState());

            if (i == 5) {
                System.out.println("\n--- 等待熔断超时 (2s) ---\n");
                Thread.sleep(2500);
            } else {
                Thread.sleep(200);
            }
        }
    }

    // ==================== 3. 配置建议 ====================

    private static void showConfigurationTips() {
        System.out.println("熔断器配置建议:");
        System.out.println();
        System.out.println("┌───────────────┬────────────┬─────────────────────────┐");
        System.out.println("│     参数       │   推荐值   │         说明            │");
        System.out.println("├───────────────┼────────────┼─────────────────────────┤");
        System.out.println("│ 失败率阈值     │   50%      │ 触发熔断的失败百分比     │");
        System.out.println("│ 最小请求数     │   10-20    │ 统计失败率的最小样本     │");
        System.out.println("│ 熔断时长       │   30-60s   │ OPEN状态持续时间         │");
        System.out.println("│ 半开测试数     │   5-10     │ HALF_OPEN允许的请求数    │");
        System.out.println("│ 慢调用阈值     │   5-10s    │ 定义慢调用的响应时间     │");
        System.out.println("└───────────────┴────────────┴─────────────────────────┘");
    }
}
