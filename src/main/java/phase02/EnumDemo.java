package phase02;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Phase 2 - Lesson 11: 枚举
 * 
 * 🎯 学习目标:
 * 1. 理解枚举的概念和用途
 * 2. 掌握 enum 关键字的使用
 * 3. 学会为枚举添加属性和方法
 * 4. 了解 EnumSet 和 EnumMap
 */
public class EnumDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 11: 枚举 ===\n");

        // ==================== 1. 什么是枚举 ====================
        System.out.println("【1. 什么是枚举】");
        System.out.println("枚举 (Enum): 一种特殊的类，表示固定常量集合");
        System.out.println("适用场景: 星期、月份、状态、方向等有限选项");
        System.out.println();
        System.out.println("优点:");
        System.out.println("  1. 类型安全，编译时检查");
        System.out.println("  2. 代码可读性好");
        System.out.println("  3. 可以添加方法和字段");

        // ==================== 2. 基本枚举定义 ====================
        System.out.println("\n【2. 基本枚举定义】");

        Day today = Day.MONDAY;
        System.out.println("今天是: " + today);
        System.out.println("ordinal (序号): " + today.ordinal());
        System.out.println("name (名称): " + today.name());

        // ==================== 3. 遍历枚举 ====================
        System.out.println("\n【3. 遍历枚举】");

        System.out.println("所有星期:");
        for (Day day : Day.values()) {
            System.out.println("  " + day.ordinal() + ": " + day);
        }

        // ==================== 4. switch 与枚举 ====================
        System.out.println("\n【4. switch 与枚举】");

        Day currentDay = Day.SATURDAY;
        String dayType = switch (currentDay) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
            case SATURDAY, SUNDAY -> "周末";
        };
        System.out.println(currentDay + " 是 " + dayType);

        // ==================== 5. 带属性的枚举 ====================
        System.out.println("\n【5. 带属性的枚举】");

        System.out.println("HTTP 状态码:");
        for (HttpStatus status : HttpStatus.values()) {
            System.out.printf("  %d %s%n", status.getCode(), status.getDescription());
        }

        HttpStatus status = HttpStatus.NOT_FOUND;
        System.out.println("\n请求状态: " + status.getCode() + " - " + status.getDescription());
        System.out.println("是否成功: " + status.isSuccess());

        // ==================== 6. 带方法的枚举 ====================
        System.out.println("\n【6. 带方法的枚举】");

        double price = 100.0;
        System.out.println("原价: ¥" + price);

        for (DiscountType discount : DiscountType.values()) {
            double discounted = discount.apply(price);
            System.out.printf("  %s: ¥%.2f%n", discount, discounted);
        }

        // ==================== 7. 抽象方法枚举 ====================
        System.out.println("\n【7. 抽象方法枚举】");

        System.out.println("计算器操作:");
        for (Operation op : Operation.values()) {
            double result = op.apply(10, 3);
            System.out.printf("  10 %s 3 = %.2f%n", op, result);
        }

        // ==================== 8. EnumSet ====================
        System.out.println("\n【8. EnumSet】");

        EnumSet<Day> workDays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);

        System.out.println("工作日: " + workDays);
        System.out.println("周末: " + weekend);

        // ==================== 9. EnumMap ====================
        System.out.println("\n【9. EnumMap】");

        EnumMap<Priority, String> tasks = new EnumMap<>(Priority.class);
        tasks.put(Priority.HIGH, "紧急Bug修复");
        tasks.put(Priority.MEDIUM, "功能开发");
        tasks.put(Priority.LOW, "代码重构");

        System.out.println("任务列表:");
        tasks.forEach((priority, task) -> System.out.println("  [" + priority + "] " + task));

        // ==================== 10. 实际应用示例 ====================
        System.out.println("\n【10. 实际应用示例 - 状态机】");

        Order order = new Order();
        System.out.println("订单初始状态: " + order.getStatus());

        order.pay();
        System.out.println("支付后: " + order.getStatus());

        order.ship();
        System.out.println("发货后: " + order.getStatus());

        order.deliver();
        System.out.println("送达后: " + order.getStatus());

        System.out.println("\n✅ Phase 2 - Lesson 11 完成！");
    }
}

// ==================== 基本枚举 ====================

enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// ==================== 带属性的枚举 ====================

enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int code;
    private final String description;

    HttpStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    // 根据状态码获取枚举
    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}

// ==================== 带方法的枚举 ====================

enum DiscountType {
    NONE(0),
    MEMBER(0.1),
    VIP(0.2),
    SUPER_VIP(0.3);

    private final double discountRate;

    DiscountType(double discountRate) {
        this.discountRate = discountRate;
    }

    public double apply(double price) {
        return price * (1 - discountRate);
    }
}

// ==================== 抽象方法枚举 ====================

enum Operation {
    ADD("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    SUBTRACT("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    MULTIPLY("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            if (y == 0)
                throw new ArithmeticException("除数不能为0");
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    // 抽象方法，每个枚举常量必须实现
    public abstract double apply(double x, double y);
}

// ==================== 优先级枚举 ====================

enum Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

// ==================== 订单状态枚举 (状态机) ====================

enum OrderStatus {
    CREATED("已创建"),
    PAID("已支付"),
    SHIPPED("已发货"),
    DELIVERED("已送达"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

class Order {
    private OrderStatus status = OrderStatus.CREATED;

    public OrderStatus getStatus() {
        return status;
    }

    public void pay() {
        if (status == OrderStatus.CREATED) {
            status = OrderStatus.PAID;
        } else {
            throw new IllegalStateException("无法从 " + status + " 状态支付");
        }
    }

    public void ship() {
        if (status == OrderStatus.PAID) {
            status = OrderStatus.SHIPPED;
        } else {
            throw new IllegalStateException("无法从 " + status + " 状态发货");
        }
    }

    public void deliver() {
        if (status == OrderStatus.SHIPPED) {
            status = OrderStatus.DELIVERED;
        } else {
            throw new IllegalStateException("无法从 " + status + " 状态送达");
        }
    }

    public void cancel() {
        if (status == OrderStatus.CREATED || status == OrderStatus.PAID) {
            status = OrderStatus.CANCELLED;
        } else {
            throw new IllegalStateException("无法从 " + status + " 状态取消");
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. enum 定义一组固定常量
 * 2. 枚举继承自 java.lang.Enum
 * 3. 枚举可以有构造方法、字段和方法
 * 4. 抽象方法让每个常量有不同实现
 * 5. EnumSet: 高效的枚举集合
 * 6. EnumMap: 以枚举为键的映射
 * 7. 枚举适合实现状态机、策略模式等
 * 
 * 🏃 练习:
 * 1. 创建一个 Season 枚举，包含季节信息和温度范围
 * 2. 使用枚举实现一个简单的权限系统
 * 3. 创建一个 Currency 枚举，支持货币转换
 */
