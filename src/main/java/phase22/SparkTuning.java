package phase22;

/**
 * Spark 性能调优
 * 
 * 本课件介绍 Spark 应用性能优化的关键技术和最佳实践。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkTuning {

    public static void main(String[] args) {
        System.out.println("=== Spark 性能调优 ===\n");

        tuningOverview();
        resourceTuning();
        shuffleTuning();
        sqlTuning();
        serializationTuning();
        parallelismTuning();
    }

    private static void tuningOverview() {
        System.out.println("【性能调优概述】\n");

        System.out.println("Spark 性能优化的关键领域：");
        System.out.println("  1. 资源配置：内存、CPU、Executor 数量");
        System.out.println("  2. Shuffle 优化：减少 Shuffle、优化 Shuffle");
        System.out.println("  3. SQL 优化：利用 Catalyst、避免 UDF");
        System.out.println("  4. 序列化优化：使用 Kryo");
        System.out.println("  5. 并行度调整：分区数优化");
        System.out.println("  6. 缓存策略：合理使用 cache/persist");
        System.out.println();

        System.out.println("性能分析工具：");
        System.out.println("  • Spark UI (http://driver:4040)");
        System.out.println("  • Event Log + History Server");
        System.out.println("  • explain() 查看执行计划");
        System.out.println("  • Spark 3.0+ Adaptive Query Execution");
        System.out.println();
    }

    private static void resourceTuning() {
        System.out.println("【资源配置调优】\n");

        System.out.println("=== Executor 配置 ===");
        System.out.println();
        System.out.println("  --num-executors 10       # Executor 数量");
        System.out.println("  --executor-memory 8g     # Executor 内存");
        System.out.println("  --executor-cores 4       # Executor CPU 核数");
        System.out.println();

        System.out.println("推荐配置公式：");
        System.out.println("  total_cores = num_executors * executor_cores");
        System.out.println("  建议：每个 Executor 4-5 个核（避免 HDFS 吞吐瓶颈）");
        System.out.println();

        System.out.println("示例 - 100 核集群：");
        System.out.println("  方案A：--num-executors 20 --executor-cores 5");
        System.out.println("  方案B：--num-executors 25 --executor-cores 4 (推荐)");
        System.out.println();

        System.out.println("=== Driver 配置 ===");
        System.out.println();
        System.out.println("  --driver-memory 4g       # Driver 内存");
        System.out.println("  --driver-cores 2         # Driver CPU");
        System.out.println();
        System.out.println("  注意：collect() 大数据可能导致 Driver OOM");
        System.out.println();

        System.out.println("=== 动态资源分配 ===");
        System.out.println();
        System.out.println("  spark.dynamicAllocation.enabled=true");
        System.out.println("  spark.dynamicAllocation.minExecutors=2");
        System.out.println("  spark.dynamicAllocation.maxExecutors=100");
        System.out.println("  spark.dynamicAllocation.executorIdleTimeout=60s");
        System.out.println();
    }

    private static void shuffleTuning() {
        System.out.println("【Shuffle 优化】\n");

        System.out.println("Shuffle 是性能瓶颈：涉及磁盘 IO + 网络 IO");
        System.out.println();

        System.out.println("1. 减少 Shuffle 数据量");
        System.out.println("   • 使用 reduceByKey 代替 groupByKey");
        System.out.println("   • 使用 combineByKey 预聚合");
        System.out.println("   • 过滤不需要的数据后再 Shuffle");
        System.out.println();

        System.out.println("2. 优化 Shuffle 分区数");
        System.out.println("   spark.sql.shuffle.partitions=200  (默认)");
        System.out.println("   建议：根据数据量调整，每个分区 128MB-256MB");
        System.out.println();

        System.out.println("3. Broadcast Join 避免 Shuffle");
        System.out.println("   小表 < 10MB 自动广播");
        System.out.println("   spark.sql.autoBroadcastJoinThreshold=10485760 (10MB)");
        System.out.println("   ");
        System.out.println("   // 手动广播");
        System.out.println("   df1.join(broadcast(df2), \"key\");");
        System.out.println();

        System.out.println("4. Shuffle 相关配置");
        System.out.println("   spark.shuffle.compress=true           # 压缩 Shuffle 数据");
        System.out.println("   spark.shuffle.spill.compress=true     # 压缩溢写数据");
        System.out.println("   spark.reducer.maxSizeInFlight=48m     # Reduce 端缓冲区");
        System.out.println("   spark.shuffle.file.buffer=32k         # Shuffle 文件缓冲区");
        System.out.println();

        System.out.println("5. AQE 自适应查询执行 (Spark 3.0+)");
        System.out.println("   spark.sql.adaptive.enabled=true");
        System.out.println("   spark.sql.adaptive.coalescePartitions.enabled=true");
        System.out.println("   spark.sql.adaptive.skewJoin.enabled=true");
        System.out.println();
    }

    private static void sqlTuning() {
        System.out.println("【SparkSQL 优化】\n");

        System.out.println("1. 使用内置函数代替 UDF");
        System.out.println("   // 避免");
        System.out.println("   df.select(myUdf(col(\"name\")));");
        System.out.println("   // 推荐");
        System.out.println("   df.select(upper(col(\"name\")));");
        System.out.println();

        System.out.println("2. 列剪裁 - 只选择需要的列");
        System.out.println("   // 避免 select(*)");
        System.out.println("   df.select(\"col1\", \"col2\").filter(...);");
        System.out.println();

        System.out.println("3. 谓词下推 - 尽早过滤");
        System.out.println("   // Catalyst 会自动优化，但显式写更清晰");
        System.out.println("   df.filter(\"date >= '2024-01-01'\").groupBy(...);");
        System.out.println();

        System.out.println("4. 分区裁剪");
        System.out.println("   // 对于分区表，过滤条件包含分区列");
        System.out.println("   spark.sql(\"SELECT * FROM logs WHERE dt='2024-01-01'\");");
        System.out.println();

        System.out.println("5. 缓存热点数据");
        System.out.println("   df.cache();  // 或 df.persist(StorageLevel.MEMORY_AND_DISK);");
        System.out.println("   // 使用完毕后释放");
        System.out.println("   df.unpersist();");
        System.out.println();

        System.out.println("6. 使用 Parquet 格式");
        System.out.println("   • 列式存储，压缩率高");
        System.out.println("   • 支持谓词下推");
        System.out.println("   • 支持列剪裁");
        System.out.println();
    }

    private static void serializationTuning() {
        System.out.println("【序列化优化】\n");

        System.out.println("默认 Java 序列化较慢，推荐使用 Kryo：");
        System.out.println();
        System.out.println("配置 Kryo：");
        System.out.println("  spark.serializer=org.apache.spark.serializer.KryoSerializer");
        System.out.println("  spark.kryo.registrationRequired=false");
        System.out.println();

        System.out.println("注册自定义类（可选，提升性能）：");
        System.out.println("  spark.kryo.classesToRegister=com.example.MyClass1,com.example.MyClass2");
        System.out.println();
        System.out.println("  // 或者在代码中");
        System.out.println("  SparkConf conf = new SparkConf();");
        System.out.println("  conf.registerKryoClasses(new Class[]{MyClass1.class, MyClass2.class});");
        System.out.println();

        System.out.println("Kryo vs Java 序列化：");
        System.out.println("  • Kryo 更快（2-10x）");
        System.out.println("  • Kryo 更紧凑");
        System.out.println("  • Java 序列化更通用");
        System.out.println();
    }

    private static void parallelismTuning() {
        System.out.println("【并行度调优】\n");

        System.out.println("分区数决定并行度：");
        System.out.println("  • 分区太少：CPU 资源利用不充分");
        System.out.println("  • 分区太多：调度开销大，小文件问题");
        System.out.println();

        System.out.println("推荐分区数：");
        System.out.println("  分区数 = 总 CPU 核数 * 2~3");
        System.out.println("  或：每个分区 128MB-256MB 数据");
        System.out.println();

        System.out.println("相关配置：");
        System.out.println("  spark.default.parallelism=200       # RDD 默认分区数");
        System.out.println("  spark.sql.shuffle.partitions=200    # SQL shuffle 分区数");
        System.out.println();

        System.out.println("调整分区：");
        System.out.println("  // 增加分区");
        System.out.println("  df.repartition(100);  // 需要 Shuffle");
        System.out.println();
        System.out.println("  // 减少分区（推荐，无 Shuffle）");
        System.out.println("  df.coalesce(10);");
        System.out.println();
        System.out.println("  // 按列重分区");
        System.out.println("  df.repartition(100, col(\"userId\"));");
        System.out.println();

        System.out.println("最佳实践：");
        System.out.println("  1. 输入数据分区数 ≈ 集群总核数");
        System.out.println("  2. Shuffle 后分区数根据数据量调整");
        System.out.println("  3. 输出前 coalesce 减少小文件");
    }
}
