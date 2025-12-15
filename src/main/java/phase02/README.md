# Phase 2: 面向对象编程

> **目标**：深入理解 OOP 核心概念  
> **预计时长**：2 周  
> **前置条件**：Phase 1 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解类和对象的概念
2. 掌握封装、继承、多态
3. 熟练使用抽象类和接口
4. 理解内部类和枚举
5. 使用 Java 16+ 的 Record 类

---

## 📚 核心概念

### OOP 四大特性

| 特性 | 说明                       |
| ---- | -------------------------- |
| 封装 | 隐藏实现细节，暴露安全接口 |
| 继承 | 复用代码，建立层次关系     |
| 多态 | 同一接口，多种实现         |
| 抽象 | 定义模板，延迟实现         |

### 接口 vs 抽象类

- 接口：定义能力（can-do），支持多实现
- 抽象类：定义是什么（is-a），单继承

---

## 📁 文件列表

| #   | 文件                                               | 描述                 | 知识点                     |
| --- | -------------------------------------------------- | -------------------- | -------------------------- |
| 1   | [ClassBasics.java](./ClassBasics.java)             | 类的定义、属性、方法 | 类, 对象, static, this     |
| 2   | [ConstructorDemo.java](./ConstructorDemo.java)     | 构造方法与重载       | 构造方法, this(), 初始化   |
| 3   | [EncapsulationDemo.java](./EncapsulationDemo.java) | 封装与访问控制       | private, getter/setter     |
| 4   | [InheritanceDemo.java](./InheritanceDemo.java)     | 继承机制             | extends, super, instanceof |
| 5   | [PolymorphismDemo.java](./PolymorphismDemo.java)   | 多态与动态绑定       | 向上转型, 动态绑定         |
| 6   | [OverrideDemo.java](./OverrideDemo.java)           | 方法重写             | @Override, 协变返回类型    |
| 7   | [AbstractClassDemo.java](./AbstractClassDemo.java) | 抽象类               | abstract, 模板方法模式     |
| 8   | [InterfaceDemo.java](./InterfaceDemo.java)         | 接口定义与实现       | interface, implements      |
| 9   | [DefaultMethodDemo.java](./DefaultMethodDemo.java) | 接口默认方法         | default, static 方法       |
| 10  | [InnerClassDemo.java](./InnerClassDemo.java)       | 内部类类型           | 成员/静态/匿名内部类       |
| 11  | [EnumDemo.java](./EnumDemo.java)                   | 枚举与枚举方法       | enum, EnumSet, EnumMap     |
| 12  | [RecordDemo.java](./RecordDemo.java)               | Record 类 (Java 16+) | record, 不可变对象         |
| 13  | [StudentManagement.java](./StudentManagement.java) | 🎯 **实战项目**      | 学生管理系统               |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行示例 (替换类名即可)
mvn exec:java -Dexec.mainClass="phase02.ClassBasics"
mvn exec:java -Dexec.mainClass="phase02.InheritanceDemo"
mvn exec:java -Dexec.mainClass="phase02.StudentManagement"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: ClassBasics → ConstructorDemo → EncapsulationDemo
2. **Day 3-4**: InheritanceDemo → PolymorphismDemo → OverrideDemo
3. **Day 5-6**: AbstractClassDemo → InterfaceDemo → DefaultMethodDemo
4. **Day 7**: InnerClassDemo → EnumDemo → RecordDemo
5. **Day 8-14**: StudentManagement 项目 + 练习

### 学习方法

1. 先运行代码，观察输出
2. 阅读代码中的注释
3. 修改代码，尝试不同参数
4. 完成每个文件末尾的练习题
5. 最后完成学生管理系统项目

---

## ✅ 完成检查

- [ ] 能够设计类的层次结构
- [ ] 理解多态的实现原理
- [ ] 能够正确使用接口和抽象类
- [ ] 掌握内部类和枚举的使用
- [ ] 能够使用 Record 定义不可变数据
- [ ] 完成学生管理系统项目

---

## 🎯 实战项目: 学生管理系统

`StudentManagement.java` 是本阶段的综合项目，包含：

- 面向对象设计 (Student, UndergraduateStudent, GraduateStudent)
- 抽象类和接口的应用
- 枚举 (Gender, Grade, ResearchArea)
- Record (Statistics)
- 完整的 CRUD 功能

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase02.StudentManagement"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 3: 核心 API](../phase03/README.md)
