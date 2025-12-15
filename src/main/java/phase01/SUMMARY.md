# Phase 1 学习总结: Java 基础语法 ✅

> **完成日期**: 2025 年 12 月 15 日  
> **学习时长**: 约 2 周  
> **状态**: 已完成 🎉

---

## 📚 知识点掌握情况

### 1. Java 程序基础 ✅

- [x] 理解 Java 程序的基本结构 (`package`, `class`, `main`)
- [x] 掌握 `main` 方法的签名与作用
- [x] 熟练使用 `System.out.println()` / `printf()` 输出
- [x] 了解 Java 命名规范（类名大驼峰、变量小驼峰、常量全大写）

### 2. 变量与数据类型 ✅

**8 种基本数据类型**:
| 类型 | 字节 | 范围/说明 |
|------|------|-----------|
| `byte` | 1 | -128 ~ 127 |
| `short` | 2 | -32,768 ~ 32,767 |
| `int` | 4 | 约 ±21 亿 (最常用) |
| `long` | 8 | 需加 `L` 后缀 |
| `float` | 4 | 需加 `f` 后缀 |
| `double` | 8 | 默认浮点类型 |
| `char` | 2 | Unicode 字符 |
| `boolean` | - | `true` / `false` |

**关键概念**:

- [x] 自动类型转换 (小 → 大): `byte → short → int → long → float → double`
- [x] 强制类型转换 (大 → 小): 需要 `(type)` 显式转换，可能溢出
- [x] 包装类: `Integer`, `Double`, `Boolean` 等
- [x] 自动装箱/拆箱机制
- [x] `var` 关键字 (Java 10+) 用于局部变量类型推断

### 3. 运算符 ✅

- [x] **算术运算符**: `+`, `-`, `*`, `/`, `%`, `++`, `--`
- [x] **关系运算符**: `==`, `!=`, `>`, `<`, `>=`, `<=`
- [x] **逻辑运算符**: `&&`, `||`, `!` (短路特性)
- [x] **位运算符**: `&`, `|`, `^`, `~`, `<<`, `>>`, `>>>`
- [x] **赋值运算符**: `=`, `+=`, `-=`, `*=`, `/=`, `%=`
- [x] **三元运算符**: `条件 ? 值1 : 值2`
- [x] **instanceof**: 类型检查

### 4. 控制流程 ✅

**条件语句**:

```java
// if-else
if (condition) {
    // ...
} else if (anotherCondition) {
    // ...
} else {
    // ...
}

// switch 表达式 (Java 14+)
String result = switch (day) {
    case "MONDAY", "FRIDAY" -> "Working";
    case "SATURDAY", "SUNDAY" -> "Weekend";
    default -> "Unknown";
};
```

**循环语句**:

- [x] `for` 循环 (包括增强 for-each)
- [x] `while` 循环
- [x] `do-while` 循环
- [x] `break` 和 `continue` 跳转

### 5. 数组 ✅

**一维数组**:

```java
int[] arr = new int[5];          // 声明并分配空间
int[] arr = {1, 2, 3, 4, 5};     // 直接初始化
```

**多维数组**:

```java
int[][] matrix = new int[3][4];  // 3行4列矩阵
int[][] jagged = new int[3][];   // 锯齿数组
```

**常用操作**:

- [x] `Arrays.sort()` - 排序
- [x] `Arrays.binarySearch()` - 二分查找
- [x] `Arrays.copyOf()` - 数组复制
- [x] `Arrays.fill()` - 填充
- [x] `Arrays.toString()` - 转字符串

### 6. 排序与查找算法 ✅

| 算法     | 时间复杂度 | 特点               |
| -------- | ---------- | ------------------ |
| 冒泡排序 | O(n²)      | 简单，稳定         |
| 选择排序 | O(n²)      | 简单，不稳定       |
| 插入排序 | O(n²)      | 稳定，小规模表现好 |
| 二分查找 | O(log n)   | 要求数组有序       |

### 7. 方法 ✅

```java
// 方法定义
public static int add(int a, int b) {
    return a + b;
}

// 方法重载 (同名不同参数)
public static double add(double a, double b) {
    return a + b;
}
```

- [x] 方法的定义与调用
- [x] 参数传递 (值传递)
- [x] 返回值类型
- [x] 方法重载 (Overloading)
- [x] 可变参数 `(int... numbers)`

### 8. 递归 ✅

**递归要素**:

1. **基准条件 (Base Case)**: 递归终止条件
2. **递归步骤 (Recursive Case)**: 问题分解

**经典递归算法**:

- [x] 阶乘: `n! = n × (n-1)!`
- [x] 斐波那契数列: `F(n) = F(n-1) + F(n-2)`
- [x] 汉诺塔问题
- [x] 二分查找 (递归版)
- [x] 字符串反转

**优化技巧**:

- 记忆化递归 (Memoization)
- 尾递归优化

---

## 🎯 实战项目: 控制台计算器

成功完成了 `Calculator.java` 项目，功能包括:

| 功能                                 | 状态 |
| ------------------------------------ | ---- |
| 基本四则运算 (+, -, \*, /, %)        | ✅   |
| 乘方运算 (^)                         | ✅   |
| 数学函数 (sqrt, sin, cos, pow, fact) | ✅   |
| 历史记录 (history)                   | ✅   |
| 帮助命令 (help)                      | ✅   |
| 交互式命令界面                       | ✅   |

---

## 📊 学习文件清单

| #   | 文件                         | 状态 | 核心知识点                  |
| --- | ---------------------------- | ---- | --------------------------- |
| 1   | `HelloWorld.java`            | ✅   | 程序结构, 输出              |
| 2   | `VariablesDemo.java`         | ✅   | 8 种基本类型, 包装类        |
| 3   | `OperatorsDemo.java`         | ✅   | 算术/逻辑/位运算            |
| 4   | `ConditionDemo.java`         | ✅   | if-else, switch             |
| 5   | `LoopDemo.java`              | ✅   | for, while, 嵌套循环        |
| 6   | `SwitchDemo.java`            | ✅   | Java 17 switch 新特性       |
| 7   | `ArrayBasics.java`           | ✅   | 数组创建, 遍历, Arrays 工具 |
| 8   | `ArrayAlgorithms.java`       | ✅   | 排序, 查找算法              |
| 9   | `MultiDimensionalArray.java` | ✅   | 二维数组, 矩阵运算          |
| 10  | `MethodDemo.java`            | ✅   | 方法重载, 参数传递          |
| 11  | `RecursionDemo.java`         | ✅   | 阶乘, 斐波那契, 汉诺塔      |
| 12  | `Calculator.java`            | ✅   | **综合项目**                |

---

## 💡 重点心得

### 需要注意的陷阱

1. **浮点数精度问题**: `0.1 + 0.2 != 0.3`，金融计算用 `BigDecimal`
2. **整数溢出**: `(byte) 130 = -126`
3. **局部变量必须初始化**: 否则编译报错
4. **== vs equals()**: 对象比较要用 `equals()`
5. **递归栈溢出**: 大规模递归需要改为迭代或尾递归

### Java 新特性体验

- **文本块** (Java 15+): 多行字符串更方便
- **var 关键字** (Java 10+): 局部变量类型推断
- **Switch 表达式** (Java 14+): 更简洁的 switch 语法
- **Pattern Matching** (Java 17+): instanceof 简化

---

## 🚀 下一步计划

Phase 1 已完成！准备进入 **[Phase 2: 面向对象编程](../phase02/README.md)**

Phase 2 将学习:

- 类与对象
- 封装、继承、多态
- 接口与抽象类
- 内部类
- 枚举类型

---

> 📝 _本总结由学习过程自动生成于 2025-12-15_
