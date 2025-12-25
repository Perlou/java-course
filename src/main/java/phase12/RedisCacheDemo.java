package phase12;

/**
 * Phase 12 - Lesson 2: Redis 缓存策略
 * 
 * 🎯 学习目标:
 * 1. 理解缓存穿透、击穿、雪崩
 * 2. 掌握缓存更新策略
 * 3. 学会缓存与数据库一致性方案
 */
public class RedisCacheDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 2: Redis 缓存策略");
        System.out.println("=".repeat(60));

        // 1. 缓存模式
        System.out.println("\n【1. 缓存架构模式】");
        System.out.println("""
                Cache-Aside (旁路缓存) - 最常用

                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  读取流程:                                              │
                │  ┌─────────┐    1. 查缓存     ┌─────────┐              │
                │  │ 应用层  │ ───────────────▶ │  Redis  │              │
                │  │         │ ◀─ 2. 返回 ─────│         │              │
                │  └─────────┘                  └─────────┘              │
                │       │ 3. 缓存未命中                                  │
                │       ▼                                                │
                │  ┌─────────┐    4. 查数据库   ┌─────────┐             │
                │  │         │ ───────────────▶ │  MySQL  │              │
                │  │         │ ◀─ 5. 返回 ─────│         │              │
                │  └─────────┘                  └─────────┘              │
                │       │ 6. 写入缓存                ▲                   │
                │       └───────────────────────────┘                    │
                │                                                         │
                │  写入流程:                                              │
                │  1. 更新数据库                                          │
                │  2. 删除缓存 (不是更新缓存)                             │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                代码示例:

                public User getUser(Long id) {
                    // 1. 查缓存
                    String key = "user:" + id;
                    String json = redis.get(key);
                    if (json != null) {
                        return JSON.parseObject(json, User.class);
                    }

                    // 2. 查数据库
                    User user = userMapper.selectById(id);
                    if (user != null) {
                        // 3. 写入缓存
                        redis.setex(key, 3600, JSON.toJSONString(user));
                    }
                    return user;
                }

                public void updateUser(User user) {
                    // 1. 更新数据库
                    userMapper.updateById(user);
                    // 2. 删除缓存
                    redis.del("user:" + user.getId());
                }
                """);

        // 2. 缓存穿透
        System.out.println("=".repeat(60));
        System.out.println("【2. 缓存穿透】");
        System.out.println("""
                问题: 查询不存在的数据，每次都打到数据库

                ┌─────────────────────────────────────────────────────────┐
                │  恶意请求: GET /user/999999999                          │
                │                                                         │
                │  ┌───────┐      缓存 MISS      ┌───────┐              │
                │  │ 请求  │ ─────────────────▶ │ Redis │              │
                │  │       │                     └───────┘              │
                │  │       │                          │                  │
                │  │       │ ◀────── 数据库也没有 ───│                  │
                │  │       │                          ▼                  │
                │  │       │ ──────────────────▶ ┌───────┐              │
                │  │       │     每次都查        │ MySQL │              │
                │  └───────┘                     └───────┘              │
                │                                                         │
                │  结果: 数据库压力大，可能被打垮                         │
                └─────────────────────────────────────────────────────────┘

                解决方案:

                1. 缓存空值
                public User getUser(Long id) {
                    String key = "user:" + id;
                    String json = redis.get(key);

                    // 缓存命中 (包括空值)
                    if (json != null) {
                        if ("NULL".equals(json)) {
                            return null;  // 空值标记
                        }
                        return JSON.parseObject(json, User.class);
                    }

                    User user = userMapper.selectById(id);
                    if (user != null) {
                        redis.setex(key, 3600, JSON.toJSONString(user));
                    } else {
                        // 缓存空值，较短过期时间
                        redis.setex(key, 300, "NULL");
                    }
                    return user;
                }

                2. 布隆过滤器
                ┌─────────────────────────────────────────────────────────┐
                │  请求 ──▶ 布隆过滤器 ──不存在──▶ 直接返回 null         │
                │                │                                        │
                │             可能存在                                    │
                │                ▼                                        │
                │              Redis ──▶ MySQL                           │
                └─────────────────────────────────────────────────────────┘

                // Guava BloomFilter
                BloomFilter<Long> filter = BloomFilter.create(
                    Funnels.longFunnel(), 1000000, 0.01);
                filter.put(userId);  // 初始化所有合法 ID

                if (!filter.mightContain(id)) {
                    return null;  // 一定不存在
                }
                """);

        // 3. 缓存击穿
        System.out.println("=".repeat(60));
        System.out.println("【3. 缓存击穿】");
        System.out.println("""
                问题: 热点 key 过期瞬间，大量请求打到数据库

                ┌─────────────────────────────────────────────────────────┐
                │  热点商品 product:1 的缓存过期                          │
                │                                                         │
                │  ┌───────┐ ┌───────┐ ┌───────┐    ┌───────┐          │
                │  │请求 1 │ │请求 2 │ │请求 N │──▶ │ MySQL │          │
                │  └───────┘ └───────┘ └───────┘    └───────┘          │
                │                                                         │
                │  同时有大量请求发现缓存失效，都去查数据库               │
                └─────────────────────────────────────────────────────────┘

                解决方案:

                1. 互斥锁 (分布式锁)

                public Product getProduct(Long id) {
                    String key = "product:" + id;
                    String json = redis.get(key);
                    if (json != null) {
                        return JSON.parseObject(json, Product.class);
                    }

                    // 获取分布式锁
                    String lockKey = "lock:product:" + id;
                    boolean locked = redis.setnx(lockKey, "1", 10, TimeUnit.SECONDS);

                    if (locked) {
                        try {
                            // 双重检查
                            json = redis.get(key);
                            if (json != null) {
                                return JSON.parseObject(json, Product.class);
                            }

                            // 查数据库
                            Product product = productMapper.selectById(id);
                            redis.setex(key, 3600, JSON.toJSONString(product));
                            return product;
                        } finally {
                            redis.del(lockKey);
                        }
                    } else {
                        // 获取锁失败，等待后重试
                        Thread.sleep(100);
                        return getProduct(id);
                    }
                }

                2. 逻辑过期 (不设置 TTL)

                {
                    "data": {...},
                    "expireTime": "2024-01-01 12:00:00"
                }

                // 发现过期后异步更新，立即返回旧数据
                """);

        // 4. 缓存雪崩
        System.out.println("=".repeat(60));
        System.out.println("【4. 缓存雪崩】");
        System.out.println("""
                问题: 大量 key 同时过期，或 Redis 宕机

                ┌─────────────────────────────────────────────────────────┐
                │  00:00:00 - 大量缓存同时失效                            │
                │                                                         │
                │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐            │
                │  │ k1  │ │ k2  │ │ k3  │ │ k4  │ │ kN  │  全部过期  │
                │  └─────┘ └─────┘ └─────┘ └─────┘ └─────┘            │
                │                       │                                │
                │                       ▼                                │
                │                   ┌───────┐                            │
                │  ─────────────────│ MySQL │──────────────────────     │
                │  大量请求涌入      └───────┘      数据库被打垮         │
                └─────────────────────────────────────────────────────────┘

                解决方案:

                1. 过期时间加随机值
                int ttl = 3600 + random.nextInt(300);  // 1小时 + 0~5分钟随机
                redis.setex(key, ttl, value);

                2. 多级缓存
                ┌───────────┐    ┌───────────┐    ┌───────────┐
                │ 本地缓存  │ ─▶ │   Redis   │ ─▶ │   MySQL   │
                │ (Caffeine)│    │           │    │           │
                └───────────┘    └───────────┘    └───────────┘

                3. Redis 高可用
                - 主从 + 哨兵
                - Redis Cluster

                4. 限流降级
                // 使用 Sentinel 限制进入数据库的请求
                @SentinelResource(value = "getUser", fallback = "getUserFallback")
                public User getUser(Long id) { ... }

                5. 预热
                // 启动时加载热点数据到缓存
                @PostConstruct
                public void warmUp() {
                    List<Product> hotProducts = productMapper.selectHot();
                    hotProducts.forEach(p -> redis.setex(
                        "product:" + p.getId(), 3600, JSON.toJSONString(p)));
                }
                """);

        // 5. 缓存一致性
        System.out.println("=".repeat(60));
        System.out.println("【5. 缓存与数据库一致性】");
        System.out.println("""
                问题: 更新数据时，如何保证缓存与数据库一致？

                策略对比:
                ┌───────────────────┬──────────────────────────────────┐
                │       策略        │             问题                  │
                ├───────────────────┼──────────────────────────────────┤
                │ 先更新缓存        │ 更新失败导致数据不一致           │
                │ 再更新数据库      │                                  │
                ├───────────────────┼──────────────────────────────────┤
                │ 先更新数据库      │ 并发问题：读到旧缓存             │
                │ 再更新缓存        │                                  │
                ├───────────────────┼──────────────────────────────────┤
                │ 先删除缓存        │ 并发问题：旧值被重新缓存         │
                │ 再更新数据库      │                                  │
                ├───────────────────┼──────────────────────────────────┤
                │ 先更新数据库      │ ⭐ 推荐方案                       │
                │ 再删除缓存        │ 可能极短时间不一致               │
                └───────────────────┴──────────────────────────────────┘

                推荐方案: 先更新数据库，再删除缓存

                @Transactional
                public void updateProduct(Product product) {
                    // 1. 更新数据库
                    productMapper.updateById(product);

                    // 2. 删除缓存
                    redis.del("product:" + product.getId());
                }

                加强版: 延迟双删

                @Transactional
                public void updateProduct(Product product) {
                    // 1. 先删缓存
                    redis.del("product:" + product.getId());

                    // 2. 更新数据库
                    productMapper.updateById(product);

                    // 3. 延迟再删一次 (异步)
                    executor.schedule(() -> {
                        redis.del("product:" + product.getId());
                    }, 1, TimeUnit.SECONDS);
                }

                最终一致性方案: 监听 Binlog

                ┌─────────┐    ┌─────────┐    ┌─────────┐
                │  MySQL  │ ─▶ │ Canal   │ ─▶ │  Redis  │
                │ binlog  │    │         │    │ 删除key │
                └─────────┘    └─────────┘    └─────────┘
                """);

        // 6. 缓存预热和淘汰
        System.out.println("=".repeat(60));
        System.out.println("【6. 缓存预热和淘汰策略】");
        System.out.println("""
                缓存预热:

                1. 启动时预热
                @Component
                public class CacheWarmer implements CommandLineRunner {
                    @Override
                    public void run(String... args) {
                        // 加载热点数据
                        loadHotProducts();
                        loadHotUsers();
                    }
                }

                2. 定时预热
                @Scheduled(cron = "0 0 6 * * ?")  // 每天6点
                public void warmUpCache() {
                    // 预热当天可能会访问的数据
                }

                缓存淘汰策略 (maxmemory-policy):
                ┌───────────────────┬──────────────────────────────────┐
                │       策略        │             说明                  │
                ├───────────────────┼──────────────────────────────────┤
                │ noeviction        │ 不淘汰，内存满时报错             │
                │ allkeys-lru       │ 所有 key 中 LRU 淘汰             │
                │ volatile-lru      │ 有过期时间的 key 中 LRU 淘汰     │
                │ allkeys-lfu       │ 所有 key 中 LFU 淘汰 (Redis 4.0) │
                │ allkeys-random    │ 随机淘汰                         │
                │ volatile-random   │ 有过期时间的 key 随机淘汰        │
                │ volatile-ttl      │ 淘汰快要过期的 key               │
                └───────────────────┴──────────────────────────────────┘

                推荐: allkeys-lru 或 allkeys-lfu

                配置:
                maxmemory 2gb
                maxmemory-policy allkeys-lru
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 缓存穿透: 布隆过滤器 / 缓存空值");
        System.out.println("💡 缓存击穿: 互斥锁 / 逻辑过期");
        System.out.println("💡 缓存雪崩: 随机过期 / 多级缓存 / 高可用");
        System.out.println("💡 一致性: 先更新数据库，再删除缓存");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 缓存问题:
 * - 穿透: 查询不存在的数据
 * - 击穿: 热点 key 过期
 * - 雪崩: 大量 key 同时过期
 * 
 * 2. 一致性方案:
 * - 先更新数据库，再删除缓存
 * - 延迟双删
 * - 监听 Binlog
 * 
 * 3. 淘汰策略:
 * - LRU / LFU
 * - allkeys-lru 推荐
 */
