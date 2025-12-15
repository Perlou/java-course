package phase02;

/**
 * Phase 2 - Lesson 5: 多态
 * 
 * 🎯 学习目标:
 * 1. 理解多态的概念
 * 2. 掌握向上转型和向下转型
 * 3. 理解动态绑定机制
 * 4. 学会利用多态编程
 */
public class PolymorphismDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 2 - Lesson 5: 多态 ===\n");

        // ==================== 1. 什么是多态 ====================
        System.out.println("【1. 什么是多态】");
        System.out.println("多态 (Polymorphism): 同一接口，多种实现");
        System.out.println("核心思想: 父类引用指向子类对象");
        System.out.println();
        System.out.println("多态的三个条件:");
        System.out.println("  1. 有继承关系");
        System.out.println("  2. 子类重写父类方法");
        System.out.println("  3. 父类引用指向子类对象");

        // ==================== 2. 向上转型 (Upcasting) ====================
        System.out.println("\n【2. 向上转型 (Upcasting)】");
        System.out.println("子类对象 → 父类引用 (自动，安全)");

        // 父类引用指向子类对象
        Shape shape1 = new Circle2(5);
        Shape shape2 = new Rectangle2(4, 6);
        Shape shape3 = new Triangle(3, 4, 5);

        // 调用被重写的方法 - 执行的是子类版本
        shape1.draw();
        System.out.println("面积: " + shape1.getArea());

        shape2.draw();
        System.out.println("面积: " + shape2.getArea());

        shape3.draw();
        System.out.println("面积: " + shape3.getArea());

        // ==================== 3. 动态绑定 ====================
        System.out.println("\n【3. 动态绑定 (Dynamic Binding)】");
        System.out.println("在运行时才确定调用哪个方法");
        System.out.println("编译时看类型，运行时看对象");

        Shape[] shapes = {
                new Circle2(3),
                new Rectangle2(2, 5),
                new Triangle(5, 5, 6)
        };

        System.out.println("遍历形状数组:");
        double totalArea = 0;
        for (Shape shape : shapes) {
            shape.draw(); // 动态绑定：根据实际对象类型调用方法
            totalArea += shape.getArea();
        }
        System.out.printf("总面积: %.2f%n", totalArea);

        // ==================== 4. 向下转型 (Downcasting) ====================
        System.out.println("\n【4. 向下转型 (Downcasting)】");
        System.out.println("父类引用 → 子类对象 (需强制转换，可能不安全)");

        Shape shape = new Circle2(10);

        // 需要强制转换
        if (shape instanceof Circle2) {
            Circle2 circle = (Circle2) shape;
            System.out.println("半径: " + circle.getRadius());
        }

        // Java 16+ 模式匹配简化写法
        if (shape instanceof Circle2 c) {
            System.out.println("半径 (模式匹配): " + c.getRadius());
        }

        // ==================== 5. 多态数组 ====================
        System.out.println("\n【5. 多态数组】");

        Employee2[] employees = {
                new Manager("张经理", 25000, 5000),
                new Developer("李程序员", 20000, "Java"),
                new Designer("王设计师", 18000, "UI/UX"),
                new Manager("刘总监", 35000, 10000)
        };

        System.out.println("公司员工列表:");
        for (Employee2 emp : employees) {
            emp.work();
            System.out.printf("  薪资: ¥%.2f%n", emp.getSalary());
        }

        // ==================== 6. 多态参数 ====================
        System.out.println("\n【6. 多态参数】");

        AnimalShelter shelter = new AnimalShelter();

        Pets dog = new Pets("旺财", "Dog");
        Pets cat = new Pets("咪咪", "Cat");
        Pets bird = new Pets("小黄", "Bird");

        shelter.adopt(dog);
        shelter.adopt(cat);
        shelter.adopt(bird);

        // ==================== 7. 多态与 equals ====================
        System.out.println("\n【7. 多态与 Object 方法】");

        Object obj1 = new PersonV2("张三", 25);
        Object obj2 = new PersonV2("张三", 25);
        Object obj3 = new PersonV2("李四", 30);

        System.out.println("obj1.equals(obj2): " + obj1.equals(obj2));
        System.out.println("obj1.equals(obj3): " + obj1.equals(obj3));
        System.out.println("obj1.toString(): " + obj1.toString());

        // ==================== 8. 多态最佳实践 ====================
        System.out.println("\n【8. 多态最佳实践】");
        System.out.println("✓ 面向接口编程");
        System.out.println("✓ 使用父类/接口声明变量");
        System.out.println("✓ 优先使用组合和委托");
        System.out.println("✓ 遵循里氏替换原则 (LSP)");
        System.out.println("✓ 避免在循环外进行类型判断");

        System.out.println("\n✅ Phase 2 - Lesson 5 完成！");
    }
}

// ==================== Shape 类层次 ====================

/**
 * Shape 抽象基类
 */
abstract class Shape {
    String name;

    Shape(String name) {
        this.name = name;
    }

    abstract void draw();

    abstract double getArea();
}

/**
 * Circle 实现
 */
class Circle2 extends Shape {
    private double radius;

    Circle2(double radius) {
        super("圆形");
        this.radius = radius;
    }

    double getRadius() {
        return radius;
    }

    @Override
    void draw() {
        System.out.println("绘制 " + name + " (半径: " + radius + ")");
    }

    @Override
    double getArea() {
        return Math.PI * radius * radius;
    }
}

/**
 * Rectangle 实现
 */
class Rectangle2 extends Shape {
    private double width;
    private double height;

    Rectangle2(double width, double height) {
        super("矩形");
        this.width = width;
        this.height = height;
    }

    @Override
    void draw() {
        System.out.println("绘制 " + name + " (" + width + " x " + height + ")");
    }

    @Override
    double getArea() {
        return width * height;
    }
}

/**
 * Triangle 实现
 */
class Triangle extends Shape {
    private double a, b, c; // 三边长

    Triangle(double a, double b, double c) {
        super("三角形");
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    void draw() {
        System.out.println("绘制 " + name + " (边: " + a + ", " + b + ", " + c + ")");
    }

    @Override
    double getArea() {
        // 海伦公式
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}

// ==================== Employee 类层次 ====================

/**
 * Employee 基类
 */
abstract class Employee2 {
    protected String name;
    protected double baseSalary;

    Employee2(String name, double baseSalary) {
        this.name = name;
        this.baseSalary = baseSalary;
    }

    abstract void work();

    abstract double getSalary();
}

class Manager extends Employee2 {
    private double bonus;

    Manager(String name, double baseSalary, double bonus) {
        super(name, baseSalary);
        this.bonus = bonus;
    }

    @Override
    void work() {
        System.out.println("[经理] " + name + " 正在开会管理团队");
    }

    @Override
    double getSalary() {
        return baseSalary + bonus;
    }
}

class Developer extends Employee2 {
    private String language;

    Developer(String name, double baseSalary, String language) {
        super(name, baseSalary);
        this.language = language;
    }

    @Override
    void work() {
        System.out.println("[开发] " + name + " 正在用 " + language + " 编写代码");
    }

    @Override
    double getSalary() {
        return baseSalary * 1.2; // 开发人员有 20% 技术补贴
    }
}

class Designer extends Employee2 {
    private String specialty;

    Designer(String name, double baseSalary, String specialty) {
        super(name, baseSalary);
        this.specialty = specialty;
    }

    @Override
    void work() {
        System.out.println("[设计] " + name + " 正在进行 " + specialty + " 设计");
    }

    @Override
    double getSalary() {
        return baseSalary * 1.1;
    }
}

// ==================== 多态参数示例 ====================

class Pets {
    String name;
    String type;

    Pets(String name, String type) {
        this.name = name;
        this.type = type;
    }
}

class AnimalShelter {
    // 多态参数：可以接受 Pets 的任何子类
    void adopt(Pets pet) {
        System.out.println("领养了一只 " + pet.type + ": " + pet.name);
    }
}

// ==================== equals/hashCode 示例 ====================

class PersonV2 {
    private String name;
    private int age;

    PersonV2(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        PersonV2 other = (PersonV2) obj;
        return age == other.age && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + age;
    }

    @Override
    public String toString() {
        return "PersonV2{name='" + name + "', age=" + age + "}";
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 多态: 父类引用指向子类对象
 * 2. 向上转型: 自动，安全
 * 3. 向下转型: 需强转，使用 instanceof 检查
 * 4. 动态绑定: 运行时确定调用哪个方法
 * 5. 多态数组: 统一处理不同类型对象
 * 6. 多态参数: 方法接受父类类型，可传入任何子类
 * 7. 重写 equals/hashCode/toString 时利用多态
 * 
 * 🏃 练习:
 * 1. 创建一个支付系统，支持多种支付方式
 * 2. 实现一个图形编辑器，使用多态处理不同形状
 * 3. 设计一个游戏角色系统，不同角色有不同技能
 */
