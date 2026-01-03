package phase22;

/**
 * Kafka 与 Spark 集成
 * 
 * Kafka 是最常用的 Spark Streaming 数据源，
 * 本课件介绍如何在 Spark 中读写 Kafka。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class KafkaIntegration {

    public static void main(String[] args) {
        System.out.println("=== Kafka 与 Spark 集成 ===\n");

        kafkaOverview();
        readFromKafka();
        writeToKafka();
        offsetManagement();
        bestPractices();
    }

    private static void kafkaOverview() {
        System.out.println("【Kafka 集成概述】\n");

        System.out.println("Maven 依赖：");
        System.out.println("  <dependency>");
        System.out.println("      <groupId>org.apache.spark</groupId>");
        System.out.println("      <artifactId>spark-sql-kafka-0-10_2.12</artifactId>");
        System.out.println("      <version>3.5.0</version>");
        System.out.println("  </dependency>");
        System.out.println();

        System.out.println("Kafka 消息结构（读取后的 Schema）：");
        System.out.println("  ┌────────────────┬──────────────────────────────┐");
        System.out.println("  │  字段           │  类型                        │");
        System.out.println("  ├────────────────┼──────────────────────────────┤");
        System.out.println("  │  key           │  binary                      │");
        System.out.println("  │  value         │  binary                      │");
        System.out.println("  │  topic         │  string                      │");
        System.out.println("  │  partition     │  int                         │");
        System.out.println("  │  offset        │  long                        │");
        System.out.println("  │  timestamp     │  timestamp                   │");
        System.out.println("  │  timestampType │  int                         │");
        System.out.println("  └────────────────┴──────────────────────────────┘");
        System.out.println();
    }

    private static void readFromKafka() {
        System.out.println("【从 Kafka 读取数据】\n");

        System.out.println("=== Structured Streaming 方式 ===");
        System.out.println();
        System.out.println("  Dataset<Row> df = spark.readStream()");
        System.out.println("      .format(\"kafka\")");
        System.out.println("      .option(\"kafka.bootstrap.servers\", \"host1:9092,host2:9092\")");
        System.out.println("      .option(\"subscribe\", \"topic1,topic2\")  // 订阅多个 Topic");
        System.out.println("      // 或者 .option(\"subscribePattern\", \"topic.*\")  // 正则匹配");
        System.out.println("      .option(\"startingOffsets\", \"earliest\")  // earliest/latest/JSON");
        System.out.println("      .option(\"failOnDataLoss\", \"false\")");
        System.out.println("      .load();");
        System.out.println();

        System.out.println("  // 解析 JSON 消息");
        System.out.println("  Dataset<Row> parsed = df");
        System.out.println("      .selectExpr(\"CAST(key AS STRING)\", \"CAST(value AS STRING)\")");
        System.out.println("      .select(");
        System.out.println("          col(\"key\"),");
        System.out.println("          from_json(col(\"value\"), schema).alias(\"data\")");
        System.out.println("      )");
        System.out.println("      .select(\"key\", \"data.*\");");
        System.out.println();

        System.out.println("=== 批处理方式 ===");
        System.out.println();
        System.out.println("  Dataset<Row> df = spark.read()");
        System.out.println("      .format(\"kafka\")");
        System.out.println("      .option(\"kafka.bootstrap.servers\", \"host:9092\")");
        System.out.println("      .option(\"subscribe\", \"topic1\")");
        System.out.println("      .option(\"startingOffsets\", \"{\\\"topic1\\\":{\\\"0\\\":0}}\")");
        System.out.println("      .option(\"endingOffsets\", \"{\\\"topic1\\\":{\\\"0\\\":100}}\")");
        System.out.println("      .load();");
        System.out.println();

        System.out.println("起始/结束 Offset 选项：");
        System.out.println("  • \"earliest\"：最早的 offset");
        System.out.println("  • \"latest\"：最新的 offset");
        System.out.println("  • JSON：指定每个分区的 offset");
        System.out.println("    {\"topic1\":{\"0\":23,\"1\":45},\"topic2\":{\"0\":100}}");
        System.out.println();
    }

    private static void writeToKafka() {
        System.out.println("【写入 Kafka】\n");

        System.out.println("=== Structured Streaming 方式 ===");
        System.out.println();
        System.out.println("  // 准备数据（必须有 key 或 value 列）");
        System.out.println("  Dataset<Row> output = df");
        System.out.println("      .selectExpr(");
        System.out.println("          \"CAST(userId AS STRING) AS key\",");
        System.out.println("          \"to_json(struct(*)) AS value\"");
        System.out.println("      );");
        System.out.println();
        System.out.println("  // 写入 Kafka");
        System.out.println("  StreamingQuery query = output.writeStream()");
        System.out.println("      .format(\"kafka\")");
        System.out.println("      .option(\"kafka.bootstrap.servers\", \"host:9092\")");
        System.out.println("      .option(\"topic\", \"output-topic\")");
        System.out.println("      .option(\"checkpointLocation\", \"hdfs:///checkpoint\")");
        System.out.println("      .outputMode(\"append\")");
        System.out.println("      .start();");
        System.out.println();

        System.out.println("=== 批处理方式 ===");
        System.out.println();
        System.out.println("  df.selectExpr(\"CAST(id AS STRING) AS key\", \"to_json(struct(*)) AS value\")");
        System.out.println("      .write()");
        System.out.println("      .format(\"kafka\")");
        System.out.println("      .option(\"kafka.bootstrap.servers\", \"host:9092\")");
        System.out.println("      .option(\"topic\", \"output-topic\")");
        System.out.println("      .save();");
        System.out.println();

        System.out.println("写入时可选列：");
        System.out.println("  • key (string/binary)：消息键");
        System.out.println("  • value (string/binary)：消息值（必需）");
        System.out.println("  • topic (string)：目标 topic（覆盖默认）");
        System.out.println("  • partition (int)：目标分区");
        System.out.println();
    }

    private static void offsetManagement() {
        System.out.println("【Offset 管理】\n");

        System.out.println("Spark Structured Streaming 自动管理 Offset：");
        System.out.println("  • Checkpoint 保存已处理的 Offset");
        System.out.println("  • 失败恢复从 Checkpoint 继续");
        System.out.println("  • 保证 Exactly-once 语义");
        System.out.println();

        System.out.println("Checkpoint 配置：");
        System.out.println("  .option(\"checkpointLocation\", \"hdfs:///checkpoint/app\")");
        System.out.println();

        System.out.println("手动指定起始 Offset：");
        System.out.println("  // 从特定 offset 开始");
        System.out.println("  .option(\"startingOffsets\",");
        System.out.println("      \"{\\\"topic\\\":{\\\"0\\\":100,\\\"1\\\":200}}\")");
        System.out.println();
        System.out.println("  // 从某个时间戳开始");
        System.out.println("  .option(\"startingOffsetsByTimestamp\",");
        System.out.println("      \"{\\\"topic\\\":{\\\"0\\\":1609459200000}}\")");
        System.out.println();

        System.out.println("Kafka Consumer 配置：");
        System.out.println("  .option(\"kafka.group.id\", \"my-consumer-group\")");
        System.out.println("  .option(\"kafka.max.poll.records\", \"500\")");
        System.out.println("  .option(\"kafka.session.timeout.ms\", \"30000\")");
        System.out.println();
    }

    private static void bestPractices() {
        System.out.println("【最佳实践】\n");

        System.out.println("1. 性能优化");
        System.out.println("   • 增加 Kafka 分区数以提高并行度");
        System.out.println("   • 调整 maxOffsetsPerTrigger 控制批次大小");
        System.out.println("   • 使用 Kryo 序列化");
        System.out.println();

        System.out.println("2. 容错配置");
        System.out.println("   • 使用可靠存储（HDFS）保存 Checkpoint");
        System.out.println("   • 设置 failOnDataLoss=false 避免数据丢失时失败");
        System.out.println("   • 合理设置 minPartitions");
        System.out.println();

        System.out.println("3. 监控");
        System.out.println("   • 监控 Kafka Consumer Lag");
        System.out.println("   • 监控 Spark Streaming 批次处理时间");
        System.out.println("   • 使用 StreamingQueryListener");
        System.out.println();

        System.out.println("4. Schema 管理");
        System.out.println("   • 使用 Schema Registry 管理 Avro Schema");
        System.out.println("   • 显式定义 Schema，避免推断");
        System.out.println();

        System.out.println("完整示例：");
        System.out.println("  Dataset<Row> df = spark.readStream()");
        System.out.println("      .format(\"kafka\")");
        System.out.println("      .option(\"kafka.bootstrap.servers\", \"host:9092\")");
        System.out.println("      .option(\"subscribe\", \"events\")");
        System.out.println("      .option(\"startingOffsets\", \"latest\")");
        System.out.println("      .option(\"maxOffsetsPerTrigger\", \"10000\")");
        System.out.println("      .load()");
        System.out.println("      .selectExpr(\"CAST(value AS STRING)\")");
        System.out.println("      .select(from_json(col(\"value\"), schema).alias(\"data\"))");
        System.out.println("      .select(\"data.*\");");
    }
}
