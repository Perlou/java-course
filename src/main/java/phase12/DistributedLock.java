package phase12;

/**
 * Phase 12 - Lesson 3: 分布式锁
 * 
 * 🎯 学习目标:
 * 1. 理解分布式锁的作用
 * 2. 掌握 Redis 分布式锁实现
 * 3. 学会使用 Redisson
 */
public class DistributedLock {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 3: 分布式锁");
        System.out.println("=".repeat(60));

        // 1. 为什么需要分布式锁
        System.out.println("\n【1. 为什么需要分布式锁】");
        System.out.println("""
                单机锁 (synchronized/ReentrantLock) 的局限:

                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  ┌─────────┐      ┌─────────┐      ┌─────────┐        │
                │  │ Server1 │      │ Server2 │      │ Server3 │        │
                │  │ 锁 A    │      │ 锁 A    │      │ 锁 A    │        │
                │  └─────────┘      └─────────┘      └─────────┘        │
                │       │               │               │                │
                │       └───────────────┼───────────────┘                │
                │                       ▼                                │
                │                  ┌─────────┐                           │
                │                  │  MySQL  │ ← 多个实例同时操作       │
                │                  └─────────┘                           │
                │                                                         │
                │  问题: 每个 JVM 的锁是独立的，无法跨进程互斥           │
                └─────────────────────────────────────────────────────────┘

                分布式锁:
                ┌─────────────────────────────────────────────────────────┐
                │                     ┌─────────┐                        │
                │                     │  Redis  │ ← 中心化锁服务         │
                │                     └─────────┘                        │
                │                    /     |     \\                       │
                │  ┌─────────┐  ┌─────────┐  ┌─────────┐                │
                │  │ Server1 │  │ Server2 │  │ Server3 │                │
                │  │ 获取锁  │  │  等待   │  │  等待   │                │
                │  └─────────┘  └─────────┘  └─────────┘                │
                └─────────────────────────────────────────────────────────┘

                典型场景:
                - 库存扣减
                - 订单创建
                - 定时任务 (防止多节点重复执行)
                - 抢红包/秒杀
                """);

        // 2. Redis 分布式锁基础
        System.out.println("=".repeat(60));
        System.out.println("【2. Redis 分布式锁基础实现】");
        System.out.println("""
                核心命令: SET key value NX EX seconds

                NX: 只有 key 不存在时才设置成功
                EX: 设置过期时间 (防止死锁)

                基础实现:

                public class RedisLock {
                    private StringRedisTemplate redis;
                    private String lockKey;
                    private String lockValue;

                    public boolean tryLock(long expireSeconds) {
                        lockValue = UUID.randomUUID().toString();
                        Boolean success = redis.opsForValue()
                            .setIfAbsent(lockKey, lockValue, expireSeconds, TimeUnit.SECONDS);
                        return Boolean.TRUE.equals(success);
                    }

                    public void unlock() {
                        // ⚠️ 必须验证是自己的锁
                        String value = redis.opsForValue().get(lockKey);
                        if (lockValue.equals(value)) {
                            redis.delete(lockKey);
                        }
                    }
                }

                使用示例:
                RedisLock lock = new RedisLock(redis, "lock:order:123");
                if (lock.tryLock(30)) {
                    try {
                        // 业务逻辑
                        deductStock();
                        createOrder();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    throw new BusinessException("获取锁失败");
                }
                """);

        // 3. 分布式锁问题
        System.out.println("=".repeat(60));
        System.out.println("【3. 分布式锁常见问题】");
        System.out.println("""
                问题 1: 误删他人的锁

                +---------------------------------------------------------+
                |  线程 A:                    线程 B:                     |
                |  1. 获取锁 (30s)                                        |
                |  2. 执行业务...                                         |
                |     (业务耗时超过 30s)                                  |
                |  3. 锁过期被释放                                        |
                |                             4. 获取锁成功               |
                |  5. 业务完成，删除锁                                    |
                |     [X] 删除了 B 的锁!                                  |
                +---------------------------------------------------------+

                解决: 删除前验证是否是自己的锁 (UUID)

                // 使用 Lua 脚本保证原子性
                String script = '''
                    if redis.call('get', KEYS[1]) == ARGV[1] then
                        return redis.call('del', KEYS[1])
                    else
                        return 0
                    end
                    ''';
                redis.execute(new DefaultRedisScript<>(script, Long.class),
                    List.of(lockKey), lockValue);

                问题 2: 锁过期但业务未完成

                解决: 锁续期 (Watch Dog 看门狗)
                - 后台线程定期检查锁是否即将过期
                - 如果业务未完成，自动续期

                问题 3: Redis 主从切换时锁丢失

                +---------------------------------------------------------+
                |  1. 客户端在 Master 获取锁                              |
                |  2. Master 宕机，锁还未同步到 Slave                     |
                |  3. Slave 升级为 Master                                 |
                |  4. 另一个客户端也获取到锁                              |
                |  [X] 两个客户端同时持有锁!                              |
                +---------------------------------------------------------+

                解决: RedLock 算法 (多实例加锁)
                """);

        // 4. Redisson 使用
        System.out.println("=".repeat(60));
        System.out.println("【4. Redisson 分布式锁】");
        System.out.println("""
                Redisson = Redis 的 Java 客户端 + 分布式工具

                添加依赖:
                <dependency>
                    <groupId>org.redisson</groupId>
                    <artifactId>redisson-spring-boot-starter</artifactId>
                    <version>3.24.3</version>
                </dependency>

                配置:
                spring:
                  redis:
                    host: localhost
                    port: 6379

                基础使用:

                @Service
                @RequiredArgsConstructor
                public class OrderService {

                    private final RedissonClient redisson;

                    public void createOrder(Long productId) {
                        // 1. 获取锁
                        RLock lock = redisson.getLock("lock:product:" + productId);

                        try {
                            // 2. 尝试加锁 (等待3秒, 持有10秒)
                            boolean locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
                            if (!locked) {
                                throw new BusinessException("系统繁忙");
                            }

                            // 3. 执行业务
                            deductStock(productId);
                            saveOrder(productId);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            // 4. 释放锁
                            if (lock.isHeldByCurrentThread()) {
                                lock.unlock();
                            }
                        }
                    }
                }

                Redisson 锁特性:
                ✅ 自动续期 (Watch Dog)
                ✅ 可重入锁
                ✅ 公平锁
                ✅ 读写锁
                ✅ 红锁 (RedLock)
                """);

        // 5. Redisson 高级功能
        System.out.println("=".repeat(60));
        System.out.println("【5. Redisson 高级功能】");
        System.out.println("""
                1. 可重入锁

                RLock lock = redisson.getLock("myLock");
                lock.lock();
                try {
                    // 同一线程可以多次获取锁
                    lock.lock();
                    try {
                        // 业务逻辑
                    } finally {
                        lock.unlock();
                    }
                } finally {
                    lock.unlock();
                }

                2. 公平锁 (FIFO)

                RLock fairLock = redisson.getFairLock("fairLock");
                fairLock.lock();
                // 按请求顺序获取锁

                3. 读写锁

                RReadWriteLock rwLock = redisson.getReadWriteLock("rwLock");

                // 读锁 (共享)
                RLock readLock = rwLock.readLock();
                readLock.lock();
                try {
                    // 读操作
                } finally {
                    readLock.unlock();
                }

                // 写锁 (独占)
                RLock writeLock = rwLock.writeLock();
                writeLock.lock();
                try {
                    // 写操作
                } finally {
                    writeLock.unlock();
                }

                4. 信号量

                RSemaphore semaphore = redisson.getSemaphore("semaphore");
                semaphore.trySetPermits(3);  // 设置 3 个许可

                semaphore.acquire();  // 获取许可
                try {
                    // 限制同时只有 3 个线程执行
                } finally {
                    semaphore.release();
                }

                5. 红锁 (多节点)

                RLock lock1 = redisson1.getLock("lock");
                RLock lock2 = redisson2.getLock("lock");
                RLock lock3 = redisson3.getLock("lock");

                RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
                redLock.lock();
                // 需要在多数节点获取锁才算成功
                """);

        // 6. 分布式锁最佳实践
        System.out.println("=".repeat(60));
        System.out.println("【6. 分布式锁最佳实践】");
        System.out.println("""
                1. 锁的粒度要小
                // 不好
                lock("order")  // 锁整个订单模块

                // 好
                lock("order:" + orderId)  // 锁单个订单

                2. 锁超时时间设置
                // 根据业务执行时间估算
                // 太短: 业务未完成锁就过期
                // 太长: 异常时锁释放慢

                3. 异常处理
                RLock lock = redisson.getLock("lock");
                try {
                    if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                        try {
                            // 业务逻辑
                        } finally {
                            lock.unlock();  // 必须在 finally 中释放
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                4. 避免死锁
                - 使用 tryLock 带超时
                - 设置锁的过期时间
                - 按固定顺序获取多个锁

                5. 使用 AOP 简化
                @Target(ElementType.METHOD)
                @Retention(RetentionPolicy.RUNTIME)
                public @interface DistributedLock {
                    String key();
                    long waitTime() default 3;
                    long leaseTime() default 30;
                }

                @Around("@annotation(distributedLock)")
                public Object around(ProceedingJoinPoint pjp, DistributedLock distributedLock) {
                    RLock lock = redisson.getLock(distributedLock.key());
                    try {
                        if (lock.tryLock(distributedLock.waitTime(),
                                        distributedLock.leaseTime(), TimeUnit.SECONDS)) {
                            try {
                                return pjp.proceed();
                            } finally {
                                lock.unlock();
                            }
                        }
                        throw new BusinessException("获取锁失败");
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }

                // 使用
                @DistributedLock(key = "'lock:product:' + #productId")
                public void deductStock(Long productId) { ... }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 分布式锁核心: SET NX EX + Lua 脚本");
        System.out.println("💡 推荐使用 Redisson，自动续期 + 可重入");
        System.out.println("💡 锁粒度要小，超时时间根据业务设置");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 基础实现:
 * - SET NX EX
 * - Lua 脚本保证原子性
 * 
 * 2. 常见问题:
 * - 误删他人锁
 * - 锁过期
 * - 主从切换
 * 
 * 3. Redisson:
 * - 自动续期
 * - 可重入、公平、读写锁
 * - 红锁
 * 
 * 4. 最佳实践:
 * - 锁粒度小
 * - finally 释放
 * - AOP 简化
 */
