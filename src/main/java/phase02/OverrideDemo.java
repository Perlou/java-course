package phase02;

/**
 * Phase 2 - Lesson 6: 方法重写
 * 
 * 🎯 学习目标:
 * 1. 理解方法重写的概念
 * 2. 掌握 @Override 注解的使用
 * 3. 区分重写 (Override) 和重载 (Overload)
 * 4. 理解协变返回类型
 */
public class OverrideDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 6: 方法重写 ===\n");

        // ==================== 1. 什么是方法重写 ====================
        System.out.println("【1. 什么是方法重写】");
        System.out.println("重写 (Override): 子类重新定义父类的同名方法");
        System.out.println("目的: 让子类有自己特定的行为实现");
        System.out.println();
        System.out.println("重写规则:");
        System.out.println("  1. 方法名相同");
        System.out.println("  2. 参数列表相同");
        System.out.println("  3. 返回类型相同或是其子类 (协变返回类型)");
        System.out.println("  4. 访问修饰符不能更严格");
        System.out.println("  5. 不能抛出更宽泛的异常");

        // ==================== 2. 基本重写示例 ====================
        System.out.println("\n【2. 基本重写示例】");

        Vehicle vehicle = new Vehicle();
        Bike bike = new Bike();
        Motorcycle motorcycle = new Motorcycle();

        vehicle.start();
        bike.start(); // 重写版本
        motorcycle.start(); // 重写版本

        // ==================== 3. @Override 注解 ====================
        System.out.println("\n【3. @Override 注解】");
        System.out.println("@Override 的作用:");
        System.out.println("  1. 表明这是一个重写方法");
        System.out.println("  2. 编译器会检查方法签名是否正确");
        System.out.println("  3. 防止拼写错误导致意外创建新方法");
        System.out.println();

        Printer basicPrinter = new Printer();
        Printer colorPrinter = new ColorPrinter();
        Printer laserPrinter = new LaserPrinter();

        basicPrinter.print("Hello");
        colorPrinter.print("Hello");
        laserPrinter.print("Hello");

        // ==================== 4. 重写 vs 重载 ====================
        System.out.println("\n【4. 重写 (Override) vs 重载 (Overload)】");
        System.out.println("┌─────────────┬─────────────────────┬─────────────────────┐");
        System.out.println("│   特性      │      重写           │       重载          │");
        System.out.println("├─────────────┼─────────────────────┼─────────────────────┤");
        System.out.println("│ 发生位置    │ 子类与父类之间      │ 同一个类中          │");
        System.out.println("│ 方法名      │ 必须相同            │ 必须相同            │");
        System.out.println("│ 参数列表    │ 必须相同            │ 必须不同            │");
        System.out.println("│ 返回类型    │ 相同或协变          │ 可以不同            │");
        System.out.println("│ 访问权限    │ 不能更严格          │ 无限制              │");
        System.out.println("│ 绑定时机    │ 运行时 (动态绑定)   │ 编译时 (静态绑定)   │");
        System.out.println("└─────────────┴─────────────────────┴─────────────────────┘");

        // 重载示例
        System.out.println("\n重载示例:");
        Calculator2 calc = new Calculator2();
        System.out.println("add(2, 3) = " + calc.add(2, 3));
        System.out.println("add(2, 3, 4) = " + calc.add(2, 3, 4));
        System.out.println("add(2.5, 3.5) = " + calc.add(2.5, 3.5));

        // ==================== 5. 协变返回类型 ====================
        System.out.println("\n【5. 协变返回类型】");
        System.out.println("子类重写方法时，可以返回更具体的类型");

        AnimalFactory baseFactory = new AnimalFactory();
        DogFactory dogFactory = new DogFactory();

        AnimalV2 animal = baseFactory.create();
        DogV2 dog = dogFactory.create(); // 返回类型是 DogV2

        animal.speak();
        dog.speak();
        dog.fetch(); // DogV2 特有的方法

        // ==================== 6. super 调用父类方法 ====================
        System.out.println("\n【6. super 调用父类方法】");

        EnhancedLogger logger = new EnhancedLogger();
        logger.log("测试消息");

        // ==================== 7. 不能重写的方法 ====================
        System.out.println("\n【7. 不能重写的方法】");
        System.out.println("以下方法不能被重写:");
        System.out.println("  1. private 方法 (子类不可见)");
        System.out.println("  2. static 方法 (属于类，不是实例)");
        System.out.println("  3. final 方法 (明确禁止重写)");
        System.out.println();

        Parent parent = new Child();
        parent.instanceMethod(); // 重写生效
        Parent.staticMethod(); // 静态方法不是重写

        // ==================== 8. 重写 Object 方法 ====================
        System.out.println("\n【8. 重写 Object 方法】");

        BookV2 book1 = new BookV2("Java编程思想", "Bruce Eckel", 108.0);
        BookV2 book2 = new BookV2("Java编程思想", "Bruce Eckel", 108.0);
        BookV2 book3 = new BookV2("Effective Java", "Joshua Bloch", 89.0);

        System.out.println("book1.toString(): " + book1.toString());
        System.out.println("book1.equals(book2): " + book1.equals(book2));
        System.out.println("book1.equals(book3): " + book1.equals(book3));
        System.out.println("book1.hashCode(): " + book1.hashCode());
        System.out.println("book2.hashCode(): " + book2.hashCode());

        System.out.println("\n✅ Phase 2 - Lesson 6 完成！");
    }
}

// ==================== 基本重写示例 ====================

class Vehicle {
    void start() {
        System.out.println("交通工具启动...");
    }

    void stop() {
        System.out.println("交通工具停止...");
    }
}

class Bike extends Vehicle {
    @Override
    void start() {
        System.out.println("自行车开始骑行 🚲");
    }
}

class Motorcycle extends Vehicle {
    @Override
    void start() {
        System.out.println("摩托车发动引擎 🏍️");
    }
}

// ==================== @Override 注解示例 ====================

class Printer {
    void print(String content) {
        System.out.println("[基础打印] " + content);
    }
}

class ColorPrinter extends Printer {
    @Override
    void print(String content) {
        System.out.println("[彩色打印] 🎨 " + content);
    }
}

class LaserPrinter extends Printer {
    @Override
    void print(String content) {
        System.out.println("[激光打印] ⚡ " + content);
    }
}

// ==================== 重载示例 ====================

class Calculator2 {
    // 方法重载：同名方法，不同参数
    int add(int a, int b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }

    double add(double a, double b) {
        return a + b;
    }
}

// ==================== 协变返回类型示例 ====================

class AnimalV2 {
    void speak() {
        System.out.println("动物发出声音");
    }
}

class DogV2 extends AnimalV2 {
    @Override
    void speak() {
        System.out.println("汪汪!");
    }

    void fetch() {
        System.out.println("狗狗捡球!");
    }
}

class AnimalFactory {
    AnimalV2 create() {
        System.out.println("创建一个普通动物");
        return new AnimalV2();
    }
}

class DogFactory extends AnimalFactory {
    @Override
    DogV2 create() { // 协变返回类型：返回 DogV2 而不是 AnimalV2
        System.out.println("创建一只狗");
        return new DogV2();
    }
}

// ==================== super 调用示例 ====================

class Logger {
    void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

class EnhancedLogger extends Logger {
    @Override
    void log(String message) {
        // 先调用父类方法
        super.log(message);
        // 然后添加额外功能
        System.out.println("  → 日志已记录到文件");
        System.out.println("  → 时间戳: " + System.currentTimeMillis());
    }
}

// ==================== 静态方法不是重写 ====================

class Parent {
    static void staticMethod() {
        System.out.println("Parent 的静态方法");
    }

    void instanceMethod() {
        System.out.println("Parent 的实例方法");
    }

    // final 方法不能被重写
    final void finalMethod() {
        System.out.println("不能被重写的方法");
    }
}

class Child extends Parent {
    // 这是隐藏 (hiding)，不是重写 (overriding)
    static void staticMethod() {
        System.out.println("Child 的静态方法");
    }

    @Override
    void instanceMethod() {
        System.out.println("Child 重写的实例方法");
    }

    // 编译错误！不能重写 final 方法
    // @Override
    // void finalMethod() { }
}

// ==================== 重写 Object 方法示例 ====================

class BookV2 {
    private String title;
    private String author;
    private double price;

    BookV2(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    @Override
    public String toString() {
        return "BookV2{title='" + title + "', author='" + author + "', price=" + price + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        BookV2 other = (BookV2) obj;
        return Double.compare(other.price, price) == 0 &&
                title.equals(other.title) &&
                author.equals(other.author);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + author.hashCode();
        result = 31 * result + Double.hashCode(price);
        return result;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 重写: 子类重新定义父类方法
 * 2. @Override: 推荐使用，让编译器检查
 * 3. 重写规则: 同名、同参数、返回类型兼容、访问权限不能更严格
 * 4. 协变返回类型: 重写时可以返回更具体的类型
 * 5. super.方法(): 调用父类被重写的方法
 * 6. static/final/private 方法不能被重写
 * 7. 重点掌握 toString/equals/hashCode 的重写
 * 
 * 🏃 练习:
 * 1. 创建一个 Notification 类层次，重写 send() 方法
 * 2. 实现一个 Product 类，正确重写 equals 和 hashCode
 * 3. 设计一个报表生成器，不同报表有不同的格式化方式
 */
