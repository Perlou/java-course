package phase22;

/**
 * Spark 内存管理与数据倾斜处理
 * 
 * 深入理解 Spark 内存模型和常见的数据倾斜问题解决方案。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkMemory {

    public static void main(String[] args) {
        System.out.println("=== Spark 内存管理与数据倾斜处理 ===\n");

        memoryModel();
        memoryConfiguration();
        dataSkew();
        skewSolutions();
        oomTroubleshooting();
    }

    private static void memoryModel() {
        System.out.println("【Spark 内存模型】\n");

        System.out.println("Executor 内存布局 (统一内存管理)：");
        System.out.println();
        System.out.println("  ┌────────────────────────────────────────────────┐");
        System.out.println("  │              Executor Memory                   │");
        System.out.println("  │              (--executor-memory)               │");
        System.out.println("  ├────────────────────────────────────────────────┤");
        System.out.println("  │        Reserved Memory (300MB 固定)            │");
        System.out.println("  ├────────────────────────────────────────────────┤");
        System.out.println("  │                                                │");
        System.out.println("  │   ┌──────────────────────────────────────┐    │");
        System.out.println("  │   │     Unified Memory (60%)              │    │");
        System.out.println("  │   │  (spark.memory.fraction=0.6)          │    │");
        System.out.println("  │   │                                       │    │");
        System.out.println("  │   │  ┌─────────────┬─────────────┐       │    │");
        System.out.println("  │   │  │  Storage    │  Execution  │       │    │");
        System.out.println("  │   │  │  (缓存)     │  (Shuffle)  │       │    │");
        System.out.println("  │   │  │             │             │       │    │");
        System.out.println("  │   │  │  可互相借用  ◄────────────►│      │    │");
        System.out.println("  │   │  └─────────────┴─────────────┘       │    │");
        System.out.println("  │   │   storageFraction=0.5 (默认各 50%)   │    │");
        System.out.println("  │   └──────────────────────────────────────┘    │");
        System.out.println("  │                                                │");
        System.out.println("  │   ┌──────────────────────────────────────┐    │");
        System.out.println("  │   │       User Memory (40%)               │    │");
        System.out.println("  │   │   (用户数据结构、RDD 转换元数据)       │    │");
        System.out.println("  │   └──────────────────────────────────────┘    │");
        System.out.println("  │                                                │");
        System.out.println("  └────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("内存区域说明：");
        System.out.println("  • Storage Memory：RDD 缓存、广播变量");
        System.out.println("  • Execution Memory：Shuffle、排序、聚合的中间数据");
        System.out.println("  • User Memory：用户代码中的数据结构");
        System.out.println("  • 统一内存池：Storage 和 Execution 可以互相借用");
        System.out.println();
    }

    private static void memoryConfiguration() {
        System.out.println("【内存配置】\n");

        System.out.println("关键配置参数：");
        System.out.println();
        System.out.println("  spark.executor.memory=8g           # Executor 堆内存");
        System.out.println("  spark.executor.memoryOverhead=1g   # 堆外内存（容器开销）");
        System.out.println("  spark.memory.fraction=0.6          # 统一内存占比");
        System.out.println("  spark.memory.storageFraction=0.5   # Storage 初始占比");
        System.out.println();

        System.out.println("堆外内存：");
        System.out.println("  spark.memory.offHeap.enabled=true");
        System.out.println("  spark.memory.offHeap.size=2g");
        System.out.println();

        System.out.println("内存计算示例（8G Executor）：");
        System.out.println("  Reserved = 300MB");
        System.out.println("  Usable = 8GB - 300MB = 7.7GB");
        System.out.println("  Unified = 7.7GB * 0.6 = 4.62GB");
        System.out.println("  Storage = 4.62GB * 0.5 = 2.31GB (初始)");
        System.out.println("  Execution = 4.62GB * 0.5 = 2.31GB (初始)");
        System.out.println("  User = 7.7GB * 0.4 = 3.08GB");
        System.out.println();

        System.out.println("缓存存储级别：");
        System.out.println("  MEMORY_ONLY          # 只内存，不序列化");
        System.out.println("  MEMORY_ONLY_SER      # 只内存，序列化（节省空间）");
        System.out.println("  MEMORY_AND_DISK      # 内存+磁盘");
        System.out.println("  MEMORY_AND_DISK_SER  # 内存+磁盘，序列化");
        System.out.println("  DISK_ONLY            # 只磁盘");
        System.out.println();
    }

    private static void dataSkew() {
        System.out.println("【数据倾斜问题】\n");

        System.out.println("什么是数据倾斜？");
        System.out.println("  某些分区的数据量远大于其他分区");
        System.out.println();
        System.out.println("  正常分布: [100] [105] [98] [102] [95]");
        System.out.println("  数据倾斜: [100] [105] [10000] [102] [95]");
        System.out.println();

        System.out.println("数据倾斜的危害：");
        System.out.println("  • 作业运行时间取决于最慢的 Task");
        System.out.println("  • 单个 Task 处理过多数据导致 OOM");
        System.out.println("  • CPU 资源利用不均匀");
        System.out.println();

        System.out.println("常见倾斜场景：");
        System.out.println("  1. groupByKey / reduceByKey：某些 key 数据量大");
        System.out.println("  2. join：热点 key 导致一个 Task 处理过多数据");
        System.out.println("  3. count distinct：某些值出现次数多");
        System.out.println();

        System.out.println("如何发现数据倾斜？");
        System.out.println("  • Spark UI：Tasks 运行时间差异大");
        System.out.println("  • Shuffle Read/Write 数据量不均匀");
        System.out.println("  • 某个 Task 持续运行，其他早已完成");
        System.out.println();
    }

    private static void skewSolutions() {
        System.out.println("【数据倾斜解决方案】\n");

        System.out.println("=== 方案一：过滤异常 Key ===");
        System.out.println("  如果倾斜 key 不重要，直接过滤");
        System.out.println("  df.filter(col(\"key\").notEqual(\"\"))");
        System.out.println();

        System.out.println("=== 方案二：增加 Shuffle 分区数 ===");
        System.out.println("  spark.sql.shuffle.partitions=1000");
        System.out.println("  适用于轻微倾斜");
        System.out.println();

        System.out.println("=== 方案三：两阶段聚合（加盐） ===");
        System.out.println("  // 第一阶段：加随机前缀局部聚合");
        System.out.println("  Dataset<Row> salted = df");
        System.out.println("      .withColumn(\"salt\", concat(col(\"key\"), lit(\"_\"), ");
        System.out.println("                                  expr(\"CAST(RAND() * 10 AS INT)\")))");
        System.out.println("      .groupBy(\"salt\")");
        System.out.println("      .agg(sum(\"value\").alias(\"partial_sum\"));");
        System.out.println();
        System.out.println("  // 第二阶段：去掉前缀全局聚合");
        System.out.println("  Dataset<Row> result = salted");
        System.out.println("      .withColumn(\"key\", expr(\"split(salt, '_')[0]\"))");
        System.out.println("      .groupBy(\"key\")");
        System.out.println("      .agg(sum(\"partial_sum\").alias(\"total\"));");
        System.out.println();

        System.out.println("=== 方案四：Broadcast Join ===");
        System.out.println("  小表广播，避免 Shuffle");
        System.out.println("  bigTable.join(broadcast(smallTable), \"key\")");
        System.out.println();

        System.out.println("=== 方案五：Skew Join 提示 (Spark 3.0+) ===");
        System.out.println("  spark.sql.adaptive.skewJoin.enabled=true");
        System.out.println("  spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes=256MB");
        System.out.println("  AQE 自动检测并处理倾斜");
        System.out.println();

        System.out.println("=== 方案六：分离热点 Key ===");
        System.out.println("  // 识别热点 key");
        System.out.println("  val hotKeys = df.groupBy(\"key\").count()");
        System.out.println("                   .filter(col(\"count\") > 10000)");
        System.out.println("                   .select(\"key\").collect();");
        System.out.println();
        System.out.println("  // 分别处理热点和非热点");
        System.out.println("  val hotData = df.filter(col(\"key\").isin(hotKeys));");
        System.out.println("  val normalData = df.filter(!col(\"key\").isin(hotKeys));");
        System.out.println("  // 对热点数据特殊处理后合并");
        System.out.println();
    }

    private static void oomTroubleshooting() {
        System.out.println("【OOM 问题排查】\n");

        System.out.println("常见 OOM 场景：");
        System.out.println();
        System.out.println("1. Driver OOM");
        System.out.println("   原因：collect() 收集大量数据到 Driver");
        System.out.println("   解决：增加 driver-memory 或使用 take()/write()");
        System.out.println();

        System.out.println("2. Executor OOM - 堆内存");
        System.out.println("   原因：缓存数据过多 / Shuffle 中间数据过大");
        System.out.println("   解决：");
        System.out.println("   • 增加 executor-memory");
        System.out.println("   • 增加分区数减少单分区数据量");
        System.out.println("   • 使用 MEMORY_AND_DISK 存储级别");
        System.out.println();

        System.out.println("3. Executor OOM - 堆外内存");
        System.out.println("   原因：堆外内存不足（Shuffle、Netty）");
        System.out.println("   解决：增加 spark.executor.memoryOverhead");
        System.out.println();

        System.out.println("4. Container killed by YARN");
        System.out.println("   原因：超出 Container 内存限制");
        System.out.println("   解决：调整 memoryOverhead 或减少并发");
        System.out.println();

        System.out.println("GC 调优：");
        System.out.println("  spark.executor.extraJavaOptions=\"-XX:+UseG1GC\"");
        System.out.println("  spark.executor.extraJavaOptions=\"-XX:MaxGCPauseMillis=200\"");
    }
}
