package phase02;

/**
 * Phase 2 - Lesson 8: 接口
 * 
 * 🎯 学习目标:
 * 1. 理解接口的概念和作用
 * 2. 掌握 interface 关键字
 * 3. 学会实现多个接口
 * 4. 理解接口与抽象类的区别
 */
public class InterfaceDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 8: 接口 ===\n");

        // ==================== 1. 什么是接口 ====================
        System.out.println("【1. 什么是接口】");
        System.out.println("接口 (Interface): 定义行为规范的抽象类型");
        System.out.println("核心思想: 定义能做什么 (can-do)，而不是是什么 (is-a)");
        System.out.println();
        System.out.println("特点:");
        System.out.println("  1. 方法默认是 public abstract");
        System.out.println("  2. 字段默认是 public static final (常量)");
        System.out.println("  3. 一个类可以实现多个接口");
        System.out.println("  4. 接口之间可以继承 (extends)");

        // ==================== 2. 基本接口使用 ====================
        System.out.println("\n【2. 基本接口使用】");

        // 使用接口类型引用
        Flyable bird = new Bird2();
        Flyable plane = new Airplane();

        bird.fly();
        plane.fly();

        // ==================== 3. 实现多个接口 ====================
        System.out.println("\n【3. 实现多个接口】");
        System.out.println("Java 不支持多重继承，但支持实现多个接口");

        Duck duck = new Duck();

        // Duck 同时实现了 Flyable 和 Swimmable
        duck.fly();
        duck.swim();
        duck.quack();

        // 可以用不同的接口引用
        Flyable flyingDuck = duck;
        Swimmable swimmingDuck = duck;
        flyingDuck.fly();
        swimmingDuck.swim();

        // ==================== 4. 接口常量 ====================
        System.out.println("\n【4. 接口常量】");
        System.out.println("接口中的字段默认是 public static final");

        System.out.println("Math 常量示例:");
        System.out.println("  MathConstants.PI = " + MathConstants.PI);
        System.out.println("  MathConstants.E = " + MathConstants.E);

        // ==================== 5. 接口继承 ====================
        System.out.println("\n【5. 接口继承】");
        System.out.println("接口可以继承其他接口");

        SportsCar sportsCar = new SportsCar();
        sportsCar.start();
        sportsCar.stop();
        sportsCar.accelerate();
        sportsCar.turboBoost();

        // ==================== 6. 接口作为类型 ====================
        System.out.println("\n【6. 接口作为类型】");

        Printable[] printables = {
                new TextPrinter("Hello, World!"),
                new NumberPrinter(12345),
                new ArrayPrinter(new int[] { 1, 2, 3, 4, 5 })
        };

        System.out.println("打印所有可打印对象:");
        for (Printable p : printables) {
            p.print();
        }

        // ==================== 7. 函数式接口 (预览) ====================
        System.out.println("\n【7. 函数式接口 (预览)】");
        System.out.println("只有一个抽象方法的接口称为函数式接口");
        System.out.println("可以使用 Lambda 表达式实现");

        // 使用 Lambda 表达式
        Calculable add = (a, b) -> a + b;
        Calculable subtract = (a, b) -> a - b;
        Calculable multiply = (a, b) -> a * b;

        System.out.println("10 + 5 = " + add.calculate(10, 5));
        System.out.println("10 - 5 = " + subtract.calculate(10, 5));
        System.out.println("10 * 5 = " + multiply.calculate(10, 5));

        // ==================== 8. 标记接口 ====================
        System.out.println("\n【8. 标记接口】");
        System.out.println("没有任何方法的接口，用于标记类的某种能力");
        System.out.println("例如: Serializable, Cloneable");

        ImportantDocument doc = new ImportantDocument("机密文件");

        if (doc instanceof Auditable) {
            System.out.println(doc.getContent() + " 需要审计追踪");
        }
        if (doc instanceof Archivable) {
            System.out.println(doc.getContent() + " 需要长期归档");
        }

        // ==================== 9. 接口 vs 抽象类 ====================
        System.out.println("\n【9. 选择接口还是抽象类】");
        System.out.println("使用接口当:");
        System.out.println("  - 需要定义通用行为规范");
        System.out.println("  - 需要实现多个规范");
        System.out.println("  - 不相关的类需要共享行为");
        System.out.println();
        System.out.println("使用抽象类当:");
        System.out.println("  - 需要共享代码实现");
        System.out.println("  - 需要定义非静态、非final字段");
        System.out.println("  - 类之间有明显的 is-a 关系");

        System.out.println("\n✅ Phase 2 - Lesson 8 完成！");
    }
}

// ==================== 基本接口定义 ====================

/**
 * Flyable 接口 - 定义飞行能力
 */
interface Flyable {
    // 接口方法默认是 public abstract
    void fly();

    // Java 8+ 可以有默认实现
    default void land() {
        System.out.println("降落...");
    }
}

/**
 * Swimmable 接口 - 定义游泳能力
 */
interface Swimmable {
    void swim();
}

// ==================== 接口实现 ====================

class Bird2 implements Flyable {
    @Override
    public void fly() {
        System.out.println("鸟儿拍打翅膀飞翔 🐦");
    }
}

class Airplane implements Flyable {
    @Override
    public void fly() {
        System.out.println("飞机引擎启动飞行 ✈️");
    }

    @Override
    public void land() {
        System.out.println("飞机降落在跑道上");
    }
}

/**
 * Duck 实现多个接口
 */
class Duck implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("鸭子振翅飞翔 🦆");
    }

    @Override
    public void swim() {
        System.out.println("鸭子在水中游泳 🦆💧");
    }

    void quack() {
        System.out.println("嘎嘎嘎!");
    }
}

// ==================== 接口常量 ====================

interface MathConstants {
    double PI = 3.14159265358979;
    double E = 2.71828182845904;
    double GOLDEN_RATIO = 1.61803398874989;
}

// ==================== 接口继承 ====================

interface Drivable {
    void start();

    void stop();
}

interface Acceleratable extends Drivable {
    void accelerate();
}

interface Turboable extends Acceleratable {
    void turboBoost();
}

class SportsCar implements Turboable {
    @Override
    public void start() {
        System.out.println("跑车启动 🏎️");
    }

    @Override
    public void stop() {
        System.out.println("跑车停止");
    }

    @Override
    public void accelerate() {
        System.out.println("跑车加速!");
    }

    @Override
    public void turboBoost() {
        System.out.println("涡轮增压启动!! 🚀");
    }
}

// ==================== 接口作为类型 ====================

interface Printable {
    void print();
}

class TextPrinter implements Printable {
    private String text;

    TextPrinter(String text) {
        this.text = text;
    }

    @Override
    public void print() {
        System.out.println("[文本] " + text);
    }
}

class NumberPrinter implements Printable {
    private int number;

    NumberPrinter(int number) {
        this.number = number;
    }

    @Override
    public void print() {
        System.out.println("[数字] " + number);
    }
}

class ArrayPrinter implements Printable {
    private int[] array;

    ArrayPrinter(int[] array) {
        this.array = array;
    }

    @Override
    public void print() {
        StringBuilder sb = new StringBuilder("[数组] ");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1)
                sb.append(", ");
        }
        System.out.println(sb);
    }
}

// ==================== 函数式接口 ====================

@FunctionalInterface
interface Calculable {
    int calculate(int a, int b);
}

// ==================== 标记接口 ====================

interface Auditable {
    // 标记接口：无方法
}

interface Archivable {
    // 标记接口：无方法
}

class ImportantDocument implements Auditable, Archivable {
    private String content;

    ImportantDocument(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 接口使用 interface 关键字
 * 2. 接口方法默认是 public abstract
 * 3. 接口字段默认是 public static final
 * 4. 类使用 implements 实现接口
 * 5. 一个类可以实现多个接口
 * 6. 接口可以继承其他接口
 * 7. 函数式接口只有一个抽象方法
 * 8. 标记接口没有方法，用于类型标记
 * 
 * 🏃 练习:
 * 1. 创建 Comparable 接口，让多个类实现排序能力
 * 2. 设计支付系统接口，支持多种支付方式
 * 3. 实现一个事件监听器接口
 */
