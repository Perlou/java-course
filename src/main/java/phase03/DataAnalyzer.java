package phase03;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Phase 3 - 实战项目: 数据分析工具
 * 
 * 🎯 项目目标:
 * 1. 综合运用 Phase 3 所学的集合框架和 Stream API
 * 2. 实践数据处理、统计分析、分组聚合
 * 3. 构建一个可交互的数据分析工具
 */
public class DataAnalyzer {

    private static final Scanner scanner = new Scanner(System.in);
    private static List<Order> orders;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║       📊 数据分析工具 v1.0                  ║");
        System.out.println("║       Phase 3 综合实战项目                  ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        // 初始化数据
        orders = generateSampleData();
        System.out.println("✅ 已加载 " + orders.size() + " 条订单数据\n");

        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllOrders();
                case "2" -> analyzeByCategory();
                case "3" -> analyzeByMonth();
                case "4" -> topProducts();
                case "5" -> customerAnalysis();
                case "6" -> searchOrders();
                case "7" -> priceRangeAnalysis();
                case "8" -> customQuery();
                case "0" -> {
                    running = false;
                    System.out.println("感谢使用，再见！👋");
                }
                default -> System.out.println("❌ 无效选项");
            }
            System.out.println();
        }
    }

    private static void showMenu() {
        System.out.println("┌────────────────────────────────────────────┐");
        System.out.println("│                  分析菜单                  │");
        System.out.println("├────────────────────────────────────────────┤");
        System.out.println("│  1. 查看所有订单                           │");
        System.out.println("│  2. 按类别统计                             │");
        System.out.println("│  3. 按月份统计                             │");
        System.out.println("│  4. 热销产品 Top N                         │");
        System.out.println("│  5. 客户消费分析                           │");
        System.out.println("│  6. 搜索订单                               │");
        System.out.println("│  7. 价格区间分布                           │");
        System.out.println("│  8. 自定义查询                             │");
        System.out.println("│  0. 退出                                   │");
        System.out.println("└────────────────────────────────────────────┘");
        System.out.print("请选择: ");
    }

    // 1. 显示所有订单
    private static void showAllOrders() {
        System.out.println("\n📋 所有订单 (前20条):");
        System.out.println("─".repeat(80));
        orders.stream()
                .limit(20)
                .forEach(System.out::println);
        System.out.println("─".repeat(80));

        DoubleSummaryStatistics stats = orders.stream()
                .collect(summarizingDouble(Order::amount));
        System.out.printf("总订单: %d | 总金额: ¥%.2f | 平均: ¥%.2f | 最高: ¥%.2f%n",
                stats.getCount(), stats.getSum(), stats.getAverage(), stats.getMax());
    }

    // 2. 按类别统计
    private static void analyzeByCategory() {
        System.out.println("\n📊 按类别统计:");
        System.out.println("─".repeat(50));

        Map<String, DoubleSummaryStatistics> byCategory = orders.stream()
                .collect(groupingBy(Order::category,
                        summarizingDouble(Order::amount)));

        byCategory.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().getSum(), e1.getValue().getSum()))
                .forEach(e -> {
                    DoubleSummaryStatistics s = e.getValue();
                    System.out.printf("  %-10s | 订单: %3d | 总额: ¥%10.2f | 均值: ¥%.2f%n",
                            e.getKey(), s.getCount(), s.getSum(), s.getAverage());
                });
    }

    // 3. 按月份统计
    private static void analyzeByMonth() {
        System.out.println("\n📅 按月份统计:");
        System.out.println("─".repeat(50));

        Map<String, Double> byMonth = orders.stream()
                .collect(groupingBy(Order::month,
                        TreeMap::new,
                        summingDouble(Order::amount)));

        byMonth.forEach((month, total) -> {
            int barLength = (int) (total / 5000);
            System.out.printf("  %s | ¥%10.2f | %s%n",
                    month, total, "█".repeat(Math.max(1, barLength)));
        });
    }

    // 4. 热销产品
    private static void topProducts() {
        System.out.print("输入显示数量 (默认10): ");
        String input = scanner.nextLine().trim();
        int n = input.isEmpty() ? 10 : Integer.parseInt(input);

        System.out.println("\n🔥 热销产品 Top " + n + ":");
        System.out.println("─".repeat(50));

        Map<String, Long> productCount = orders.stream()
                .collect(groupingBy(Order::product, counting()));

        productCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .forEach(e -> System.out.printf("  %-15s | 销量: %d%n",
                        e.getKey(), e.getValue()));
    }

    // 5. 客户分析
    private static void customerAnalysis() {
        System.out.println("\n👥 客户消费分析:");
        System.out.println("─".repeat(60));

        // 客户消费总额排名
        Map<String, Double> customerTotal = orders.stream()
                .collect(groupingBy(Order::customer,
                        summingDouble(Order::amount)));

        System.out.println("消费 Top 5 客户:");
        customerTotal.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> System.out.printf("  %-10s | ¥%.2f%n",
                        e.getKey(), e.getValue()));

        // 客户订单频次
        System.out.println("\n订单频次 Top 5:");
        Map<String, Long> customerCount = orders.stream()
                .collect(groupingBy(Order::customer, counting()));

        customerCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> System.out.printf("  %-10s | %d 单%n",
                        e.getKey(), e.getValue()));

        // 客户平均单价
        System.out.println("\n平均客单价 Top 5:");
        Map<String, Double> avgSpend = orders.stream()
                .collect(groupingBy(Order::customer,
                        averagingDouble(Order::amount)));

        avgSpend.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> System.out.printf("  %-10s | ¥%.2f%n",
                        e.getKey(), e.getValue()));
    }

    // 6. 搜索订单
    private static void searchOrders() {
        System.out.print("输入搜索关键词 (产品名/客户名): ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        List<Order> results = orders.stream()
                .filter(o -> o.product().toLowerCase().contains(keyword) ||
                        o.customer().toLowerCase().contains(keyword))
                .collect(toList());

        System.out.println("\n🔍 搜索结果 (" + results.size() + " 条):");
        System.out.println("─".repeat(80));
        results.stream().limit(20).forEach(System.out::println);

        if (results.size() > 20) {
            System.out.println("... 还有 " + (results.size() - 20) + " 条未显示");
        }
    }

    // 7. 价格区间分析
    private static void priceRangeAnalysis() {
        System.out.println("\n💰 价格区间分布:");
        System.out.println("─".repeat(50));

        Map<String, Long> priceRange = orders.stream()
                .collect(groupingBy(o -> {
                    double amt = o.amount();
                    if (amt < 100)
                        return "0-100";
                    else if (amt < 500)
                        return "100-500";
                    else if (amt < 1000)
                        return "500-1000";
                    else if (amt < 5000)
                        return "1000-5000";
                    else
                        return "5000+";
                }, TreeMap::new, counting()));

        long total = orders.size();
        priceRange.forEach((range, count) -> {
            double percent = count * 100.0 / total;
            int barLen = (int) (percent / 2);
            System.out.printf("  %-12s | %4d (%5.1f%%) | %s%n",
                    range, count, percent, "█".repeat(Math.max(1, barLen)));
        });
    }

    // 8. 自定义查询
    private static void customQuery() {
        System.out.println("\n🔧 自定义查询");
        System.out.println("  1. 金额大于指定值");
        System.out.println("  2. 指定类别的订单");
        System.out.println("  3. 指定月份的订单");
        System.out.print("选择查询类型: ");

        String type = scanner.nextLine().trim();

        switch (type) {
            case "1" -> {
                System.out.print("输入最小金额: ");
                double minAmount = Double.parseDouble(scanner.nextLine().trim());
                orders.stream()
                        .filter(o -> o.amount() >= minAmount)
                        .sorted(Comparator.comparing(Order::amount).reversed())
                        .limit(20)
                        .forEach(System.out::println);
            }
            case "2" -> {
                Set<String> categories = orders.stream()
                        .map(Order::category)
                        .collect(toSet());
                System.out.println("可选类别: " + categories);
                System.out.print("输入类别: ");
                String cat = scanner.nextLine().trim();
                orders.stream()
                        .filter(o -> o.category().equals(cat))
                        .limit(20)
                        .forEach(System.out::println);
            }
            case "3" -> {
                Set<String> months = orders.stream()
                        .map(Order::month)
                        .collect(TreeSet::new, Set::add, Set::addAll);
                System.out.println("可选月份: " + months);
                System.out.print("输入月份: ");
                String month = scanner.nextLine().trim();
                orders.stream()
                        .filter(o -> o.month().equals(month))
                        .limit(20)
                        .forEach(System.out::println);
            }
            default -> System.out.println("无效选项");
        }
    }

    // 生成测试数据
    private static List<Order> generateSampleData() {
        String[] products = { "iPhone", "MacBook", "iPad", "AirPods", "Apple Watch",
                "T恤", "牛仔裤", "运动鞋", "夹克", "帽子",
                "咖啡", "面包", "牛奶", "果汁", "零食" };
        String[] categories = { "电子", "电子", "电子", "电子", "电子",
                "服装", "服装", "服装", "服装", "服装",
                "食品", "食品", "食品", "食品", "食品" };
        double[] prices = { 6999, 12999, 3999, 1299, 2999,
                199, 399, 599, 899, 99,
                49, 15, 25, 18, 12 };
        String[] customers = { "张三", "李四", "王五", "赵六", "钱七",
                "孙八", "周九", "吴十", "郑一", "冯二" };
        String[] months = { "2024-01", "2024-02", "2024-03", "2024-04",
                "2024-05", "2024-06" };

        Random random = new Random(42);
        List<Order> data = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            int idx = random.nextInt(products.length);
            double amount = prices[idx] * (0.8 + random.nextDouble() * 0.4);
            data.add(new Order(
                    "ORD" + String.format("%04d", i + 1),
                    products[idx],
                    categories[idx],
                    customers[random.nextInt(customers.length)],
                    months[random.nextInt(months.length)],
                    Math.round(amount * 100.0) / 100.0));
        }

        return data;
    }
}

record Order(String id, String product, String category,
        String customer, String month, double amount) {
    @Override
    public String toString() {
        return String.format("[%s] %-10s | %-6s | %-4s | %s | ¥%.2f",
                id, product, category, customer, month, amount);
    }
}

/*
 * 📚 项目总结:
 * 
 * 本项目综合运用了 Phase 3 的核心知识:
 * - List/Set/Map 集合操作
 * - Stream API: filter, map, collect, groupingBy
 * - Collectors: counting, summingDouble, averagingDouble
 * - Optional 处理空值
 * - 泛型结合集合使用
 * - Record 定义数据类
 * 
 * 🎯 扩展任务:
 * 1. 添加数据导出功能 (CSV/JSON)
 * 2. 实现更复杂的多条件筛选
 * 3. 添加时间序列分析
 * 4. 实现数据可视化 (文本图表)
 */
