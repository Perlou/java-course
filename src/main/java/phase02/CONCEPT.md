# Phase 2: 面向对象编程 - 核心概念

> 面向对象是一种思维方式，将现实世界的事物抽象为对象

---

## 🎯 面向对象全景图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        面向对象编程 (OOP)                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  核心概念                                                           │
│  ├── 类 (Class) - 对象的模板/蓝图                                  │
│  ├── 对象 (Object) - 类的实例                                      │
│  └── 成员 - 属性 (Field) + 方法 (Method)                           │
│                                                                     │
│  四大特性                                                           │
│  ├── 封装 (Encapsulation) - 隐藏实现细节                           │
│  ├── 继承 (Inheritance) - 代码复用与扩展                           │
│  ├── 多态 (Polymorphism) - 同一接口不同实现                        │
│  └── 抽象 (Abstraction) - 提取共同特征                             │
│                                                                     │
│  高级特性                                                           │
│  ├── 接口 (Interface) - 定义行为契约                               │
│  ├── 抽象类 (Abstract Class) - 部分实现的模板                      │
│  ├── 内部类 (Inner Class) - 类中的类                               │
│  ├── 枚举 (Enum) - 固定常量集合                                    │
│  └── 记录类 (Record) - 不可变数据载体                              │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📦 类与对象

### 类的结构

```java
public class Person {
    // 属性（成员变量）
    private String name;
    private int age;

    // 构造方法
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 方法（成员方法）
    public void introduce() {
        System.out.println("我是 " + name);
    }

    // getter/setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

### 对象的创建与使用

```java
// 创建对象
Person person = new Person("张三", 25);

// 使用对象
person.introduce();
String name = person.getName();
```

---

## 🔒 封装 (Encapsulation)

```
┌─────────────────────────────────────────────────────────────────────┐
│                          封装                                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  核心思想: 隐藏内部实现，只暴露必要接口                              │
│                                                                     │
│  访问修饰符                                                         │
│  ┌───────────────┬───────┬─────────┬────────┬────────┐            │
│  │    修饰符     │ 类内  │ 同包    │ 子类   │ 任意   │            │
│  ├───────────────┼───────┼─────────┼────────┼────────┤            │
│  │ private       │  ✓    │         │        │        │            │
│  │ (default)     │  ✓    │   ✓     │        │        │            │
│  │ protected     │  ✓    │   ✓     │   ✓    │        │            │
│  │ public        │  ✓    │   ✓     │   ✓    │   ✓    │            │
│  └───────────────┴───────┴─────────┴────────┴────────┘            │
│                                                                     │
│  最佳实践:                                                          │
│  • 属性使用 private                                                │
│  • 通过 getter/setter 访问                                         │
│  • 在 setter 中可以添加校验逻辑                                    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 继承 (Inheritance)

```java
// 父类
public class Animal {
    protected String name;

    public void eat() {
        System.out.println(name + " 正在吃东西");
    }
}

// 子类
public class Dog extends Animal {
    public void bark() {
        System.out.println(name + " 汪汪叫");
    }

    @Override
    public void eat() {
        System.out.println(name + " 正在吃狗粮");  // 重写父类方法
    }
}
```

### 继承特点

```
• Java 只支持单继承（一个类只能有一个直接父类）
• 子类继承父类的非私有成员
• 所有类都直接或间接继承 Object 类
• 使用 super 关键字访问父类成员
```

---

## 🎭 多态 (Polymorphism)

```
┌─────────────────────────────────────────────────────────────────────┐
│                          多态                                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  定义: 同一类型的对象，调用同一方法，表现不同行为                    │
│                                                                     │
│  实现条件:                                                          │
│  1. 继承关系                                                        │
│  2. 方法重写                                                        │
│  3. 父类引用指向子类对象                                            │
│                                                                     │
│  Animal animal = new Dog();  // 父类引用指向子类对象                │
│  animal.eat();               // 调用的是 Dog 的 eat() 方法          │
│                                                                     │
│  编译时看左边（Animal），运行时看右边（Dog）                        │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔌 接口 (Interface)

```java
// 定义接口
public interface Flyable {
    void fly();  // 抽象方法

    default void land() {  // 默认方法 (Java 8+)
        System.out.println("降落");
    }
}

// 实现接口
public class Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("鸟儿飞翔");
    }
}
```

### 接口 vs 抽象类

| 特性     | 接口           | 抽象类         |
| -------- | -------------- | -------------- |
| 关键字   | interface      | abstract class |
| 多继承   | 支持多实现     | 单继承         |
| 构造方法 | 无             | 有             |
| 成员变量 | 只能是常量     | 可以有实例变量 |
| 方法     | 抽象/默认/静态 | 抽象/具体      |
| 设计目的 | 定义行为契约   | 代码复用       |

---

## 📚 高级特性

### 内部类

```java
public class Outer {
    private int x = 10;

    // 成员内部类
    class Inner {
        void show() {
            System.out.println(x);  // 可以访问外部类私有成员
        }
    }

    // 静态内部类
    static class StaticInner {
        void show() {
            // 不能直接访问外部类实例成员
        }
    }

    void method() {
        // 局部内部类
        class LocalInner { }

        // 匿名内部类
        Runnable r = new Runnable() {
            @Override
            public void run() { }
        };
    }
}
```

### 枚举 (Enum)

```java
public enum Status {
    PENDING("待处理"),
    PROCESSING("处理中"),
    COMPLETED("已完成");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

### 记录类 (Record) - Java 16+

```java
// 自动生成 构造器、getter、equals、hashCode、toString
public record Point(int x, int y) {
    // 可以添加自定义方法
    public double distance() {
        return Math.sqrt(x * x + y * y);
    }
}

// 使用
Point p = new Point(3, 4);
int x = p.x();  // getter 方法名不带 get 前缀
```

---

## 📖 学习要点

```
✅ 理解类和对象的关系
✅ 掌握封装、继承、多态的应用
✅ 理解接口和抽象类的区别与使用场景
✅ 掌握方法重写 vs 方法重载
✅ 了解内部类的种类和使用
✅ 熟练使用枚举和 Record
```

---

> 接下来学习集合框架：[Phase 3 README](../phase03/README.md)
