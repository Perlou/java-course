package phase13;

/**
 * Phase 13: 缓存策略设计
 * 
 * 本课程涵盖：
 * 1. 缓存架构设计
 * 2. 缓存问题解决
 * 3. 缓存一致性
 * 4. 本地缓存实现
 */
public class CacheStrategy {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 13: 缓存策略设计");
        System.out.println("=".repeat(60));

        cacheArchitecture();
        cacheProblems();
        cacheConsistency();
        localCacheDemo();
    }

    private static void cacheArchitecture() {
        System.out.println("\n【1. 缓存架构设计】");
        System.out.println("-".repeat(50));

        String arch = """

                ═══════════════════════════════════════════════════════════
                                  多级缓存架构
                ═══════════════════════════════════════════════════════════

                ┌─────────┐   ┌───────────┐   ┌─────────┐   ┌────────┐
                │  客户端  │ → │  CDN 缓存  │ → │ Nginx   │ → │  应用  │
                └─────────┘   └───────────┘   └─────────┘   └────────┘
                                                                ↓
                ┌─────────┐   ┌───────────┐   ┌─────────┐   ┌────────┐
                │ 数据库   │ ← │  Redis    │ ← │ 本地缓存 │ ← │  应用  │
                └─────────┘   └───────────┘   └─────────┘   └────────┘
                     ↓            ↓              ↓
                   ~10ms        ~1ms           ~1μs


                ═══════════════════════════════════════════════════════════
                                  各级缓存特点
                ═══════════════════════════════════════════════════════════

                │ 级别      │ 技术            │ 延迟   │ 容量    │
                ├───────────┼─────────────────┼────────┼─────────┤
                │ CDN       │ CloudFlare等   │ ~100ms │ 海量    │
                │ Nginx     │ Proxy Cache    │ ~10ms  │ 中等    │
                │ 本地缓存  │ Caffeine/Guava │ ~1μs   │ 较小    │
                │ 分布式缓存│ Redis/Memcached│ ~1ms   │ 较大    │
                │ 数据库    │ MySQL Query    │ ~10ms  │ 按需    │


                ═══════════════════════════════════════════════════════════
                                  缓存使用场景
                ═══════════════════════════════════════════════════════════

                ✅ 适合缓存:
                • 读多写少的数据
                • 计算成本高的结果
                • 热点数据
                • 配置信息

                ❌ 不适合缓存:
                • 频繁更新的数据
                • 强一致性要求的数据
                • 敏感隐私数据
                """;
        System.out.println(arch);
    }

    private static void cacheProblems() {
        System.out.println("\n【2. 缓存常见问题】");
        System.out.println("-".repeat(50));

        String problems = """

                ═══════════════════════════════════════════════════════════
                                  缓存穿透
                ═══════════════════════════════════════════════════════════

                问题：查询不存在的数据，每次都穿透到数据库

                场景：恶意攻击，查询 id=-1 的数据

                解决方案：

                1. 布隆过滤器（推荐）
                ─────────────────────────────────────────────────────────
                if (!bloomFilter.mightContain(id)) {
                    return null;  // 一定不存在，直接返回
                }
                // 可能存在，继续查询

                2. 缓存空值
                ─────────────────────────────────────────────────────────
                Object value = cache.get(key);
                if (value == null) {
                    value = db.get(key);
                    if (value == null) {
                        cache.set(key, "NULL", 60);  // 缓存空值，短过期
                    } else {
                        cache.set(key, value, 3600);
                    }
                }


                ═══════════════════════════════════════════════════════════
                                  缓存击穿
                ═══════════════════════════════════════════════════════════

                问题：热点 key 失效，大量请求同时穿透到数据库

                解决方案：

                1. 互斥锁
                ─────────────────────────────────────────────────────────
                Object value = cache.get(key);
                if (value == null) {
                    if (tryLock(lockKey)) {
                        try {
                            value = db.get(key);
                            cache.set(key, value);
                        } finally {
                            unlock(lockKey);
                        }
                    } else {
                        Thread.sleep(50);
                        return get(key);  // 重试
                    }
                }

                2. 逻辑过期（不真正设置过期时间）
                ─────────────────────────────────────────────────────────
                CacheData data = cache.get(key);
                if (data.isLogicalExpired()) {
                    // 异步更新缓存
                    executor.submit(() -> refreshCache(key));
                }
                return data.getValue();  // 返回旧值


                ═══════════════════════════════════════════════════════════
                                  缓存雪崩
                ═══════════════════════════════════════════════════════════

                问题：大量 key 同时过期，或 Redis 宕机

                解决方案：

                1. 随机过期时间
                ─────────────────────────────────────────────────────────
                int expire = 3600 + random.nextInt(600);  // 1小时 + 0-10分钟
                cache.set(key, value, expire);

                2. 多级缓存
                ─────────────────────────────────────────────────────────
                本地缓存 → Redis → 数据库

                3. 限流降级
                ─────────────────────────────────────────────────────────
                if (redis.isDown()) {
                    if (rateLimiter.tryAcquire()) {
                        return db.get(key);
                    }
                    return fallback();  // 降级响应
                }
                """;
        System.out.println(problems);
    }

    private static void cacheConsistency() {
        System.out.println("\n【3. 缓存一致性】");
        System.out.println("-".repeat(50));

        String consistency = """

                ═══════════════════════════════════════════════════════════
                             Cache-Aside 模式（推荐）
                ═══════════════════════════════════════════════════════════

                读流程：
                1. 先读缓存
                2. 缓存命中 → 直接返回
                3. 缓存未命中 → 查 DB → 写入缓存 → 返回

                写流程：
                1. 更新数据库
                2. 删除缓存（而不是更新！）

                public Object read(String key) {
                    Object value = cache.get(key);
                    if (value == null) {
                        value = db.get(key);
                        if (value != null) {
                            cache.set(key, value);
                        }
                    }
                    return value;
                }

                public void write(String key, Object value) {
                    db.update(key, value);  // 先更新 DB
                    cache.delete(key);       // 再删除缓存
                }


                ═══════════════════════════════════════════════════════════
                             为什么删除而不是更新？
                ═══════════════════════════════════════════════════════════

                假设：线程 A 更新为值 1，线程 B 更新为值 2

                ❌ 更新缓存（可能不一致）：
                1. A 更新 DB = 1
                2. B 更新 DB = 2
                3. B 更新缓存 = 2
                4. A 更新缓存 = 1  ← 缓存变成了旧值！

                ✅ 删除缓存（最终一致）：
                1. A 更新 DB = 1, 删除缓存
                2. B 更新 DB = 2, 删除缓存
                3. 下次读取时从 DB 加载最新值


                ═══════════════════════════════════════════════════════════
                             延迟双删（更安全）
                ═══════════════════════════════════════════════════════════

                public void write(String key, Object value) {
                    cache.delete(key);       // 1. 先删缓存
                    db.update(key, value);   // 2. 更新 DB

                    // 3. 延迟再删一次（防止并发读写不一致）
                    executor.schedule(() -> {
                        cache.delete(key);
                    }, 500, TimeUnit.MILLISECONDS);
                }
                """;
        System.out.println(consistency);
    }

    private static void localCacheDemo() {
        System.out.println("\n【4. 本地缓存示例】");
        System.out.println("-".repeat(50));

        String demo = """

                ═══════════════════════════════════════════════════════════
                             Caffeine 本地缓存（推荐）
                ═══════════════════════════════════════════════════════════

                <!-- Maven 依赖 -->
                <dependency>
                    <groupId>com.github.ben-manes.caffeine</groupId>
                    <artifactId>caffeine</artifactId>
                    <version>3.1.8</version>
                </dependency>

                // 创建缓存
                Cache<String, User> cache = Caffeine.newBuilder()
                    .maximumSize(10_000)                 // 最大条目数
                    .expireAfterWrite(Duration.ofMinutes(10))  // 写入10分钟后过期
                    .expireAfterAccess(Duration.ofMinutes(5))  // 5分钟未访问过期
                    .recordStats()                       // 记录统计信息
                    .build();

                // 使用缓存
                cache.put("user:1", user);
                User user = cache.getIfPresent("user:1");

                // 带加载器（推荐）
                LoadingCache<String, User> loadingCache = Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .expireAfterWrite(Duration.ofMinutes(10))
                    .build(key -> userService.getById(key));  // 自动加载

                User user = loadingCache.get("user:1");  // 自动查库


                ═══════════════════════════════════════════════════════════
                             多级缓存示例
                ═══════════════════════════════════════════════════════════

                public User getUser(String userId) {
                    // 1. 查本地缓存
                    User user = localCache.getIfPresent(userId);
                    if (user != null) {
                        return user;
                    }

                    // 2. 查 Redis
                    String json = redis.get("user:" + userId);
                    if (json != null) {
                        user = JSON.parseObject(json, User.class);
                        localCache.put(userId, user);  // 回填本地缓存
                        return user;
                    }

                    // 3. 查数据库
                    user = userDao.findById(userId);
                    if (user != null) {
                        redis.setex("user:" + userId, 3600, JSON.toJSONString(user));
                        localCache.put(userId, user);
                    }
                    return user;
                }
                """;
        System.out.println(demo);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 缓存策略课程完成！下一课: ConnectionPoolTuning.java");
        System.out.println("=".repeat(60));
    }
}
