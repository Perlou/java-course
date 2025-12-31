package phase21;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 经典 WordCount 案例
 * 
 * WordCount 是 MapReduce 的 "Hello World" 程序，
 * 用于统计文本中每个单词出现的次数。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class WordCount {

    public static void main(String[] args) {
        System.out.println("=== WordCount 经典案例 ===\n");

        // 1. WordCount 原理
        explainPrinciple();

        // 2. 标准 Hadoop 实现代码
        showHadoopCode();

        // 3. 本地模拟实现
        localDemo();

        // 4. 运行说明
        runInstructions();
    }

    private static void explainPrinciple() {
        System.out.println("【WordCount 原理】\n");

        System.out.println("输入: 多个文本文件");
        System.out.println("输出: <单词, 出现次数>");
        System.out.println();

        System.out.println("处理流程:");
        System.out.println("┌────────────────────────────────────────────────┐");
        System.out.println("│ Input: \"hello world hello\"                    │");
        System.out.println("│                  ↓                             │");
        System.out.println("│ Map: <hello,1> <world,1> <hello,1>             │");
        System.out.println("│                  ↓                             │");
        System.out.println("│ Shuffle: <hello,[1,1]> <world,[1]>             │");
        System.out.println("│                  ↓                             │");
        System.out.println("│ Reduce: <hello,2> <world,1>                    │");
        System.out.println("└────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void showHadoopCode() {
        System.out.println("【Hadoop MapReduce 实现】\n");

        System.out.println("=== Mapper 类 ===");
        System.out.println("public class WordCountMapper");
        System.out.println("    extends Mapper<LongWritable, Text, Text, IntWritable> {");
        System.out.println("    ");
        System.out.println("    private final Text word = new Text();");
        System.out.println("    private final IntWritable one = new IntWritable(1);");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    protected void map(LongWritable key, Text value, Context ctx) {");
        System.out.println("        String[] words = value.toString().split(\"\\\\s+\");");
        System.out.println("        for (String w : words) {");
        System.out.println("            word.set(w.toLowerCase());");
        System.out.println("            ctx.write(word, one);");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();

        System.out.println("=== Reducer 类 ===");
        System.out.println("public class WordCountReducer");
        System.out.println("    extends Reducer<Text, IntWritable, Text, IntWritable> {");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    protected void reduce(Text key, Iterable<IntWritable> values, Context ctx) {");
        System.out.println("        int sum = 0;");
        System.out.println("        for (IntWritable val : values) {");
        System.out.println("            sum += val.get();");
        System.out.println("        }");
        System.out.println("        ctx.write(key, new IntWritable(sum));");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();

        System.out.println("=== Driver 类 ===");
        System.out.println("public class WordCountDriver {");
        System.out.println("    public static void main(String[] args) throws Exception {");
        System.out.println("        Configuration conf = new Configuration();");
        System.out.println("        Job job = Job.getInstance(conf, \"word count\");");
        System.out.println("        job.setJarByClass(WordCountDriver.class);");
        System.out.println("        job.setMapperClass(WordCountMapper.class);");
        System.out.println("        job.setCombinerClass(WordCountReducer.class);");
        System.out.println("        job.setReducerClass(WordCountReducer.class);");
        System.out.println("        job.setOutputKeyClass(Text.class);");
        System.out.println("        job.setOutputValueClass(IntWritable.class);");
        System.out.println("        FileInputFormat.addInputPath(job, new Path(args[0]));");
        System.out.println("        FileOutputFormat.setOutputPath(job, new Path(args[1]));");
        System.out.println("        System.exit(job.waitForCompletion(true) ? 0 : 1);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
    }

    private static void localDemo() {
        System.out.println("【本地模拟演示】\n");

        String[] inputLines = {
                "hello world hello hadoop",
                "hadoop spark flink",
                "hello spark world",
                "big data hadoop flink"
        };

        System.out.println("输入数据:");
        for (String line : inputLines) {
            System.out.println("  " + line);
        }
        System.out.println();

        // Map 阶段
        System.out.println("=== Map 阶段 ===");
        List<Map.Entry<String, Integer>> mapOutput = new ArrayList<>();
        for (String line : inputLines) {
            for (String word : line.split("\\s+")) {
                mapOutput.add(new AbstractMap.SimpleEntry<>(word, 1));
                System.out.printf("  emit: <%s, 1>%n", word);
            }
        }
        System.out.println();

        // Shuffle & Sort
        System.out.println("=== Shuffle & Sort ===");
        Map<String, List<Integer>> shuffled = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : mapOutput) {
            shuffled.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .add(entry.getValue());
        }
        for (Map.Entry<String, List<Integer>> entry : shuffled.entrySet()) {
            System.out.printf("  <%s, %s>%n", entry.getKey(), entry.getValue());
        }
        System.out.println();

        // Reduce 阶段
        System.out.println("=== Reduce 阶段 ===");
        Map<String, Integer> result = new TreeMap<>();
        for (Map.Entry<String, List<Integer>> entry : shuffled.entrySet()) {
            int sum = entry.getValue().stream().mapToInt(Integer::intValue).sum();
            result.put(entry.getKey(), sum);
            System.out.printf("  <%s, %d>%n", entry.getKey(), sum);
        }
        System.out.println();

        System.out.println("=== 最终结果 ===");
        result.forEach((k, v) -> System.out.printf("  %s\t%d%n", k, v));
    }

    private static void runInstructions() {
        System.out.println("\n【运行说明】\n");
        System.out.println("1. 打包 JAR:");
        System.out.println("   mvn clean package");
        System.out.println();
        System.out.println("2. 提交到 Hadoop:");
        System.out.println("   hadoop jar wordcount.jar WordCountDriver /input /output");
        System.out.println();
        System.out.println("3. 查看结果:");
        System.out.println("   hdfs dfs -cat /output/part-r-00000");
    }
}
