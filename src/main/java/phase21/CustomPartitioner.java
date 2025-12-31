package phase21;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 自定义分区器
 * 
 * Partitioner 决定 Map 输出的数据发送到哪个 Reducer。
 * 默认使用 HashPartitioner，可以自定义实现特殊分区需求。
 * 
 * @author Java Course
 * @since Phase 21
 */
public class CustomPartitioner {

    public static void main(String[] args) {
        System.out.println("=== 自定义分区器 ===\n");

        explainPartitioner();
        defaultPartitioner();
        customPartitionerExample();
        localDemo();
        useCases();
    }

    private static void explainPartitioner() {
        System.out.println("【Partitioner 作用】\n");
        System.out.println("Partitioner 决定 Map 输出的 key-value 发送到哪个 Reducer：");
        System.out.println();
        System.out.println("  Map Output          Partitioner         Reducer");
        System.out.println("  ┌─────────┐         ┌───────┐          ┌─────────┐");
        System.out.println("  │ <A, 1>  │ ───────>│       │ ───────> │ Reducer0│");
        System.out.println("  │ <B, 1>  │         │ Hash  │          └─────────┘");
        System.out.println("  │ <C, 1>  │ ───────>│   %   │ ───────> ┌─────────┐");
        System.out.println("  │ <D, 1>  │         │ Num   │          │ Reducer1│");
        System.out.println("  └─────────┘         └───────┘          └─────────┘");
        System.out.println();
    }

    private static void defaultPartitioner() {
        System.out.println("【默认 HashPartitioner】\n");
        System.out.println("public class HashPartitioner<K, V> extends Partitioner<K, V> {");
        System.out.println("    public int getPartition(K key, V value, int numReducers) {");
        System.out.println("        return (key.hashCode() & Integer.MAX_VALUE) % numReducers;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        System.out.println("特点：");
        System.out.println("  • 基于 key 的 hashCode 分区");
        System.out.println("  • 相同 key 一定分到同一个 Reducer");
        System.out.println("  • 数据分布可能不均匀");
        System.out.println();
    }

    private static void customPartitionerExample() {
        System.out.println("【自定义分区器示例】\n");

        System.out.println("例1：按首字母分区");
        System.out.println("─────────────────────────────────");
        System.out.println("public class AlphabetPartitioner extends Partitioner<Text, IntWritable> {");
        System.out.println("    @Override");
        System.out.println("    public int getPartition(Text key, IntWritable value, int numReducers) {");
        System.out.println("        String word = key.toString();");
        System.out.println("        if (word.isEmpty()) return 0;");
        System.out.println("        char first = Character.toLowerCase(word.charAt(0));");
        System.out.println("        if (first >= 'a' && first <= 'z') {");
        System.out.println("            return (first - 'a') % numReducers;");
        System.out.println("        }");
        System.out.println("        return 0;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();

        System.out.println("例2：按省份分区（订单数据）");
        System.out.println("─────────────────────────────────");
        System.out.println("public class ProvincePartitioner extends Partitioner<Text, OrderBean> {");
        System.out.println("    private static Map<String, Integer> provinceMap = new HashMap<>();");
        System.out.println("    static {");
        System.out.println("        provinceMap.put(\"北京\", 0);");
        System.out.println("        provinceMap.put(\"上海\", 1);");
        System.out.println("        provinceMap.put(\"广东\", 2);");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    @Override");
        System.out.println("    public int getPartition(Text key, OrderBean value, int numReducers) {");
        System.out.println("        return provinceMap.getOrDefault(value.getProvince(), 3);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();

        System.out.println("Driver 配置：");
        System.out.println("  job.setPartitionerClass(AlphabetPartitioner.class);");
        System.out.println("  job.setNumReduceTasks(4);  // 必须设置 Reducer 数量");
        System.out.println();
    }

    private static void localDemo() {
        System.out.println("【本地模拟演示】\n");

        String[] words = { "apple", "banana", "cherry", "apricot", "blueberry", "avocado" };
        int numReducers = 3;

        System.out.printf("模拟数据: %s%n", Arrays.toString(words));
        System.out.printf("Reducer 数量: %d%n%n", numReducers);

        // HashPartitioner
        System.out.println("=== HashPartitioner 分区结果 ===");
        Map<Integer, List<String>> hashResult = new TreeMap<>();
        for (String word : words) {
            int partition = (word.hashCode() & Integer.MAX_VALUE) % numReducers;
            hashResult.computeIfAbsent(partition, k -> new ArrayList<>()).add(word);
        }
        hashResult.forEach((k, v) -> System.out.printf("  Reducer %d: %s%n", k, v));
        System.out.println();

        // AlphabetPartitioner
        System.out.println("=== AlphabetPartitioner 分区结果 ===");
        Map<Integer, List<String>> alphaResult = new TreeMap<>();
        for (String word : words) {
            int partition = (word.charAt(0) - 'a') % numReducers;
            alphaResult.computeIfAbsent(partition, k -> new ArrayList<>()).add(word);
        }
        alphaResult.forEach((k, v) -> System.out.printf("  Reducer %d: %s%n", k, v));
        System.out.println();
    }

    private static void useCases() {
        System.out.println("【使用场景】\n");
        System.out.println("1. 数据分类输出 - 按类型分到不同文件");
        System.out.println("2. 负载均衡 - 避免数据倾斜");
        System.out.println("3. 全局排序 - 使用全局分区边界");
        System.out.println("4. 二次排序 - 配合组合 key 实现");
    }
}
