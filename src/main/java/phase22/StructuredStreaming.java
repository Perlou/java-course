package phase22;

/**
 * Structured Streaming - 结构化流处理
 * 
 * Structured Streaming 是 Spark 2.0+ 引入的新流处理 API，
 * 基于 DataFrame/Dataset API，提供更高级的抽象。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class StructuredStreaming {

    public static void main(String[] args) {
        System.out.println("=== Structured Streaming ===\n");

        overview();
        programmingModel();
        inputSources();
        outputSinks();
        triggerModes();
        watermarkAndLateData();
    }

    private static void overview() {
        System.out.println("【Structured Streaming 概述】\n");

        System.out.println("核心理念：将流数据视为无限增长的表");
        System.out.println();
        System.out.println("     输入流             无界表              查询结果");
        System.out.println("  ┌─────────┐      ┌─────────────┐      ┌─────────┐");
        System.out.println("  │ record1 │ ──>  │   row1      │  ──> │ output1 │");
        System.out.println("  │ record2 │ ──>  │   row2      │  ──> │ output2 │");
        System.out.println("  │ record3 │ ──>  │   row3      │  ──> │ output3 │");
        System.out.println("  │   ...   │      │   ...       │      │   ...   │");
        System.out.println("  └─────────┘      └─────────────┘      └─────────┘");
        System.out.println();

        System.out.println("优势：");
        System.out.println("  • 统一批处理和流处理 API");
        System.out.println("  • 支持 Event-time 处理");
        System.out.println("  • Exactly-once 语义");
        System.out.println("  • 端到端容错");
        System.out.println("  • 增量查询优化");
        System.out.println();

        System.out.println("vs DStream：");
        System.out.println("  ┌─────────────┬────────────────┬──────────────────┐");
        System.out.println("  │   特性       │    DStream     │ Structured       │");
        System.out.println("  ├─────────────┼────────────────┼──────────────────┤");
        System.out.println("  │  API        │   RDD-based    │  DataFrame-based │");
        System.out.println("  │  时间语义    │   Processing   │   Event-time     │");
        System.out.println("  │  状态管理    │   手动管理     │   自动管理       │");
        System.out.println("  │  优化        │   有限         │   Catalyst       │");
        System.out.println("  │  维护状态    │   已过时       │   推荐使用       │");
        System.out.println("  └─────────────┴────────────────┴──────────────────┘");
        System.out.println();
    }

    private static void programmingModel() {
        System.out.println("【编程模型】\n");

        System.out.println("基本流程：");
        System.out.println("  1. 定义输入源 (Source)");
        System.out.println("  2. 定义转换操作");
        System.out.println("  3. 定义输出接收器 (Sink)");
        System.out.println("  4. 启动流查询");
        System.out.println();

        System.out.println("代码示例 - 流式 WordCount：");
        System.out.println("  SparkSession spark = SparkSession.builder()");
        System.out.println("      .appName(\"StructuredWordCount\")");
        System.out.println("      .getOrCreate();");
        System.out.println();
        System.out.println("  // 1. 定义输入源");
        System.out.println("  Dataset<Row> lines = spark.readStream()");
        System.out.println("      .format(\"socket\")");
        System.out.println("      .option(\"host\", \"localhost\")");
        System.out.println("      .option(\"port\", 9999)");
        System.out.println("      .load();");
        System.out.println();
        System.out.println("  // 2. 转换操作");
        System.out.println("  Dataset<Row> words = lines");
        System.out.println("      .as(Encoders.STRING())");
        System.out.println("      .flatMap(line -> Arrays.asList(line.split(\" \")).iterator(),");
        System.out.println("               Encoders.STRING())");
        System.out.println("      .toDF(\"word\");");
        System.out.println();
        System.out.println("  Dataset<Row> wordCounts = words");
        System.out.println("      .groupBy(\"word\")");
        System.out.println("      .count();");
        System.out.println();
        System.out.println("  // 3. 定义输出");
        System.out.println("  StreamingQuery query = wordCounts.writeStream()");
        System.out.println("      .outputMode(\"complete\")  // 输出模式");
        System.out.println("      .format(\"console\")       // 输出目标");
        System.out.println("      .start();");
        System.out.println();
        System.out.println("  // 4. 等待终止");
        System.out.println("  query.awaitTermination();");
        System.out.println();
    }

    private static void inputSources() {
        System.out.println("【输入源 (Sources)】\n");

        System.out.println("1. File Source");
        System.out.println("   spark.readStream()");
        System.out.println("       .format(\"parquet\")  // json, csv, orc, text");
        System.out.println("       .schema(schema)");
        System.out.println("       .option(\"maxFilesPerTrigger\", \"100\")");
        System.out.println("       .load(\"hdfs:///input/path\");");
        System.out.println();

        System.out.println("2. Kafka Source");
        System.out.println("   spark.readStream()");
        System.out.println("       .format(\"kafka\")");
        System.out.println("       .option(\"kafka.bootstrap.servers\", \"host:9092\")");
        System.out.println("       .option(\"subscribe\", \"topic1,topic2\")");
        System.out.println("       .option(\"startingOffsets\", \"earliest\")");
        System.out.println("       .load();");
        System.out.println();

        System.out.println("3. Socket Source (测试用)");
        System.out.println("   spark.readStream()");
        System.out.println("       .format(\"socket\")");
        System.out.println("       .option(\"host\", \"localhost\")");
        System.out.println("       .option(\"port\", 9999)");
        System.out.println("       .load();");
        System.out.println();

        System.out.println("4. Rate Source (测试用)");
        System.out.println("   spark.readStream()");
        System.out.println("       .format(\"rate\")");
        System.out.println("       .option(\"rowsPerSecond\", \"100\")");
        System.out.println("       .load();");
        System.out.println();
    }

    private static void outputSinks() {
        System.out.println("【输出接收器 (Sinks) 与输出模式】\n");

        System.out.println("输出模式 (Output Mode)：");
        System.out.println();
        System.out.println("  1. Append（追加）");
        System.out.println("     只输出新增的行，适用于无聚合或带 Watermark 的聚合");
        System.out.println();
        System.out.println("  2. Complete（完整）");
        System.out.println("     输出所有结果，适用于聚合查询");
        System.out.println();
        System.out.println("  3. Update（更新）");
        System.out.println("     只输出更新的行，适用于聚合查询");
        System.out.println();

        System.out.println("常用 Sinks：");
        System.out.println();
        System.out.println("1. Console Sink (调试)");
        System.out.println("   .writeStream()");
        System.out.println("   .format(\"console\")");
        System.out.println("   .option(\"truncate\", \"false\")");
        System.out.println("   .outputMode(\"complete\")");
        System.out.println();

        System.out.println("2. File Sink");
        System.out.println("   .writeStream()");
        System.out.println("   .format(\"parquet\")");
        System.out.println("   .option(\"path\", \"hdfs:///output\")");
        System.out.println("   .option(\"checkpointLocation\", \"hdfs:///checkpoint\")");
        System.out.println("   .outputMode(\"append\")");
        System.out.println();

        System.out.println("3. Kafka Sink");
        System.out.println("   .writeStream()");
        System.out.println("   .format(\"kafka\")");
        System.out.println("   .option(\"kafka.bootstrap.servers\", \"host:9092\")");
        System.out.println("   .option(\"topic\", \"output-topic\")");
        System.out.println("   .option(\"checkpointLocation\", \"/checkpoint\")");
        System.out.println();

        System.out.println("4. ForeachBatch Sink (自定义)");
        System.out.println("   .writeStream()");
        System.out.println("   .foreachBatch((df, batchId) -> {");
        System.out.println("       // 自定义处理每个批次");
        System.out.println("       df.write().jdbc(url, table, props);");
        System.out.println("   })");
        System.out.println();
    }

    private static void triggerModes() {
        System.out.println("【触发模式 (Trigger)】\n");

        System.out.println("1. 默认 - 微批处理");
        System.out.println("   尽可能快地处理，上一批次完成后立即开始下一批次");
        System.out.println("   .trigger(Trigger.ProcessingTime(\"0 seconds\"))");
        System.out.println();

        System.out.println("2. 固定间隔");
        System.out.println("   .trigger(Trigger.ProcessingTime(\"10 seconds\"))");
        System.out.println();

        System.out.println("3. 一次性触发");
        System.out.println("   处理所有可用数据后停止，适合批处理场景");
        System.out.println("   .trigger(Trigger.Once())");
        System.out.println();

        System.out.println("4. 连续处理 (Continuous，实验性)");
        System.out.println("   真正的低延迟流处理，毫秒级延迟");
        System.out.println("   .trigger(Trigger.Continuous(\"1 second\"))");
        System.out.println();
    }

    private static void watermarkAndLateData() {
        System.out.println("【Watermark 与晚到数据】\n");

        System.out.println("Event-time vs Processing-time：");
        System.out.println("  Event-time：事件发生的时间（数据自带时间戳）");
        System.out.println("  Processing-time：处理数据的时间");
        System.out.println();

        System.out.println("Watermark 机制：");
        System.out.println("  定义数据的最大延迟时间，超过后丢弃");
        System.out.println();
        System.out.println("  Dataset<Row> result = events");
        System.out.println("      .withWatermark(\"eventTime\", \"10 minutes\")");
        System.out.println("      .groupBy(");
        System.out.println("          functions.window(col(\"eventTime\"), \"5 minutes\"),");
        System.out.println("          col(\"deviceId\")");
        System.out.println("      )");
        System.out.println("      .count();");
        System.out.println();

        System.out.println("Watermark 工作原理：");
        System.out.println("  Watermark = max(eventTime) - threshold");
        System.out.println("  当数据 eventTime < Watermark 时被丢弃");
        System.out.println();

        System.out.println("窗口操作示例：");
        System.out.println("  // 滑动窗口：窗口大小 10 分钟，滑动间隔 5 分钟");
        System.out.println("  .groupBy(functions.window(col(\"timestamp\"), \"10 minutes\", \"5 minutes\"))");
        System.out.println();
        System.out.println("  // 滚动窗口：窗口大小 10 分钟");
        System.out.println("  .groupBy(functions.window(col(\"timestamp\"), \"10 minutes\"))");
    }
}
