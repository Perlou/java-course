package phase23;

/**
 * Flink 基础架构与核心概念
 * 
 * Apache Flink 是一个分布式流批一体计算引擎，
 * 具有低延迟、高吞吐、精确一次语义等特性。
 * 
 * 【Flink 核心优势】
 * 1. 真正的流处理 - 逐条处理，毫秒级延迟
 * 2. 流批一体 - 统一API处理流和批
 * 3. 状态管理 - 原生支持大规模状态
 * 4. 精确一次 - 端到端 Exactly-Once 语义
 * 5. 事件时间 - 完善的水印机制处理乱序
 * 
 * @author Java Course
 * @since Phase 23
 */
public class FlinkBasics {

    /**
     * ========================================
     * 第一部分：Flink 架构
     * ========================================
     * 
     * Flink 采用主从架构：
     * 
     * ┌─────────────────────────────────────────────────────────┐
     * │ Flink Cluster │
     * │ │
     * │ ┌─────────────────────────────────────────────────┐ │
     * │ │ JobManager (Master) │ │
     * │ │ • ResourceManager - 资源分配 │ │
     * │ │ • Dispatcher - 提交作业的入口 │ │
     * │ │ • JobMaster - 管理单个Job的执行 │ │
     * │ └─────────────────────────────────────────────────┘ │
     * │ │ │
     * │ ┌───────────────┼───────────────┐ │
     * │ ▼ ▼ ▼ │
     * │ ┌────────────┐ ┌────────────┐ ┌────────────┐ │
     * │ │TaskManager │ │TaskManager │ │TaskManager │ │
     * │ │ (Worker) │ │ (Worker) │ │ (Worker) │ │
     * │ │ ┌──────┐ │ │ ┌──────┐ │ │ ┌──────┐ │ │
     * │ │ │ Slot │ │ │ │ Slot │ │ │ │ Slot │ │ │
     * │ │ │ Slot │ │ │ │ Slot │ │ │ │ Slot │ │ │
     * │ │ └──────┘ │ │ └──────┘ │ │ └──────┘ │ │
     * │ └────────────┘ └────────────┘ └────────────┘ │
     * │ │
     * └─────────────────────────────────────────────────────────┘
     * 
     * 【核心组件】
     * 
     * 1. JobManager (主节点)
     * - ResourceManager: 管理 TaskManager 的资源
     * - Dispatcher: 提供 REST 接口，接收作业提交
     * - JobMaster: 负责单个作业的管理和调度
     * 
     * 2. TaskManager (工作节点)
     * - 执行具体的 Task
     * - 每个 TaskManager 有多个 Slot
     * - Slot 是资源调度的基本单位
     * 
     * 3. Slot (任务槽)
     * - TaskManager 的资源子集
     * - 一个 Slot 可以运行一个并行子任务
     * - Slot 共享可以复用资源
     */
    public static void explainArchitecture() {
        System.out.println("=== Flink 架构 ===");
        System.out.println();

        // JobManager 职责
        System.out.println("【JobManager 职责】");
        System.out.println("  1. 接收并调度作业");
        System.out.println("  2. 协调 Checkpoint");
        System.out.println("  3. 故障恢复");
        System.out.println("  4. 资源管理（与 YARN/K8s 交互）");
        System.out.println();

        // TaskManager 职责
        System.out.println("【TaskManager 职责】");
        System.out.println("  1. 执行 Task（算子的并行实例）");
        System.out.println("  2. 管理本地状态");
        System.out.println("  3. 数据交换（Shuffle）");
        System.out.println("  4. 心跳上报");
        System.out.println();

        // Slot 机制
        System.out.println("【Slot 机制】");
        System.out.println("  • Slot 是固定大小的资源单位");
        System.out.println("  • 默认 Slot 共享：一个 Slot 可运行 Pipeline");
        System.out.println("  • 建议：Slot 数 = CPU 核数");
    }

    /**
     * ========================================
     * 第二部分：数据流图（DataFlow）
     * ========================================
     * 
     * Flink 程序 → JobGraph → ExecutionGraph → 物理执行
     * 
     * StreamGraph（逻辑图）
     * ↓ 优化
     * JobGraph（作业图）
     * ↓ 并行化
     * ExecutionGraph（执行图）
     * ↓ 调度
     * 物理执行
     */
    public static void explainDataFlow() {
        System.out.println("=== 数据流图转换 ===");
        System.out.println();

        System.out.println("【程序到执行的转换过程】");
        System.out.println();
        System.out.println("  用户代码 (DataStream API)");
        System.out.println("        ↓");
        System.out.println("  StreamGraph (逻辑流图)");
        System.out.println("    • 记录算子和边");
        System.out.println("    • 未并行化");
        System.out.println("        ↓");
        System.out.println("  JobGraph (作业图)");
        System.out.println("    • 算子链优化 (Operator Chain)");
        System.out.println("    • 减少序列化开销");
        System.out.println("        ↓");
        System.out.println("  ExecutionGraph (执行图)");
        System.out.println("    • 按并行度展开");
        System.out.println("    • 每个并行实例一个节点");
        System.out.println("        ↓");
        System.out.println("  Physical Execution (物理执行)");
        System.out.println("    • 调度到 TaskManager");
        System.out.println("    • 实际数据处理");
    }

    /**
     * ========================================
     * 第三部分：流批一体
     * ========================================
     * 
     * Flink 统一了流处理和批处理：
     * - 批是流的特例（有界的无限流）
     * - 同一套 API 处理流和批
     * - 运行时自动优化
     */
    public static void explainUnifiedStreamBatch() {
        System.out.println("=== 流批一体 ===");
        System.out.println();

        System.out.println("【流 vs 批】");
        System.out.println("  流处理：处理无界数据流，实时产出结果");
        System.out.println("  批处理：处理有界数据集，一次性产出结果");
        System.out.println();

        System.out.println("【Flink 流批一体理念】");
        System.out.println("  \"批是流的特例\"");
        System.out.println("  • 无界流：源源不断的数据");
        System.out.println("  • 有界流：有开始和结束的数据（即批）");
        System.out.println();

        System.out.println("【统一 API】");
        System.out.println("  DataStream API 同时支持:");
        System.out.println("  • 流模式 (STREAMING)");
        System.out.println("  • 批模式 (BATCH)");
        System.out.println();

        // 代码示例
        System.out.println("【代码示例】");
        System.out.println("```java");
        System.out.println("StreamExecutionEnvironment env = ");
        System.out.println("    StreamExecutionEnvironment.getExecutionEnvironment();");
        System.out.println("");
        System.out.println("// 设置运行模式");
        System.out.println("env.setRuntimeMode(RuntimeExecutionMode.STREAMING);");
        System.out.println("// 或");
        System.out.println("env.setRuntimeMode(RuntimeExecutionMode.BATCH);");
        System.out.println("// 或自动判断");
        System.out.println("env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：并行度
     * ========================================
     */
    public static void explainParallelism() {
        System.out.println("=== 并行度 ===");
        System.out.println();

        System.out.println("【并行度定义】");
        System.out.println("  并行度 = 算子的并行实例数");
        System.out.println();

        System.out.println("【并行度设置层级（优先级从高到低）】");
        System.out.println("  1. 算子级别: stream.map(...).setParallelism(4)");
        System.out.println("  2. 环境级别: env.setParallelism(4)");
        System.out.println("  3. 客户端: flink run -p 4 ...");
        System.out.println("  4. 集群配置: flink-conf.yaml");
        System.out.println();

        System.out.println("【并行度建议】");
        System.out.println("  • Source: 等于分区数（如 Kafka 分区数）");
        System.out.println("  • 算子: 根据处理能力调整");
        System.out.println("  • Sink: 根据目标系统能力调整");
        System.out.println("  • keyBy 后并行度不受限制");
    }

    /**
     * ========================================
     * 第五部分：算子链（Operator Chain）
     * ========================================
     */
    public static void explainOperatorChain() {
        System.out.println("=== 算子链 (Operator Chain) ===");
        System.out.println();

        System.out.println("【算子链优化】");
        System.out.println("  将多个算子合并为一个 Task，减少序列化和线程切换");
        System.out.println();

        System.out.println("【链接条件】");
        System.out.println("  1. 上下游并行度相同");
        System.out.println("  2. 一对一的前向连接 (Forward)");
        System.out.println("  3. 没有禁用链接");
        System.out.println();

        System.out.println("【示意图】");
        System.out.println("  优化前:");
        System.out.println("    Source → Map → Filter → Sink");
        System.out.println("    (4个Task, 4次序列化)");
        System.out.println();
        System.out.println("  优化后 (算子链):");
        System.out.println("    [Source → Map → Filter] → Sink");
        System.out.println("    (2个Task, 1次序列化)");
        System.out.println();

        System.out.println("【控制算子链】");
        System.out.println("```java");
        System.out.println("// 禁用全局链接");
        System.out.println("env.disableOperatorChaining();");
        System.out.println("");
        System.out.println("// 从当前算子开始新链");
        System.out.println("stream.map(...).startNewChain();");
        System.out.println("");
        System.out.println("// 禁止当前算子链接");
        System.out.println("stream.map(...).disableChaining();");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：部署模式
     * ========================================
     */
    public static void explainDeploymentModes() {
        System.out.println("=== 部署模式 ===");
        System.out.println();

        System.out.println("【Session 模式】");
        System.out.println("  • 预先启动集群");
        System.out.println("  • 多个作业共享资源");
        System.out.println("  • 适合：开发测试、短作业");
        System.out.println();

        System.out.println("【Per-Job 模式】(已弃用)");
        System.out.println("  • 每个作业单独集群");
        System.out.println("  • 资源隔离");
        System.out.println("  • 适合：生产环境");
        System.out.println();

        System.out.println("【Application 模式】(推荐)");
        System.out.println("  • main() 在 JobManager 执行");
        System.out.println("  • 减少客户端依赖");
        System.out.println("  • 适合：生产环境、大规模部署");
        System.out.println();

        System.out.println("【资源管理器】");
        System.out.println("  • Standalone: Flink 自带");
        System.out.println("  • YARN: Hadoop 生态集成");
        System.out.println("  • Kubernetes: 云原生首选");
    }

    /**
     * ========================================
     * 第七部分：Flink vs Spark 对比
     * ========================================
     */
    public static void compareWithSpark() {
        System.out.println("=== Flink vs Spark Streaming ===");
        System.out.println();

        System.out.println("┌──────────────────┬────────────────┬────────────────┐");
        System.out.println("│     特性         │     Flink      │ Spark Streaming│");
        System.out.println("├──────────────────┼────────────────┼────────────────┤");
        System.out.println("│ 处理模型         │ 真正流处理     │ 微批处理       │");
        System.out.println("│ 延迟             │ 毫秒级         │ 秒级           │");
        System.out.println("│ 状态管理         │ 原生支持       │ 有限支持       │");
        System.out.println("│ Exactly-Once     │ 端到端支持     │ 需额外配置     │");
        System.out.println("│ 窗口             │ 丰富类型       │ 基于时间       │");
        System.out.println("│ 反压             │ 动态传播       │ 批间调节       │");
        System.out.println("│ SQL 支持         │ 流表统一       │ 有限支持       │");
        System.out.println("│ 适用场景         │ 实时计算       │ 准实时/批      │");
        System.out.println("└──────────────────┴────────────────┴────────────────┘");
        System.out.println();

        System.out.println("【选择建议】");
        System.out.println("  • 延迟要求高 (毫秒级) → Flink");
        System.out.println("  • 复杂状态处理 → Flink");
        System.out.println("  • 已有 Spark 生态 → Spark Structured Streaming");
        System.out.println("  • 纯批处理场景 → Spark");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Flink 基础架构                        ║");
        System.out.println("║          流批一体的新一代计算引擎                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainArchitecture();
        System.out.println();

        explainDataFlow();
        System.out.println();

        explainUnifiedStreamBatch();
        System.out.println();

        explainParallelism();
        System.out.println();

        explainOperatorChain();
        System.out.println();

        explainDeploymentModes();
        System.out.println();

        compareWithSpark();
    }
}
