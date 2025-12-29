package phase21;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * MapReduce 编程模型基础
 * 
 * MapReduce 是一种分布式计算编程模型，用于处理和生成大数据集。
 * 用户指定 Map 函数处理 key/value 对生成中间 key/value 对，
 * 然后 Reduce 函数合并所有具有相同中间 key 的中间 value。
 * 
 * 核心思想：分而治之
 * - Map 阶段：将大任务拆分成多个小任务并行处理
 * - Reduce 阶段：汇总 Map 阶段的结果
 * 
 * @author Java Course
 * @since Phase 21
 */
public class MapReduceBasics {

    public static void main(String[] args) {
        System.out.println("=== MapReduce 编程模型基础 ===\n");

        // 1. MapReduce 核心概念
        explainConcepts();

        // 2. MapReduce 执行流程
        explainExecutionFlow();

        // 3. 经典 WordCount 示例
        wordCountDemo();

        // 4. Shuffle 过程详解
        explainShuffle();

        // 5. MapReduce 优化策略
        optimizationStrategies();
    }

    /**
     * MapReduce 核心概念
     */
    private static void explainConcepts() {
        System.out.println("【MapReduce 核心概念】\n");

        System.out.println("1. InputFormat（输入格式）");
        System.out.println("   - 负责读取输入数据并切分为 InputSplit");
        System.out.println("   - 常用：TextInputFormat、SequenceFileInputFormat");
        System.out.println();

        System.out.println("2. InputSplit（输入分片）");
        System.out.println("   - 逻辑切分，一个 Split 对应一个 Map 任务");
        System.out.println("   - 默认大小 = HDFS Block 大小（128MB）");
        System.out.println();

        System.out.println("3. Mapper（映射器）");
        System.out.println("   - 处理输入的 key-value 对");
        System.out.println("   - 输出中间 key-value 对");
        System.out.println("   - 方法：map(key, value, context)");
        System.out.println();

        System.out.println("4. Combiner（本地聚合）");
        System.out.println("   - 在 Map 端进行局部聚合，减少数据传输");
        System.out.println("   - 本质上是一个本地 Reducer");
        System.out.println("   - 适用于满足交换律和结合律的运算");
        System.out.println();

        System.out.println("5. Partitioner（分区器）");
        System.out.println("   - 决定 Map 输出发送到哪个 Reducer");
        System.out.println("   - 默认：HashPartitioner（key.hashCode() % numReducers）");
        System.out.println();

        System.out.println("6. Reducer（归约器）");
        System.out.println("   - 接收所有相同 key 的 value 集合");
        System.out.println("   - 进行汇总计算");
        System.out.println("   - 方法：reduce(key, values, context)");
        System.out.println();

        System.out.println("7. OutputFormat（输出格式）");
        System.out.println("   - 负责将结果写入输出");
        System.out.println("   - 常用：TextOutputFormat、SequenceFileOutputFormat");
        System.out.println();
    }

    /**
     * MapReduce 执行流程
     */
    private static void explainExecutionFlow() {
        System.out.println("【MapReduce 执行流程】\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      MapReduce 作业执行流程                  │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                             │");
        System.out.println("│  ┌─────────┐    ┌─────────┐    ┌─────────┐                 │");
        System.out.println("│  │ Split 1 │    │ Split 2 │    │ Split 3 │    输入分片     │");
        System.out.println("│  └────┬────┘    └────┬────┘    └────┬────┘                 │");
        System.out.println("│       ▼              ▼              ▼                       │");
        System.out.println("│  ┌─────────┐    ┌─────────┐    ┌─────────┐                 │");
        System.out.println("│  │  Map 1  │    │  Map 2  │    │  Map 3  │    Map 阶段     │");
        System.out.println("│  └────┬────┘    └────┬────┘    └────┬────┘                 │");
        System.out.println("│       │              │              │                       │");
        System.out.println("│       └──────────────┼──────────────┘                       │");
        System.out.println("│                      ▼                                      │");
        System.out.println("│              ┌─────────────┐                               │");
        System.out.println("│              │   Shuffle   │        Shuffle & Sort          │");
        System.out.println("│              │    Sort     │                               │");
        System.out.println("│              └──────┬──────┘                               │");
        System.out.println("│         ┌───────────┼───────────┐                          │");
        System.out.println("│         ▼           ▼           ▼                          │");
        System.out.println("│    ┌─────────┐ ┌─────────┐ ┌─────────┐                     │");
        System.out.println("│    │Reduce 1 │ │Reduce 2 │ │Reduce 3 │   Reduce 阶段      │");
        System.out.println("│    └────┬────┘ └────┬────┘ └────┬────┘                     │");
        System.out.println("│         ▼           ▼           ▼                          │");
        System.out.println("│    ┌─────────┐ ┌─────────┐ ┌─────────┐                     │");
        System.out.println("│    │Output 1 │ │Output 2 │ │Output 3 │   输出文件         │");
        System.out.println("│    └─────────┘ └─────────┘ └─────────┘                     │");
        System.out.println("│                                                             │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    /**
     * 经典 WordCount 示例
     * 使用 Java 模拟 MapReduce 的 WordCount 过程
     */
    private static void wordCountDemo() {
        System.out.println("【WordCount 示例】\n");

        // 输入数据（模拟多个文件的内容）
        String[] inputLines = {
                "hello world hello",
                "hadoop spark flink",
                "hello hadoop world",
                "big data hello world"
        };

        System.out.println("输入数据:");
        for (String line : inputLines) {
            System.out.println("  " + line);
        }
        System.out.println();

        // ========== Map 阶段 ==========
        System.out.println("=== Map 阶段 ===");
        List<Map.Entry<String, Integer>> mapOutput = new ArrayList<>();

        for (int i = 0; i < inputLines.length; i++) {
            System.out.printf("Mapper %d 处理: \"%s\"%n", i, inputLines[i]);
            String[] words = inputLines[i].split("\\s+");
            for (String word : words) {
                // Map 输出: (word, 1)
                mapOutput.add(new AbstractMap.SimpleEntry<>(word, 1));
                System.out.printf("  输出: <%s, 1>%n", word);
            }
        }
        System.out.println();

        // ========== Shuffle & Sort 阶段 ==========
        System.out.println("=== Shuffle & Sort 阶段 ===");
        // 按 key 分组
        Map<String, List<Integer>> shuffled = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : mapOutput) {
            shuffled.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .add(entry.getValue());
        }

        for (Map.Entry<String, List<Integer>> entry : shuffled.entrySet()) {
            System.out.printf("  <%s, %s>%n", entry.getKey(), entry.getValue());
        }
        System.out.println();

        // ========== Reduce 阶段 ==========
        System.out.println("=== Reduce 阶段 ===");
        Map<String, Integer> result = new TreeMap<>();

        for (Map.Entry<String, List<Integer>> entry : shuffled.entrySet()) {
            String key = entry.getKey();
            List<Integer> values = entry.getValue();
            // Reduce: 对 values 求和
            int sum = values.stream().mapToInt(Integer::intValue).sum();
            result.put(key, sum);
            System.out.printf("  Reduce <%s, %s> => <%s, %d>%n",
                    key, values, key, sum);
        }
        System.out.println();

        // ========== 最终结果 ==========
        System.out.println("=== 最终结果 ===");
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            System.out.printf("  %s\t%d%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    /**
     * Shuffle 过程详解
     * Shuffle 是 MapReduce 中最核心也是最复杂的部分
     */
    private static void explainShuffle() {
        System.out.println("【Shuffle 过程详解】\n");

        System.out.println("Shuffle 是 Map 阶段到 Reduce 阶段之间的数据传输过程：\n");

        System.out.println("Map 端 Shuffle：");
        System.out.println("  1. Collect（收集）");
        System.out.println("     - Map 输出写入环形缓冲区（默认 100MB）");
        System.out.println("  2. Spill（溢写）");
        System.out.println("     - 缓冲区达到 80% 时，触发溢写到磁盘");
        System.out.println("     - 溢写前进行分区、排序、可选 Combiner");
        System.out.println("  3. Merge（合并）");
        System.out.println("     - 将多个溢写文件合并为一个有序文件");
        System.out.println();

        System.out.println("Reduce 端 Shuffle：");
        System.out.println("  1. Copy（拷贝）");
        System.out.println("     - 从各个 Map 任务拉取属于自己分区的数据");
        System.out.println("  2. Merge（合并）");
        System.out.println("     - 将来自不同 Map 的数据合并");
        System.out.println("  3. Sort（排序）");
        System.out.println("     - 对所有数据进行归并排序");
        System.out.println("     - 相同 key 的数据聚合在一起");
        System.out.println();

        System.out.println("Shuffle 优化要点：");
        System.out.println("  • 增大环形缓冲区：减少溢写次数");
        System.out.println("  • 使用 Combiner：减少网络传输数据量");
        System.out.println("  • 压缩 Map 输出：减少磁盘 IO 和网络传输");
        System.out.println("  • 调整并行度：合理设置 Reduce 任务数");
        System.out.println();
    }

    /**
     * MapReduce 优化策略
     */
    private static void optimizationStrategies() {
        System.out.println("【MapReduce 优化策略】\n");

        System.out.println("1. 数据倾斜处理");
        System.out.println("   问题：某些 key 的数据量远大于其他 key");
        System.out.println("   解决：");
        System.out.println("   - 增加 Reducer 数量");
        System.out.println("   - 对热点 key 加随机前缀打散");
        System.out.println("   - 使用 Combiner 预聚合");
        System.out.println();

        System.out.println("2. 小文件问题");
        System.out.println("   问题：大量小文件导致大量 Map 任务开销");
        System.out.println("   解决：");
        System.out.println("   - 使用 CombineFileInputFormat 合并小文件");
        System.out.println("   - 预先将小文件合并成大文件");
        System.out.println("   - 使用 SequenceFile 存储");
        System.out.println();

        System.out.println("3. 推测执行");
        System.out.println("   问题：个别任务执行慢拖累整体进度");
        System.out.println("   解决：");
        System.out.println("   - 开启推测执行（speculation）");
        System.out.println("   - 同一任务在多个节点并行执行");
        System.out.println("   - 采用先完成的结果");
        System.out.println();

        System.out.println("4. 压缩优化");
        System.out.println("   - Map 输出压缩：减少 Shuffle 数据量");
        System.out.println("   - 最终输出压缩：减少存储空间");
        System.out.println("   - 推荐格式：Snappy（速度快）、LZO（可分片）");
        System.out.println();

        System.out.println("5. JVM 重用");
        System.out.println("   - 减少 JVM 启动开销");
        System.out.println("   - 配置：mapreduce.job.jvm.numtasks");
        System.out.println();
    }
}
