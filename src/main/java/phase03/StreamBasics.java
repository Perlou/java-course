package phase03;

import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Phase 3 - Lesson 6: Stream API 基础
 * 
 * 🎯 学习目标:
 * 1. 理解 Stream 的概念
 * 2. 掌握 Stream 的创建方式
 * 3. 学会使用中间操作和终端操作
 * 4. 理解惰性求值
 */
public class StreamBasics {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 6: Stream API 基础 ===\n");

        // ==================== 1. 什么是 Stream ====================
        System.out.println("【1. 什么是 Stream】");
        System.out.println("Stream 是 Java 8 引入的函数式编程风格的数据处理 API");
        System.out.println("特点:");
        System.out.println("  - 不存储数据");
        System.out.println("  - 不修改源数据");
        System.out.println("  - 惰性求值 (延迟执行)");
        System.out.println("  - 只能消费一次");

        // ==================== 2. 创建 Stream ====================
        System.out.println("\n【2. 创建 Stream】");

        // 从集合创建
        List<String> list = List.of("a", "b", "c");
        Stream<String> stream1 = list.stream();
        System.out.println("从 List 创建: " + stream1);

        // 从数组创建
        String[] arr = { "x", "y", "z" };
        Stream<String> stream2 = Arrays.stream(arr);

        // 直接创建
        Stream<Integer> stream3 = Stream.of(1, 2, 3);

        // 无限流
        Stream<Integer> infinite = Stream.iterate(0, n -> n + 2);
        System.out.print("无限流 (前5个偶数): ");
        infinite.limit(5).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // generate
        Stream<Double> randoms = Stream.generate(Math::random);
        System.out.print("随机数 (前3个): ");
        randoms.limit(3).forEach(n -> System.out.printf("%.2f ", n));
        System.out.println();

        // 范围
        IntStream.range(1, 5).forEach(n -> System.out.print(n + " "));
        System.out.println("<- IntStream.range(1, 5)");

        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " "));
        System.out.println("<- IntStream.rangeClosed(1, 5)");

        // ==================== 3. 中间操作 (Intermediate) ====================
        System.out.println("\n【3. 中间操作 (返回 Stream)】");

        List<Integer> numbers = List.of(5, 2, 8, 1, 9, 3, 7, 4, 6);

        // filter: 过滤
        System.out.print("filter (偶数): ");
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // map: 转换
        System.out.print("map (平方): ");
        numbers.stream()
                .map(n -> n * n)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // sorted: 排序
        System.out.print("sorted: ");
        numbers.stream()
                .sorted()
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // distinct: 去重
        List<Integer> dups = List.of(1, 2, 2, 3, 3, 3);
        System.out.print("distinct: ");
        dups.stream()
                .distinct()
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // limit & skip
        System.out.print("skip(2).limit(3): ");
        numbers.stream()
                .skip(2)
                .limit(3)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // peek: 调试
        System.out.print("peek: ");
        numbers.stream()
                .filter(n -> n > 5)
                .peek(n -> System.out.print("[" + n + "] "))
                .map(n -> n * 2)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // ==================== 4. 终端操作 (Terminal) ====================
        System.out.println("\n【4. 终端操作 (产生结果)】");

        // forEach
        System.out.print("forEach: ");
        numbers.stream().limit(3).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // count
        long count = numbers.stream().filter(n -> n > 5).count();
        System.out.println("count (>5): " + count);

        // collect
        List<Integer> collected = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("collect: " + collected);

        // toList (Java 16+)
        List<Integer> toList = numbers.stream()
                .filter(n -> n < 5)
                .toList();
        System.out.println("toList: " + toList);

        // reduce
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println("reduce (sum): " + sum);

        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("reduce (max): " + max.orElse(0));

        // min/max
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        System.out.println("min: " + min.orElse(0));

        // findFirst/findAny
        Optional<Integer> first = numbers.stream().filter(n -> n > 5).findFirst();
        System.out.println("findFirst (>5): " + first.orElse(-1));

        // anyMatch/allMatch/noneMatch
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("anyEven: " + anyEven + ", allPositive: " + allPositive + ", noneNegative: " + noneNegative);

        // ==================== 5. 链式操作 ====================
        System.out.println("\n【5. 链式操作】");

        List<String> words = List.of("apple", "banana", "cherry", "date", "elderberry");

        String result = words.stream()
                .filter(w -> w.length() > 4) // 长度 > 4
                .map(String::toUpperCase) // 转大写
                .sorted() // 排序
                .collect(Collectors.joining(", ")); // 连接

        System.out.println("链式操作结果: " + result);

        // ==================== 6. 惰性求值 ====================
        System.out.println("\n【6. 惰性求值】");
        System.out.println("中间操作不会立即执行，只有遇到终端操作才执行");

        Stream<Integer> lazyStream = numbers.stream()
                .filter(n -> {
                    System.out.println("  filter: " + n);
                    return n > 5;
                })
                .map(n -> {
                    System.out.println("  map: " + n);
                    return n * 2;
                });

        System.out.println("Stream 创建完成，但还未执行");
        System.out.println("调用 findFirst():");
        Optional<Integer> lazyResult = lazyStream.findFirst();
        System.out.println("结果: " + lazyResult.orElse(-1));

        // ==================== 7. 基本类型 Stream ====================
        System.out.println("\n【7. 基本类型 Stream】");

        // IntStream, LongStream, DoubleStream 避免装箱
        int sumInt = IntStream.of(1, 2, 3, 4, 5).sum();
        System.out.println("IntStream.sum(): " + sumInt);

        double avg = IntStream.range(1, 101).average().orElse(0);
        System.out.println("1-100 平均值: " + avg);

        IntSummaryStatistics stats = IntStream.of(1, 2, 3, 4, 5).summaryStatistics();
        System.out.println("统计: count=" + stats.getCount() +
                ", sum=" + stats.getSum() +
                ", avg=" + stats.getAverage() +
                ", min=" + stats.getMin() +
                ", max=" + stats.getMax());

        // ==================== 8. 实际应用示例 ====================
        System.out.println("\n【8. 实际应用示例】");

        List<Employee> employees = List.of(
                new Employee("张三", "开发", 15000),
                new Employee("李四", "开发", 18000),
                new Employee("王五", "测试", 12000),
                new Employee("赵六", "开发", 20000),
                new Employee("钱七", "测试", 14000));

        // 找出开发部门薪资最高的员工
        Optional<Employee> topDev = employees.stream()
                .filter(e -> e.dept().equals("开发"))
                .max(Comparator.comparing(Employee::salary));
        System.out.println("开发部最高薪资: " + topDev.orElse(null));

        // 按部门分组统计平均薪资
        Map<String, Double> avgByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::dept,
                        Collectors.averagingDouble(Employee::salary)));
        System.out.println("各部门平均薪资: " + avgByDept);

        // 薪资总和
        double totalSalary = employees.stream()
                .mapToDouble(Employee::salary)
                .sum();
        System.out.println("薪资总和: " + totalSalary);

        System.out.println("\n✅ Phase 3 - Lesson 6 完成！");
    }
}

record Employee(String name, String dept, double salary) {
}

/*
 * 📚 知识点总结:
 * 
 * 1. Stream 不存储数据，不修改源数据
 * 2. 创建: stream(), Stream.of(), Arrays.stream()
 * 3. 中间操作: filter, map, sorted, distinct, limit, skip
 * 4. 终端操作: forEach, collect, reduce, count, min/max
 * 5. 惰性求值: 中间操作延迟执行
 * 6. 基本类型 Stream: IntStream, LongStream, DoubleStream
 * 7. 只能消费一次
 * 
 * 🏃 练习:
 * 1. 用 Stream 实现列表去重并排序
 * 2. 统计一段文本中每个单词出现的次数
 * 3. 找出年龄最大的前3个用户
 */
