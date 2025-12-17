package phase03.ex;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StreamAdvanced {
    public static void main(String[] args) {

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

        List<String> names = products.stream()
                .map(Product::name)
                .collect(toList());

        Set<String> categories = products.stream()
                .map(Product::category)
                .collect(toSet());

        Map<String, Double> priceMap = products.stream()
                .collect(toMap(Product::name, Product::price));

        String joined = products.stream()
                .map(Product::name)
                .collect(joining(", ", "[", "]"));

        long count = products.stream().collect(counting());

        double totalPrice = products.stream()
                .collect(summingDouble(Product::price));

        double avgPrice = products.stream()
                .collect(averagingDouble(Product::price));
        Optional<Product> mostExpensive = (products.stream().collect(maxBy(Comparator.comparing(Product::price))));

        System.out.println("\n【3. 分组 (groupingBy)】");

        // ==================== 8. 实战案例 ====================
        System.out.println("\n【8. 实战案例】");

        // 案例1: 找出每个类别中价格最高的前2个产品
        System.out.println("每类别价格最高的2个产品:");
        products.stream()
                .collect(groupingBy(Product::category))
                .forEach((cat, prods) -> {
                    List<String> top2 = prods.stream()
                            .sorted(Comparator.comparing(Product::price).reversed()).limit(2).map(Product::name)
                            .collect(toList());
                    System.out.println("  " + cat + ": " + top2);
                });

        Map<String, Double> inventoryValue = products.stream()
                .collect(groupingBy(Product::category, summingDouble(p -> p.price() * p.stock())));
    }
}

record Product(String name, String category, double price, int stock) {
}
