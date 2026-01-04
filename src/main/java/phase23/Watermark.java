package phase23;

/**
 * Watermark 水印与事件时间
 * 
 * 水印是 Flink 处理乱序数据的核心机制，用于追踪事件时间进度。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class Watermark {

    /**
     * ========================================
     * 第一部分：时间语义
     * ========================================
     */
    public static void explainTimeSemantics() {
        System.out.println("=== 时间语义 ===");
        System.out.println();

        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│                     三种时间语义                           │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│                                                           │");
        System.out.println("│   数据产生        进入 Flink       被处理                  │");
        System.out.println("│      │               │              │                     │");
        System.out.println("│      ▼               ▼              ▼                     │");
        System.out.println("│   Event Time    Ingestion Time  Processing Time          │");
        System.out.println("│   (事件时间)     (摄入时间)      (处理时间)                │");
        System.out.println("│                                                           │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("【1. Processing Time - 处理时间】");
        System.out.println("  • 数据被算子处理时的系统时间");
        System.out.println("  • 最简单，性能最好");
        System.out.println("  • 结果不确定（受处理速度影响）");
        System.out.println("  • 适用：对时间精度要求不高的场景");
        System.out.println();

        System.out.println("【2. Event Time - 事件时间】");
        System.out.println("  • 事件实际发生的时间（数据中的时间戳）");
        System.out.println("  • 结果确定，可重放");
        System.out.println("  • 需要处理乱序和延迟");
        System.out.println("  • 适用：准确性要求高的业务");
        System.out.println();

        System.out.println("【3. Ingestion Time - 摄入时间】");
        System.out.println("  • 数据进入 Flink 的时间");
        System.out.println("  • 介于两者之间");
        System.out.println("  • 较少使用");
    }

    /**
     * ========================================
     * 第二部分：Watermark 概念
     * ========================================
     */
    public static void explainWatermarkConcept() {
        System.out.println("=== Watermark 水印 ===");
        System.out.println();

        System.out.println("【为什么需要 Watermark】");
        System.out.println("  网络延迟、分布式系统等原因导致数据乱序到达");
        System.out.println("  需要一种机制来判断\"什么时候可以认为某个时间点的数据都到了\"");
        System.out.println();

        System.out.println("【Watermark 定义】");
        System.out.println("  Watermark(t) 表示: 事件时间 <= t 的数据都已经到达");
        System.out.println("  Watermark 是一个时间戳，随数据流动");
        System.out.println();

        System.out.println("【工作原理示意】");
        System.out.println("```");
        System.out.println("数据流:  [5] [3] [8] [6] [10] [4] [12] [9] ...");
        System.out.println("         ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓");
        System.out.println("事件时间: 5   3   8   6  10   4  12   9");
        System.out.println();
        System.out.println("当前最大事件时间: 12");
        System.out.println("允许延迟: 5秒");
        System.out.println("Watermark = 12 - 5 = 7");
        System.out.println();
        System.out.println("含义: 事件时间 <= 7 的数据都认为已到达");
        System.out.println("      如果还有更早的数据到来，会被认为是\"迟到数据\"");
        System.out.println("```");
        System.out.println();

        System.out.println("【Watermark 与窗口触发】");
        System.out.println("  当 Watermark >= 窗口结束时间 时，窗口会触发计算");
        System.out.println("```");
        System.out.println("窗口 [0, 10): 当 Watermark >= 10 时触发");
        System.out.println("窗口 [10, 20): 当 Watermark >= 20 时触发");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：Watermark 策略
     * ========================================
     */
    public static void explainWatermarkStrategies() {
        System.out.println("=== Watermark 策略 ===");
        System.out.println();

        System.out.println("【1. 有序流 - forMonotonousTimestamps】");
        System.out.println("  适用: 数据严格有序（时间戳单调递增）");
        System.out.println("  Watermark = 最大事件时间");
        System.out.println("```java");
        System.out.println("WatermarkStrategy<Event> strategy = WatermarkStrategy");
        System.out.println("    .<Event>forMonotonousTimestamps()");
        System.out.println("    .withTimestampAssigner((event, timestamp) -> event.getTimestamp());");
        System.out.println();
        System.out.println("DataStream<Event> result = env");
        System.out.println("    .fromSource(source, strategy, \"source\");");
        System.out.println("```");
        System.out.println();

        System.out.println("【2. 乱序流 - forBoundedOutOfOrderness】");
        System.out.println("  适用: 数据乱序，但在一定范围内");
        System.out.println("  Watermark = 最大事件时间 - 最大延迟");
        System.out.println("```java");
        System.out.println("WatermarkStrategy<Event> strategy = WatermarkStrategy");
        System.out.println("    .<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5)) // 最大5秒乱序");
        System.out.println("    .withTimestampAssigner((event, timestamp) -> event.getTimestamp());");
        System.out.println();
        System.out.println("DataStream<Event> stream = source");
        System.out.println("    .assignTimestampsAndWatermarks(strategy);");
        System.out.println("```");
        System.out.println();

        System.out.println("【3. 无 Watermark - noWatermarks】");
        System.out.println("  适用: 使用处理时间，不关心事件时间");
        System.out.println("```java");
        System.out.println("WatermarkStrategy<Event> strategy = WatermarkStrategy.noWatermarks();");
        System.out.println("```");
        System.out.println();

        System.out.println("【4. 自定义 Watermark】");
        System.out.println("```java");
        System.out.println("WatermarkStrategy<Event> strategy = WatermarkStrategy");
        System.out.println("    .<Event>forGenerator(ctx -> new WatermarkGenerator<Event>() {");
        System.out.println("        private long maxTimestamp = Long.MIN_VALUE;");
        System.out.println("        private final long maxDelay = 5000L; // 5秒");
        System.out.println("        ");
        System.out.println("        @Override");
        System.out.println("        public void onEvent(Event event, long timestamp,");
        System.out.println("                WatermarkOutput output) {");
        System.out.println("            maxTimestamp = Math.max(maxTimestamp, event.getTimestamp());");
        System.out.println("        }");
        System.out.println("        ");
        System.out.println("        @Override");
        System.out.println("        public void onPeriodicEmit(WatermarkOutput output) {");
        System.out.println("            output.emitWatermark(new Watermark(maxTimestamp - maxDelay));");
        System.out.println("        }");
        System.out.println("    })");
        System.out.println("    .withTimestampAssigner((event, ts) -> event.getTimestamp());");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第四部分：Watermark 传播
     * ========================================
     */
    public static void explainWatermarkPropagation() {
        System.out.println("=== Watermark 传播 ===");
        System.out.println();

        System.out.println("【单输入算子】");
        System.out.println("  Watermark 直接传递给下游");
        System.out.println("```");
        System.out.println("  Source[WM=10] → Map → Filter → [WM=10] Sink");
        System.out.println("```");
        System.out.println();

        System.out.println("【多输入算子（如 Union、Join）】");
        System.out.println("  取所有输入中最小的 Watermark");
        System.out.println("```");
        System.out.println("  Stream1 [WM=15] ─┬→ Union → [WM=10] → Sink");
        System.out.println("  Stream2 [WM=10] ─┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【并行子任务】");
        System.out.println("  下游取所有上游分区中最小的 Watermark");
        System.out.println("```");
        System.out.println("  Source(0) [WM=12] ─┬");
        System.out.println("  Source(1) [WM=8]  ─┼→ Map [WM=8]");
        System.out.println("  Source(2) [WM=15] ─┘");
        System.out.println("```");
        System.out.println();

        System.out.println("【空闲源处理】");
        System.out.println("  某些分区没有数据时，Watermark 会阻塞");
        System.out.println("```java");
        System.out.println("WatermarkStrategy<Event> strategy = WatermarkStrategy");
        System.out.println("    .<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))");
        System.out.println("    .withIdleness(Duration.ofMinutes(1)); // 1分钟空闲则忽略");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：时间戳提取
     * ========================================
     */
    public static void explainTimestampAssignment() {
        System.out.println("=== 时间戳提取 ===");
        System.out.println();

        System.out.println("【提取方式】");
        System.out.println("```java");
        System.out.println("// 从事件字段提取");
        System.out.println("WatermarkStrategy<Event> strategy = WatermarkStrategy");
        System.out.println("    .<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))");
        System.out.println("    .withTimestampAssigner((event, recordTimestamp) -> {");
        System.out.println("        return event.getEventTime();  // 毫秒时间戳");
        System.out.println("    });");
        System.out.println("```");
        System.out.println();

        System.out.println("【常见时间格式转换】");
        System.out.println("```java");
        System.out.println("// 字符串 -> 时间戳");
        System.out.println(".withTimestampAssigner((event, ts) -> {");
        System.out.println("    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(");
        System.out.println("        \"yyyy-MM-dd HH:mm:ss\");");
        System.out.println("    LocalDateTime dateTime = LocalDateTime.parse(event.getTimeStr(), formatter);");
        System.out.println("    return dateTime.atZone(ZoneId.systemDefault())");
        System.out.println("                   .toInstant().toEpochMilli();");
        System.out.println("});");
        System.out.println();
        System.out.println("// Instant -> 时间戳");
        System.out.println(".withTimestampAssigner((event, ts) -> ");
        System.out.println("    event.getInstant().toEpochMilli());");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：常见问题
     * ========================================
     */
    public static void explainCommonIssues() {
        System.out.println("=== 常见问题与解决方案 ===");
        System.out.println();

        System.out.println("【问题1: 窗口不触发】");
        System.out.println("  原因: Watermark 没有前进到窗口结束时间");
        System.out.println("  排查:");
        System.out.println("    • 检查时间戳提取是否正确（毫秒 vs 秒）");
        System.out.println("    • 检查是否有空闲分区阻塞 Watermark");
        System.out.println("    • 检查 BoundedOutOfOrderness 设置是否过大");
        System.out.println();

        System.out.println("【问题2: 大量迟到数据】");
        System.out.println("  解决:");
        System.out.println("    • 增加 allowedLateness");
        System.out.println("    • 使用侧输出收集迟到数据单独处理");
        System.out.println("    • 调整 BoundedOutOfOrderness");
        System.out.println();

        System.out.println("【问题3: Watermark 跳跃过大】");
        System.out.println("  原因: 时间戳字段值异常（如未来时间）");
        System.out.println("  解决:");
        System.out.println("```java");
        System.out.println(".withTimestampAssigner((event, ts) -> {");
        System.out.println("    long eventTime = event.getTimestamp();");
        System.out.println("    // 过滤异常时间");
        System.out.println("    if (eventTime > System.currentTimeMillis() + 60000) {");
        System.out.println("        return System.currentTimeMillis(); // 使用当前时间");
        System.out.println("    }");
        System.out.println("    return eventTime;");
        System.out.println("});");
        System.out.println("```");
        System.out.println();

        System.out.println("【最佳实践】");
        System.out.println("  1. BoundedOutOfOrderness 设置为数据实际延迟的上界");
        System.out.println("  2. 始终配置 withIdleness 处理空闲分区");
        System.out.println("  3. 使用侧输出保存迟到数据，避免丢失");
        System.out.println("  4. 监控 Watermark 延迟指标");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Watermark 水印                        ║");
        System.out.println("║          处理乱序数据的核心机制                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainTimeSemantics();
        System.out.println();

        explainWatermarkConcept();
        System.out.println();

        explainWatermarkStrategies();
        System.out.println();

        explainWatermarkPropagation();
        System.out.println();

        explainTimestampAssignment();
        System.out.println();

        explainCommonIssues();
    }
}
