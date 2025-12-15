# Phase 1: Java 基础语法

> **目标**：掌握 Java 核心语法与编程基础  
> **预计时长**：2 周  
> **前置条件**：无  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 配置 Java 开发环境
2. 理解 Java 程序的基本结构
3. 熟练使用变量、数据类型和运算符
4. 掌握条件语句和循环结构
5. 理解数组和方法的使用
6. 编写递归算法

---

## 📚 核心概念

### 数据类型

Java 有 8 种基本数据类型：

- 整型：`byte` (1), `short` (2), `int` (4), `long` (8)
- 浮点型：`float` (4), `double` (8)
- 字符型：`char` (2)
- 布尔型：`boolean`

### 控制流程

- 条件语句：`if-else`, `switch`
- 循环语句：`for`, `while`, `do-while`
- 跳转语句：`break`, `continue`, `return`

### 方法

- 方法定义与调用
- 参数传递（值传递）
- 方法重载与递归

---

## 📁 文件列表

| #   | 文件                                                       | 描述             | 知识点                  |
| --- | ---------------------------------------------------------- | ---------------- | ----------------------- |
| 1   | [HelloWorld.java](./HelloWorld.java)                       | 第一个 Java 程序 | 程序结构, 输出          |
| 2   | [VariablesDemo.java](./VariablesDemo.java)                 | 变量与数据类型   | 8 种基本类型, 包装类    |
| 3   | [OperatorsDemo.java](./OperatorsDemo.java)                 | 运算符详解       | 算术/逻辑/位运算        |
| 4   | [ConditionDemo.java](./ConditionDemo.java)                 | 条件语句         | if-else, switch         |
| 5   | [LoopDemo.java](./LoopDemo.java)                           | 循环结构         | for, while, 嵌套循环    |
| 6   | [SwitchDemo.java](./SwitchDemo.java)                       | Switch 表达式    | Java 17 新特性          |
| 7   | [ArrayBasics.java](./ArrayBasics.java)                     | 数组基础         | 创建, 遍历, Arrays 工具 |
| 8   | [ArrayAlgorithms.java](./ArrayAlgorithms.java)             | 数组算法         | 排序, 查找算法          |
| 9   | [MultiDimensionalArray.java](./MultiDimensionalArray.java) | 多维数组         | 二维数组, 矩阵运算      |
| 10  | [MethodDemo.java](./MethodDemo.java)                       | 方法定义与调用   | 重载, 参数传递          |
| 11  | [RecursionDemo.java](./RecursionDemo.java)                 | 递归算法         | 阶乘, 斐波那契, 汉诺塔  |
| 12  | [Calculator.java](./Calculator.java)                       | 🎯 **实战项目**  | 控制台计算器            |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行示例 (替换类名即可)
mvn exec:java -Dexec.mainClass="phase01.HelloWorld"
mvn exec:java -Dexec.mainClass="phase01.VariablesDemo"
mvn exec:java -Dexec.mainClass="phase01.Calculator"

# 或直接使用 java 命令
cd target/classes
java phase01.HelloWorld
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: HelloWorld → VariablesDemo → OperatorsDemo
2. **Day 3-4**: ConditionDemo → LoopDemo → SwitchDemo
3. **Day 5-6**: ArrayBasics → ArrayAlgorithms → MultiDimensionalArray
4. **Day 7**: MethodDemo → RecursionDemo
5. **Day 8-14**: Calculator 项目 + 练习

### 学习方法

1. 先运行代码，观察输出
2. 阅读代码中的注释
3. 修改代码，尝试不同参数
4. 完成每个文件末尾的练习题
5. 最后完成 Calculator 项目

---

## ✅ 完成检查

- [ ] 能够编写并运行 Java 程序
- [ ] 理解基本数据类型和包装类
- [ ] 熟练使用条件和循环语句
- [ ] 掌握 switch 表达式 (Java 17)
- [ ] 能够操作一维和多维数组
- [ ] 理解常见排序和查找算法
- [ ] 能够定义和调用方法
- [ ] 理解递归算法的原理
- [ ] ✅ 完成控制台计算器项目

---

## 🎯 实战项目: 控制台计算器

`Calculator.java` 是本阶段的综合项目，包含：

- 基本四则运算 (+, -, \*, /, %)
- 乘方运算 (^)
- 数学函数 (sqrt, sin, cos, pow, fact)
- 历史记录功能
- 交互式命令界面

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase01.Calculator"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 2: 面向对象编程](../phase02/README.md)
