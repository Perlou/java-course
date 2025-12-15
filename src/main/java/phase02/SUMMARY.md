# Phase 2 学习总结: 面向对象编程 ✅

> **完成日期**: 2025 年 12 月 16 日  
> **学习时长**: 约 2 周  
> **状态**: 已完成 🎉

---

## 📚 知识点掌握情况

### 1. 类与对象 ✅

- [x] 理解类是对象的模板/蓝图
- [x] 掌握成员变量（实例变量）和成员方法
- [x] 区分静态成员 vs 实例成员
- [x] 理解 `this` 关键字的作用
- [x] 掌握对象引用与内存模型

```java
// 类定义
public class Person {
    String name;        // 成员变量
    static int count;   // 静态变量

    void sayHello() {   // 成员方法
        System.out.println("Hello, " + this.name);
    }
}
```

### 2. 构造方法 ✅

- [x] 理解构造方法的作用（初始化对象）
- [x] 掌握构造方法重载
- [x] 掌握 `this()` 调用其他构造方法
- [x] 理解初始化顺序：静态成员 → 实例成员 → 构造方法
- [x] 了解私有构造方法的用途（单例模式、工厂方法）

### 3. 封装 ✅

**访问修饰符**:
| 修饰符 | 同类 | 同包 | 子类 | 不同包 |
|--------|------|------|------|--------|
| `public` | ✓ | ✓ | ✓ | ✓ |
| `protected` | ✓ | ✓ | ✓ | ✗ |
| `default` | ✓ | ✓ | ✗ | ✗ |
| `private` | ✓ | ✗ | ✗ | ✗ |

**封装原则**:

- [x] 成员变量使用 `private`
- [x] 通过 `getter/setter` 提供访问
- [x] 在 setter 中添加数据验证
- [x] 理解不可变对象（`final` 类 + `final` 字段）

### 4. 继承 ✅

- [x] 掌握 `extends` 关键字
- [x] 理解 `super` 关键字（访问父类成员/调用父类构造方法）
- [x] 理解构造方法链（子类会调用父类构造方法）
- [x] 掌握 `instanceof` 类型检查
- [x] 了解 `final` 禁止继承

```java
class Animal {
    String name;
    Animal(String name) { this.name = name; }
}

class Dog extends Animal {
    Dog(String name) { super(name); }
    void bark() { System.out.println(name + " 汪汪!"); }
}
```

### 5. 多态 ✅

- [x] 理解多态的三个条件：继承 + 重写 + 父类引用指向子类对象
- [x] 掌握向上转型（自动，安全）
- [x] 掌握向下转型（强制转换，使用 `instanceof` 检查）
- [x] 理解动态绑定（运行时确定调用哪个方法）
- [x] 运用多态数组和多态参数

```java
Animal animal = new Dog("旺财");  // 向上转型
animal.makeSound();  // 动态绑定：调用 Dog 的方法

if (animal instanceof Dog dog) {  // 模式匹配
    dog.bark();  // 安全的向下转型
}
```

### 6. 方法重写 ✅

- [x] 掌握 `@Override` 注解的使用
- [x] 理解重写规则（同名、同参数、返回类型兼容、访问权限不能更严格）
- [x] 区分重写 (Override) vs 重载 (Overload)
- [x] 了解协变返回类型
- [x] 掌握 `toString()`、`equals()`、`hashCode()` 的重写

### 7. 抽象类 ✅

- [x] 理解 `abstract` 关键字
- [x] 抽象类不能实例化
- [x] 抽象方法没有方法体，子类必须实现
- [x] 掌握模板方法模式

```java
abstract class Document {
    abstract void open();    // 抽象方法
    void close() { ... }     // 具体方法
}
```

### 8. 接口 ✅

- [x] 掌握 `interface` 和 `implements` 关键字
- [x] 理解接口方法默认是 `public abstract`
- [x] 理解接口字段默认是 `public static final`
- [x] 掌握实现多个接口
- [x] 了解函数式接口和 Lambda 表达式

**抽象类 vs 接口**:
| 特性 | 抽象类 | 接口 |
|------|--------|------|
| 继承 | 单继承 | 多实现 |
| 方法 | 抽象和具体都可 | 默认抽象 |
| 字段 | 可有实例变量 | 只能常量 |
| 语义 | is-a (是什么) | can-do (能做什么) |

### 9. 接口默认方法 ✅

- [x] 掌握 `default` 方法（Java 8+）
- [x] 掌握接口中的 `static` 方法
- [x] 了解 `private` 方法（Java 9+）
- [x] 处理默认方法冲突（`接口名.super.方法()`）

### 10. 内部类 ✅

| 类型       | 特点                                   |
| ---------- | -------------------------------------- |
| 成员内部类 | 与外部类实例关联，可访问外部类所有成员 |
| 静态内部类 | 不依赖外部类实例，只能访问静态成员     |
| 局部内部类 | 定义在方法中，作用域有限               |
| 匿名内部类 | 一次性使用，简化接口实现               |

- [x] 掌握 Builder 模式（使用静态内部类）
- [x] 了解匿名内部类 vs Lambda 表达式

### 11. 枚举 ✅

- [x] 掌握 `enum` 关键字
- [x] 为枚举添加字段和方法
- [x] 使用抽象方法让每个常量有不同实现
- [x] 掌握 `EnumSet` 和 `EnumMap`
- [x] 使用枚举实现状态机

```java
enum HttpStatus {
    OK(200), NOT_FOUND(404), ERROR(500);

    private final int code;
    HttpStatus(int code) { this.code = code; }
    public int getCode() { return code; }
}
```

### 12. Record 类 (Java 16+) ✅

- [x] 理解 Record 是不可变数据类
- [x] 自动生成：构造方法、getter、equals、hashCode、toString
- [x] 掌握紧凑构造方法（数据验证）
- [x] 为 Record 添加自定义方法

```java
record Point(int x, int y) {
    public Point {  // 紧凑构造方法
        if (x < 0 || y < 0) throw new IllegalArgumentException();
    }

    public double distance() {  // 自定义方法
        return Math.sqrt(x * x + y * y);
    }
}
```

---

## 🎯 实战项目: 学生管理系统

成功完成了 `StudentManagement.java` 项目，功能包括:

| 功能                                   | 知识点应用        |
| -------------------------------------- | ----------------- |
| Student 抽象类                         | 抽象类、继承      |
| UndergraduateStudent / GraduateStudent | 多态、方法重写    |
| Displayable 接口                       | 接口实现          |
| Gender / Grade / ResearchArea 枚举     | 枚举类型          |
| Statistics Record                      | Record 不可变对象 |
| StudentService                         | 封装、集合操作    |
| CRUD 功能                              | 综合应用          |

---

## 📊 学习文件清单

| #   | 文件                     | 状态 | 核心知识点                 |
| --- | ------------------------ | ---- | -------------------------- |
| 1   | `ClassBasics.java`       | ✅   | 类, 对象, static, this     |
| 2   | `ConstructorDemo.java`   | ✅   | 构造方法, this(), 初始化   |
| 3   | `EncapsulationDemo.java` | ✅   | private, getter/setter     |
| 4   | `InheritanceDemo.java`   | ✅   | extends, super, instanceof |
| 5   | `PolymorphismDemo.java`  | ✅   | 向上转型, 动态绑定         |
| 6   | `OverrideDemo.java`      | ✅   | @Override, 协变返回类型    |
| 7   | `AbstractClassDemo.java` | ✅   | abstract, 模板方法模式     |
| 8   | `InterfaceDemo.java`     | ✅   | interface, implements      |
| 9   | `DefaultMethodDemo.java` | ✅   | default, static 方法       |
| 10  | `InnerClassDemo.java`    | ✅   | 成员/静态/匿名内部类       |
| 11  | `EnumDemo.java`          | ✅   | enum, EnumSet, EnumMap     |
| 12  | `RecordDemo.java`        | ✅   | record, 不可变对象         |
| 13  | `StudentManagement.java` | ✅   | **综合项目**               |

---

## 💡 重点心得

### OOP 四大特性总结

```
┌─────────────────────────────────────────────────┐
│                   OOP 四大特性                   │
├─────────────┬───────────────────────────────────┤
│   封装      │ 隐藏实现细节，暴露安全接口        │
│   继承      │ 代码复用，建立 is-a 层次关系      │
│   多态      │ 同一接口，多种实现                │
│   抽象      │ 定义模板，延迟实现细节            │
└─────────────┴───────────────────────────────────┘
```

### 设计原则

1. **封装优先**: 成员变量用 `private`，通过方法访问
2. **组合优于继承**: 不要滥用继承，优先考虑组合
3. **面向接口编程**: 使用接口/抽象类声明变量
4. **里氏替换原则**: 子类应该能够替换父类
5. **单一职责**: 一个类只负责一件事

### Java 版本特性使用

- **Java 8**: Lambda、Stream、接口默认方法
- **Java 14**: Switch 表达式
- **Java 16**: Record 类、模式匹配 instanceof
- **Java 17**: Sealed 类
- **Java 21**: 虚拟线程、模式匹配增强

---

## 🚀 下一步计划

Phase 2 已完成！准备进入 **[Phase 3: 核心 API](../phase03/README.md)**

Phase 3 将学习:

- String 与 StringBuilder
- 日期时间 API
- 集合框架 (List, Set, Map)
- 泛型
- Lambda 与 Stream API
- Optional 类

---

> 📝 _本总结由学习过程自动生成于 2025-12-16_
