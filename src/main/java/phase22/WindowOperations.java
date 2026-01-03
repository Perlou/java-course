package phase22;

/**
 * Spark 窗口操作
 * 
 * 窗口操作是流处理的核心功能，用于对一段时间内的数据进行聚合。
 * 
 * @author Java Course
 * @since Phase 22
 */
public class WindowOperations {

    public static void main(String[] args) {
        System.out.println("=== Spark 窗口操作 ===\n");

        windowOverview();
        tumblingWindow();
        slidingWindow();
        sessionWindow();
        windowWithWatermark();
        dstreamWindow();
    }

    private static void windowOverview() {
        System.out.println("【窗口操作概述】\n");

        System.out.println("什么是窗口？");
        System.out.println("  将无限数据流切分为有限的数据块进行处理");
        System.out.println();

        System.out.println("窗口类型：");
        System.out.println("  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │                    时间线                                │");
        System.out.println("  │  ─────────────────────────────────────────────>         │");
        System.out.println("  │                                                         │");
        System.out.println("  │  滚动窗口 (Tumbling Window)：                           │");
        System.out.println("  │  [窗口1][窗口2][窗口3][窗口4]                           │");
        System.out.println("  │  不重叠，窗口大小固定                                    │");
        System.out.println("  │                                                         │");
        System.out.println("  │  滑动窗口 (Sliding Window)：                            │");
        System.out.println("  │  [  窗口1  ]                                            │");
        System.out.println("  │     [  窗口2  ]                                         │");
        System.out.println("  │        [  窗口3  ]                                      │");
        System.out.println("  │  窗口可重叠，滑动间隔 < 窗口大小                          │");
        System.out.println("  │                                                         │");
        System.out.println("  │  会话窗口 (Session Window)：                            │");
        System.out.println("  │  [会话1]      [会话2]  [会话3]                          │");
        System.out.println("  │  根据活动间隔动态确定窗口边界                             │");
        System.out.println("  └─────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void tumblingWindow() {
        System.out.println("【滚动窗口 (Tumbling Window)】\n");

        System.out.println("特点：");
        System.out.println("  • 窗口之间不重叠");
        System.out.println("  • 每条数据只属于一个窗口");
        System.out.println("  • 窗口大小固定");
        System.out.println();

        System.out.println("代码示例：");
        System.out.println("  import static org.apache.spark.sql.functions.*;");
        System.out.println();
        System.out.println("  Dataset<Row> result = events");
        System.out.println("      .groupBy(");
        System.out.println("          window(col(\"timestamp\"), \"10 minutes\"),");
        System.out.println("          col(\"userId\")");
        System.out.println("      )");
        System.out.println("      .agg(");
        System.out.println("          count(\"*\").alias(\"event_count\"),");
        System.out.println("          sum(\"amount\").alias(\"total_amount\")");
        System.out.println("      );");
        System.out.println();

        System.out.println("窗口列结构：");
        System.out.println("  window: struct<start: timestamp, end: timestamp>");
        System.out.println();
        System.out.println("  // 提取窗口开始和结束时间");
        System.out.println("  result.select(");
        System.out.println("      col(\"window.start\").alias(\"window_start\"),");
        System.out.println("      col(\"window.end\").alias(\"window_end\"),");
        System.out.println("      col(\"event_count\")");
        System.out.println("  );");
        System.out.println();
    }

    private static void slidingWindow() {
        System.out.println("【滑动窗口 (Sliding Window)】\n");

        System.out.println("特点：");
        System.out.println("  • 窗口之间可以重叠");
        System.out.println("  • 一条数据可能属于多个窗口");
        System.out.println("  • 需要指定窗口大小和滑动间隔");
        System.out.println();

        System.out.println("代码示例：");
        System.out.println("  // 窗口大小 10 分钟，滑动间隔 5 分钟");
        System.out.println("  Dataset<Row> result = events");
        System.out.println("      .groupBy(");
        System.out.println("          window(col(\"timestamp\"), \"10 minutes\", \"5 minutes\"),");
        System.out.println("          col(\"category\")");
        System.out.println("      )");
        System.out.println("      .count();");
        System.out.println();

        System.out.println("窗口示意：");
        System.out.println("  时间轴: 0min     5min     10min    15min    20min");
        System.out.println("              |--------|--------|--------|--------|");
        System.out.println("  窗口1:  [0-10min   ]");
        System.out.println("  窗口2:       [5-15min    ]");
        System.out.println("  窗口3:            [10-20min   ]");
        System.out.println();

        System.out.println("  // 时间戳为 08:00 的数据会出现在：");
        System.out.println("  // [00:00-10:00] 和 [05:00-15:00] 两个窗口中");
        System.out.println();
    }

    private static void sessionWindow() {
        System.out.println("【会话窗口 (Session Window)】\n");

        System.out.println("特点：");
        System.out.println("  • 根据活动间隙(gap)动态确定窗口边界");
        System.out.println("  • 适合用户会话分析");
        System.out.println("  • Spark 3.2+ 支持");
        System.out.println();

        System.out.println("代码示例：");
        System.out.println("  import static org.apache.spark.sql.functions.*;");
        System.out.println();
        System.out.println("  Dataset<Row> result = events");
        System.out.println("      .groupBy(");
        System.out.println("          session_window(col(\"timestamp\"), \"30 minutes\"),");
        System.out.println("          col(\"userId\")");
        System.out.println("      )");
        System.out.println("      .agg(");
        System.out.println("          count(\"*\").alias(\"page_views\"),");
        System.out.println("          min(\"timestamp\").alias(\"session_start\"),");
        System.out.println("          max(\"timestamp\").alias(\"session_end\")");
        System.out.println("      );");
        System.out.println();

        System.out.println("会话示意：");
        System.out.println("  用户活动: ●  ● ●     ●●●       ● ● ●●");
        System.out.println("             <gap>      <gap>");
        System.out.println("  会话:    [会话1]    [会话2]   [会话3]");
        System.out.println();
    }

    private static void windowWithWatermark() {
        System.out.println("【窗口 + Watermark】\n");

        System.out.println("组合使用处理晚到数据：");
        System.out.println();
        System.out.println("  Dataset<Row> result = events");
        System.out.println("      // 允许最多 10 分钟的数据延迟");
        System.out.println("      .withWatermark(\"timestamp\", \"10 minutes\")");
        System.out.println("      .groupBy(");
        System.out.println("          window(col(\"timestamp\"), \"5 minutes\"),");
        System.out.println("          col(\"deviceId\")");
        System.out.println("      )");
        System.out.println("      .count();");
        System.out.println();

        System.out.println("Watermark 机制：");
        System.out.println("  max_event_time = 当前处理的最大事件时间");
        System.out.println("  watermark = max_event_time - 延迟阈值");
        System.out.println("  当 window.end < watermark 时，窗口关闭并输出结果");
        System.out.println();

        System.out.println("示例时间线：");
        System.out.println("  max_event_time = 10:20");
        System.out.println("  watermark = 10:20 - 10min = 10:10");
        System.out.println("  窗口 [10:00-10:05] 已关闭（end < watermark）");
        System.out.println("  窗口 [10:05-10:10] 已关闭（end <= watermark）");
        System.out.println("  窗口 [10:10-10:15] 仍然打开");
        System.out.println();
    }

    private static void dstreamWindow() {
        System.out.println("【DStream 窗口操作】\n");

        System.out.println("（传统 Spark Streaming API，已过时）");
        System.out.println();

        System.out.println("窗口操作：");
        System.out.println("  // 窗口大小 30 秒，滑动间隔 10 秒");
        System.out.println("  JavaPairDStream<String, Integer> windowedCounts =");
        System.out.println("      wordCounts.reduceByKeyAndWindow(");
        System.out.println("          (a, b) -> a + b,   // 聚合函数");
        System.out.println("          Durations.seconds(30),  // 窗口大小");
        System.out.println("          Durations.seconds(10)   // 滑动间隔");
        System.out.println("      );");
        System.out.println();

        System.out.println("优化的窗口操作（增量计算）：");
        System.out.println("  JavaPairDStream<String, Integer> windowedCounts =");
        System.out.println("      wordCounts.reduceByKeyAndWindow(");
        System.out.println("          (a, b) -> a + b,   // 加入新数据");
        System.out.println("          (a, b) -> a - b,   // 减去旧数据");
        System.out.println("          Durations.seconds(30),");
        System.out.println("          Durations.seconds(10)");
        System.out.println("      );");
        System.out.println();

        System.out.println("其他窗口算子：");
        System.out.println("  countByWindow(windowDuration, slideDuration)");
        System.out.println("  countByValueAndWindow(windowDuration, slideDuration)");
        System.out.println("  window(windowDuration, slideDuration)");
    }
}
