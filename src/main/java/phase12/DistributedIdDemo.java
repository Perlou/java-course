package phase12;

/**
 * Phase 12 - Lesson 8: 分布式 ID 生成
 * 
 * 🎯 学习目标:
 * 1. 理解分布式 ID 的需求
 * 2. 掌握常见 ID 生成方案
 * 3. 学会雪花算法实现
 */
public class DistributedIdDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 12 - Lesson 8: 分布式 ID 生成");
        System.out.println("=".repeat(60));

        // 1. 为什么需要分布式 ID
        System.out.println("\n【1. 为什么需要分布式 ID】");
        System.out.println("""
                单机数据库:
                - 自增 ID: AUTO_INCREMENT，简单高效

                分布式系统:
                ┌─────────────────────────────────────────────────────────┐
                │  ┌─────────┐  ┌─────────┐  ┌─────────┐                │
                │  │ 订单服务│  │ 订单服务│  │ 订单服务│                │
                │  │ 实例 1  │  │ 实例 2  │  │ 实例 3  │                │
                │  └─────────┘  └─────────┘  └─────────┘                │
                │       │            │            │                      │
                │       ▼            ▼            ▼                      │
                │  ┌─────────┐  ┌─────────┐  ┌─────────┐                │
                │  │ DB 分片1│  │ DB 分片2│  │ DB 分片3│                │
                │  └─────────┘  └─────────┘  └─────────┘                │
                │                                                         │
                │  问题: 每个分片的自增 ID 会冲突！                       │
                └─────────────────────────────────────────────────────────┘

                分布式 ID 要求:
                ┌──────────────────┬──────────────────────────────────────┐
                │       要求       │             说明                      │
                ├──────────────────┼──────────────────────────────────────┤
                │ 全局唯一         │ 跨实例、跨数据库不冲突               │
                │ 趋势递增         │ 有利于数据库索引                     │
                │ 高可用           │ ID 生成服务不能单点故障              │
                │ 高性能           │ 每秒可生成大量 ID                    │
                │ 信息安全         │ ID 不应暴露业务信息                  │
                └──────────────────┴──────────────────────────────────────┘
                """);

        // 2. UUID
        System.out.println("=".repeat(60));
        System.out.println("【2. UUID】");
        System.out.println("""
                UUID = 128 位随机数

                String uuid = UUID.randomUUID().toString();
                // 示例: 550e8400-e29b-41d4-a716-446655440000

                优点:
                ✅ 本地生成，无网络开销
                ✅ 全局唯一
                ✅ 简单实现

                缺点:
                ❌ 无序，不适合作为主键
                ❌ 36 字符太长，占用空间
                ❌ 索引效率差 (B+ 树频繁分裂)
                ❌ 无可读性

                适用场景:
                - 文件名、请求追踪 ID
                - 对顺序性无要求的场景
                """);

        // 3. 数据库自增
        System.out.println("=".repeat(60));
        System.out.println("【3. 数据库自增方案】");
        System.out.println("""
                方案 1: 单独的 ID 生成表

                CREATE TABLE id_generator (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    stub CHAR(1) NOT NULL DEFAULT 'a',
                    UNIQUE KEY uk_stub (stub)
                );

                // 获取 ID
                REPLACE INTO id_generator (stub) VALUES ('a');
                SELECT LAST_INSERT_ID();

                缺点: 单点问题，性能瓶颈

                方案 2: 号段模式

                CREATE TABLE id_segment (
                    biz_type VARCHAR(50) PRIMARY KEY,
                    max_id BIGINT NOT NULL,
                    step INT NOT NULL DEFAULT 1000,
                    version INT NOT NULL,
                    updated_at DATETIME
                );

                // 每次获取一个号段
                @Transactional
                public IdSegment getNextSegment(String bizType) {
                    IdSegment segment = mapper.selectByBizType(bizType);

                    long newMaxId = segment.getMaxId() + segment.getStep();
                    int rows = mapper.updateMaxId(bizType, newMaxId, segment.getVersion());

                    if (rows == 0) {
                        throw new OptimisticLockException();
                    }

                    return new IdSegment(segment.getMaxId() + 1, newMaxId);
                }

                内存中分配:
                ┌─────────────────────────────────────────────────────────┐
                │  号段缓存: [1001, 2000]                                 │
                │  当前值: 1001 → 1002 → 1003 → ... → 1999 → 2000        │
                │  用完再申请下一个号段                                    │
                └─────────────────────────────────────────────────────────┘

                优点: 减少数据库访问
                实现: 美团 Leaf-segment
                """);

        // 4. 雪花算法
        System.out.println("=".repeat(60));
        System.out.println("【4. 雪花算法 (Snowflake)】");
        System.out.println("""
                Twitter 开源的分布式 ID 生成算法

                结构 (64 位):
                ┌─────────────────────────────────────────────────────────┐
                │ 0 │ 41位时间戳 │ 10位机器ID │ 12位序列号 │              │
                │   │           │   (5+5)    │            │              │
                └─────────────────────────────────────────────────────────┘

                - 1 位: 符号位，始终为 0
                - 41 位: 时间戳毫秒数 (约 69 年)
                - 10 位: 机器 ID (5位数据中心 + 5位机器)
                - 12 位: 序列号 (每毫秒 4096 个)

                特点:
                ✅ 趋势递增 (毫秒级有序)
                ✅ 高性能 (本地生成)
                ✅ 长度短 (64 位 long)

                实现:
                public class SnowflakeIdGenerator {
                    private final long epoch = 1609459200000L;  // 2021-01-01
                    private final long workerIdBits = 5L;
                    private final long datacenterIdBits = 5L;
                    private final long sequenceBits = 12L;

                    private final long maxWorkerId = ~(-1L << workerIdBits);
                    private final long maxDatacenterId = ~(-1L << datacenterIdBits);

                    private final long workerIdShift = sequenceBits;
                    private final long datacenterIdShift = sequenceBits + workerIdBits;
                    private final long timestampShift = sequenceBits + workerIdBits + datacenterIdBits;
                    private final long sequenceMask = ~(-1L << sequenceBits);

                    private long workerId;
                    private long datacenterId;
                    private long sequence = 0L;
                    private long lastTimestamp = -1L;

                    public synchronized long nextId() {
                        long timestamp = System.currentTimeMillis();

                        if (timestamp < lastTimestamp) {
                            throw new RuntimeException("时钟回拨");
                        }

                        if (timestamp == lastTimestamp) {
                            sequence = (sequence + 1) & sequenceMask;
                            if (sequence == 0) {
                                timestamp = waitNextMillis(lastTimestamp);
                            }
                        } else {
                            sequence = 0L;
                        }

                        lastTimestamp = timestamp;

                        return ((timestamp - epoch) << timestampShift) |
                               (datacenterId << datacenterIdShift) |
                               (workerId << workerIdShift) |
                               sequence;
                    }
                }
                """);

        // 5. 常见实现
        System.out.println("=".repeat(60));
        System.out.println("【5. 业界常见实现】");
        System.out.println("""
                1. 美团 Leaf

                支持两种模式:
                - Segment: 号段模式，双 Buffer 预加载
                - Snowflake: 雪花算法，ZK 分配 workerId

                <dependency>
                    <groupId>com.sankuai.inf.leaf</groupId>
                    <artifactId>leaf-boot-starter</artifactId>
                </dependency>

                2. 百度 UidGenerator

                改进版雪花算法:
                - 借用未来时间解决时钟回拨
                - RingBuffer 缓存提高性能

                3. Redis INCR

                // 简单高效
                Long id = redis.incr("order:id");

                // 或者结合日期
                String key = "order:id:" + LocalDate.now();
                Long seq = redis.incr(key);
                String orderId = "ORD" + LocalDate.now() + String.format("%06d", seq);

                4. 数据库分段 + MyBatis-Plus

                @TableId(type = IdType.ASSIGN_ID)  // 雪花算法
                private Long id;

                5. Hutool 工具

                <dependency>
                    <groupId>cn.hutool</groupId>
                    <artifactId>hutool-core</artifactId>
                </dependency>

                // 雪花算法
                Snowflake snowflake = IdUtil.getSnowflake(1, 1);
                long id = snowflake.nextId();

                // NanoId (短 ID)
                String nanoId = IdUtil.nanoId();  // Y1_nQpY8dcB
                """);

        // 6. 方案选择
        System.out.println("=".repeat(60));
        System.out.println("【6. 方案选择建议】");
        System.out.println("""
                ┌──────────────────┬──────────────────────────────────────┐
                │       场景       │             推荐方案                  │
                ├──────────────────┼──────────────────────────────────────┤
                │ 单机应用         │ 数据库自增                           │
                │ 分布式小规模     │ Redis INCR / Hutool 雪花             │
                │ 分布式大规模     │ 美团 Leaf / 百度 UidGenerator        │
                │ 订单号生成       │ 日期 + 序号 + 随机                   │
                │ 文件名           │ UUID                                 │
                └──────────────────┴──────────────────────────────────────┘

                订单号生成示例:

                public String generateOrderNo() {
                    // 格式: 年月日时分秒 + 3位机器码 + 4位序列号
                    // 例: 20241225093045001-0001
                    String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    String machineId = String.format("%03d", getMachineId());
                    String sequence = String.format("%04d", redis.incr("order:seq:" + timestamp));

                    return timestamp + machineId + "-" + sequence;
                }

                注意事项:
                ⚠️ 雪花算法依赖时钟，需处理时钟回拨
                ⚠️ 分布式需要保证 workerId 唯一
                ⚠️ 订单号不要暴露业务量信息
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 雪花算法是主流方案");
        System.out.println("💡 MyBatis-Plus 内置雪花 ID");
        System.out.println("💡 订单号建议: 时间 + 机器 + 序列");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 分布式 ID 要求:
 * - 全局唯一
 * - 趋势递增
 * - 高可用
 * 
 * 2. 常见方案:
 * - UUID (无序)
 * - 号段模式 (Leaf)
 * - 雪花算法 (Snowflake)
 * 
 * 3. 雪花算法:
 * - 时间戳 + 机器 ID + 序列号
 * - 趋势递增，本地生成
 * 
 * 4. 工具:
 * - Hutool IdUtil
 * - MyBatis-Plus ASSIGN_ID
 */
