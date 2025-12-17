package phase03;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Phase 3 - Lesson 7: Stream API 高级
 * 
 * 🎯 学习目标:
 * 1. 掌握 Collectors 工具类
 * 2. 学会分组和分区操作
 * 3. 了解并行流
 * 4. 掌握 flatMap 操作
 */
public class StreamAdvanced {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 7: Stream API 高级 ===\n");

        // 准备测试数据
        List<Product> products = List.of(
                new Product("iPhone", "电子", 6999, 100),
                new Product("MacBook", "电子", 12999, 50),
                new Product("AirPods", "电子", 1299, 200),
                new Product("T恤", "服装", 199, 500),
                new Product("牛仔裤", "服装", 399, 300),
                new Product("运动鞋", "服装", 599, 400),
                new Product("咖啡", "食品", 49, 1000),
                new Product("面包", "食品", 15, 800));

        // ==================== 1. Collectors 收集器 ====================
        System.out.println("【1. Collectors 收集器】");

        // toList, toSet
        List<String> names = products.stream()
                .map(Product::name)
                .collect(toList());
        System.out.println("toList: " + names);

        Set<String> categories = products.stream()
                .map(Product::category)
                .collect(toSet());
        System.out.println("toSet: " + categories);

        // toMap
        Map<String, Double> priceMap = products.stream()
                .collect(toMap(Product::name, Product::price));
        System.out.println("toMap: " + priceMap);

        // joining
        String joined = products.stream()
                .map(Product::name)
                .collect(joining(", ", "[", "]"));
        System.out.println("joining: " + joined);

        // ==================== 2. 统计收集器 ====================
        System.out.println("\n【2. 统计收集器】");

        // counting
        long count = products.stream().collect(counting());
        System.out.println("counting: " + count);

        // summingDouble, averagingDouble
        double totalPrice = products.stream()
                .collect(summingDouble(Product::price));
        double avgPrice = products.stream()
                .collect(averagingDouble(Product::price));
        System.out.println("summingDouble: " + totalPrice);
        System.out.println("averagingDouble: " + avgPrice);

        // summarizingDouble
        DoubleSummaryStatistics stats = products.stream()
                .collect(summarizingDouble(Product::price));
        System.out.println("summarizingDouble: " + stats);

        // maxBy, minBy
        Optional<Product> mostExpensive = products.stream()
                .collect(maxBy(Comparator.comparing(Product::price)));
        System.out.println("最贵的产品: " + mostExpensive.orElse(null));

        // ==================== 3. 分组 (groupingBy) ====================
        System.out.println("\n【3. 分组 (groupingBy)】");

        // 简单分组
        Map<String, List<Product>> byCategory = products.stream()
                .collect(groupingBy(Product::category));
        System.out.println("按类别分组:");
        byCategory.forEach((cat, prods) -> System.out.println("  " + cat + ": " + prods.size() + "个产品"));

        // 分组后计数
        Map<String, Long> countByCategory = products.stream()
                .collect(groupingBy(Product::category, counting()));
        System.out.println("各类别数量: " + countByCategory);

        // 分组后求和
        Map<String, Double> sumByCategory = products.stream()
                .collect(groupingBy(
                        Product::category,
                        summingDouble(Product::price)));
        System.out.println("各类别价格总和: " + sumByCategory);

        // 分组后取最大值
        Map<String, Optional<Product>> maxByCategory = products.stream()
                .collect(groupingBy(
                        Product::category,
                        maxBy(Comparator.comparing(Product::price))));
        System.out.println("各类别最贵产品:");
        maxByCategory
                .forEach((cat, prod) -> System.out.println("  " + cat + ": " + prod.map(Product::name).orElse("无")));

        // 多级分组
        Map<String, Map<Boolean, List<Product>>> multiLevel = products.stream()
                .collect(groupingBy(
                        Product::category,
                        partitioningBy(p -> p.price() > 500)));
        System.out.println("多级分组 (类别 -> 是否>500): " + multiLevel.keySet());

        // ==================== 4. 分区 (partitioningBy) ====================
        System.out.println("\n【4. 分区 (partitioningBy)】");

        // 分区: 只能分成两组 (true/false)
        Map<Boolean, List<Product>> partition = products.stream()
                .collect(partitioningBy(p -> p.price() > 1000));
        System.out.println("价格>1000: " + partition.get(true).size() + "个");
        System.out.println("价格<=1000: " + partition.get(false).size() + "个");

        // ==================== 5. flatMap ====================
        System.out.println("\n【5. flatMap (扁平化)】");

        List<List<Integer>> nested = List.of(
                List.of(1, 2, 3),
                List.of(4, 5),
                List.of(6, 7, 8, 9));

        // flatMap 将嵌套结构扁平化
        List<Integer> flat = nested.stream()
                .flatMap(List::stream)
                .collect(toList());
        System.out.println("嵌套: " + nested);
        System.out.println("扁平化: " + flat);

        // 实际应用: 提取所有单词
        List<String> sentences = List.of(
                "Hello World",
                "Java Stream API",
                "Functional Programming");

        List<String> words = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .map(String::toLowerCase)
                .distinct()
                .sorted()
                .collect(toList());
        System.out.println("所有单词: " + words);

        // ==================== 6. 自定义收集器 ====================
        System.out.println("\n【6. reducing 归约】");

        // reducing 更灵活的归约
        Optional<Product> cheapest = products.stream()
                .collect(reducing((p1, p2) -> p1.price() < p2.price() ? p1 : p2));
        System.out.println("最便宜的: " + cheapest.map(Product::name).orElse("无"));

        // 带初始值的 reducing
        double total = products.stream()
                .collect(reducing(0.0, Product::price, Double::sum));
        System.out.println("价格总和: " + total);

        // ==================== 7. 并行流 ====================
        System.out.println("\n【7. 并行流 (parallelStream)】");

        // 创建并行流
        List<Integer> numbers = IntStream.rangeClosed(1, 10)
                .boxed()
                .collect(toList());

        // 串行 vs 并行
        System.out.print("串行: ");
        numbers.stream()
                .map(n -> {
                    System.out.print(Thread.currentThread().getName().substring(0, 4) + " ");
                    return n;
                })
                .forEach(n -> {
                });
        System.out.println();

        System.out.print("并行: ");
        numbers.parallelStream()
                .map(n -> {
                    System.out.print(Thread.currentThread().getName().substring(0, 4) + " ");
                    return n;
                })
                .forEach(n -> {
                });
        System.out.println();

        // 并行性能测试
        int size = 10_000_000;
        List<Integer> bigList = IntStream.range(0, size).boxed().collect(toList());

        long start = System.currentTimeMillis();
        bigList.stream().reduce(0, Integer::sum);
        long serialTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        bigList.parallelStream().reduce(0, Integer::sum);
        long parallelTime = System.currentTimeMillis() - start;

        System.out.println("串行时间: " + serialTime + "ms");
        System.out.println("并行时间: " + parallelTime + "ms");

        // ==================== 8. 实战案例 ====================
        System.out.println("\n【8. 实战案例】");

        // 案例1: 找出每个类别中价格最高的前2个产品
        System.out.println("每类别价格最高的2个产品:");
        products.stream()
                .collect(groupingBy(Product::category))
                .forEach((cat, prods) -> {
                    List<String> top2 = prods.stream()
                            .sorted(Comparator.comparing(Product::price).reversed())
                            .limit(2)
                            .map(Product::name)
                            .collect(toList());
                    System.out.println("  " + cat + ": " + top2);
                });

        // 案例2: 计算库存总价值并按类别汇总
        Map<String, Double> inventoryValue = products.stream()
                .collect(groupingBy(
                        Product::category,
                        summingDouble(p -> p.price() * p.stock())));
        System.out.println("各类别库存价值: " + inventoryValue);

        // 案例3: 价格区间分布
        Map<String, Long> priceRange = products.stream()
                .collect(groupingBy(
                        p -> {
                            if (p.price() < 100)
                                return "0-100";
                            else if (p.price() < 500)
                                return "100-500";
                            else if (p.price() < 1000)
                                return "500-1000";
                            else
                                return "1000+";
                        },
                        counting()));
        System.out.println("价格区间分布: " + priceRange);

        System.out.println("\n✅ Phase 3 - Lesson 7 完成！");
    }
}

record Product(String name, String category, double price, int stock) {
}

/*
 * 📚 知识点总结:
 * 
 * 1. Collectors: toList, toSet, toMap, joining
 * 2. 统计: counting, summingDouble, averagingDouble
 * 3. groupingBy: 分组，支持多级分组
 * 4. partitioningBy: 分区，只分成两组
 * 5. flatMap: 扁平化嵌套结构
 * 6. parallelStream: 并行处理，适合大数据量
 * 7. 注意: 并行流不一定更快，要考虑数据量和操作类型
 * 
 * 🏃 练习:
 * 1. 按年龄段统计用户数量
 * 2. 找出每个部门工资最高的员工
 * 3. 将嵌套的订单商品列表扁平化并统计
 */
