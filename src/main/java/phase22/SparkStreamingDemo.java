package phase22;

/**
 * Spark Streaming - DStream 编程
 * 
 * Spark Streaming 是 Spark 的流处理模块，
 * 使用微批处理 (Micro-batch) 模式处理实时数据流。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class SparkStreamingDemo {

    public static void main(String[] args) {
        System.out.println("=== Spark Streaming - DStream 编程 ===\n");

        streamingOverview();
        dstreamConcept();
        basicOperations();
        statefulOperations();
        outputOperations();
        checkpointing();
    }

    private static void streamingOverview() {
        System.out.println("【Spark Streaming 概述】\n");

        System.out.println("什么是 Spark Streaming？");
        System.out.println("  • Spark 的流处理扩展模块");
        System.out.println("  • 将数据流切分为小批次 (Micro-batch)");
        System.out.println("  • 每个批次作为 RDD 处理");
        System.out.println("  • 提供近实时的流处理能力");
        System.out.println();

        System.out.println("处理流程：");
        System.out.println("  数据流 ─── [batch1] [batch2] [batch3] ───>");
        System.out.println("               ↓         ↓         ↓");
        System.out.println("            [RDD 1]   [RDD 2]   [RDD 3]");
        System.out.println("               ↓         ↓         ↓");
        System.out.println("           处理结果   处理结果   处理结果");
        System.out.println();

        System.out.println("支持的数据源：");
        System.out.println("  • Kafka");
        System.out.println("  • Flume");
        System.out.println("  • Kinesis");
        System.out.println("  • TCP Socket");
        System.out.println("  • 自定义 Receiver");
        System.out.println();

        System.out.println("注意：DStream API 已过时，推荐使用 Structured Streaming");
        System.out.println();
    }

    private static void dstreamConcept() {
        System.out.println("【DStream 概念】\n");

        System.out.println("DStream (Discretized Stream)：");
        System.out.println("  • 离散化流，Spark Streaming 的核心抽象");
        System.out.println("  • 本质上是一系列连续的 RDD");
        System.out.println("  • 每个时间间隔产生一个 RDD");
        System.out.println();

        System.out.println("入口点 - StreamingContext：");
        System.out.println("  // 创建 StreamingContext，批次间隔 5 秒");
        System.out.println("  SparkConf conf = new SparkConf().setAppName(\"App\");");
        System.out.println("  JavaStreamingContext jssc = ");
        System.out.println("      new JavaStreamingContext(conf, Durations.seconds(5));");
        System.out.println();

        System.out.println("Socket 数据源示例：");
        System.out.println("  // 从 Socket 接收数据");
        System.out.println("  JavaReceiverInputDStream<String> lines = ");
        System.out.println("      jssc.socketTextStream(\"localhost\", 9999);");
        System.out.println();
        System.out.println("  // 处理数据");
        System.out.println("  JavaDStream<String> words = lines.flatMap(");
        System.out.println("      line -> Arrays.asList(line.split(\" \")).iterator()");
        System.out.println("  );");
        System.out.println();
        System.out.println("  // 启动流处理");
        System.out.println("  jssc.start();");
        System.out.println("  jssc.awaitTermination();");
        System.out.println();
    }

    private static void basicOperations() {
        System.out.println("【基本操作】\n");

        System.out.println("=== 转换操作 ===");
        System.out.println();
        System.out.println("与 RDD 类似的算子：");
        System.out.println("  dstream.map(func)");
        System.out.println("  dstream.flatMap(func)");
        System.out.println("  dstream.filter(func)");
        System.out.println("  dstream.reduce(func)");
        System.out.println("  dstream.count()");
        System.out.println("  dstream.union(otherDStream)");
        System.out.println();

        System.out.println("K-V 操作：");
        System.out.println("  pairDStream.reduceByKey(func)");
        System.out.println("  pairDStream.groupByKey()");
        System.out.println("  pairDStream.join(otherPairDStream)");
        System.out.println();

        System.out.println("transform 操作（访问底层 RDD）：");
        System.out.println("  dstream.transform(rdd -> {");
        System.out.println("      // 可以使用任何 RDD 操作");
        System.out.println("      return rdd.map(...)");
        System.out.println("  });");
        System.out.println();

        System.out.println("WordCount 示例：");
        System.out.println("  JavaDStream<String> lines = jssc.socketTextStream(...);");
        System.out.println("  ");
        System.out.println("  JavaDStream<String> words = lines.flatMap(");
        System.out.println("      line -> Arrays.asList(line.split(\" \")).iterator()");
        System.out.println("  );");
        System.out.println("  ");
        System.out.println("  JavaPairDStream<String, Integer> pairs = words.mapToPair(");
        System.out.println("      word -> new Tuple2<>(word, 1)");
        System.out.println("  );");
        System.out.println("  ");
        System.out.println("  JavaPairDStream<String, Integer> counts = pairs.reduceByKey(");
        System.out.println("      (a, b) -> a + b");
        System.out.println("  );");
        System.out.println("  ");
        System.out.println("  counts.print();  // 打印每批次结果");
        System.out.println();
    }

    private static void statefulOperations() {
        System.out.println("【有状态操作】\n");

        System.out.println("=== updateStateByKey ===");
        System.out.println("在批次之间维护状态（需要 Checkpoint）");
        System.out.println();
        System.out.println("  // 启用 Checkpoint");
        System.out.println("  jssc.checkpoint(\"hdfs:///checkpoint\");");
        System.out.println();
        System.out.println("  // 状态更新函数");
        System.out.println("  Function2<List<Integer>, Optional<Integer>, Optional<Integer>> updateFunc =");
        System.out.println("      (values, state) -> {");
        System.out.println("          int sum = state.orElse(0);");
        System.out.println("          for (Integer v : values) {");
        System.out.println("              sum += v;");
        System.out.println("          }");
        System.out.println("          return Optional.of(sum);");
        System.out.println("      };");
        System.out.println();
        System.out.println("  // 累计统计");
        System.out.println("  JavaPairDStream<String, Integer> runningCounts = ");
        System.out.println("      wordCounts.updateStateByKey(updateFunc);");
        System.out.println();

        System.out.println("=== mapWithState (Spark 1.6+) ===");
        System.out.println("性能更好的状态操作");
        System.out.println();
        System.out.println("  StateSpec<String, Integer, Integer, Tuple2<String, Integer>> stateSpec =");
        System.out.println("      StateSpec.function((key, value, state) -> {");
        System.out.println("          int sum = state.exists() ? state.get() : 0;");
        System.out.println("          sum += value.orElse(0);");
        System.out.println("          state.update(sum);");
        System.out.println("          return new Tuple2<>(key, sum);");
        System.out.println("      });");
        System.out.println();
        System.out.println("  JavaMapWithStateDStream<String, Integer, Integer, Tuple2<String, Integer>> result =");
        System.out.println("      wordCounts.mapWithState(stateSpec);");
        System.out.println();
    }

    private static void outputOperations() {
        System.out.println("【输出操作】\n");

        System.out.println("常用输出操作：");
        System.out.println();
        System.out.println("1. print()   - 打印到控制台（调试用）");
        System.out.println("   dstream.print(10);  // 打印前 10 条");
        System.out.println();
        System.out.println("2. saveAsTextFiles(prefix, suffix)");
        System.out.println("   dstream.saveAsTextFiles(\"output/prefix\", \"txt\");");
        System.out.println("   // 每批次生成：output/prefix-1234567890.txt");
        System.out.println();
        System.out.println("3. saveAsObjectFiles(prefix)");
        System.out.println("   dstream.saveAsObjectFiles(\"output/obj\");");
        System.out.println();
        System.out.println("4. saveAsHadoopFiles(prefix, suffix)");
        System.out.println("   pairDStream.saveAsHadoopFiles(\"output/hadoop\", \"seq\");");
        System.out.println();
        System.out.println("5. foreachRDD(func) - 最灵活的输出");
        System.out.println("   dstream.foreachRDD((rdd, time) -> {");
        System.out.println("       // 可以做任何操作：写数据库、发送消息等");
        System.out.println("       rdd.foreachPartition(partition -> {");
        System.out.println("           // 在每个分区创建连接");
        System.out.println("           Connection conn = createConnection();");
        System.out.println("           partition.forEachRemaining(record -> {");
        System.out.println("               // 写入数据库");
        System.out.println("               conn.insert(record);");
        System.out.println("           });");
        System.out.println("           conn.close();");
        System.out.println("       });");
        System.out.println("   });");
        System.out.println();
    }

    private static void checkpointing() {
        System.out.println("【Checkpoint 机制】\n");

        System.out.println("为什么需要 Checkpoint？");
        System.out.println("  • 失败恢复：从检查点恢复状态");
        System.out.println("  • 有状态操作必须启用");
        System.out.println("  • 截断 RDD 血统，防止 StackOverflow");
        System.out.println();

        System.out.println("Checkpoint 内容：");
        System.out.println("  • 元数据：配置、DStream 操作、未完成的批次");
        System.out.println("  • 数据：RDD 数据（有状态操作）");
        System.out.println();

        System.out.println("配置 Checkpoint：");
        System.out.println("  jssc.checkpoint(\"hdfs:///checkpoint/myapp\");");
        System.out.println();

        System.out.println("从 Checkpoint 恢复：");
        System.out.println("  JavaStreamingContext jssc = JavaStreamingContext.getOrCreate(");
        System.out.println("      checkpointDir,");
        System.out.println("      () -> createContext()  // 如果没有 Checkpoint 则创建新的");
        System.out.println("  );");
        System.out.println();

        System.out.println("最佳实践：");
        System.out.println("  • Checkpoint 目录使用可靠存储（HDFS）");
        System.out.println("  • 定期清理过期的 Checkpoint");
        System.out.println("  • Checkpoint 间隔 = 5-10 倍批次间隔");
    }
}
