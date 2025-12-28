package phase17;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存策略模式 (Caching Patterns)
 * 
 * 常见的缓存模式：
 * 1. Cache-Aside: 应用管理缓存
 * 2. Read-Through: 读穿透
 * 3. Write-Through: 写穿透
 * 4. Write-Behind: 异步写回
 */
public class CacheAsidePattern {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("           Phase 17: 缓存策略模式 (Caching Patterns)         ");
        System.out.println("============================================================");

        System.out.println("\n[1] 缓存模式对比\n");
        showCachingPatterns();

        System.out.println("\n[2] Cache-Aside 模式\n");
        demonstrateCacheAside();

        System.out.println("\n[3] 缓存一致性问题\n");
        explainConsistencyIssues();

        System.out.println("\n[4] 缓存最佳实践\n");
        showBestPractices();
    }

    private static void showCachingPatterns() {
        System.out.println("四种缓存模式对比:");
        System.out.println();
        System.out.println("+----------------+------------------+------------------+");
        System.out.println("|     模式        |       优点        |       缺点       |");
        System.out.println("+----------------+------------------+------------------+");
        System.out.println("| Cache-Aside    | 实现简单          | 数据可能不一致   |");
        System.out.println("| (旁路缓存)      | 应用控制缓存      | 首次请求慢       |");
        System.out.println("+----------------+------------------+------------------+");
        System.out.println("| Read-Through   | 透明读取          | 首次请求慢       |");
        System.out.println("| (读穿透)        | 代码简洁          | 依赖缓存框架     |");
        System.out.println("+----------------+------------------+------------------+");
        System.out.println("| Write-Through  | 数据强一致        | 写入延迟高       |");
        System.out.println("| (写穿透)        | 缓存始终最新      | 每次写都慢       |");
        System.out.println("+----------------+------------------+------------------+");
        System.out.println("| Write-Behind   | 写入性能高        | 可能丢失数据     |");
        System.out.println("| (写回)          | 异步批量写        | 一致性复杂       |");
        System.out.println("+----------------+------------------+------------------+");
    }

    // ==================== Cache-Aside 实现 ====================

    static class CacheAsideService {
        // 模拟数据库
        private final Map<String, String> database = new ConcurrentHashMap<>();
        // 模拟 Redis 缓存
        private final Map<String, String> cache = new ConcurrentHashMap<>();

        public CacheAsideService() {
            // 初始化数据库
            database.put("user:1", "Alice");
            database.put("user:2", "Bob");
            database.put("user:3", "Charlie");
        }

        /**
         * 读取 - Cache-Aside 模式
         * 1. 先查缓存
         * 2. 缓存未命中，查数据库
         * 3. 写入缓存
         */
        public String read(String key) {
            // 1. 查缓存
            String value = cache.get(key);
            if (value != null) {
                System.out.println("    [缓存命中] " + key + " = " + value);
                return value;
            }

            // 2. 缓存未命中，查数据库
            System.out.println("    [缓存未命中] 查询数据库...");
            value = database.get(key);

            // 3. 写入缓存
            if (value != null) {
                cache.put(key, value);
                System.out.println("    [回填缓存] " + key + " = " + value);
            }

            return value;
        }

        /**
         * 写入 - Cache-Aside 模式
         * 1. 更新数据库
         * 2. 删除缓存 (而不是更新缓存)
         */
        public void write(String key, String value) {
            // 1. 更新数据库
            database.put(key, value);
            System.out.println("    [更新数据库] " + key + " = " + value);

            // 2. 删除缓存
            cache.remove(key);
            System.out.println("    [删除缓存] " + key);
        }
    }

    private static void demonstrateCacheAside() {
        CacheAsideService service = new CacheAsideService();

        System.out.println("Cache-Aside 读取流程:");
        System.out.println();

        // 第一次读取 - 未命中
        System.out.println("1. 首次读取 user:1:");
        service.read("user:1");

        // 第二次读取 - 命中
        System.out.println("\n2. 再次读取 user:1:");
        service.read("user:1");

        // 写入 - 删除缓存
        System.out.println("\n3. 更新 user:1:");
        service.write("user:1", "Alice Updated");

        // 读取 - 未命中，重新加载
        System.out.println("\n4. 更新后读取 user:1:");
        service.read("user:1");
    }

    private static void explainConsistencyIssues() {
        System.out.println("缓存与数据库一致性问题:");
        System.out.println();
        System.out.println("问题1: 先更新缓存后更新数据库");
        System.out.println("  时间线:");
        System.out.println("  T1: 更新缓存为 A'");
        System.out.println("  T2: 数据库更新失败!");
        System.out.println("  结果: 缓存是 A'，数据库是 A，不一致!");
        System.out.println();
        System.out.println("问题2: 先更新数据库后更新缓存");
        System.out.println("  时间线:");
        System.out.println("  T1: 线程1 更新数据库为 A");
        System.out.println("  T2: 线程2 更新数据库为 B");
        System.out.println("  T3: 线程2 更新缓存为 B");
        System.out.println("  T4: 线程1 更新缓存为 A (覆盖!)");
        System.out.println("  结果: 数据库是 B，缓存是 A，不一致!");
        System.out.println();
        System.out.println("推荐方案: 先更新数据库，后删除缓存");
        System.out.println("  - 删除缓存而不是更新缓存");
        System.out.println("  - 下次读取时重新加载");
        System.out.println("  - 加上延迟双删更保险");
    }

    private static void showBestPractices() {
        System.out.println("缓存最佳实践:");
        System.out.println();
        System.out.println("1. 缓存穿透防护");
        System.out.println("   - 问题: 查询不存在的 key，每次都访问数据库");
        System.out.println("   - 方案: 布隆过滤器 + 空值缓存");
        System.out.println();
        System.out.println("2. 缓存击穿防护");
        System.out.println("   - 问题: 热点 key 过期，大量请求打到数据库");
        System.out.println("   - 方案: 互斥锁 + 热点数据永不过期");
        System.out.println();
        System.out.println("3. 缓存雪崩防护");
        System.out.println("   - 问题: 大量 key 同时过期");
        System.out.println("   - 方案: 随机过期时间 + 多级缓存");
        System.out.println();
        System.out.println("4. 延迟双删");
        System.out.println("   - 更新数据库");
        System.out.println("   - 删除缓存");
        System.out.println("   - 等待 1 秒");
        System.out.println("   - 再次删除缓存");
    }
}
