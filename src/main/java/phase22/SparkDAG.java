package phase22;

/**
 * Spark DAG 执行引擎原理
 * 
 * DAG (Directed Acyclic Graph) 有向无环图是 Spark 的核心执行模型，
 * 描述了作业的执行计划和依赖关系。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkDAG {

    public static void main(String[] args) {
        System.out.println("=== Spark DAG 执行引擎原理 ===\n");

        dagConcept();
        executionProcess();
        stageAndTask();
        shuffleProcess();
        dagOptimization();
    }

    private static void dagConcept() {
        System.out.println("【DAG 概念】\n");

        System.out.println("什么是 DAG？");
        System.out.println("  • Directed：有方向，表示数据流动方向");
        System.out.println("  • Acyclic：无环，不会形成循环依赖");
        System.out.println("  • Graph：图，由节点和边组成");
        System.out.println();

        System.out.println("DAG 在 Spark 中的作用：");
        System.out.println("  • 描述 RDD 之间的依赖关系");
        System.out.println("  • 优化执行计划，合并操作");
        System.out.println("  • 根据依赖类型划分 Stage");
        System.out.println("  • 实现容错恢复（通过血统重建数据）");
        System.out.println();

        System.out.println("WordCount DAG 示例：");
        System.out.println("  ┌─────────────┐");
        System.out.println("  │  textFile   │  读取文件");
        System.out.println("  └──────┬──────┘");
        System.out.println("         │ 窄依赖");
        System.out.println("  ┌──────▼──────┐");
        System.out.println("  │  flatMap    │  拆分单词");
        System.out.println("  └──────┬──────┘");
        System.out.println("         │ 窄依赖");
        System.out.println("  ┌──────▼──────┐");
        System.out.println("  │    map      │  (word, 1)");
        System.out.println("  └──────┬──────┘");
        System.out.println("         │ 宽依赖 (Shuffle)");
        System.out.println("  ┌──────▼──────┐");
        System.out.println("  │ reduceByKey │  聚合计数");
        System.out.println("  └──────┬──────┘");
        System.out.println("         │ 窄依赖");
        System.out.println("  ┌──────▼──────┐");
        System.out.println("  │  collect    │  收集结果");
        System.out.println("  └─────────────┘");
        System.out.println();
    }

    private static void executionProcess() {
        System.out.println("【执行流程】\n");

        System.out.println("Spark 作业执行层次：");
        System.out.println("  Application -> Job -> Stage -> Task");
        System.out.println();

        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│                     Application                           │");
        System.out.println("│  (一个 SparkContext 对应一个 Application)                  │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│                                                           │");
        System.out.println("│  ┌─────────────────────────────────────────────────────┐ │");
        System.out.println("│  │                      Job 1                          │ │");
        System.out.println("│  │  (一个 Action 算子触发一个 Job)                      │ │");
        System.out.println("│  │                                                     │ │");
        System.out.println("│  │  ┌───────────┐  ┌───────────┐  ┌───────────┐       │ │");
        System.out.println("│  │  │  Stage 1  │─→│  Stage 2  │─→│  Stage 3  │       │ │");
        System.out.println("│  │  │(ShuffleMap)│ │(ShuffleMap)│ │ (Result)  │       │ │");
        System.out.println("│  │  │           │  │           │  │           │       │ │");
        System.out.println("│  │  │Task Task  │  │Task Task  │  │Task Task  │       │ │");
        System.out.println("│  │  │Task Task  │  │Task Task  │  │Task       │       │ │");
        System.out.println("│  │  └───────────┘  └───────────┘  └───────────┘       │ │");
        System.out.println("│  │                                                     │ │");
        System.out.println("│  └─────────────────────────────────────────────────────┘ │");
        System.out.println("│                                                           │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void stageAndTask() {
        System.out.println("【Stage 与 Task】\n");

        System.out.println("Stage 划分规则：");
        System.out.println("  • 遇到宽依赖（Shuffle）就切分 Stage");
        System.out.println("  • 同一 Stage 内都是窄依赖，可以 Pipeline 执行");
        System.out.println();

        System.out.println("Stage 类型：");
        System.out.println("  • ShuffleMapStage：产生 Shuffle 数据的 Stage");
        System.out.println("  • ResultStage：最终产生结果的 Stage");
        System.out.println();

        System.out.println("Task 数量：");
        System.out.println("  • Stage 内的 Task 数 = 该 Stage 最后一个 RDD 的分区数");
        System.out.println("  • Task 是最小调度单元，一个 Task 处理一个 Partition");
        System.out.println();

        System.out.println("Task 类型：");
        System.out.println("  • ShuffleMapTask：对应 ShuffleMapStage");
        System.out.println("  • ResultTask：对应 ResultStage");
        System.out.println();

        System.out.println("示例 - reduceByKey 的 Stage 划分：");
        System.out.println("  ┌─────────────── Stage 1 ───────────────┐");
        System.out.println("  │ textFile → flatMap → map              │");
        System.out.println("  │ (窄依赖，Pipeline 执行，产生 Shuffle 数据) │");
        System.out.println("  └──────────────────┬────────────────────┘");
        System.out.println("                     │ Shuffle");
        System.out.println("  ┌──────────────────▼────────────────────┐");
        System.out.println("  │             Stage 2                   │");
        System.out.println("  │ reduceByKey → saveAsTextFile          │");
        System.out.println("  │ (读取 Shuffle 数据，输出结果)           │");
        System.out.println("  └───────────────────────────────────────┘");
        System.out.println();
    }

    private static void shuffleProcess() {
        System.out.println("【Shuffle 过程】\n");

        System.out.println("Shuffle 是 Spark 中最消耗资源的操作：");
        System.out.println("  • 涉及磁盘 IO");
        System.out.println("  • 涉及网络 IO");
        System.out.println("  • 可能导致数据倾斜");
        System.out.println();

        System.out.println("Shuffle Write（Map 端）：");
        System.out.println("  1. 计算数据属于哪个分区");
        System.out.println("  2. 将数据写入内存缓冲区");
        System.out.println("  3. 缓冲区满后溢写到磁盘");
        System.out.println("  4. 生成索引文件记录分区信息");
        System.out.println();

        System.out.println("Shuffle Read（Reduce 端）：");
        System.out.println("  1. 从各个 Map 端拉取属于自己分区的数据");
        System.out.println("  2. 合并、聚合数据");
        System.out.println("  3. 进行后续计算");
        System.out.println();

        System.out.println("需要 Shuffle 的算子：");
        System.out.println("  • groupByKey, reduceByKey, aggregateByKey");
        System.out.println("  • join, cogroup");
        System.out.println("  • distinct, intersection, subtract");
        System.out.println("  • repartition, sortByKey");
        System.out.println();
    }

    private static void dagOptimization() {
        System.out.println("【DAG 优化】\n");

        System.out.println("1. Pipeline 优化");
        System.out.println("   • 窄依赖的多个操作合并到一个 Task 执行");
        System.out.println("   • 减少中间数据的物化");
        System.out.println();

        System.out.println("2. Stage 优化");
        System.out.println("   • 尽量使用窄依赖算子，减少 Shuffle");
        System.out.println("   • 使用 reduceByKey 代替 groupByKey");
        System.out.println();

        System.out.println("3. 缓存优化");
        System.out.println("   • 对重复使用的 RDD 进行 persist/cache");
        System.out.println("   • 选择合适的存储级别");
        System.out.println();

        System.out.println("存储级别：");
        System.out.println("  • MEMORY_ONLY：仅内存");
        System.out.println("  • MEMORY_AND_DISK：内存+磁盘");
        System.out.println("  • MEMORY_ONLY_SER：序列化存储，更省空间");
        System.out.println("  • DISK_ONLY：仅磁盘");
        System.out.println();

        System.out.println("4. 数据本地性");
        System.out.println("   • PROCESS_LOCAL：同进程");
        System.out.println("   • NODE_LOCAL：同节点");
        System.out.println("   • RACK_LOCAL：同机架");
        System.out.println("   • ANY：任意位置");
    }
}
