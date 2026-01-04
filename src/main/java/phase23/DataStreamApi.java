package phase23;

/**
 * DataStream API 详解
 * 
 * DataStream API 是 Flink 的核心编程接口，
 * 提供丰富的算子来处理无界和有界数据流。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class DataStreamApi {

    /**
     * ========================================
     * 第一部分：程序结构
     * ========================================
     * 
     * 每个 Flink 程序包含以下部分：
     * 
     * 1. 获取执行环境 (ExecutionEnvironment)
     * 2. 加载/创建数据源 (Source)
     * 3. 数据转换 (Transformation)
     * 4. 指定输出 (Sink)
     * 5. 触发执行 (execute)
     */
    public static void explainProgramStructure() {
        System.out.println("=== Flink 程序结构 ===");
        System.out.println();

        System.out.println("【基本程序模板】");
        System.out.println("```java");
        System.out.println("// 1. 获取执行环境");
        System.out.println("StreamExecutionEnvironment env = ");
        System.out.println("    StreamExecutionEnvironment.getExecutionEnvironment();");
        System.out.println();
        System.out.println("// 2. 读取数据源");
        System.out.println("DataStream<String> source = env.socketTextStream(\"localhost\", 9999);");
        System.out.println();
        System.out.println("// 3. 数据转换");
        System.out.println("DataStream<Tuple2<String, Integer>> result = source");
        System.out.println("    .flatMap(new Tokenizer())");
        System.out.println("    .keyBy(t -> t.f0)");
        System.out.println("    .sum(1);");
        System.out.println();
        System.out.println("// 4. 输出结果");
        System.out.println("result.print();");
        System.out.println();
        System.out.println("// 5. 触发执行");
        System.out.println("env.execute(\"Word Count\");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第二部分：执行环境
     * ========================================
     */
    public static void explainExecutionEnvironment() {
        System.out.println("=== 执行环境 ===");
        System.out.println();

        System.out.println("【获取执行环境】");
        System.out.println("```java");
        System.out.println("// 自动根据运行环境选择");
        System.out.println("StreamExecutionEnvironment env =");
        System.out.println("    StreamExecutionEnvironment.getExecutionEnvironment();");
        System.out.println();
        System.out.println("// 本地执行环境");
        System.out.println("StreamExecutionEnvironment env =");
        System.out.println("    StreamExecutionEnvironment.createLocalEnvironment();");
        System.out.println();
        System.out.println("// 远程执行环境");
        System.out.println("StreamExecutionEnvironment env =");
        System.out.println("    StreamExecutionEnvironment.createRemoteEnvironment(");
        System.out.println("        \"jobmanager-host\", 8081, \"path/to/jar\");");
        System.out.println("```");
        System.out.println();

        System.out.println("【环境配置】");
        System.out.println("```java");
        System.out.println("// 设置并行度");
        System.out.println("env.setParallelism(4);");
        System.out.println();
        System.out.println("// 设置运行模式");
        System.out.println("env.setRuntimeMode(RuntimeExecutionMode.STREAMING);");
        System.out.println();
        System.out.println("// 启用 Checkpoint");
        System.out.println("env.enableCheckpointing(10000); // 每 10 秒");
        System.out.println();
        System.out.println("// 设置时间语义");
        System.out.println("env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：Source 数据源
     * ========================================
     */
    public static void explainSources() {
        System.out.println("=== 数据源 (Source) ===");
        System.out.println();

        System.out.println("【内置数据源】");
        System.out.println("```java");
        System.out.println("// 从集合创建");
        System.out.println("DataStream<Integer> fromCollection = ");
        System.out.println("    env.fromCollection(Arrays.asList(1, 2, 3, 4, 5));");
        System.out.println();
        System.out.println("// 从元素创建");
        System.out.println("DataStream<String> fromElements = ");
        System.out.println("    env.fromElements(\"a\", \"b\", \"c\");");
        System.out.println();
        System.out.println("// 从文件读取");
        System.out.println("DataStream<String> fromFile = ");
        System.out.println("    env.readTextFile(\"/path/to/file\");");
        System.out.println();
        System.out.println("// 从 Socket 读取");
        System.out.println("DataStream<String> fromSocket = ");
        System.out.println("    env.socketTextStream(\"localhost\", 9999);");
        System.out.println("```");
        System.out.println();

        System.out.println("【Kafka Source (Flink 1.14+)】");
        System.out.println("```java");
        System.out.println("KafkaSource<String> kafkaSource = KafkaSource.<String>builder()");
        System.out.println("    .setBootstrapServers(\"kafka:9092\")");
        System.out.println("    .setTopics(\"input-topic\")");
        System.out.println("    .setGroupId(\"my-group\")");
        System.out.println("    .setStartingOffsets(OffsetsInitializer.earliest())");
        System.out.println("    .setValueOnlyDeserializer(new SimpleStringSchema())");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("DataStream<String> stream = env.fromSource(");
        System.out.println("    kafkaSource, WatermarkStrategy.noWatermarks(), \"Kafka Source\");");
        System.out.println("```");
        System.out.println();

        System.out.println("【自定义 Source】");
        System.out.println("```java");
        System.out.println("// 实现 SourceFunction 接口");
        System.out.println("public class MySource implements SourceFunction<Long> {");
        System.out.println("    private volatile boolean running = true;");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void run(SourceContext<Long> ctx) {");
        System.out.println("        long count = 0;");
        System.out.println("        while (running) {");
        System.out.println("            ctx.collect(count++);");
        System.out.println("            Thread.sleep(1000);");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public void cancel() { running = false; }");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：基本转换算子
     * ========================================
     */
    public static void explainBasicTransformations() {
        System.out.println("=== 基本转换算子 ===");
        System.out.println();

        System.out.println("【map - 一对一转换】");
        System.out.println("```java");
        System.out.println("DataStream<Integer> doubled = source.map(x -> x * 2);");
        System.out.println();
        System.out.println("// 使用 MapFunction");
        System.out.println("DataStream<Integer> doubled = source.map(new MapFunction<Integer, Integer>() {");
        System.out.println("    @Override");
        System.out.println("    public Integer map(Integer value) { return value * 2; }");
        System.out.println("});");
        System.out.println("```");
        System.out.println();

        System.out.println("【flatMap - 一对多转换】");
        System.out.println("```java");
        System.out.println("// 将句子拆分为单词");
        System.out.println("DataStream<String> words = lines.flatMap(");
        System.out.println("    (String line, Collector<String> out) -> {");
        System.out.println("        for (String word : line.split(\" \")) {");
        System.out.println("            out.collect(word);");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println(").returns(Types.STRING);");
        System.out.println("```");
        System.out.println();

        System.out.println("【filter - 过滤】");
        System.out.println("```java");
        System.out.println("DataStream<Integer> filtered = source.filter(x -> x > 0);");
        System.out.println("```");
        System.out.println();

        System.out.println("【keyBy - 分组(逻辑分区)】");
        System.out.println("```java");
        System.out.println("// 按字段分组");
        System.out.println("KeyedStream<Event, String> keyed = events.keyBy(event -> event.getUser());");
        System.out.println();
        System.out.println("// Tuple 按位置分组");
        System.out.println("KeyedStream<Tuple2<String, Integer>, String> keyed = ");
        System.out.println("    tuples.keyBy(t -> t.f0);");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：聚合算子
     * ========================================
     */
    public static void explainAggregations() {
        System.out.println("=== 聚合算子 ===");
        System.out.println();

        System.out.println("【注意】聚合算子只能在 KeyedStream 上调用");
        System.out.println();

        System.out.println("【sum - 求和】");
        System.out.println("```java");
        System.out.println("// 按第一个字段分组，对第二个字段求和");
        System.out.println("DataStream<Tuple2<String, Integer>> result = ");
        System.out.println("    tuples.keyBy(t -> t.f0).sum(1);");
        System.out.println("```");
        System.out.println();

        System.out.println("【min/max - 最小/最大值】");
        System.out.println("```java");
        System.out.println("// 获取每个 key 的最小值记录");
        System.out.println("DataStream<Event> minResult = events.keyBy(e -> e.getKey()).min(\"value\");");
        System.out.println();
        System.out.println("// minBy/maxBy 返回整条记录");
        System.out.println("DataStream<Event> minByResult = events.keyBy(e -> e.getKey()).minBy(\"value\");");
        System.out.println("```");
        System.out.println();

        System.out.println("【reduce - 自定义聚合】");
        System.out.println("```java");
        System.out.println("DataStream<Tuple2<String, Integer>> result = tuples");
        System.out.println("    .keyBy(t -> t.f0)");
        System.out.println("    .reduce((t1, t2) -> Tuple2.of(t1.f0, t1.f1 + t2.f1));");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：分区算子
     * ========================================
     */
    public static void explainPartitioning() {
        System.out.println("=== 分区算子 ===");
        System.out.println();

        System.out.println("【物理分区 - 控制数据如何分布】");
        System.out.println();

        System.out.println("```java");
        System.out.println("// shuffle - 随机均匀分布");
        System.out.println("stream.shuffle();");
        System.out.println();
        System.out.println("// rebalance - 轮询分布（默认负载均衡）");
        System.out.println("stream.rebalance();");
        System.out.println();
        System.out.println("// rescale - 局部轮询（同 TaskManager 内）");
        System.out.println("stream.rescale();");
        System.out.println();
        System.out.println("// broadcast - 广播到所有下游");
        System.out.println("stream.broadcast();");
        System.out.println();
        System.out.println("// global - 发送到下游第一个并行实例");
        System.out.println("stream.global();");
        System.out.println();
        System.out.println("// 自定义分区");
        System.out.println("stream.partitionCustom(");
        System.out.println("    (key, numPartitions) -> key.hashCode() % numPartitions,");
        System.out.println("    event -> event.getKey()");
        System.out.println(");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第七部分：Sink 输出
     * ========================================
     */
    public static void explainSinks() {
        System.out.println("=== 数据输出 (Sink) ===");
        System.out.println();

        System.out.println("【内置 Sink】");
        System.out.println("```java");
        System.out.println("// 打印到控制台");
        System.out.println("stream.print();");
        System.out.println();
        System.out.println("// 写入文件");
        System.out.println("stream.writeAsText(\"/path/to/output\");");
        System.out.println();
        System.out.println("// 写入到 Socket");
        System.out.println("stream.writeToSocket(\"localhost\", 9999, new SimpleStringSchema());");
        System.out.println("```");
        System.out.println();

        System.out.println("【Kafka Sink (Flink 1.14+)】");
        System.out.println("```java");
        System.out.println("KafkaSink<String> kafkaSink = KafkaSink.<String>builder()");
        System.out.println("    .setBootstrapServers(\"kafka:9092\")");
        System.out.println("    .setRecordSerializer(KafkaRecordSerializationSchema.builder()");
        System.out.println("        .setTopic(\"output-topic\")");
        System.out.println("        .setValueSerializationSchema(new SimpleStringSchema())");
        System.out.println("        .build())");
        System.out.println("    .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)");
        System.out.println("    .build();");
        System.out.println();
        System.out.println("stream.sinkTo(kafkaSink);");
        System.out.println("```");
        System.out.println();

        System.out.println("【自定义 Sink】");
        System.out.println("```java");
        System.out.println("stream.addSink(new RichSinkFunction<String>() {");
        System.out.println("    @Override");
        System.out.println("    public void invoke(String value, Context context) {");
        System.out.println("        // 自定义输出逻辑");
        System.out.println("        System.out.println(value);");
        System.out.println("    }");
        System.out.println("});");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第八部分：类型系统
     * ========================================
     */
    public static void explainTypeSystem() {
        System.out.println("=== Flink 类型系统 ===");
        System.out.println();

        System.out.println("【类型擦除问题】");
        System.out.println("  Java 泛型在运行时会被擦除，Flink 需要显式类型信息");
        System.out.println();

        System.out.println("【解决方案】");
        System.out.println("```java");
        System.out.println("// 1. 使用 returns() 指定返回类型");
        System.out.println("stream.map(x -> x * 2).returns(Types.INT);");
        System.out.println();
        System.out.println("// 2. 使用 TypeInformation");
        System.out.println("stream.map(x -> x * 2).returns(TypeInformation.of(Integer.class));");
        System.out.println();
        System.out.println("// 3. 使用 TypeHint (用于复杂泛型)");
        System.out.println("stream.map(...)");
        System.out.println("    .returns(new TypeHint<Tuple2<String, Integer>>() {});");
        System.out.println("```");
        System.out.println();

        System.out.println("【常用类型】");
        System.out.println("  • Types.STRING");
        System.out.println("  • Types.INT, Types.LONG, Types.DOUBLE");
        System.out.println("  • Types.TUPLE(Types.STRING, Types.INT)");
        System.out.println("  • Types.POJO(MyClass.class)");
        System.out.println("  • Types.LIST(Types.STRING)");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: DataStream API                        ║");
        System.out.println("║          Flink 核心编程接口                               ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainProgramStructure();
        System.out.println();

        explainExecutionEnvironment();
        System.out.println();

        explainSources();
        System.out.println();

        explainBasicTransformations();
        System.out.println();

        explainAggregations();
        System.out.println();

        explainPartitioning();
        System.out.println();

        explainSinks();
        System.out.println();

        explainTypeSystem();
    }
}
