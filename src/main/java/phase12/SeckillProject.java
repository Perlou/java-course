package phase12;

import java.util.List;

/**
 * Phase 12 - 实战项目: 秒杀系统
 * 
 * 🎯 项目目标:
 * 1. 综合运用 Redis、MQ、分布式锁
 * 2. 解决高并发库存扣减问题
 * 3. 实现稳定可靠的秒杀功能
 */
public class SeckillProject {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - 实战项目: 秒杀系统");
        System.out.println("=".repeat(60));

        // 1. 项目概述
        System.out.println("\n【1. 项目概述】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                    秒杀系统架构                          │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  ┌─────────┐    ┌─────────┐    ┌─────────┐            │
                │  │ 用户请求│───▶│ Gateway │───▶│ 限流    │            │
                │  └─────────┘    └─────────┘    └─────────┘            │
                │                                     │                   │
                │                                     ▼                   │
                │                              ┌─────────┐               │
                │                              │秒杀服务 │               │
                │                              └─────────┘               │
                │                                     │                   │
                │                    ┌────────────────┼────────────────┐ │
                │                    ▼                ▼                ▼ │
                │              ┌─────────┐    ┌─────────┐    ┌─────────┐│
                │              │  Redis  │    │   MQ    │    │  MySQL  ││
                │              │库存预扣 │    │异步下单 │    │数据持久 ││
                │              └─────────┘    └─────────┘    └─────────┘│
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                核心挑战:
                ┌──────────────────┬──────────────────────────────────────┐
                │       问题       │             解决方案                  │
                ├──────────────────┼──────────────────────────────────────┤
                │ 高并发请求       │ 限流、降级、熔断                     │
                │ 超卖问题         │ Redis 预扣 + 分布式锁               │
                │ 数据库压力       │ 异步下单、MQ 削峰                   │
                │ 用户体验         │ 库存预热、前端限流                   │
                └──────────────────┴──────────────────────────────────────┘
                """);

        // 2. 系统设计
        System.out.println("=".repeat(60));
        System.out.println("【2. 系统设计】");
        System.out.println("""
                秒杀流程:

                ┌───────────────────────────────────────────────────────────┐
                │  1. 秒杀开始前:                                          │
                │     - 将商品库存预热到 Redis                             │
                │     - 生成秒杀令牌                                       │
                │                                                           │
                │  2. 用户请求:                                            │
                │     - 验证秒杀资格 (登录、活动时间)                      │
                │     - 检查是否已购买 (防止重复)                          │
                │     - Redis 预扣库存 (DECR)                              │
                │                                                           │
                │  3. 预扣成功:                                            │
                │     - 发送 MQ 消息，异步创建订单                         │
                │     - 返回 "排队中"                                      │
                │                                                           │
                │  4. 订单服务消费:                                        │
                │     - 扣减数据库库存                                     │
                │     - 创建订单                                           │
                │                                                           │
                │  5. 用户轮询:                                            │
                │     - 查询订单状态                                       │
                │     - 显示结果                                           │
                └───────────────────────────────────────────────────────────┘

                数据库设计:

                -- 秒杀商品表
                CREATE TABLE seckill_goods (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    goods_id BIGINT NOT NULL,
                    goods_name VARCHAR(100) NOT NULL,
                    seckill_price DECIMAL(10,2) NOT NULL,
                    stock_count INT NOT NULL,
                    start_time DATETIME NOT NULL,
                    end_time DATETIME NOT NULL
                );

                -- 秒杀订单表
                CREATE TABLE seckill_order (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    goods_id BIGINT NOT NULL,
                    order_id BIGINT NOT NULL,
                    status TINYINT DEFAULT 0,  -- 0排队 1成功 2失败
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY uk_user_goods (user_id, goods_id)  -- 一人一单
                );
                """);

        // 3. 库存预扣
        System.out.println("=".repeat(60));
        System.out.println("【3. Redis 库存预扣】");
        System.out.println("""
                1. 库存预热

                @Component
                @RequiredArgsConstructor
                public class SeckillInitializer implements CommandLineRunner {

                    private final SeckillGoodsMapper goodsMapper;
                    private final StringRedisTemplate redis;

                    @Override
                    public void run(String... args) {
                        List<SeckillGoods> goods = goodsMapper.selectAll();
                        for (SeckillGoods g : goods) {
                            // 缓存库存
                            redis.opsForValue().set(
                                "seckill:stock:" + g.getId(),
                                String.valueOf(g.getStockCount())
                            );
                        }
                    }
                }

                2. Lua 脚本原子扣减

                -- seckill.lua
                local key = KEYS[1]
                local userId = ARGV[1]

                -- 检查是否已购买
                local bought = redis.call('SISMEMBER', 'seckill:bought:' .. key, userId)
                if bought == 1 then
                    return -1  -- 已购买
                end

                -- 检查库存
                local stock = tonumber(redis.call('GET', 'seckill:stock:' .. key))
                if stock <= 0 then
                    return 0  -- 库存不足
                end

                -- 扣减库存
                redis.call('DECR', 'seckill:stock:' .. key)
                -- 记录已购买
                redis.call('SADD', 'seckill:bought:' .. key, userId)

                return 1  -- 成功

                3. 服务调用

                @Service
                @RequiredArgsConstructor
                public class SeckillService {

                    private final StringRedisTemplate redis;
                    private final RocketMQTemplate mq;

                    private static final String LUA_SCRIPT = """
                        local stock = tonumber(redis.call('GET', KEYS[1]))
                        if stock <= 0 then return 0 end
                        local bought = redis.call('SISMEMBER', KEYS[2], ARGV[1])
                        if bought == 1 then return -1 end
                        redis.call('DECR', KEYS[1])
                        redis.call('SADD', KEYS[2], ARGV[1])
                        return 1
                        """;

    public Result doSeckill(Long userId, Long goodsId) {
                        // 执行 Lua 脚本
                        Long result = redis.execute(
                            new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
                            List.of("seckill:stock:" + goodsId, "seckill:bought:" + goodsId),
                            String.valueOf(userId)
                        );

                        if (result == 0) {
                            return Result.fail("库存不足");
                        }
                        if (result == -1) {
                            return Result.fail("请勿重复购买");
                        }

                        // 发送下单消息
                        SeckillMessage msg = new SeckillMessage(userId, goodsId);
                        mq.asyncSend("seckill-topic", msg, new SendCallback() { ... });

                        return Result.success("排队中，请稍后查询");
                    }
}""");

// 4. 异步下单
System.out.println("=".repeat(60));System.out.println("【4. MQ 异步下单】");System.out.println("""
                @Service
                @RocketMQMessageListener(
                    topic = "seckill-topic",
                    consumerGroup = "seckill-consumer-group"
                )
                public class SeckillConsumer implements RocketMQListener<SeckillMessage> {

                    @Autowired
                    private SeckillGoodsMapper goodsMapper;
                    @Autowired
                    private OrderMapper orderMapper;
                    @Autowired
                    private SeckillOrderMapper seckillOrderMapper;

                    @Override
                    @Transactional
                    public void onMessage(SeckillMessage message) {
                        Long userId = message.getUserId();
                        Long goodsId = message.getGoodsId();

                        // 1. 再次检查重复
                        SeckillOrder existing = seckillOrderMapper.selectByUserAndGoods(userId, goodsId);
                        if (existing != null) {
                            return;
                        }

                        // 2. 扣减数据库库存 (乐观锁)
                        int rows = goodsMapper.deductStock(goodsId);
                        if (rows == 0) {
                            // 库存已扣完，更新秒杀订单状态
                            seckillOrderMapper.updateStatus(userId, goodsId, 2);  // 失败
                            return;
                        }

                        // 3. 创建订单
                        Order order = new Order();
                        order.setUserId(userId);
                        order.setGoodsId(goodsId);
                        order.setStatus(1);  // 待支付
                        orderMapper.insert(order);

                        // 4. 记录秒杀订单
                        SeckillOrder seckillOrder = new SeckillOrder();
                        seckillOrder.setUserId(userId);
                        seckillOrder.setGoodsId(goodsId);
                        seckillOrder.setOrderId(order.getId());
                        seckillOrder.setStatus(1);  // 成功
                        seckillOrderMapper.insert(seckillOrder);
                    }
                }

                库存扣减 SQL (乐观锁):

                <update id="deductStock">
                    UPDATE seckill_goods
                    SET stock_count = stock_count - 1
                    WHERE id = #{goodsId} AND stock_count > 0
                </update>
                """);

// 5. 限流和降级
System.out.println("=".repeat(60));System.out.println("【5. 限流和降级】");System.out.println("""
                1. Gateway 限流

                spring:
                  cloud:
                    gateway:
                      routes:
                        - id: seckill-route
                          uri: lb://seckill-service
                          predicates:
                            - Path=/api/seckill/**
                          filters:
                            - name: RequestRateLimiter
                              args:
                                redis-rate-limiter.replenishRate: 100  # 每秒100个
                                redis-rate-limiter.burstCapacity: 200

                2. Sentinel 限流

                @SentinelResource(
                    value = "doSeckill",
                    blockHandler = "seckillBlockHandler",
                    fallback = "seckillFallback"
                )
                public Result doSeckill(Long userId, Long goodsId) { ... }

                // 限流处理
                public Result seckillBlockHandler(Long userId, Long goodsId, BlockException e) {
                    return Result.fail("系统繁忙，请稍后再试");
                }

                // 降级处理
                public Result seckillFallback(Long userId, Long goodsId, Throwable t) {
                    return Result.fail("服务暂不可用");
                }

                3. 前端限流
                - 按钮点击后禁用
                - 倒计时结束才能点击
                - 验证码/滑动验证
                """);

// 6. 项目结构
System.out.println("=".repeat(60));System.out.println("【6. 项目结构】");System.out.println("""
                seckill-system/
                ├── seckill-gateway/           # 网关服务
                │   └── 限流配置
                ├── seckill-service/           # 秒杀服务
                │   ├── controller/
                │   │   └── SeckillController.java
                │   ├── service/
                │   │   ├── SeckillService.java
                │   │   └── SeckillInitializer.java
                │   ├── mq/
                │   │   └── SeckillConsumer.java
                │   ├── mapper/
                │   │   ├── SeckillGoodsMapper.java
                │   │   └── SeckillOrderMapper.java
                │   └── config/
                │       └── SentinelConfig.java
                ├── seckill-common/            # 公共模块
                │   ├── dto/
                │   │   └── SeckillMessage.java
                │   └── result/
                │       └── Result.java
                └── resources/
                    ├── lua/
                    │   └── seckill.lua
                    └── application.yml
                """);

// 7. 开发步骤
System.out.println("=".repeat(60));System.out.println("【7. 开发建议】");System.out.println("""
                开发顺序:
                ┌─────┬───────────────────────────────────────────────────┐
                │ Day │ 任务                                              │
                ├─────┼───────────────────────────────────────────────────┤
                │  1  │ 项目搭建，数据库设计                              │
                │  2  │ 秒杀商品管理 (CRUD)                               │
                │  3  │ Redis 库存预热、Lua 脚本                          │
                │  4  │ 秒杀接口、限流配置                                │
                │  5  │ MQ 异步下单                                       │
                │  6  │ 订单查询、状态同步                                │
                │  7  │ 压测优化、问题修复                                │
                └─────┴───────────────────────────────────────────────────┘

                性能优化:
                ✅ Redis 本地内存库存标记 (JVM 内存标记库存售罄)
                ✅ 线程池异步处理
                ✅ 热点数据预加载
                ✅ 接口响应压缩

                测试工具:
                - JMeter 压力测试
                - 模拟 1000 并发
                - 观察库存扣减正确性
                """);

System.out.println("\n"+"=".repeat(60));System.out.println("💡 核心: Redis 预扣 + MQ 异步 + 限流保护");System.out.println("💡 Lua 脚本保证库存扣减原子性");System.out.println("💡 多重检查防止超卖和重复购买");System.out.println("=".repeat(60));}}

/*
 * 📚 项目知识点:
 * 
 * 1. 高并发方案:
 * - Redis 缓存库存
 * - MQ 异步下单
 * - 限流降级
 * 
 * 2. 防超卖:
 * - Lua 脚本原子扣减
 * - 数据库乐观锁
 * 
 * 3. 一人一单:
 * - Redis Set 记录
 * - 数据库唯一索引
 * 
 * 4. 架构:
 * - 网关限流
 * - 微服务解耦
 */
