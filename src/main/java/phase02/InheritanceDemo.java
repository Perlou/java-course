package phase02;

/**
 * Phase 2 - Lesson 4: 继承
 * 
 * 🎯 学习目标:
 * 1. 理解继承的概念和意义
 * 2. 掌握 extends 关键字
 * 3. 了解 super 关键字的使用
 * 4. 理解继承链和 Object 类
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 4: 继承 ===\n");

        // ==================== 1. 什么是继承 ====================
        System.out.println("【1. 什么是继承】");
        System.out.println("继承 (Inheritance) 是 OOP 的核心特性");
        System.out.println("允许一个类从另一个类获取属性和方法");
        System.out.println();
        System.out.println("术语:");
        System.out.println("  父类 (Parent/Super/Base Class): 被继承的类");
        System.out.println("  子类 (Child/Sub/Derived Class): 继承的类");
        System.out.println("  is-a 关系: Dog is an Animal");

        // ==================== 2. 基本继承 ====================
        System.out.println("\n【2. 基本继承】");

        Dog dog = new Dog();
        dog.name = "旺财";
        dog.age = 3;
        dog.breed = "金毛";

        dog.eat(); // 继承自 Animal
        dog.sleep(); // 继承自 Animal
        dog.bark(); // Dog 自己的方法
        dog.showInfo(); // Dog 自己的方法

        // ==================== 3. super 关键字 ====================
        System.out.println("\n【3. super 关键字】");
        System.out.println("super 用于访问父类的成员");
        System.out.println("  - super.变量: 访问父类变量");
        System.out.println("  - super.方法(): 调用父类方法");
        System.out.println("  - super(): 调用父类构造方法");
        System.out.println();

        Cat cat = new Cat("咪咪", 2, "橘猫");
        cat.eat();
        cat.meow();

        // ==================== 4. 构造方法链 ====================
        System.out.println("\n【4. 构造方法链】");
        System.out.println("子类构造方法会自动调用父类构造方法");
        System.out.println("如果父类没有无参构造方法，必须显式调用 super(...)");
        System.out.println();

        System.out.println("创建 Puppy 对象:");
        Puppy puppy = new Puppy("小黑", 1, "土狗", true);
        puppy.showInfo();

        // ==================== 5. 继承层次 ====================
        System.out.println("\n【5. 继承层次】");
        System.out.println("Java 支持多层继承 (A → B → C)");
        System.out.println("但不支持多重继承 (A 同时继承 B 和 C)");
        System.out.println();
        System.out.println("继承层次示例:");
        System.out.println("  Object");
        System.out.println("    └── Animal");
        System.out.println("          ├── Dog");
        System.out.println("          │     └── Puppy");
        System.out.println("          └── Cat");

        // ==================== 6. Object 类 ====================
        System.out.println("\n【6. Object 类】");
        System.out.println("Object 是所有 Java 类的根类");
        System.out.println("常用方法:");
        System.out.println("  - toString(): 返回对象的字符串表示");
        System.out.println("  - equals(): 比较对象是否相等");
        System.out.println("  - hashCode(): 返回对象的哈希码");
        System.out.println("  - getClass(): 返回对象的类信息");
        System.out.println();

        System.out.println("dog.toString(): " + dog.toString());
        System.out.println("dog.getClass(): " + dog.getClass().getName());
        System.out.println("dog.hashCode(): " + dog.hashCode());

        // ==================== 7. instanceof 运算符 ====================
        System.out.println("\n【7. instanceof 运算符】");
        System.out.println("用于检查对象是否是某个类的实例");

        Animal animal = new Dog();

        System.out.println("animal instanceof Animal: " + (animal instanceof Animal));
        System.out.println("animal instanceof Dog: " + (animal instanceof Dog));
        System.out.println("animal instanceof Cat: " + (animal instanceof Cat));
        System.out.println("animal instanceof Object: " + (animal instanceof Object));

        // ==================== 8. final 关键字与继承 ====================
        System.out.println("\n【8. final 关键字与继承】");
        System.out.println("final 类: 不能被继承");
        System.out.println("final 方法: 不能被子类重写");
        System.out.println("final 变量: 不能被修改");
        System.out.println();
        System.out.println("常见的 final 类: String, Integer, Math");

        // ==================== 9. 继承的优缺点 ====================
        System.out.println("\n【9. 继承的优缺点】");
        System.out.println("优点:");
        System.out.println("  ✓ 代码复用");
        System.out.println("  ✓ 建立类层次结构");
        System.out.println("  ✓ 支持多态");
        System.out.println();
        System.out.println("缺点:");
        System.out.println("  ✗ 增加耦合度");
        System.out.println("  ✗ 破坏封装性");
        System.out.println("  ✗ 继承层次过深难以维护");
        System.out.println();
        System.out.println("原则: 优先使用组合而非继承 (Composition over Inheritance)");

        System.out.println("\n✅ Phase 2 - Lesson 4 完成！");
    }
}

// ==================== 父类定义 ====================

/**
 * Animal 类 - 动物基类
 */
class Animal {
    String name;
    int age;

    // 无参构造方法
    Animal() {
        System.out.println("  → Animal 无参构造方法被调用");
    }

    // 有参构造方法
    Animal(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("  → Animal(name, age) 构造方法被调用");
    }

    void eat() {
        System.out.println(name + " 正在吃东西...");
    }

    void sleep() {
        System.out.println(name + " 正在睡觉...");
    }

    String getDescription() {
        return "动物: " + name + ", " + age + "岁";
    }
}

// ==================== 子类定义 ====================

/**
 * Dog 类 - 继承 Animal
 */
class Dog extends Animal {
    String breed; // 子类特有的属性

    // Dog 自己的方法
    void bark() {
        System.out.println(name + " 汪汪叫!");
    }

    void showInfo() {
        System.out.println("狗狗: " + name + ", " + age + "岁, 品种: " + breed);
    }

    // 重写父类方法
    @Override
    String getDescription() {
        return "狗狗: " + name + ", " + age + "岁, 品种: " + breed;
    }
}

/**
 * Cat 类 - 演示 super 关键字
 */
class Cat extends Animal {
    String type;

    Cat(String name, int age, String type) {
        // 调用父类构造方法
        super(name, age);
        this.type = type;
        System.out.println("  → Cat 构造方法被调用");
    }

    void meow() {
        System.out.println(name + " 喵喵叫!");
    }

    // 重写父类方法，同时调用父类实现
    @Override
    void eat() {
        super.eat(); // 调用父类的 eat()
        System.out.println("  (猫咪优雅地吃鱼...)");
    }
}

/**
 * Puppy 类 - 多层继承示例
 * Puppy → Dog → Animal → Object
 */
class Puppy extends Dog {
    boolean isVaccinated;

    Puppy(String name, int age, String breed, boolean isVaccinated) {
        // 不能直接调用 Animal 的构造方法
        // 必须通过 Dog 的属性来设置
        super(); // 调用 Dog 的无参构造方法
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.isVaccinated = isVaccinated;
        System.out.println("  → Puppy 构造方法被调用");
    }

    void play() {
        System.out.println("小狗 " + name + " 正在玩耍!");
    }

    @Override
    void showInfo() {
        super.showInfo(); // 调用父类方法
        System.out.println("是否接种疫苗: " + (isVaccinated ? "是" : "否"));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 继承: 子类 extends 父类
 * 2. 子类继承父类的 public 和 protected 成员
 * 3. 子类无法继承父类的 private 成员和构造方法
 * 4. super: 访问父类成员或调用父类构造方法
 * 5. 构造方法链: 子类构造方法会先调用父类构造方法
 * 6. Object: 所有类的根类
 * 7. instanceof: 类型检查
 * 8. final: 禁止继承或重写
 * 
 * 🏃 练习:
 * 1. 创建一个 Vehicle → Car → ElectricCar 继承层次
 * 2. 使用 super 在子类中复用父类逻辑
 * 3. 重写 toString() 和 equals() 方法
 */
