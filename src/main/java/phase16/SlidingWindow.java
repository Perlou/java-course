package phase16;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 滑动窗口算法 (Sliding Window Algorithm)
 * 
 * 核心思想：
 * 1. 将时间窗口划分为多个小的时间槽 (slot)
 * 2. 每个槽记录该时间段内的请求数
 * 3. 窗口随时间滑动，过期的槽被丢弃
 * 
 * 优点：解决固定窗口的边界问题，限流更精确
 * 缺点：实现相对复杂，需要更多内存
 */
public class SlidingWindow {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          ⚡ Phase 16: 滑动窗口算法 (Sliding Window)            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        System.out.println("\n📌 1. 固定窗口的边界问题\n");
        demonstrateFixedWindowProblem();

        System.out.println("\n📌 2. 滑动窗口原理图解\n");
        explainSlidingWindowPrinciple();

        System.out.println("\n📌 3. 滑动窗口计数器实现\n");
        demonstrateSlidingWindowCounter();

        System.out.println("\n📌 4. 滑动日志算法\n");
        demonstrateSlidingLog();
    }

    // ==================== 1. 固定窗口问题 ====================

    private static void demonstrateFixedWindowProblem() {
        System.out.println("固定窗口的临界问题:");
        System.out.println();
        System.out.println("假设: 限制 100 请求/秒");
        System.out.println();
        System.out.println("  0s          0.5s         1s          1.5s         2s");
        System.out.println("  │            │            │            │            │");
        System.out.println("  │← 窗口1(100请求上限) →│← 窗口2(100请求上限) →│");
        System.out.println("  │            ●●●●●●●●●●●●●            │            │");
        System.out.println("  │         0.9s:100个  1.1s:100个      │            │");
        System.out.println();
        System.out.println("⚠️  问题: 0.9s~1.1s 这 0.2 秒内通过了 200 个请求！");
    }

    // ==================== 2. 滑动窗口原理 ====================

    private static void explainSlidingWindowPrinciple() {
        System.out.println("将 1 秒窗口划分为 10 个 100ms 的时间槽:");
        System.out.println();
        System.out.println("T = 0ms 时:");
        System.out.println("┌────┬────┬────┬────┬────┬────┬────┬────┬────┬────┐");
        System.out.println("│ 10 │  8 │ 12 │  5 │  9 │ 11 │  7 │ 13 │  6 │  8 │  总计: 89");
        System.out.println("└────┴────┴────┴────┴────┴────┴────┴────┴────┴────┘");
        System.out.println("│←───────────── 当前窗口 (1000ms) ──────────────→│");
        System.out.println();
        System.out.println("💡 窗口随时间连续滑动，任意时刻都受限制");
    }

    // ==================== 3. 滑动窗口计数器 ====================

    static class SlidingWindowCounter {
        private final int limit;
        private final int slotCount;
        private final int slotSizeMs;
        private final AtomicInteger[] slots;
        private volatile long windowStart;

        public SlidingWindowCounter(int limit, int windowSizeMs, int slotCount) {
            this.limit = limit;
            this.slotCount = slotCount;
            this.slotSizeMs = windowSizeMs / slotCount;
            this.slots = new AtomicInteger[slotCount];
            for (int i = 0; i < slotCount; i++) {
                slots[i] = new AtomicInteger(0);
            }
            this.windowStart = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            cleanExpiredSlots(now);

            int total = 0;
            for (AtomicInteger slot : slots)
                total += slot.get();

            if (total < limit) {
                int idx = (int) ((now - windowStart) / slotSizeMs) % slotCount;
                slots[idx].incrementAndGet();
                return true;
            }
            return false;
        }

        private void cleanExpiredSlots(long now) {
            long elapsed = now - windowStart;
            if (elapsed >= slotSizeMs) {
                int toClean = Math.min((int) (elapsed / slotSizeMs), slotCount);
                windowStart += (long) toClean * slotSizeMs;
                for (int i = 0; i < slotCount; i++) {
                    slots[i].set(i < slotCount - toClean ? slots[i + toClean].get() : 0);
                }
            }
        }

        public int getCurrentCount() {
            int total = 0;
            for (AtomicInteger slot : slots)
                total += slot.get();
            return total;
        }
    }

    private static void demonstrateSlidingWindowCounter() {
        SlidingWindowCounter counter = new SlidingWindowCounter(10, 1000, 10);

        System.out.println("滑动窗口计数器: 限制 10 请求/秒");
        System.out.println();

        for (int i = 1; i <= 15; i++) {
            boolean success = counter.tryAcquire();
            System.out.printf("  请求 %2d: %s (当前: %d)%n",
                    i, success ? "✅ 通过" : "❌ 拒绝", counter.getCurrentCount());
        }
    }

    // ==================== 4. 滑动日志算法 ====================

    static class SlidingLog {
        private final int limit;
        private final long windowSizeMs;
        private final Queue<Long> timestamps = new LinkedList<>();

        public SlidingLog(int limit, long windowSizeMs) {
            this.limit = limit;
            this.windowSizeMs = windowSizeMs;
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            long windowStart = now - windowSizeMs;

            while (!timestamps.isEmpty() && timestamps.peek() <= windowStart) {
                timestamps.poll();
            }

            if (timestamps.size() < limit) {
                timestamps.offer(now);
                return true;
            }
            return false;
        }
    }

    private static void demonstrateSlidingLog() {
        SlidingLog log = new SlidingLog(5, 1000);

        System.out.println("滑动日志算法: 限制 5 请求/秒");
        System.out.println("优点: 精确 | 缺点: 内存消耗大");
        System.out.println();

        for (int i = 1; i <= 8; i++) {
            boolean result = log.tryAcquire();
            System.out.printf("  请求 %d: %s%n", i, result ? "✅ 通过" : "❌ 拒绝");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
