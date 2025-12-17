package phase03;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Phase 3 - Lesson 9: Optional
 * 
 * 🎯 学习目标:
 * 1. 理解 Optional 的作用
 * 2. 掌握 Optional 的创建和使用
 * 3. 学会正确使用 Optional 避免 NPE
 * 4. 了解 Optional 的链式操作
 */
public class OptionalDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 9: Optional ===\n");

        // ==================== 1. 为什么需要 Optional ====================
        System.out.println("【1. 为什么需要 Optional】");
        System.out.println("Optional 是用于表示可能为空的值的容器");
        System.out.println("目的: 消除 NullPointerException，让空值处理更明确");

        // 传统方式 (容易 NPE)
        String name = null;
        // System.out.println(name.length()); // NPE!

        // Optional 方式
        Optional<String> optName = Optional.ofNullable(name);
        System.out.println("Optional 是否有值: " + optName.isPresent());

        // ==================== 2. 创建 Optional ====================
        System.out.println("\n【2. 创建 Optional】");

        // of: 值必须非空
        Optional<String> opt1 = Optional.of("Hello");
        System.out.println("Optional.of: " + opt1);

        // ofNullable: 值可以为空
        Optional<String> opt2 = Optional.ofNullable(null);
        System.out.println("Optional.ofNullable(null): " + opt2);

        // empty: 空 Optional
        Optional<String> opt3 = Optional.empty();
        System.out.println("Optional.empty(): " + opt3);

        // ==================== 3. 获取值 ====================
        System.out.println("\n【3. 获取值】");

        Optional<String> opt = Optional.of("Java");
        Optional<String> emptyOpt = Optional.empty();

        // get: 有值返回，无值抛异常 (不推荐)
        System.out.println("get(): " + opt.get());
        // emptyOpt.get(); // NoSuchElementException

        // orElse: 有值返回，无值返回默认值
        System.out.println("orElse: " + emptyOpt.orElse("默认值"));

        // orElseGet: 有值返回，无值调用函数获取默认值
        System.out.println("orElseGet: " + emptyOpt.orElseGet(() -> "计算的默认值"));

        // orElseThrow: 有值返回，无值抛出自定义异常
        try {
            emptyOpt.orElseThrow(() -> new RuntimeException("值不能为空"));
        } catch (RuntimeException e) {
            System.out.println("orElseThrow: 抛出异常 - " + e.getMessage());
        }

        // or (Java 9+): 有值返回，无值返回另一个 Optional
        Optional<String> result = emptyOpt.or(() -> Optional.of("备选值"));
        System.out.println("or(): " + result);

        // ==================== 4. 判断和消费 ====================
        System.out.println("\n【4. 判断和消费】");

        Optional<String> greeting = Optional.of("你好");

        // isPresent / isEmpty
        System.out.println("isPresent: " + greeting.isPresent());
        System.out.println("isEmpty: " + greeting.isEmpty()); // Java 11+

        // ifPresent: 有值时执行
        greeting.ifPresent(v -> System.out.println("ifPresent: " + v));

        // ifPresentOrElse (Java 9+): 有值/无值分别执行
        greeting.ifPresentOrElse(
                v -> System.out.println("有值: " + v),
                () -> System.out.println("无值"));

        emptyOpt.ifPresentOrElse(
                v -> System.out.println("有值: " + v),
                () -> System.out.println("无值时执行"));

        // ==================== 5. 转换操作 ====================
        System.out.println("\n【5. 转换操作】");

        Optional<String> text = Optional.of("  hello  ");

        // map: 转换值
        Optional<String> trimmed = text.map(String::trim);
        System.out.println("map(trim): " + trimmed);

        Optional<Integer> length = text.map(String::trim).map(String::length);
        System.out.println("map(trim).map(length): " + length);

        // flatMap: 用于返回 Optional 的函数
        Optional<String> upper = text.flatMap(s -> Optional.of(s.trim().toUpperCase()));
        System.out.println("flatMap: " + upper);

        // filter: 过滤
        Optional<String> filtered = text.map(String::trim)
                .filter(s -> s.length() > 3);
        System.out.println("filter(length > 3): " + filtered);

        Optional<String> filteredEmpty = text.map(String::trim)
                .filter(s -> s.length() > 10);
        System.out.println("filter(length > 10): " + filteredEmpty);

        // ==================== 6. 链式操作 ====================
        System.out.println("\n【6. 链式操作】");

        UserProfile user = new UserProfile("张三", new Address("北京", "海淀区"));
        UserProfile userNoAddress = new UserProfile("李四", null);

        // 传统方式 (层层判空)
        String city1 = null;
        if (user != null && user.address() != null) {
            city1 = user.address().city();
        }
        System.out.println("传统方式获取城市: " + city1);

        // Optional 链式操作
        String city2 = Optional.ofNullable(user)
                .map(UserProfile::address)
                .map(Address::city)
                .orElse("未知");
        System.out.println("Optional 链式获取城市: " + city2);

        String city3 = Optional.ofNullable(userNoAddress)
                .map(UserProfile::address)
                .map(Address::city)
                .orElse("未知");
        System.out.println("无地址时获取城市: " + city3);

        // ==================== 7. Optional 在方法中使用 ====================
        System.out.println("\n【7. Optional 在方法中使用】");

        // 返回 Optional 表明可能没有值
        Optional<Customer> customer = findCustomerById(1);
        customer.ifPresent(c -> System.out.println("找到客户: " + c));

        Optional<Customer> notFound = findCustomerById(999);
        System.out.println("查找999: " + notFound.orElse(new Customer(-1, "未知")));

        // ==================== 8. Optional 最佳实践 ====================
        System.out.println("\n【8. Optional 最佳实践】");
        System.out.println("✓ 用作方法返回值表示可能没有结果");
        System.out.println("✓ 使用 map/flatMap 进行链式处理");
        System.out.println("✓ 使用 orElse/orElseGet 提供默认值");
        System.out.println();
        System.out.println("✗ 不要用 Optional 作为字段类型");
        System.out.println("✗ 不要用 Optional 作为方法参数");
        System.out.println("✗ 不要用 Optional 包装集合 (返回空集合即可)");
        System.out.println("✗ 不要用 isPresent + get，用 orElse 系列");

        // ==================== 9. 实际应用示例 ====================
        System.out.println("\n【9. 实际应用示例】");

        // 示例: 从配置中获取值，提供默认值
        Map<String, String> config = Map.of(
                "timeout", "30",
                "host", "localhost");

        int timeout = Optional.ofNullable(config.get("timeout"))
                .map(Integer::parseInt)
                .orElse(60);
        int port = Optional.ofNullable(config.get("port"))
                .map(Integer::parseInt)
                .orElse(8080);

        System.out.println("timeout: " + timeout);
        System.out.println("port (默认): " + port);

        // 示例: 安全地获取第一个元素
        List<String> items = List.of("A", "B", "C");
        List<String> emptyList = List.of();

        String first = items.stream().findFirst().orElse("无");
        String firstEmpty = emptyList.stream().findFirst().orElse("无");

        System.out.println("第一个元素: " + first);
        System.out.println("空列表第一个: " + firstEmpty);

        System.out.println("\n✅ Phase 3 - Lesson 9 完成！");
    }

    static Optional<Customer> findCustomerById(int id) {
        Map<Integer, Customer> db = Map.of(
                1, new Customer(1, "张三"),
                2, new Customer(2, "李四"));
        return Optional.ofNullable(db.get(id));
    }
}

record Address(String city, String district) {
}

record UserProfile(String name, Address address) {
}

record Customer(int id, String name) {
}

/*
 * 📚 知识点总结:
 * 
 * 1. Optional: 表示可能为空的值的容器
 * 2. 创建: of, ofNullable, empty
 * 3. 获取: orElse, orElseGet, orElseThrow
 * 4. 判断: isPresent, isEmpty, ifPresent
 * 5. 转换: map, flatMap, filter
 * 6. 链式操作避免层层 null 检查
 * 7. 适合作为返回值，不适合作为字段或参数
 * 
 * 🏃 练习:
 * 1. 用 Optional 安全地获取嵌套对象属性
 * 2. 实现一个方法返回 Optional 并正确消费
 * 3. 将传统的 null 检查代码重构为 Optional
 */
