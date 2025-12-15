package phase02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Phase 2 - Lesson 9: 接口默认方法
 * 
 * 🎯 学习目标:
 * 1. 理解 Java 8 接口默认方法
 * 2. 掌握静态方法和私有方法
 * 3. 处理默认方法冲突
 * 4. 了解接口演进
 */
public class DefaultMethodDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 9: 接口默认方法 ===\n");

        // ==================== 1. 什么是默认方法 ====================
        System.out.println("【1. 什么是默认方法】");
        System.out.println("Java 8 引入的特性，允许接口有默认实现");
        System.out.println("使用 default 关键字修饰");
        System.out.println();
        System.out.println("优点:");
        System.out.println("  1. 接口演进：向接口添加新方法不破坏现有实现");
        System.out.println("  2. 代码复用：多个实现类可以共享默认行为");
        System.out.println("  3. 可选实现：实现类可以选择性重写");

        // ==================== 2. 基本使用 ====================
        System.out.println("\n【2. 基本使用】");

        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");

        // forEach 是 Iterable 接口的默认方法
        System.out.println("使用 forEach 默认方法:");
        names.forEach(name -> System.out.println("  - " + name));

        // ==================== 3. 自定义默认方法 ====================
        System.out.println("\n【3. 自定义默认方法】");

        Vehicle2 car = new Car3();
        Vehicle2 bike = new Bike2();

        car.start();
        car.honk(); // 默认方法
        car.stop();

        System.out.println();

        bike.start();
        bike.honk(); // 重写的默认方法
        bike.stop();

        // ==================== 4. 接口静态方法 ====================
        System.out.println("\n【4. 接口静态方法】");
        System.out.println("Java 8 允许接口有静态方法");

        // 调用接口静态方法
        String info = StringUtils.join(Arrays.asList("Java", "Python", "Go"), ", ");
        System.out.println("StringUtils.join(): " + info);

        boolean empty = StringUtils.isEmpty("");
        System.out.println("StringUtils.isEmpty(\"\"): " + empty);

        boolean notEmpty = StringUtils.isNotEmpty("Hello");
        System.out.println("StringUtils.isNotEmpty(\"Hello\"): " + notEmpty);

        // ==================== 5. 私有方法 (Java 9+) ====================
        System.out.println("\n【5. 私有方法 (Java 9+)】");
        System.out.println("Java 9 允许接口有私有方法");
        System.out.println("用于默认方法之间共享代码");

        Logger2 logger = new ConsoleLogger();
        logger.logInfo("系统启动");
        logger.logWarning("内存使用率较高");
        logger.logError("连接失败");

        // ==================== 6. 默认方法冲突 ====================
        System.out.println("\n【6. 默认方法冲突】");
        System.out.println("当类实现多个接口，且接口有相同的默认方法时:");
        System.out.println("  1. 类必须显式重写该方法");
        System.out.println("  2. 可以使用 接口名.super.方法() 调用特定接口的实现");

        MultiInterfaceClass mic = new MultiInterfaceClass();
        mic.commonMethod();

        // ==================== 7. 接口继承与默认方法 ====================
        System.out.println("\n【7. 接口继承与默认方法】");

        AdvancedCollection<String> list = new MyAdvancedList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        System.out.println("元素个数: " + list.count());
        System.out.println("是否为空: " + list.isEmpty()); // 默认方法

        // ==================== 8. 实际应用示例 ====================
        System.out.println("\n【8. 实际应用示例 - 策略模式】");

        PaymentProcessor processor = new PaymentProcessor();

        // 使用不同的支付策略
        processor.processPayment(new CreditCardPayment(), 100.0);
        processor.processPayment(new PayPalPayment(), 50.0);
        processor.processPayment(new CryptoPayment(), 200.0);

        System.out.println("\n✅ Phase 2 - Lesson 9 完成！");
    }
}

// ==================== 基本默认方法 ====================

interface Vehicle2 {
    void start();

    void stop();

    // 默认方法
    default void honk() {
        System.out.println("嘟嘟! 🚗");
    }
}

class Car3 implements Vehicle2 {
    @Override
    public void start() {
        System.out.println("汽车启动");
    }

    @Override
    public void stop() {
        System.out.println("汽车停止");
    }
    // honk() 使用默认实现
}

class Bike2 implements Vehicle2 {
    @Override
    public void start() {
        System.out.println("自行车开始骑行");
    }

    @Override
    public void stop() {
        System.out.println("自行车停止");
    }

    @Override
    public void honk() {
        // 重写默认方法
        System.out.println("叮铃铃! 🚲");
    }
}

// ==================== 接口静态方法 ====================

interface StringUtils {
    // 静态方法
    static String join(List<String> parts, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            sb.append(parts.get(i));
            if (i < parts.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}

// ==================== 私有方法 ====================

interface Logger2 {
    void log(String message);

    default void logInfo(String message) {
        log(formatMessage("INFO", message));
    }

    default void logWarning(String message) {
        log(formatMessage("WARNING", message));
    }

    default void logError(String message) {
        log(formatMessage("ERROR", message));
    }

    // 私有方法 - 在默认方法之间共享代码
    private String formatMessage(String level, String message) {
        return "[" + level + "] " + java.time.LocalTime.now() + " - " + message;
    }
}

class ConsoleLogger implements Logger2 {
    @Override
    public void log(String message) {
        System.out.println(message);
    }
}

// ==================== 默认方法冲突 ====================

interface InterfaceA {
    default void commonMethod() {
        System.out.println("InterfaceA 的实现");
    }
}

interface InterfaceB {
    default void commonMethod() {
        System.out.println("InterfaceB 的实现");
    }
}

class MultiInterfaceClass implements InterfaceA, InterfaceB {
    @Override
    public void commonMethod() {
        // 必须重写来解决冲突
        System.out.println("解决接口冲突:");

        // 可以选择调用某个接口的默认实现
        InterfaceA.super.commonMethod();
        InterfaceB.super.commonMethod();

        // 也可以提供完全自己的实现
        System.out.println("自己的实现");
    }
}

// ==================== 接口继承与默认方法 ====================

interface Collection2<T> {
    void add(T item);

    int count();

    default boolean isEmpty() {
        return count() == 0;
    }
}

interface AdvancedCollection<T> extends Collection2<T> {
    default void clear() {
        System.out.println("清空集合...");
    }
}

class MyAdvancedList<T> implements AdvancedCollection<T> {
    private List<T> items = new ArrayList<>();

    @Override
    public void add(T item) {
        items.add(item);
    }

    @Override
    public int count() {
        return items.size();
    }
}

// ==================== 策略模式示例 ====================

interface PaymentStrategy {
    void pay(double amount);

    // 默认方法：交易验证
    default boolean validate() {
        System.out.println("  验证交易...");
        return true;
    }

    // 默认方法：交易日志
    default void logTransaction(double amount) {
        System.out.println("  记录交易: ¥" + amount);
    }
}

class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        if (validate()) {
            System.out.println("信用卡支付: ¥" + amount);
            logTransaction(amount);
        }
    }
}

class PayPalPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        if (validate()) {
            System.out.println("PayPal 支付: ¥" + amount);
            logTransaction(amount);
        }
    }

    @Override
    public boolean validate() {
        System.out.println("  PayPal 账户验证...");
        return true;
    }
}

class CryptoPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        if (validate()) {
            System.out.println("加密货币支付: ¥" + amount);
            logTransaction(amount);
        }
    }
}

class PaymentProcessor {
    void processPayment(PaymentStrategy strategy, double amount) {
        System.out.println("处理支付:");
        strategy.pay(amount);
        System.out.println();
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. default 方法: 接口中有默认实现的方法
 * 2. static 方法: 接口中的静态工具方法
 * 3. private 方法 (Java 9+): 接口内部共享代码
 * 4. 默认方法冲突: 实现类必须重写来解决
 * 5. 接口名.super.方法(): 调用特定接口的默认实现
 * 6. 接口演进: 向接口添加默认方法不破坏向后兼容性
 * 
 * 🏃 练习:
 * 1. 为 Comparable 接口添加默认的比较方法
 * 2. 创建一个带有默认认证方法的 Authenticator 接口
 * 3. 实现一个带有默认序列化方法的 Serializable 接口
 */
