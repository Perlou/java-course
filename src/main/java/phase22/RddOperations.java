package phase22;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RDD 转换与行动算子
 * 
 * RDD 算子分为两类：
 * - 转换算子 (Transformation)：延迟执行，返回新 RDD
 * - 行动算子 (Action)：触发计算，返回结果
 * 
 * @author Java Course
 * @since Phase 22
 */
public class RddOperations {

    public static void main(String[] args) {
        System.out.println("=== RDD 转换与行动算子 ===\n");

        transformationOperators();
        actionOperators();
        keyValueOperators();
        localDemo();
    }

    private static void transformationOperators() {
        System.out.println("【转换算子 Transformation】\n");
        System.out.println("特点：延迟执行(Lazy)，返回新 RDD，不触发计算\n");

        System.out.println("=== 常用转换算子 ===");
        System.out.println();

        System.out.println("1. map(func)");
        System.out.println("   对每个元素应用函数，返回新 RDD");
        System.out.println("   rdd.map(x -> x * 2)  // [1,2,3] -> [2,4,6]");
        System.out.println();

        System.out.println("2. flatMap(func)");
        System.out.println("   对每个元素应用函数，展平结果");
        System.out.println("   rdd.flatMap(line -> Arrays.asList(line.split(\" \")))");
        System.out.println("   // [\"a b\", \"c d\"] -> [\"a\",\"b\",\"c\",\"d\"]");
        System.out.println();

        System.out.println("3. filter(func)");
        System.out.println("   过滤满足条件的元素");
        System.out.println("   rdd.filter(x -> x > 10)  // [5,15,20] -> [15,20]");
        System.out.println();

        System.out.println("4. distinct()");
        System.out.println("   去重（需要 Shuffle）");
        System.out.println("   rdd.distinct()  // [1,1,2,2,3] -> [1,2,3]");
        System.out.println();

        System.out.println("5. union(otherRdd)");
        System.out.println("   合并两个 RDD");
        System.out.println("   rdd1.union(rdd2)  // [1,2] + [3,4] -> [1,2,3,4]");
        System.out.println();

        System.out.println("6. intersection(otherRdd)");
        System.out.println("   取交集（需要 Shuffle）");
        System.out.println("   rdd1.intersection(rdd2)  // [1,2,3] ∩ [2,3,4] -> [2,3]");
        System.out.println();

        System.out.println("7. subtract(otherRdd)");
        System.out.println("   取差集（需要 Shuffle）");
        System.out.println("   rdd1.subtract(rdd2)  // [1,2,3] - [2,3,4] -> [1]");
        System.out.println();

        System.out.println("8. repartition(n) / coalesce(n)");
        System.out.println("   重新分区");
        System.out.println("   repartition: 增加或减少分区（Shuffle）");
        System.out.println("   coalesce: 减少分区（可选是否 Shuffle）");
        System.out.println();

        System.out.println("9. sortBy(func)");
        System.out.println("   排序（需要 Shuffle）");
        System.out.println("   rdd.sortBy(x -> x, false)  // 降序排序");
        System.out.println();
    }

    private static void actionOperators() {
        System.out.println("【行动算子 Action】\n");
        System.out.println("特点：触发计算，返回结果到 Driver 或写入存储\n");

        System.out.println("=== 常用行动算子 ===");
        System.out.println();

        System.out.println("1. collect()");
        System.out.println("   返回所有元素到 Driver（注意内存溢出）");
        System.out.println("   List<Integer> result = rdd.collect();");
        System.out.println();

        System.out.println("2. count()");
        System.out.println("   返回元素个数");
        System.out.println("   long n = rdd.count();");
        System.out.println();

        System.out.println("3. first() / take(n)");
        System.out.println("   返回第一个或前 n 个元素");
        System.out.println("   Integer first = rdd.first();");
        System.out.println("   List<Integer> top5 = rdd.take(5);");
        System.out.println();

        System.out.println("4. reduce(func)");
        System.out.println("   使用函数聚合所有元素");
        System.out.println("   int sum = rdd.reduce((a, b) -> a + b);");
        System.out.println();

        System.out.println("5. aggregate(zeroValue, seqOp, combOp)");
        System.out.println("   分区内聚合 + 分区间聚合");
        System.out.println();

        System.out.println("6. foreach(func)");
        System.out.println("   遍历每个元素（在 Executor 执行）");
        System.out.println("   rdd.foreach(x -> System.out.println(x));");
        System.out.println();

        System.out.println("7. saveAsTextFile(path)");
        System.out.println("   保存到文本文件");
        System.out.println("   rdd.saveAsTextFile(\"hdfs://output\");");
        System.out.println();

        System.out.println("8. countByValue()");
        System.out.println("   统计每个值出现的次数");
        System.out.println("   Map<String, Long> counts = rdd.countByValue();");
        System.out.println();
    }

    private static void keyValueOperators() {
        System.out.println("【Key-Value 算子】\n");
        System.out.println("用于 PairRDD (键值对 RDD)\n");

        System.out.println("=== 转换算子 ===");
        System.out.println();

        System.out.println("1. mapValues(func)");
        System.out.println("   只对 value 应用函数");
        System.out.println("   rdd.mapValues(v -> v * 2)");
        System.out.println();

        System.out.println("2. groupByKey()");
        System.out.println("   按 key 分组（需要 Shuffle，可能 OOM）");
        System.out.println("   [(a,1),(a,2),(b,3)] -> [(a,[1,2]),(b,[3])]");
        System.out.println();

        System.out.println("3. reduceByKey(func)");
        System.out.println("   按 key 聚合（推荐，有 Combiner 优化）");
        System.out.println("   rdd.reduceByKey((a, b) -> a + b)");
        System.out.println();

        System.out.println("4. aggregateByKey(zeroValue, seqOp, combOp)");
        System.out.println("   按 key 聚合，分区内和分区间可用不同函数");
        System.out.println();

        System.out.println("5. sortByKey(ascending)");
        System.out.println("   按 key 排序");
        System.out.println("   rdd.sortByKey(true)  // 升序");
        System.out.println();

        System.out.println("6. join(otherRdd)");
        System.out.println("   内连接（需要 Shuffle）");
        System.out.println("   rdd1.join(rdd2)  // 按 key 连接");
        System.out.println();

        System.out.println("7. leftOuterJoin / rightOuterJoin / fullOuterJoin");
        System.out.println("   外连接");
        System.out.println();

        System.out.println("8. cogroup(otherRdd)");
        System.out.println("   按 key 分组合并多个 RDD");
        System.out.println();

        System.out.println("=== 行动算子 ===");
        System.out.println();
        System.out.println("1. countByKey()");
        System.out.println("   统计每个 key 的个数");
        System.out.println();
        System.out.println("2. collectAsMap()");
        System.out.println("   收集为 Map（key 重复时只保留一个）");
        System.out.println();
    }

    private static void localDemo() {
        System.out.println("【本地模拟演示】\n");

        List<String> lines = Arrays.asList(
                "hello world hello",
                "spark hadoop spark",
                "hello spark world");

        System.out.println("输入数据: " + lines);
        System.out.println();

        // 模拟 WordCount
        System.out.println("=== 模拟 WordCount ===");
        System.out.println();

        // flatMap
        System.out.println("1. flatMap: 拆分单词");
        List<String> words = lines.stream()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .collect(Collectors.toList());
        System.out.println("   " + words);

        // map
        System.out.println("\n2. map: 生成 (word, 1)");
        Map<String, List<Integer>> mapped = words.stream()
                .collect(Collectors.groupingBy(
                        w -> w,
                        Collectors.mapping(w -> 1, Collectors.toList())));
        mapped.forEach((k, v) -> System.out.printf("   <%s, %s>%n", k, v));

        // reduceByKey
        System.out.println("\n3. reduceByKey: 聚合计数");
        Map<String, Long> result = words.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        result.forEach((k, v) -> System.out.printf("   <%s, %d>%n", k, v));

        // sortBy
        System.out.println("\n4. sortBy: 按计数降序");
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("   <%s, %d>%n", e.getKey(), e.getValue()));
    }
}
