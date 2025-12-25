package phase12;

/**
 * Phase 12 - Lesson 1: Redis 数据类型
 * 
 * 🎯 学习目标:
 * 1. 掌握 Redis 五种基本数据类型
 * 2. 了解每种类型的应用场景
 * 3. 学会常用命令
 */
public class RedisBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 1: Redis 数据类型");
        System.out.println("=".repeat(60));

        // 1. Redis 简介
        System.out.println("\n【1. Redis 简介】");
        System.out.println("""
                Redis = Remote Dictionary Server

                ┌─────────────────────────────────────────────────────────┐
                │                    Redis 特点                           │
                ├─────────────────────────────────────────────────────────┤
                │  ✅ 内存存储: 读写速度极快 (10万+ QPS)                  │
                │  ✅ 数据持久化: RDB 快照 + AOF 日志                     │
                │  ✅ 丰富的数据类型: String, Hash, List, Set, ZSet       │
                │  ✅ 单线程: 避免上下文切换和锁竞争                      │
                │  ✅ 原子操作: 所有操作都是原子的                        │
                │  ✅ 支持事务: MULTI/EXEC                                │
                │  ✅ 主从复制 + 集群                                     │
                └─────────────────────────────────────────────────────────┘

                应用场景:
                - 缓存 (最常见)
                - 分布式锁
                - 计数器 (点赞、播放量)
                - 排行榜 (ZSet)
                - 消息队列 (List/Stream)
                - 会话存储 (Session)
                """);

        // 2. String 类型
        System.out.println("=".repeat(60));
        System.out.println("【2. String 类型】");
        System.out.println("""
                最基本的数据类型，可以存储字符串、数字、二进制数据

                常用命令:
                ┌────────────────────────────┬────────────────────────────┐
                │         命令               │           作用              │
                ├────────────────────────────┼────────────────────────────┤
                │ SET key value              │ 设置值                      │
                │ GET key                    │ 获取值                      │
                │ SETNX key value            │ 不存在才设置 (原子)         │
                │ SETEX key seconds value    │ 设置值并指定过期时间        │
                │ INCR key                   │ 自增 1                      │
                │ INCRBY key increment       │ 增加指定值                  │
                │ DECR key                   │ 自减 1                      │
                │ MSET k1 v1 k2 v2          │ 批量设置                    │
                │ MGET k1 k2                │ 批量获取                    │
                └────────────────────────────┴────────────────────────────┘

                示例:
                SET user:1:name "张三"
                GET user:1:name  → "张三"

                SETEX token:abc 3600 "user_info"  # 1小时过期

                SET counter 0
                INCR counter → 1
                INCRBY counter 10 → 11

                应用场景:
                - 缓存对象 (JSON 序列化)
                - 计数器 (INCR)
                - 分布式锁 (SETNX)
                - 限流 (计数器)
                """);

        // 3. Hash 类型
        System.out.println("=".repeat(60));
        System.out.println("【3. Hash 类型】");
        System.out.println("""
                类似 Map<String, Map<String, String>>

                结构:
                ┌──────────────────────────────────────────────────────┐
                │  key: user:1                                         │
                │  ┌─────────────────────────────────────────────────┐ │
                │  │  field: name    →  value: "张三"                │ │
                │  │  field: age     →  value: "25"                  │ │
                │  │  field: email   →  value: "zhang@test.com"      │ │
                │  └─────────────────────────────────────────────────┘ │
                └──────────────────────────────────────────────────────┘

                常用命令:
                ┌────────────────────────────┬────────────────────────────┐
                │         命令               │           作用              │
                ├────────────────────────────┼────────────────────────────┤
                │ HSET key field value       │ 设置单个字段                │
                │ HGET key field             │ 获取单个字段                │
                │ HMSET key f1 v1 f2 v2      │ 批量设置                    │
                │ HMGET key f1 f2            │ 批量获取                    │
                │ HGETALL key                │ 获取所有字段和值            │
                │ HDEL key field             │ 删除字段                    │
                │ HINCRBY key field incr     │ 字段值增加                  │
                │ HEXISTS key field          │ 判断字段是否存在            │
                └────────────────────────────┴────────────────────────────┘

                示例:
                HSET user:1 name "张三" age 25 email "zhang@test.com"
                HGET user:1 name → "张三"
                HGETALL user:1 → {"name": "张三", "age": "25", ...}
                HINCRBY user:1 age 1 → 26

                应用场景:
                - 存储对象 (比 String + JSON 更灵活)
                - 购物车 (cart:userId field=productId value=quantity)
                - 用户信息缓存
                """);

        // 4. List 类型
        System.out.println("=".repeat(60));
        System.out.println("【4. List 类型】");
        System.out.println("""
                双向链表，支持从头/尾插入和弹出

                结构:
                ┌──────────────────────────────────────────────────────┐
                │  key: messages                                       │
                │  ┌────┐   ┌────┐   ┌────┐   ┌────┐   ┌────┐        │
                │  │ A  │ ↔ │ B  │ ↔ │ C  │ ↔ │ D  │ ↔ │ E  │        │
                │  └────┘   └────┘   └────┘   └────┘   └────┘        │
                │   HEAD                                    TAIL      │
                └──────────────────────────────────────────────────────┘

                常用命令:
                ┌────────────────────────────┬────────────────────────────┐
                │         命令               │           作用              │
                ├────────────────────────────┼────────────────────────────┤
                │ LPUSH key value            │ 从左边插入                  │
                │ RPUSH key value            │ 从右边插入                  │
                │ LPOP key                   │ 从左边弹出                  │
                │ RPOP key                   │ 从右边弹出                  │
                │ LRANGE key start end       │ 获取范围元素                │
                │ LLEN key                   │ 获取长度                    │
                │ LINDEX key index           │ 获取指定位置元素            │
                │ BLPOP key timeout          │ 阻塞弹出                    │
                └────────────────────────────┴────────────────────────────┘

                示例:
                RPUSH messages "msg1" "msg2" "msg3"
                LRANGE messages 0 -1 → ["msg1", "msg2", "msg3"]
                LPOP messages → "msg1"

                # 实现队列 (FIFO)
                RPUSH queue task1  # 入队
                LPOP queue         # 出队

                # 实现栈 (LIFO)
                LPUSH stack item1  # 入栈
                LPOP stack         # 出栈

                应用场景:
                - 消息队列
                - 最新消息列表
                - 时间线 (Timeline)
                """);

        // 5. Set 类型
        System.out.println("=".repeat(60));
        System.out.println("【5. Set 类型】");
        System.out.println("""
                无序集合，元素唯一，支持交集、并集、差集

                结构:
                ┌──────────────────────────────────────────────────────┐
                │  key: tags                                           │
                │  ┌──────────────────────────────────────────────┐   │
                │  │  "java"  "python"  "redis"  "mysql"          │   │
                │  └──────────────────────────────────────────────┘   │
                └──────────────────────────────────────────────────────┘

                常用命令:
                ┌────────────────────────────┬────────────────────────────┐
                │         命令               │           作用              │
                ├────────────────────────────┼────────────────────────────┤
                │ SADD key member            │ 添加元素                    │
                │ SREM key member            │ 移除元素                    │
                │ SMEMBERS key               │ 获取所有元素                │
                │ SISMEMBER key member       │ 判断是否存在                │
                │ SCARD key                  │ 获取元素个数                │
                │ SINTER k1 k2               │ 交集                        │
                │ SUNION k1 k2               │ 并集                        │
                │ SDIFF k1 k2                │ 差集 (k1 有 k2 没有)        │
                │ SRANDMEMBER key count      │ 随机获取元素                │
                └────────────────────────────┴────────────────────────────┘

                示例:
                SADD user:1:following 2 3 4 5
                SADD user:2:following 3 4 6 7

                # 共同关注
                SINTER user:1:following user:2:following → [3, 4]

                # 推荐关注 (我关注的人关注了谁)
                SDIFF user:2:following user:1:following → [6, 7]

                应用场景:
                - 标签 (Tags)
                - 共同好友/关注
                - 抽奖 (SRANDMEMBER)
                - 去重
                """);

        // 6. Sorted Set (ZSet)
        System.out.println("=".repeat(60));
        System.out.println("【6. Sorted Set (ZSet)】");
        System.out.println("""
                有序集合，每个元素关联一个分数 (score)，按分数排序

                结构:
                ┌──────────────────────────────────────────────────────┐
                │  key: ranking                                        │
                │  ┌────────────────────────────────────────────────┐ │
                │  │  score: 100  member: "张三"                    │ │
                │  │  score: 95   member: "李四"                    │ │
                │  │  score: 88   member: "王五"                    │ │
                │  └────────────────────────────────────────────────┘ │
                └──────────────────────────────────────────────────────┘

                常用命令:
                ┌────────────────────────────┬────────────────────────────┐
                │         命令               │           作用              │
                ├────────────────────────────┼────────────────────────────┤
                │ ZADD key score member      │ 添加元素                    │
                │ ZREM key member            │ 移除元素                    │
                │ ZSCORE key member          │ 获取分数                    │
                │ ZRANK key member           │ 获取排名 (从低到高)         │
                │ ZREVRANK key member        │ 获取排名 (从高到低)         │
                │ ZRANGE key start end       │ 按排名获取 (升序)           │
                │ ZREVRANGE key start end    │ 按排名获取 (降序)           │
                │ ZINCRBY key incr member    │ 增加分数                    │
                │ ZCOUNT key min max         │ 分数范围内元素个数          │
                └────────────────────────────┴────────────────────────────┘

                示例:
                ZADD leaderboard 100 "player1" 95 "player2" 88 "player3"

                # 获取排行榜前 10
                ZREVRANGE leaderboard 0 9 WITHSCORES

                # 获取某人排名
                ZREVRANK leaderboard "player2" → 1 (第2名)

                # 增加分数
                ZINCRBY leaderboard 10 "player2" → 105

                应用场景:
                - 排行榜
                - 延迟队列 (score = 执行时间戳)
                - 范围查询
                - 带权重的去重
                """);

        // 7. Spring Data Redis
        System.out.println("=".repeat(60));
        System.out.println("【7. Spring Data Redis 集成】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-data-redis</artifactId>
                </dependency>

                2. 配置

                spring:
                  data:
                    redis:
                      host: localhost
                      port: 6379
                      password:
                      lettuce:
                        pool:
                          max-active: 8
                          max-idle: 8
                          min-idle: 0

                3. 使用 RedisTemplate

                @Service
                @RequiredArgsConstructor
                public class RedisService {

                    private final StringRedisTemplate redisTemplate;

                    // String 操作
                    public void setString(String key, String value, long timeout) {
                        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
                    }

                    // Hash 操作
                    public void setHash(String key, String field, String value) {
                        redisTemplate.opsForHash().put(key, field, value);
                    }

                    // List 操作
                    public void pushList(String key, String value) {
                        redisTemplate.opsForList().rightPush(key, value);
                    }

                    // Set 操作
                    public void addSet(String key, String... values) {
                        redisTemplate.opsForSet().add(key, values);
                    }

                    // ZSet 操作
                    public void addZSet(String key, String value, double score) {
                        redisTemplate.opsForZSet().add(key, value, score);
                    }
                }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 String: 通用缓存、计数器");
        System.out.println("💡 Hash: 对象存储、购物车");
        System.out.println("💡 List: 消息队列、时间线");
        System.out.println("💡 Set: 去重、交集并集");
        System.out.println("💡 ZSet: 排行榜、延迟队列");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. String:
 * - SET/GET/SETNX/INCR
 * - 缓存、计数器
 * 
 * 2. Hash:
 * - HSET/HGET/HGETALL
 * - 对象存储
 * 
 * 3. List:
 * - LPUSH/RPUSH/LPOP/RPOP
 * - 队列、栈
 * 
 * 4. Set:
 * - SADD/SINTER/SUNION
 * - 去重、集合运算
 * 
 * 5. ZSet:
 * - ZADD/ZRANGE/ZRANK
 * - 排行榜
 */
