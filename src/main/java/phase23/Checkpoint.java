package phase23;

/**
 * Flink Checkpoint 检查点机制
 * 
 * Checkpoint 是 Flink 容错的核心机制，基于分布式快照算法实现。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class Checkpoint {

    /**
     * ========================================
     * 第一部分：Checkpoint 概述
     * ========================================
     */
    public static void explainCheckpointConcept() {
        System.out.println("=== Checkpoint 概述 ===");
        System.out.println();

        System.out.println("【什么是 Checkpoint】");
        System.out.println("  Checkpoint 是作业状态的一致性快照");
        System.out.println("  用于故障恢复时恢复作业状态");
        System.out.println();

        System.out.println("【Checkpoint vs Savepoint】");
        System.out.println("┌─────────────────┬─────────────────────┬────────────────────┐");
        System.out.println("│                 │     Checkpoint      │     Savepoint      │");
        System.out.println("├─────────────────┼─────────────────────┼────────────────────┤");
        System.out.println("│ 触发方式        │ 自动周期性          │ 手动触发           │");
        System.out.println("│ 主要目的        │ 故障恢复            │ 作业迁移/升级      │");
        System.out.println("│ 存储格式        │ 后端相关            │ 标准化格式         │");
        System.out.println("│ 生命周期        │ 作业停止后清理      │ 手动管理           │");
        System.out.println("│ 使用场景        │ 自动容错            │ A/B测试、版本回退  │");
        System.out.println("└─────────────────┴─────────────────────┴────────────────────┘");
    }

    /**
     * ========================================
     * 第二部分：分布式快照算法
     * ========================================
     */
    public static void explainSnapshotAlgorithm() {
        System.out.println("=== 分布式快照算法 (Chandy-Lamport) ===");
        System.out.println();

        System.out.println("【核心思想 - Barrier】");
        System.out.println("  Barrier 是特殊标记，将数据流切分为两部分:");
        System.out.println("  • Barrier 之前的数据: 属于当前 Checkpoint");
        System.out.println("  • Barrier 之后的数据: 属于下次 Checkpoint");
        System.out.println();

        System.out.println("【Checkpoint 流程】");
        System.out.println("```");
        System.out.println("步骤1: JobManager 触发 Checkpoint");
        System.out.println("       向所有 Source 发送 Barrier");
        System.out.println();
        System.out.println("步骤2: Source 接收 Barrier");
        System.out.println("       • 快照自己的状态");
        System.out.println("       • 向下游发送 Barrier");
        System.out.println();
        System.out.println("       Source ──[data]──[barrier]──[data]──► Operator");
        System.out.println();
        System.out.println("步骤3: 算子接收 Barrier");
        System.out.println("       • 等待所有输入的 Barrier 对齐");
        System.out.println("       • 快照自己的状态");
        System.out.println("       • 向下游发送 Barrier");
        System.out.println();
        System.out.println("步骤4: Sink 接收 Barrier");
        System.out.println("       • 快照状态");
        System.out.println("       • 向 JobManager 确认完成");
        System.out.println();
        System.out.println("步骤5: JobManager 收到所有确认");
        System.out.println("       Checkpoint 完成!");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：Barrier 对齐
     * ========================================
     */
    public static void explainBarrierAlignment() {
        System.out.println("=== Barrier 对齐 ===");
        System.out.println();

        System.out.println("【多输入场景的问题】");
        System.out.println("  当算子有多个输入时，Barrier 可能不同时到达");
        System.out.println();
        System.out.println("```");
        System.out.println("  Input1: ──data──data──[barrier]──data──►");
        System.out.println("                            ↓");
        System.out.println("  Operator               等待对齐");
        System.out.println("                            ↑");
        System.out.println("  Input2: ──data──data──data──data──[barrier]──►");
        System.out.println("```");
        System.out.println();

        System.out.println("【对齐模式 (Exactly-Once)】");
        System.out.println("  • 先到达 Barrier 的输入：后续数据缓存，不处理");
        System.out.println("  • 等待所有输入的 Barrier 到齐");
        System.out.println("  • 做快照，发送 Barrier，处理缓存数据");
        System.out.println("  • 保证 Exactly-Once，但增加延迟");
        System.out.println();

        System.out.println("【非对齐模式 (At-Least-Once)】");
        System.out.println("  • 不等待，直接处理数据");
        System.out.println("  • 延迟低，但可能重复处理数据");
        System.out.println();

        System.out.println("【Unaligned Checkpoint (Flink 1.11+)】");
        System.out.println("  • 不阻塞数据，将未处理数据一起快照");
        System.out.println("  • 保证 Exactly-Once，降低反压影响");
        System.out.println("```java");
        System.out.println("env.getCheckpointConfig().enableUnalignedCheckpoints();");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：Checkpoint 配置
     * ========================================
     */
    public static void explainCheckpointConfig() {
        System.out.println("=== Checkpoint 配置 ===");
        System.out.println();

        System.out.println("【基本配置】");
        System.out.println("```java");
        System.out.println("StreamExecutionEnvironment env = ");
        System.out.println("    StreamExecutionEnvironment.getExecutionEnvironment();");
        System.out.println();
        System.out.println("// 启用 Checkpoint，间隔 30 秒");
        System.out.println("env.enableCheckpointing(30000);");
        System.out.println();
        System.out.println("CheckpointConfig config = env.getCheckpointConfig();");
        System.out.println();
        System.out.println("// 设置 Exactly-Once 模式");
        System.out.println("config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);");
        System.out.println();
        System.out.println("// Checkpoint 超时时间");
        System.out.println("config.setCheckpointTimeout(60000); // 60秒");
        System.out.println();
        System.out.println("// 同时进行的 Checkpoint 数量");
        System.out.println("config.setMaxConcurrentCheckpoints(1);");
        System.out.println();
        System.out.println("// 两次 Checkpoint 之间最小间隔");
        System.out.println("config.setMinPauseBetweenCheckpoints(500);");
        System.out.println();
        System.out.println("// 可容忍的 Checkpoint 失败次数");
        System.out.println("config.setTolerableCheckpointFailureNumber(3);");
        System.out.println("```");
        System.out.println();

        System.out.println("【作业取消时的 Checkpoint 行为】");
        System.out.println("```java");
        System.out.println("// 取消时保留 Checkpoint (用于恢复)");
        System.out.println("config.setExternalizedCheckpointCleanup(");
        System.out.println("    ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);");
        System.out.println();
        System.out.println("// 取消时删除 Checkpoint");
        System.out.println("config.setExternalizedCheckpointCleanup(");
        System.out.println("    ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：State Backend
     * ========================================
     */
    public static void explainStateBackend() {
        System.out.println("=== State Backend (状态后端) ===");
        System.out.println();

        System.out.println("【状态后端的作用】");
        System.out.println("  • 决定状态存储位置");
        System.out.println("  • 决定 Checkpoint 快照方式");
        System.out.println();

        System.out.println("【1. HashMapStateBackend】");
        System.out.println("  • 状态存储: JVM 堆内存");
        System.out.println("  • 快照: 全量快照到 CheckpointStorage");
        System.out.println("  • 特点: 快速访问，受限于内存大小");
        System.out.println("  • 适用: 小状态、快速访问");
        System.out.println("```java");
        System.out.println("env.setStateBackend(new HashMapStateBackend());");
        System.out.println("```");
        System.out.println();

        System.out.println("【2. EmbeddedRocksDBStateBackend】");
        System.out.println("  • 状态存储: 本地 RocksDB (磁盘)");
        System.out.println("  • 快照: 增量快照到 CheckpointStorage");
        System.out.println("  • 特点: 支持大状态，访问较慢");
        System.out.println("  • 适用: 大状态、生产环境");
        System.out.println("```java");
        System.out.println("env.setStateBackend(new EmbeddedRocksDBStateBackend());");
        System.out.println("```");
        System.out.println();

        System.out.println("【Checkpoint Storage】");
        System.out.println("  决定快照存储位置");
        System.out.println("```java");
        System.out.println("// 存储到本地文件系统");
        System.out.println("config.setCheckpointStorage(\"file:///path/to/checkpoints\");");
        System.out.println();
        System.out.println("// 存储到 HDFS");
        System.out.println("config.setCheckpointStorage(\"hdfs://namenode:8020/flink/checkpoints\");");
        System.out.println();
        System.out.println("// 存储到 S3");
        System.out.println("config.setCheckpointStorage(\"s3://bucket/flink/checkpoints\");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：故障恢复
     * ========================================
     */
    public static void explainRecovery() {
        System.out.println("=== 故障恢复 ===");
        System.out.println();

        System.out.println("【恢复流程】");
        System.out.println("```");
        System.out.println("1. 检测到故障");
        System.out.println("2. 暂停所有任务");
        System.out.println("3. 从最近的 Checkpoint 加载状态");
        System.out.println("4. 重置 Source 位置（如 Kafka offset）");
        System.out.println("5. 重新启动任务");
        System.out.println("6. 继续处理");
        System.out.println("```");
        System.out.println();

        System.out.println("【重启策略配置】");
        System.out.println("```java");
        System.out.println("// 固定延迟重启");
        System.out.println("env.setRestartStrategy(RestartStrategies.fixedDelayRestart(");
        System.out.println("    3,           // 最多重试3次");
        System.out.println("    Time.seconds(10)  // 每次间隔10秒");
        System.out.println("));");
        System.out.println();
        System.out.println("// 失败率重启");
        System.out.println("env.setRestartStrategy(RestartStrategies.failureRateRestart(");
        System.out.println("    3,           // 5分钟内最多失败3次");
        System.out.println("    Time.minutes(5),");
        System.out.println("    Time.seconds(10) // 重启间隔");
        System.out.println("));");
        System.out.println();
        System.out.println("// 指数退避重启");
        System.out.println("env.setRestartStrategy(RestartStrategies.exponentialDelayRestart(");
        System.out.println("    Time.seconds(1),   // 初始延迟");
        System.out.println("    Time.minutes(5),   // 最大延迟");
        System.out.println("    2.0,               // 退避系数");
        System.out.println("    Time.hours(1),     // 重置时间");
        System.out.println("    0.1                // 抖动系数");
        System.out.println("));");
        System.out.println();
        System.out.println("// 不重启");
        System.out.println("env.setRestartStrategy(RestartStrategies.noRestart());");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第七部分：端到端 Exactly-Once
     * ========================================
     */
    public static void explainExactlyOnce() {
        System.out.println("=== 端到端 Exactly-Once ===");
        System.out.println();

        System.out.println("【实现条件】");
        System.out.println("  1. Source 支持重放（如 Kafka）");
        System.out.println("  2. Flink 内部 Checkpoint 保证");
        System.out.println("  3. Sink 支持幂等或事务");
        System.out.println();

        System.out.println("【两阶段提交 (2PC)】");
        System.out.println("```");
        System.out.println("阶段1 (Pre-commit):");
        System.out.println("  • Checkpoint Barrier 到达 Sink");
        System.out.println("  • Sink 预提交数据（但还可回滚）");
        System.out.println("  • 快照状态，报告完成");
        System.out.println();
        System.out.println("阶段2 (Commit):");
        System.out.println("  • JobManager 确认所有任务完成");
        System.out.println("  • 通知 Sink 正式提交");
        System.out.println("  • 数据对外可见");
        System.out.println("```");
        System.out.println();

        System.out.println("【Kafka Exactly-Once 示例】");
        System.out.println("```java");
        System.out.println("KafkaSink<String> sink = KafkaSink.<String>builder()");
        System.out.println("    .setBootstrapServers(\"kafka:9092\")");
        System.out.println("    .setRecordSerializer(...)");
        System.out.println("    .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)");
        System.out.println("    .setTransactionalIdPrefix(\"my-tx-\") // 事务 ID 前缀");
        System.out.println("    .build();");
        System.out.println("```");
        System.out.println();

        System.out.println("【注意事项】");
        System.out.println("  • Kafka 事务超时必须 > Checkpoint 间隔");
        System.out.println("  • 消费者需设置 isolation.level=read_committed");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Checkpoint 检查点                     ║");
        System.out.println("║          Flink 容错的核心                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainCheckpointConcept();
        System.out.println();

        explainSnapshotAlgorithm();
        System.out.println();

        explainBarrierAlignment();
        System.out.println();

        explainCheckpointConfig();
        System.out.println();

        explainStateBackend();
        System.out.println();

        explainRecovery();
        System.out.println();

        explainExactlyOnce();
    }
}
