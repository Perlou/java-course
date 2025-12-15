package phase02;

/**
 * Phase 2 - Lesson 1: 类的基础
 * 
 * 🎯 学习目标:
 * 1. 理解类和对象的概念
 * 2. 掌握类的定义方式
 * 3. 了解成员变量和成员方法
 * 4. 学会创建和使用对象
 */
public class ClassBasics {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 1: 类的基础 ===\n");

        // ==================== 1. 什么是类和对象 ====================
        System.out.println("【1. 什么是类和对象】");
        System.out.println("类 (Class): 对象的模板/蓝图，定义了对象的属性和行为");
        System.out.println("对象 (Object): 类的实例，具有状态和行为");
        System.out.println();
        System.out.println("  现实世界          Java 世界");
        System.out.println("  ─────────        ─────────");
        System.out.println("  汽车设计图   →   Car 类");
        System.out.println("  一辆具体的车 →   Car 对象");

        // ==================== 2. 创建对象 ====================
        System.out.println("\n【2. 创建对象】");

        // 使用 new 关键字创建对象
        Person person1 = new Person();
        person1.name = "张三";
        person1.age = 25;

        Person person2 = new Person();
        person2.name = "李四";
        person2.age = 30;

        System.out.println("person1: " + person1.name + ", " + person1.age + "岁");
        System.out.println("person2: " + person2.name + ", " + person2.age + "岁");

        // ==================== 3. 成员变量 (实例变量) ====================
        System.out.println("\n【3. 成员变量 (实例变量)】");
        System.out.println("成员变量属于对象，每个对象有自己的副本");

        Car car1 = new Car();
        car1.brand = "Tesla";
        car1.model = "Model 3";
        car1.year = 2023;
        car1.price = 299900.0;

        Car car2 = new Car();
        car2.brand = "BYD";
        car2.model = "汉 EV";
        car2.year = 2024;
        car2.price = 269800.0;

        System.out.println("car1: " + car1.brand + " " + car1.model);
        System.out.println("car2: " + car2.brand + " " + car2.model);

        // ==================== 4. 成员方法 (实例方法) ====================
        System.out.println("\n【4. 成员方法 (实例方法)】");
        System.out.println("成员方法定义了对象的行为");

        car1.start();
        car1.accelerate(60);
        car1.showInfo();
        car1.stop();

        // ==================== 5. 静态成员 vs 实例成员 ====================
        System.out.println("\n【5. 静态成员 vs 实例成员】");

        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();

        System.out.println("创建了 3 个 Counter 对象");
        System.out.println("静态变量 totalCount (所有对象共享): " + Counter.totalCount);
        System.out.println("c1.instanceId (实例变量): " + c1.instanceId);
        System.out.println("c2.instanceId (实例变量): " + c2.instanceId);
        System.out.println("c3.instanceId (实例变量): " + c3.instanceId);

        System.out.println("\n静态 vs 实例 对比:");
        System.out.println("┌──────────────┬──────────────────┬──────────────────┐");
        System.out.println("│     类型     │     静态成员     │     实例成员     │");
        System.out.println("├──────────────┼──────────────────┼──────────────────┤");
        System.out.println("│ 关键字       │ static           │ 无               │");
        System.out.println("│ 内存         │ 类加载时分配     │ 创建对象时分配   │");
        System.out.println("│ 归属         │ 属于类           │ 属于对象         │");
        System.out.println("│ 访问方式     │ 类名.成员        │ 对象名.成员      │");
        System.out.println("│ 生命周期     │ 程序运行期间     │ 对象存在期间     │");
        System.out.println("└──────────────┴──────────────────┴──────────────────┘");

        // ==================== 6. this 关键字 ====================
        System.out.println("\n【6. this 关键字】");
        System.out.println("this 指向当前对象，用于区分成员变量和局部变量");

        Student student = new Student("王五", 20, "计算机科学");
        student.printInfo();

        // ==================== 7. 对象引用与内存 ====================
        System.out.println("\n【7. 对象引用与内存】");

        Person p1 = new Person();
        p1.name = "原始人";

        Person p2 = p1; // p2 和 p1 指向同一个对象
        p2.name = "被修改了";

        System.out.println("p1.name = " + p1.name); // 也会改变
        System.out.println("p2.name = " + p2.name);
        System.out.println("p1 == p2: " + (p1 == p2)); // true，同一对象

        Person p3 = new Person();
        p3.name = "被修改了";
        System.out.println("p1 == p3: " + (p1 == p3)); // false，不同对象

        // ==================== 8. null 值 ====================
        System.out.println("\n【8. null 值】");

        Person nobody = null; // 引用不指向任何对象
        System.out.println("nobody = " + nobody);

        // 空指针检查
        if (nobody != null) {
            System.out.println(nobody.name);
        } else {
            System.out.println("对象为空，无法访问成员");
        }

        System.out.println("\n✅ Phase 2 - Lesson 1 完成！");
    }
}

// ==================== 辅助类定义 ====================

/**
 * 简单的 Person 类
 * 演示基本的类结构
 */
class Person {
    // 成员变量 (实例变量)
    String name;
    int age;

    // 成员方法 (实例方法)
    void sayHello() {
        System.out.println("你好，我是 " + name);
    }
}

/**
 * Car 类
 * 演示更完整的类定义
 */
class Car {
    // 成员变量
    String brand; // 品牌
    String model; // 型号
    int year; // 年份
    double price; // 价格
    int speed = 0; // 当前速度，有默认值

    // 成员方法
    void start() {
        System.out.println(brand + " " + model + " 启动了");
    }

    void stop() {
        speed = 0;
        System.out.println(brand + " " + model + " 停止了");
    }

    void accelerate(int delta) {
        speed += delta;
        System.out.println("加速到 " + speed + " km/h");
    }

    void showInfo() {
        System.out.println("车辆信息: " + year + "年 " + brand + " " + model);
        System.out.println("价格: ¥" + price + ", 当前速度: " + speed + " km/h");
    }
}

/**
 * Counter 类
 * 演示静态变量和实例变量的区别
 */
class Counter {
    // 静态变量 (类变量) - 所有对象共享
    static int totalCount = 0;

    // 实例变量 - 每个对象独有
    int instanceId;

    // 构造方法 (后面会详细讲)
    Counter() {
        totalCount++; // 静态变量累加
        instanceId = totalCount; // 实例变量赋值
    }

    // 静态方法 - 只能访问静态成员
    static void printTotal() {
        System.out.println("总共创建了 " + totalCount + " 个对象");
        // System.out.println(instanceId); // 错误！静态方法不能访问实例变量
    }
}

/**
 * Student 类
 * 演示 this 关键字的使用
 */
class Student {
    String name;
    int age;
    String major;

    // 使用 this 区分成员变量和参数
    Student(String name, int age, String major) {
        this.name = name; // this.name 是成员变量，name 是参数
        this.age = age;
        this.major = major;
    }

    void printInfo() {
        System.out.println("学生信息: " + this.name + ", " + this.age + "岁, " + this.major + "专业");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 类是对象的模板，对象是类的实例
 * 2. 成员变量: 定义对象的状态
 * 3. 成员方法: 定义对象的行为
 * 4. static: 静态成员属于类，非静态成员属于对象
 * 5. this: 指向当前对象
 * 6. 对象变量存储的是引用（地址），不是对象本身
 * 7. null 表示引用不指向任何对象
 * 
 * 🏃 练习:
 * 1. 创建一个 Book 类，包含 title, author, price 属性
 * 2. 为 Book 类添加 discount() 方法，计算折扣价
 * 3. 添加静态变量 bookCount，统计创建的书籍数量
 */
