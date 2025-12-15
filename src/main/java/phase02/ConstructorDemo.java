package phase02;

/**
 * Phase 2 - Lesson 2: 构造方法
 * 
 * 🎯 学习目标:
 * 1. 理解构造方法的作用
 * 2. 掌握构造方法重载
 * 3. 了解 this() 调用
 * 4. 理解默认构造方法
 */
public class ConstructorDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 2: 构造方法 ===\n");

        // ==================== 1. 什么是构造方法 ====================
        System.out.println("【1. 什么是构造方法】");
        System.out.println("构造方法 (Constructor) 是创建对象时自动调用的特殊方法");
        System.out.println("特点:");
        System.out.println("  - 方法名与类名相同");
        System.out.println("  - 没有返回值 (连 void 也没有)");
        System.out.println("  - 用于初始化对象状态");
        System.out.println();

        // ==================== 2. 默认构造方法 ====================
        System.out.println("【2. 默认构造方法】");
        System.out.println("如果没有定义任何构造方法，Java 会自动提供默认构造方法");

        DefaultExample d1 = new DefaultExample(); // 使用默认构造方法
        System.out.println("DefaultExample 对象创建成功，value = " + d1.value);

        // ==================== 3. 自定义构造方法 ====================
        System.out.println("\n【3. 自定义构造方法】");

        Book book1 = new Book("Java 编程思想", "Bruce Eckel", 108.0);
        book1.showInfo();

        // ==================== 4. 构造方法重载 ====================
        System.out.println("\n【4. 构造方法重载】");
        System.out.println("可以定义多个构造方法，参数不同即可");

        Product p1 = new Product(); // 无参构造
        Product p2 = new Product("iPhone 15"); // 一个参数
        Product p3 = new Product("MacBook Pro", 14999); // 两个参数
        Product p4 = new Product("AirPods", 1299, 100); // 三个参数

        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("p3: " + p3);
        System.out.println("p4: " + p4);

        // ==================== 5. this() 调用其他构造方法 ====================
        System.out.println("\n【5. this() 调用其他构造方法】");
        System.out.println("使用 this() 可以在一个构造方法中调用另一个构造方法");
        System.out.println("注意: this() 必须是构造方法的第一条语句");

        Employee e1 = new Employee("张三");
        Employee e2 = new Employee("李四", "工程师");
        Employee e3 = new Employee("王五", "经理", 50000);

        System.out.println("e1: " + e1);
        System.out.println("e2: " + e2);
        System.out.println("e3: " + e3);

        // ==================== 6. 构造方法与初始化顺序 ====================
        System.out.println("\n【6. 构造方法与初始化顺序】");
        System.out.println("对象初始化顺序:");
        System.out.println("  1. 父类静态成员和静态代码块");
        System.out.println("  2. 子类静态成员和静态代码块");
        System.out.println("  3. 父类成员变量和实例代码块");
        System.out.println("  4. 父类构造方法");
        System.out.println("  5. 子类成员变量和实例代码块");
        System.out.println("  6. 子类构造方法");
        System.out.println();

        System.out.println("创建 InitOrder 对象:");
        InitOrder io = new InitOrder();
        System.out.println("io.value = " + io.value);

        // ==================== 7. 私有构造方法 ====================
        System.out.println("\n【7. 私有构造方法】");
        System.out.println("私有构造方法用于:");
        System.out.println("  - 单例模式");
        System.out.println("  - 工具类 (防止实例化)");
        System.out.println("  - 工厂方法模式");

        // Singleton singleton = new Singleton(); // 编译错误！
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println("s1 == s2: " + (s1 == s2)); // true，同一个实例

        // ==================== 8. 拷贝构造方法 ====================
        System.out.println("\n【8. 拷贝构造方法】");
        System.out.println("通过已存在的对象创建新对象");

        Point original = new Point(10, 20);
        Point copy = new Point(original); // 拷贝构造

        System.out.println("原始对象: " + original);
        System.out.println("拷贝对象: " + copy);
        System.out.println("是同一对象? " + (original == copy)); // false

        System.out.println("\n✅ Phase 2 - Lesson 2 完成！");
    }
}

// ==================== 辅助类定义 ====================

/**
 * 演示默认构造方法
 * 没有定义构造方法，Java 自动提供 DefaultExample() {}
 */
class DefaultExample {
    int value = 42; // 成员变量有默认值
    // 如果添加任何自定义构造方法，默认构造方法就不会自动生成
}

/**
 * Book 类 - 自定义构造方法
 */
class Book {
    String title;
    String author;
    double price;

    // 自定义构造方法
    Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
        System.out.println("Book 构造方法被调用");
    }

    void showInfo() {
        System.out.println("《" + title + "》 by " + author + ", ¥" + price);
    }
}

/**
 * Product 类 - 演示构造方法重载
 */
class Product {
    String name;
    double price;
    int stock;

    // 无参构造方法
    Product() {
        this.name = "未知产品";
        this.price = 0;
        this.stock = 0;
    }

    // 一个参数的构造方法
    Product(String name) {
        this.name = name;
        this.price = 0;
        this.stock = 0;
    }

    // 两个参数的构造方法
    Product(String name, double price) {
        this.name = name;
        this.price = price;
        this.stock = 0;
    }

    // 三个参数的构造方法
    Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return name + " - ¥" + price + " (库存: " + stock + ")";
    }
}

/**
 * Employee 类 - 演示 this() 调用
 */
class Employee {
    String name;
    String position;
    double salary;

    // 最完整的构造方法
    Employee(String name, String position, double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    // 调用其他构造方法
    Employee(String name, String position) {
        this(name, position, 10000); // 调用三参数构造方法
    }

    Employee(String name) {
        this(name, "实习生"); // 调用两参数构造方法，进而调用三参数
    }

    @Override
    public String toString() {
        return name + " - " + position + " - ¥" + salary;
    }
}

/**
 * 演示初始化顺序
 */
class InitOrder {
    // 静态变量
    static int staticValue;

    // 静态代码块
    static {
        staticValue = 1;
        System.out.println("  1. 静态代码块执行, staticValue = " + staticValue);
    }

    // 实例变量
    int value = initValue();

    // 实例初始化方法
    int initValue() {
        System.out.println("  2. 实例变量初始化");
        return 10;
    }

    // 实例代码块
    {
        System.out.println("  3. 实例代码块执行");
        value = 20;
    }

    // 构造方法
    InitOrder() {
        System.out.println("  4. 构造方法执行");
        value = 30;
    }
}

/**
 * 单例模式 - 私有构造方法
 */
class Singleton {
    // 唯一实例
    private static final Singleton INSTANCE = new Singleton();

    // 私有构造方法，防止外部创建实例
    private Singleton() {
        System.out.println("Singleton 构造方法被调用（只会调用一次）");
    }

    // 公共静态方法，返回唯一实例
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

/**
 * Point 类 - 演示拷贝构造方法
 */
class Point {
    int x;
    int y;

    // 普通构造方法
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 拷贝构造方法
    Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 构造方法用于初始化对象
 * 2. 构造方法名与类名相同，没有返回值
 * 3. 如果不定义构造方法，Java 提供默认无参构造方法
 * 4. 定义了任何构造方法后，默认构造方法不再提供
 * 5. 构造方法可以重载
 * 6. this() 用于调用其他构造方法，必须在第一行
 * 7. 初始化顺序: 静态成员 → 实例成员 → 构造方法
 * 8. 私有构造方法用于控制实例化
 * 
 * 🏃 练习:
 * 1. 创建一个 Rectangle 类，提供多个构造方法
 * 2. 实现一个计数器类，使用私有构造方法
 * 3. 创建一个 Date 类，支持拷贝构造
 */
