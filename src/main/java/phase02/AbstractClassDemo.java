package phase02;

/**
 * Phase 2 - Lesson 7: 抽象类
 * 
 * 🎯 学习目标:
 * 1. 理解抽象类的概念
 * 2. 掌握 abstract 关键字的使用
 * 3. 了解抽象类与普通类的区别
 * 4. 学会设计抽象类层次结构
 */
public class AbstractClassDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 7: 抽象类 ===\n");

        // ==================== 1. 什么是抽象类 ====================
        System.out.println("【1. 什么是抽象类】");
        System.out.println("抽象类 (Abstract Class): 不能被实例化的类");
        System.out.println("用途: 定义通用模板，强制子类实现特定方法");
        System.out.println();
        System.out.println("特点:");
        System.out.println("  1. 使用 abstract 关键字修饰");
        System.out.println("  2. 可以包含抽象方法和具体方法");
        System.out.println("  3. 可以有构造方法 (但不能直接实例化)");
        System.out.println("  4. 子类必须实现所有抽象方法，除非子类也是抽象类");

        // ==================== 2. 不能实例化 ====================
        System.out.println("\n【2. 不能实例化】");
        // Animal3 animal = new Animal3(); // 编译错误！
        System.out.println("// Animal3 animal = new Animal3();  ← 编译错误！");
        System.out.println("抽象类只能被继承，不能被实例化");

        // ==================== 3. 基本使用 ====================
        System.out.println("\n【3. 基本使用】");

        // 使用抽象类的引用指向具体子类
        Animal3 dog = new Dog3("旺财");
        Animal3 cat = new Cat3("咪咪");
        Animal3 bird = new Bird("小黄");

        dog.makeSound();
        cat.makeSound();
        bird.makeSound();

        System.out.println();
        dog.sleep(); // 继承的具体方法
        cat.sleep();

        // ==================== 4. 抽象方法与具体方法 ====================
        System.out.println("\n【4. 抽象方法与具体方法】");
        System.out.println("抽象方法: 只有声明，没有实现，子类必须重写");
        System.out.println("具体方法: 有完整实现，子类可以继承或重写");
        System.out.println();

        Document doc1 = new PDFDocument("报告.pdf");
        Document doc2 = new WordDocument("论文.docx");
        Document doc3 = new ExcelDocument("数据.xlsx");

        System.out.println("文档操作:");
        doc1.open();
        doc1.save();
        doc1.close();

        System.out.println();
        doc2.open();
        doc3.open();

        // ==================== 5. 抽象类的构造方法 ====================
        System.out.println("\n【5. 抽象类的构造方法】");
        System.out.println("抽象类可以有构造方法，但不能直接调用");
        System.out.println("子类构造方法会调用父类 (抽象类) 的构造方法");

        ElectricCar2 tesla = new ElectricCar2("Tesla", "Model 3", 75);
        tesla.displayInfo();
        tesla.startEngine();
        tesla.charge();

        // ==================== 6. 模板方法模式 ====================
        System.out.println("\n【6. 模板方法模式】");
        System.out.println("抽象类常用于实现模板方法模式");
        System.out.println("定义算法骨架，让子类实现具体步骤");

        Beverage tea = new Tea();
        Beverage coffee = new Coffee();

        System.out.println("=== 制作茶 ===");
        tea.prepare();

        System.out.println("\n=== 制作咖啡 ===");
        coffee.prepare();

        // ==================== 7. 抽象类 vs 接口 ====================
        System.out.println("\n【7. 抽象类 vs 接口 (预览)】");
        System.out.println("┌─────────────────┬─────────────────────┬─────────────────────┐");
        System.out.println("│     特性        │      抽象类         │       接口          │");
        System.out.println("├─────────────────┼─────────────────────┼─────────────────────┤");
        System.out.println("│ 继承            │ 单继承              │ 多实现              │");
        System.out.println("│ 方法            │ 抽象和具体都可以    │ 默认都是抽象        │");
        System.out.println("│ 字段            │ 可以有实例变量      │ 只能有常量          │");
        System.out.println("│ 构造方法        │ 可以有              │ 不能有              │");
        System.out.println("│ 语义            │ is-a (是什么)       │ can-do (能做什么)   │");
        System.out.println("└─────────────────┴─────────────────────┴─────────────────────┘");

        System.out.println("\n✅ Phase 2 - Lesson 7 完成！");
    }
}

// ==================== 基本抽象类示例 ====================

/**
 * 抽象类 Animal3
 */
abstract class Animal3 {
    protected String name;

    // 抽象类可以有构造方法
    Animal3(String name) {
        this.name = name;
    }

    // 抽象方法 - 子类必须实现
    abstract void makeSound();

    // 具体方法 - 子类可以继承
    void sleep() {
        System.out.println(name + " 正在睡觉 💤");
    }
}

class Dog3 extends Animal3 {
    Dog3(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " 汪汪叫 🐕");
    }
}

class Cat3 extends Animal3 {
    Cat3(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " 喵喵叫 🐱");
    }
}

class Bird extends Animal3 {
    Bird(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " 叽叽叫 🐦");
    }

    // 子类特有的方法
    void fly() {
        System.out.println(name + " 飞翔!");
    }
}

// ==================== Document 类层次 ====================

abstract class Document {
    protected String filename;

    Document(String filename) {
        this.filename = filename;
    }

    // 抽象方法
    abstract void open();

    abstract void save();

    // 具体方法
    void close() {
        System.out.println("关闭文档: " + filename);
    }
}

class PDFDocument extends Document {
    PDFDocument(String filename) {
        super(filename);
    }

    @Override
    void open() {
        System.out.println("PDF阅读器打开: " + filename);
    }

    @Override
    void save() {
        System.out.println("保存PDF文档: " + filename);
    }
}

class WordDocument extends Document {
    WordDocument(String filename) {
        super(filename);
    }

    @Override
    void open() {
        System.out.println("Word打开: " + filename);
    }

    @Override
    void save() {
        System.out.println("保存Word文档: " + filename);
    }
}

class ExcelDocument extends Document {
    ExcelDocument(String filename) {
        super(filename);
    }

    @Override
    void open() {
        System.out.println("Excel打开: " + filename);
    }

    @Override
    void save() {
        System.out.println("保存Excel文档: " + filename);
    }
}

// ==================== 抽象类的构造方法 ====================

abstract class Car2 {
    protected String brand;
    protected String model;

    Car2(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    abstract void startEngine();

    void displayInfo() {
        System.out.println("车辆: " + brand + " " + model);
    }
}

class ElectricCar2 extends Car2 {
    private int batteryCapacity;

    ElectricCar2(String brand, String model, int batteryCapacity) {
        super(brand, model); // 调用抽象类的构造方法
        this.batteryCapacity = batteryCapacity;
    }

    @Override
    void startEngine() {
        System.out.println(brand + " " + model + " 电动引擎静默启动 ⚡");
    }

    void charge() {
        System.out.println("正在充电... 电池容量: " + batteryCapacity + " kWh");
    }
}

// ==================== 模板方法模式 ====================

/**
 * 模板方法模式示例
 * 定义制作饮料的标准流程
 */
abstract class Beverage {
    // 模板方法 - 定义算法骨架
    final void prepare() {
        boilWater();
        brew(); // 抽象方法
        pourInCup();
        addCondiments(); // 抽象方法
    }

    // 通用步骤 - 具体方法
    void boilWater() {
        System.out.println("1. 烧水");
    }

    void pourInCup() {
        System.out.println("3. 倒入杯中");
    }

    // 特定步骤 - 抽象方法，由子类实现
    abstract void brew();

    abstract void addCondiments();
}

class Tea extends Beverage {
    @Override
    void brew() {
        System.out.println("2. 浸泡茶叶");
    }

    @Override
    void addCondiments() {
        System.out.println("4. 加入柠檬");
    }
}

class Coffee extends Beverage {
    @Override
    void brew() {
        System.out.println("2. 冲泡咖啡粉");
    }

    @Override
    void addCondiments() {
        System.out.println("4. 加入牛奶和糖");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 抽象类使用 abstract 关键字
 * 2. 抽象类不能被实例化
 * 3. 抽象方法没有方法体，子类必须实现
 * 4. 抽象类可以有构造方法、具体方法、字段
 * 5. 模板方法模式是抽象类的经典应用
 * 6. 抽象类表示 "is-a" 关系
 * 
 * 🏃 练习:
 * 1. 创建一个 Game 抽象类，定义游戏的生命周期方法
 * 2. 使用模板方法模式实现数据导出功能 (CSV, JSON, XML)
 * 3. 设计一个 Account 抽象类，让不同类型的账户继承
 */
