package phase16;

/**
 * 分布式限流器 (Distributed Rate Limiter)
 * 
 * 单机限流无法满足分布式系统需求
 * 分布式限流通常基于 Redis + Lua 实现
 * 
 * 本文件演示分布式限流的设计思路和 Lua 脚本
 */
public class DistributedRateLimiter {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ⚡ Phase 16: 分布式限流 (Redis + Lua)                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        System.out.println("\n📌 1. 为什么需要分布式限流\n");
        showWhyDistributed();

        System.out.println("\n📌 2. 固定窗口分布式限流\n");
        showFixedWindowLua();

        System.out.println("\n📌 3. 滑动窗口分布式限流\n");
        showSlidingWindowLua();

        System.out.println("\n📌 4. 令牌桶分布式限流\n");
        showTokenBucketLua();

        System.out.println("\n📌 5. Java 集成示例\n");
        showJavaIntegration();
    }

    private static void showWhyDistributed() {
        System.out.println("单机限流的问题:");
        System.out.println();
        System.out.println("  用户请求");
        System.out.println("      ↓");
        System.out.println("  ┌────────┐");
        System.out.println("  │负载均衡│");
        System.out.println("  └────┬───┘");
        System.out.println("  ┌────┴────┬─────────┐");
        System.out.println("  ↓         ↓         ↓");
        System.out.println("┌────┐   ┌────┐   ┌────┐");
        System.out.println("│节点1│   │节点2│   │节点3│  每个节点限流 100 QPS");
        System.out.println("│100 │   │100 │   │100 │");
        System.out.println("└────┘   └────┘   └────┘");
        System.out.println();
        System.out.println("问题: 总限流变成 300 QPS，非预期!");
        System.out.println();
        System.out.println("解决方案: Redis 中心化限流");
        System.out.println("  所有节点共享一个限流计数器");
    }

    private static void showFixedWindowLua() {
        System.out.println("固定窗口 Lua 脚本:");
        System.out.println();
        System.out.println("```lua");
        System.out.println("-- KEYS[1]: 限流 key");
        System.out.println("-- ARGV[1]: 限流阈值");
        System.out.println("-- ARGV[2]: 窗口大小(秒)");
        System.out.println();
        System.out.println("local key = KEYS[1]");
        System.out.println("local limit = tonumber(ARGV[1])");
        System.out.println("local window = tonumber(ARGV[2])");
        System.out.println();
        System.out.println("local current = redis.call('GET', key)");
        System.out.println("if current and tonumber(current) >= limit then");
        System.out.println("    return 0  -- 限流");
        System.out.println("end");
        System.out.println();
        System.out.println("current = redis.call('INCR', key)");
        System.out.println("if tonumber(current) == 1 then");
        System.out.println("    redis.call('EXPIRE', key, window)");
        System.out.println("end");
        System.out.println();
        System.out.println("return 1  -- 放行");
        System.out.println("```");
    }

    private static void showSlidingWindowLua() {
        System.out.println("滑动窗口 Lua 脚本 (基于 Sorted Set):");
        System.out.println();
        System.out.println("```lua");
        System.out.println("-- KEYS[1]: 限流 key");
        System.out.println("-- ARGV[1]: 限流阈值");
        System.out.println("-- ARGV[2]: 当前时间戳(ms)");
        System.out.println("-- ARGV[3]: 窗口大小(ms)");
        System.out.println();
        System.out.println("local key = KEYS[1]");
        System.out.println("local limit = tonumber(ARGV[1])");
        System.out.println("local now = tonumber(ARGV[2])");
        System.out.println("local window = tonumber(ARGV[3])");
        System.out.println();
        System.out.println("-- 移除窗口外的数据");
        System.out.println("redis.call('ZREMRANGEBYSCORE', key, 0, now - window)");
        System.out.println();
        System.out.println("-- 统计窗口内请求数");
        System.out.println("local count = redis.call('ZCARD', key)");
        System.out.println("if count >= limit then");
        System.out.println("    return 0  -- 限流");
        System.out.println("end");
        System.out.println();
        System.out.println("-- 添加当前请求");
        System.out.println("redis.call('ZADD', key, now, now .. '-' .. math.random())");
        System.out.println("redis.call('EXPIRE', key, window / 1000 + 1)");
        System.out.println();
        System.out.println("return 1  -- 放行");
        System.out.println("```");
    }

    private static void showTokenBucketLua() {
        System.out.println("令牌桶 Lua 脚本:");
        System.out.println();
        System.out.println("```lua");
        System.out.println("-- KEYS[1]: 令牌数 key");
        System.out.println("-- KEYS[2]: 最后填充时间 key");
        System.out.println("-- ARGV[1]: 桶容量");
        System.out.println("-- ARGV[2]: 每秒生成令牌数");
        System.out.println("-- ARGV[3]: 当前时间戳(ms)");
        System.out.println("-- ARGV[4]: 请求令牌数");
        System.out.println();
        System.out.println("local tokens_key = KEYS[1]");
        System.out.println("local timestamp_key = KEYS[2]");
        System.out.println("local capacity = tonumber(ARGV[1])");
        System.out.println("local rate = tonumber(ARGV[2])");
        System.out.println("local now = tonumber(ARGV[3])");
        System.out.println("local requested = tonumber(ARGV[4])");
        System.out.println();
        System.out.println("-- 获取当前令牌数和时间");
        System.out.println("local tokens = tonumber(redis.call('GET', tokens_key) or capacity)");
        System.out.println("local last_time = tonumber(redis.call('GET', timestamp_key) or now)");
        System.out.println();
        System.out.println("-- 计算新增令牌");
        System.out.println("local delta = (now - last_time) / 1000 * rate");
        System.out.println("tokens = math.min(capacity, tokens + delta)");
        System.out.println();
        System.out.println("-- 判断是否有足够令牌");
        System.out.println("if tokens >= requested then");
        System.out.println("    tokens = tokens - requested");
        System.out.println("    redis.call('SET', tokens_key, tokens)");
        System.out.println("    redis.call('SET', timestamp_key, now)");
        System.out.println("    return 1  -- 放行");
        System.out.println("end");
        System.out.println();
        System.out.println("return 0  -- 限流");
        System.out.println("```");
    }

    private static void showJavaIntegration() {
        System.out.println("Java 集成示例:");
        System.out.println();
        System.out.println("```java");
        System.out.println("@Component");
        System.out.println("public class RedisRateLimiter {");
        System.out.println();
        System.out.println("    @Autowired");
        System.out.println("    private StringRedisTemplate redisTemplate;");
        System.out.println();
        System.out.println("    private static final String LUA_SCRIPT = \"...\";");
        System.out.println();
        System.out.println("    public boolean tryAcquire(String key, int limit, int window) {");
        System.out.println("        RedisScript<Long> script = new DefaultRedisScript<>");
        System.out.println("            (LUA_SCRIPT, Long.class);");
        System.out.println();
        System.out.println("        Long result = redisTemplate.execute(");
        System.out.println("            script,");
        System.out.println("            Collections.singletonList(key),");
        System.out.println("            String.valueOf(limit),");
        System.out.println("            String.valueOf(window)");
        System.out.println("        );");
        System.out.println();
        System.out.println("        return result != null && result == 1L;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println();
        System.out.println("💡 要点:");
        System.out.println("   1. Lua 脚本保证原子性");
        System.out.println("   2. 使用 Redis EVAL 命令执行");
        System.out.println("   3. 注意 Redis 集群的 key 分布");
    }
}
