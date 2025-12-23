# Java .class 字节码文件详解

## 📌 基本概念

`.class` 文件是 Java 源代码（`.java`）经过编译后生成的**二进制文件**，包含 JVM（Java 虚拟机）可以执行的**字节码指令**。

```
┌──────────────┐      编译器       ┌──────────────┐      JVM        ┌──────────────┐
│  Hello.java  │  ──────────────▶ │  Hello.class │ ──────────────▶ │   程序运行    │
│  (源代码)     │     javac        │  (字节码)     │     java        │              │
└──────────────┘                  └──────────────┘                 └──────────────┘
```

---

## 🔄 编译过程示例

```java
// Hello.java (源代码)
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```

```bash
# 编译：生成 Hello.class
javac Hello.java

# 运行：JVM 执行字节码
java Hello
```

---

## 📐 .class 文件结构

```
┌─────────────────────────────────────────────────┐
│              .class 文件结构                      │
├─────────────────────────────────────────────────┤
│  魔数 (Magic Number)      → 0xCAFEBABE          │
│  版本号 (Version)          → 主版本.次版本        │
│  常量池 (Constant Pool)   → 类名、方法名、字符串等 │
│  访问标志 (Access Flags)  → public, final 等    │
│  类索引 (This Class)      → 当前类              │
│  父类索引 (Super Class)   → 父类                │
│  接口表 (Interfaces)      → 实现的接口           │
│  字段表 (Fields)          → 成员变量            │
│  方法表 (Methods)         → 方法信息+字节码指令   │
│  属性表 (Attributes)      → 附加信息            │
└─────────────────────────────────────────────────┘
```

---

## 🔍 查看字节码内容

使用 `javap` 命令反编译：

```bash
javap -c Hello.class
```

输出示例：

```
public class Hello {
  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #2    // 获取 System.out
       3: ldc           #3    // 加载字符串 "Hello World"
       5: invokevirtual #4    // 调用 println 方法
       8: return              // 返回
}
```

---

## ⭐ 为什么需要字节码？

| 特性         | 说明                                                     |
| ------------ | -------------------------------------------------------- |
| **跨平台**   | "一次编译，到处运行" - 同一个.class 可在不同操作系统运行 |
| **安全性**   | JVM 可以在执行前验证字节码                               |
| **优化**     | JIT 编译器可以在运行时优化字节码                         |
| **保护源码** | 发布时只需提供.class，无需提供源码                       |

```
┌─────────┐
│  .class │ ──▶  Windows JVM  ──▶  Windows 运行
│  字节码  │ ──▶  Linux JVM    ──▶  Linux 运行
│         │ ──▶  macOS JVM    ──▶  macOS 运行
└─────────┘
```

---
