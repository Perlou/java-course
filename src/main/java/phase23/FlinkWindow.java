package phase23;

/**
 * Flink 窗口机制
 * 
 * 窗口是流处理中的核心概念，用于将无界流切分为有界的数据块进行处理。
 * Flink 提供了丰富的窗口类型和灵活的窗口操作。
 * 
 * @author Java Course
 * @since Phase 23
 */
public class FlinkWindow {

    /**
     * ========================================
     * 第一部分：窗口概述
     * ========================================
     * 
     * 【为什么需要窗口】
     * 流数据是无界的，很多操作（如聚合）需要有界数据
     * 窗口将无限流切分为有限块进行处理
     * 
     * 【窗口 vs 批处理】
     * 批处理：一次性处理所有数据
     * 窗口：持续处理，按窗口输出结果
     */
    public static void explainWindowConcept() {
        System.out.println("=== 窗口概述 ===");
        System.out.println();

        System.out.println("【窗口的作用】");
        System.out.println("  1. 将无界流切分为有界块");
        System.out.println("  2. 支持时间维度的聚合统计");
        System.out.println("  3. 控制计算的触发时机");
        System.out.println();

        System.out.println("【窗口的组成】");
        System.out.println("  • 窗口分配器 (Window Assigner): 决定元素属于哪个窗口");
        System.out.println("  • 触发器 (Trigger): 决定何时计算窗口");
        System.out.println("  • 移除器 (Evictor): 计算前/后移除元素");
        System.out.println("  • 窗口函数 (Window Function): 执行计算逻辑");
    }

    /**
     * ========================================
     * 第二部分：窗口类型
     * ========================================
     */
    public static void explainWindowTypes() {
        System.out.println("=== 窗口类型 ===");
        System.out.println();

        // 滚动窗口
        System.out.println("【1. 滚动窗口 (Tumbling Window)】");
        System.out.println("  特点: 固定大小，不重叠，连续");
        System.out.println();
        System.out.println("  时间线:");
        System.out.println("  |---Window 1---|---Window 2---|---Window 3---|");
        System.out.println("  0             10             20             30");
        System.out.println();
        System.out.println("```java");
        System.out.println("// 基于处理时间的滚动窗口（10秒）");
        System.out.println("stream.keyBy(e -> e.getKey())");
        System.out.println("      .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))");
        System.out.println("      .sum(\"value\");");
        System.out.println();
        System.out.println("// 基于事件时间的滚动窗口");
        System.out.println("stream.keyBy(e -> e.getKey())");
        System.out.println("      .window(TumblingEventTimeWindows.of(Time.hours(1)))");
        System.out.println("      .sum(\"value\");");
        System.out.println("```");
        System.out.println();

        // 滑动窗口
        System.out.println("【2. 滑动窗口 (Sliding Window)】");
        System.out.println("  特点: 固定大小，可重叠，按步长滑动");
        System.out.println();
        System.out.println("  时间线:");
        System.out.println("  |----Window 1----|");
        System.out.println("       |----Window 2----|");
        System.out.println("            |----Window 3----|");
        System.out.println("  0    5   10   15   20   25");
        System.out.println("  窗口大小=10秒, 滑动步长=5秒");
        System.out.println();
        System.out.println("```java");
        System.out.println("// 窗口大小10秒，每5秒滑动一次");
        System.out.println("stream.keyBy(e -> e.getKey())");
        System.out.println("      .window(SlidingProcessingTimeWindows.of(");
        System.out.println("          Time.seconds(10),  // 窗口大小");
        System.out.println("          Time.seconds(5)))  // 滑动步长");
        System.out.println("      .sum(\"value\");");
        System.out.println("```");
        System.out.println();

        // 会话窗口
        System.out.println("【3. 会话窗口 (Session Window)】");
        System.out.println("  特点: 按活动间隙划分，大小可变");
        System.out.println();
        System.out.println("  时间线:");
        System.out.println("  |e e e|         |e e|      |e e e e|");
        System.out.println("  Session1  Gap   Session2  Gap  Session3");
        System.out.println("  间隙超过阈值时，前一个窗口关闭");
        System.out.println();
        System.out.println("```java");
        System.out.println("// 5分钟无活动则窗口结束");
        System.out.println("stream.keyBy(e -> e.getUser())");
        System.out.println("      .window(ProcessingTimeSessionWindows.withGap(Time.minutes(5)))");
        System.out.println("      .aggregate(new SessionAggregator());");
        System.out.println();
        System.out.println("// 动态间隙（根据元素决定）");
        System.out.println("stream.keyBy(e -> e.getUser())");
        System.out.println("      .window(EventTimeSessionWindows.withDynamicGap(");
        System.out.println("          event -> event.getSessionTimeout()))");
        System.out.println("      .sum(\"count\");");
        System.out.println("```");
        System.out.println();

        // 全局窗口
        System.out.println("【4. 全局窗口 (Global Window)】");
        System.out.println("  特点: 同 Key 的所有元素在一个窗口，需自定义触发器");
        System.out.println();
        System.out.println("```java");
        System.out.println("// 每收到100个元素触发一次");
        System.out.println("stream.keyBy(e -> e.getKey())");
        System.out.println("      .window(GlobalWindows.create())");
        System.out.println("      .trigger(CountTrigger.of(100))");
        System.out.println("      .sum(\"value\");");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第三部分：计数窗口
     * ========================================
     */
    public static void explainCountWindows() {
        System.out.println("=== 计数窗口 ===");
        System.out.println();

        System.out.println("【计数窗口 - 按元素个数划分】");
        System.out.println();

        System.out.println("```java");
        System.out.println("// 滚动计数窗口（每5个元素一个窗口）");
        System.out.println("stream.keyBy(e -> e.getKey())");
        System.out.println("      .countWindow(5)");
        System.out.println("      .sum(\"value\");");
        System.out.println();
        System.out.println("// 滑动计数窗口（窗口大小10，滑动步长2）");
        System.out.println("stream.keyBy(e -> e.getKey())");
        System.out.println("      .countWindow(10, 2)");
        System.out.println("      .aggregate(new MyAggregator());");
        System.out.println("```");
        System.out.println();

        System.out.println("【注意】");
        System.out.println("  • 计数窗口是 Global Window + Count Trigger 的简写");
        System.out.println("  • 计数是 per key 的，不是全局计数");
    }

    /**
     * ========================================
     * 第四部分：窗口函数
     * ========================================
     */
    public static void explainWindowFunctions() {
        System.out.println("=== 窗口函数 ===");
        System.out.println();

        System.out.println("【1. ReduceFunction - 增量聚合】");
        System.out.println("  特点: 来一条处理一条，内存占用低");
        System.out.println("```java");
        System.out.println("stream.keyBy(...)");
        System.out.println("      .window(...)");
        System.out.println("      .reduce((v1, v2) -> new MyValue(v1.key, v1.count + v2.count));");
        System.out.println("```");
        System.out.println();

        System.out.println("【2. AggregateFunction - 通用增量聚合】");
        System.out.println("  特点: 更灵活，支持不同输入输出类型");
        System.out.println("```java");
        System.out.println("public class AvgAggregator ");
        System.out.println("    implements AggregateFunction<Event, Tuple2<Long, Long>, Double> {");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Tuple2<Long, Long> createAccumulator() {");
        System.out.println("        return Tuple2.of(0L, 0L); // (sum, count)");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Tuple2<Long, Long> add(Event value, Tuple2<Long, Long> acc) {");
        System.out.println("        return Tuple2.of(acc.f0 + value.getValue(), acc.f1 + 1);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Double getResult(Tuple2<Long, Long> acc) {");
        System.out.println("        return acc.f0.doubleValue() / acc.f1;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public Tuple2<Long, Long> merge(Tuple2<Long, Long> a, Tuple2<Long, Long> b) {");
        System.out.println("        return Tuple2.of(a.f0 + b.f0, a.f1 + b.f1);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("【3. ProcessWindowFunction - 全量处理】");
        System.out.println("  特点: 可以获取窗口元数据，但需要缓存所有数据");
        System.out.println("```java");
        System.out.println("stream.keyBy(...)");
        System.out.println("      .window(...)");
        System.out.println("      .process(new ProcessWindowFunction<Event, Result, String, TimeWindow>() {");
        System.out.println("          @Override");
        System.out.println("          public void process(String key, Context ctx, ");
        System.out.println("                  Iterable<Event> elements, Collector<Result> out) {");
        System.out.println("              ");
        System.out.println("              TimeWindow window = ctx.window();");
        System.out.println("              long count = StreamSupport.stream(");
        System.out.println("                  elements.spliterator(), false).count();");
        System.out.println("              ");
        System.out.println("              out.collect(new Result(key, window.getEnd(), count));");
        System.out.println("          }");
        System.out.println("      });");
        System.out.println("```");
        System.out.println();

        System.out.println("【4. 增量聚合 + ProcessWindowFunction】");
        System.out.println("  最佳实践: 结合两者优点");
        System.out.println("```java");
        System.out.println("stream.keyBy(...)");
        System.out.println("      .window(...)");
        System.out.println("      .aggregate(");
        System.out.println("          new AvgAggregator(),    // 增量聚合");
        System.out.println("          new MyProcessFunction() // 获取窗口信息");
        System.out.println("      );");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第五部分：触发器
     * ========================================
     */
    public static void explainTriggers() {
        System.out.println("=== 触发器 (Trigger) ===");
        System.out.println();

        System.out.println("【触发器决定何时计算窗口】");
        System.out.println();

        System.out.println("【内置触发器】");
        System.out.println("  • EventTimeTrigger: 水印到达窗口结束时触发");
        System.out.println("  • ProcessingTimeTrigger: 处理时间到达窗口结束时触发");
        System.out.println("  • CountTrigger: 元素数量达到阈值触发");
        System.out.println("  • PurgingTrigger: 触发后清空窗口内容");
        System.out.println();

        System.out.println("【触发结果】");
        System.out.println("  • CONTINUE: 继续等待");
        System.out.println("  • FIRE: 触发计算，保留窗口内容");
        System.out.println("  • PURGE: 清空窗口内容");
        System.out.println("  • FIRE_AND_PURGE: 触发计算并清空");
        System.out.println();

        System.out.println("【自定义触发器】");
        System.out.println("```java");
        System.out.println("public class MyTrigger extends Trigger<Event, TimeWindow> {");
        System.out.println("    @Override");
        System.out.println("    public TriggerResult onElement(Event element, long timestamp,");
        System.out.println("            TimeWindow window, TriggerContext ctx) {");
        System.out.println("        // 每个元素到达时调用");
        System.out.println("        if (element.isImportant()) {");
        System.out.println("            return TriggerResult.FIRE;");
        System.out.println("        }");
        System.out.println("        return TriggerResult.CONTINUE;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public TriggerResult onEventTime(long time, TimeWindow window,");
        System.out.println("            TriggerContext ctx) {");
        System.out.println("        // 事件时间定时器触发");
        System.out.println("        return TriggerResult.FIRE_AND_PURGE;");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public TriggerResult onProcessingTime(long time, TimeWindow window,");
        System.out.println("            TriggerContext ctx) {");
        System.out.println("        // 处理时间定时器触发");
        System.out.println("        return TriggerResult.FIRE;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
    }

    /**
     * ========================================
     * 第六部分：迟到数据处理
     * ========================================
     */
    public static void explainLateData() {
        System.out.println("=== 迟到数据处理 ===");
        System.out.println();

        System.out.println("【什么是迟到数据】");
        System.out.println("  水印已经越过窗口结束时间后到达的数据");
        System.out.println();

        System.out.println("【处理策略】");
        System.out.println();

        System.out.println("【1. 允许延迟 (Allowed Lateness)】");
        System.out.println("```java");
        System.out.println("stream.keyBy(...)");
        System.out.println("      .window(TumblingEventTimeWindows.of(Time.minutes(10)))");
        System.out.println("      .allowedLateness(Time.minutes(1))  // 允许1分钟延迟");
        System.out.println("      .sum(\"value\");");
        System.out.println("```");
        System.out.println("  效果: 窗口触发后仍接收迟到数据，重新计算并输出");
        System.out.println();

        System.out.println("【2. 侧输出迟到数据】");
        System.out.println("```java");
        System.out.println("OutputTag<Event> lateTag = new OutputTag<Event>(\"late-data\") {};");
        System.out.println();
        System.out.println("SingleOutputStreamOperator<Result> result = stream");
        System.out.println("    .keyBy(...)");
        System.out.println("    .window(TumblingEventTimeWindows.of(Time.minutes(10)))");
        System.out.println("    .allowedLateness(Time.minutes(1))");
        System.out.println("    .sideOutputLateData(lateTag)  // 超过延迟的数据输出到侧流");
        System.out.println("    .sum(\"value\");");
        System.out.println();
        System.out.println("// 获取迟到数据流");
        System.out.println("DataStream<Event> lateStream = result.getSideOutput(lateTag);");
        System.out.println("lateStream.addSink(new LateDateSink());  // 单独处理");
        System.out.println("```");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          Phase 23: Flink 窗口机制                        ║");
        System.out.println("║          流处理的核心概念                                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        explainWindowConcept();
        System.out.println();

        explainWindowTypes();
        System.out.println();

        explainCountWindows();
        System.out.println();

        explainWindowFunctions();
        System.out.println();

        explainTriggers();
        System.out.println();

        explainLateData();
    }
}
