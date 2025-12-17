package phase06;

/**
 * Phase 6 - Lesson 1: JVM 内存模型
 * 
 * 🎯 学习目标:
 * 1. 理解 JVM 整体架构
 * 2. 掌握 JVM 内存区域划分
 * 3. 理解各内存区域的作用
 * 4. 了解 Java 内存模型 (JMM)
 */
public class MemoryModelDemo {

    // 静态变量 - 存储在方法区/元空间
    private static final String STATIC_CONSTANT = "常量";
    private static int staticVar = 100;

    // 实例变量 - 存储在堆中
    private int instanceVar = 200;
    private Object instanceObject = new Object();

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 6 - Lesson 1: JVM 内存模型");
        System.out.println("=".repeat(60));

        // 1. JVM 整体架构
        System.out.println("\n【1. JVM 整体架构】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                    Java 应用程序                         │
                └─────────────────────────────────────────────────────────┘
                                  │ 编译
                                  ▼
                ┌─────────────────────────────────────────────────────────┐
                │                   .class 字节码文件                      │
                └─────────────────────────────────────────────────────────┘
                                  │ 加载
                                  ▼
                ┌─────────────────────────────────────────────────────────┐
                │                    JVM                                   │
                │  ┌────────────────────────────────────────────────────┐ │
                │  │              类加载子系统                           │ │
                │  │     (加载 → 链接 → 初始化)                         │ │
                │  └────────────────────────────────────────────────────┘ │
                │  ┌────────────────────────────────────────────────────┐ │
                │  │              运行时数据区                           │ │
                │  │  ┌──────┐ ┌──────┐ ┌────────┐ ┌────────┐ ┌──────┐ │ │
                │  │  │ 堆   │ │方法区│ │ 虚拟机栈│ │本地方法栈│ │程序计│ │ │
                │  │  │(Heap)│ │      │ │        │ │        │ │数器  │ │ │
                │  │  └──────┘ └──────┘ └────────┘ └────────┘ └──────┘ │ │
                │  └────────────────────────────────────────────────────┘ │
                │  ┌────────────────────────────────────────────────────┐ │
                │  │              执行引擎                               │ │
                │  │     (解释器 + JIT编译器 + GC)                      │ │
                │  └────────────────────────────────────────────────────┘ │
                │  ┌────────────────────────────────────────────────────┐ │
                │  │              本地方法接口 (JNI)                     │ │
                │  └────────────────────────────────────────────────────┘ │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 运行时数据区详解
        System.out.println("【2. 运行时数据区详解】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                   运行时数据区                           │
                ├──────────────────────┬──────────────────────────────────┤
                │    线程私有区域       │         线程共享区域             │
                ├──────────────────────┼──────────────────────────────────┤
                │  ┌────────────────┐  │  ┌────────────────────────────┐  │
                │  │  程序计数器    │  │  │            堆              │  │
                │  │  (PC Register) │  │  │         (Heap)             │  │
                │  │  • 当前线程执行│  │  │  • 对象实例                │  │
                │  │    的字节码行号│  │  │  • 数组                    │  │
                │  │  • 唯一不会OOM │  │  │  • GC 主要区域             │  │
                │  └────────────────┘  │  │  • 分代：新生代/老年代     │  │
                │  ┌────────────────┐  │  └────────────────────────────┘  │
                │  │  虚拟机栈      │  │  ┌────────────────────────────┐  │
                │  │  (VM Stack)    │  │  │          方法区            │  │
                │  │  • 栈帧        │  │  │      (Method Area)         │  │
                │  │  • 局部变量表  │  │  │  • 类信息                  │  │
                │  │  • 操作数栈    │  │  │  • 常量池                  │  │
                │  │  • 方法调用    │  │  │  • 静态变量                │  │
                │  └────────────────┘  │  │  • JDK8+: 元空间           │  │
                │  ┌────────────────┐  │  └────────────────────────────┘  │
                │  │  本地方法栈    │  │                                  │
                │  │  (Native Stack)│  │                                  │
                │  │  • Native方法  │  │                                  │
                │  └────────────────┘  │                                  │
                └──────────────────────┴──────────────────────────────────┘
                """);

        // 3. 堆内存结构
        System.out.println("【3. 堆内存结构】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                       堆 (Heap)                          │
                ├────────────────────────────────┬────────────────────────┤
                │          新生代 (Young Gen)     │     老年代 (Old Gen)   │
                │              1/3                │          2/3           │
                ├────────┬────────┬──────────────┤                        │
                │  Eden  │   S0   │     S1       │                        │
                │  8/10  │  1/10  │    1/10      │                        │
                │        │ (From) │   (To)       │                        │
                └────────┴────────┴──────────────┴────────────────────────┘

                对象分配流程:
                1. 新对象优先在 Eden 区分配
                2. Eden 满了触发 Minor GC (Young GC)
                3. 存活对象复制到 S0/S1 (交替使用)
                4. 经过多次 GC 仍存活 → 晋升到老年代
                5. 老年代满了触发 Major GC / Full GC
                """);

        // 4. 查看当前 JVM 内存信息
        System.out.println("【4. 当前 JVM 内存信息】");

        Runtime runtime = Runtime.getRuntime();
        long mb = 1024 * 1024;

        System.out.println("最大内存 (Xmx):  " + runtime.maxMemory() / mb + " MB");
        System.out.println("已分配内存:      " + runtime.totalMemory() / mb + " MB");
        System.out.println("已使用内存:      " + (runtime.totalMemory() - runtime.freeMemory()) / mb + " MB");
        System.out.println("可用内存:        " + runtime.freeMemory() / mb + " MB");
        System.out.println("处理器数量:      " + runtime.availableProcessors());

        // 5. 演示栈帧
        System.out.println("\n【5. 虚拟机栈与栈帧】");
        System.out.println("""
                每次方法调用都会创建一个栈帧 (Stack Frame):

                ┌─────────────────────────┐
                │        栈帧             │
                ├─────────────────────────┤
                │  局部变量表             │ ← 方法参数、局部变量
                │  (Local Variable Table) │
                ├─────────────────────────┤
                │  操作数栈               │ ← 计算过程的临时存储
                │  (Operand Stack)        │
                ├─────────────────────────┤
                │  动态链接               │ ← 运行时常量池引用
                │  (Dynamic Linking)      │
                ├─────────────────────────┤
                │  方法返回地址           │ ← 方法结束后返回位置
                │  (Return Address)       │
                └─────────────────────────┘
                """);

        demonstrateStack(3);

        // 6. 方法区演示
        System.out.println("\n【6. 方法区 / 元空间】");

        System.out.println("静态常量: " + STATIC_CONSTANT);
        System.out.println("静态变量: " + staticVar);

        // 获取类信息
        Class<?> clazz = MemoryModelDemo.class;
        System.out.println("类名: " + clazz.getName());
        System.out.println("类加载器: " + clazz.getClassLoader());
        System.out.println("方法数量: " + clazz.getDeclaredMethods().length);
        System.out.println("字段数量: " + clazz.getDeclaredFields().length);

        System.out.println("""

                方法区存储:
                - 类的结构信息 (字段、方法、构造方法)
                - 运行时常量池
                - 静态变量 (JDK7+移到堆中)
                - JIT 编译后的代码

                JDK 8 变化:
                - 永久代 (PermGen) → 元空间 (Metaspace)
                - 元空间使用本地内存
                - 避免 PermGen OOM
                """);

        // 7. Java 内存模型 (JMM)
        System.out.println("【7. Java 内存模型 (JMM)】");
        System.out.println("""
                JMM 定义了线程和主内存之间的抽象关系:

                ┌─────────────────────────────────────────┐
                │                主内存                    │
                │           (共享变量存储)                 │
                └───────────────────┬─────────────────────┘
                        ▲           │           ▲
                        │   read    │   write   │
                        │           ▼           │
                ┌───────┴────┐           ┌──────┴─────┐
                │  工作内存   │           │  工作内存   │
                │  (线程1)   │           │  (线程2)   │
                │  变量副本   │           │  变量副本   │
                └────────────┘           └────────────┘

                JMM 保证:
                - 原子性: synchronized
                - 可见性: volatile, synchronized
                - 有序性: happens-before 规则
                """);

        // 8. 常用 JVM 参数
        System.out.println("【8. 常用 JVM 参数】");
        System.out.println("""
                内存设置:
                -Xms512m       初始堆大小
                -Xmx1024m      最大堆大小
                -Xmn256m       新生代大小
                -Xss256k       每个线程栈大小

                元空间设置 (JDK8+):
                -XX:MetaspaceSize=128m      初始元空间
                -XX:MaxMetaspaceSize=256m   最大元空间

                GC 选择:
                -XX:+UseG1GC             使用 G1 收集器
                -XX:+UseZGC              使用 ZGC (JDK11+)
                -XX:+UseShenandoahGC     使用 Shenandoah

                GC 日志:
                -Xlog:gc*                GC 日志 (JDK9+)
                -XX:+PrintGCDetails      详细 GC 日志 (JDK8)

                调试:
                -XX:+HeapDumpOnOutOfMemoryError  OOM时dump
                -XX:HeapDumpPath=/path/to/dump   dump路径
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 堆是 GC 主要管理区域，分新生代和老年代");
        System.out.println("💡 方法区存储类信息，JDK8+使用元空间");
        System.out.println("💡 每个线程有独立的栈和程序计数器");
        System.out.println("=".repeat(60));
    }

    /**
     * 演示栈帧的创建
     */
    private static void demonstrateStack(int depth) {
        System.out.println("  ".repeat(3 - depth) + "→ 调用 demonstrateStack(" + depth + ")");
        if (depth > 0) {
            demonstrateStack(depth - 1);
        }
        System.out.println("  ".repeat(3 - depth) + "← 返回 demonstrateStack(" + depth + ")");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. JVM 内存区域:
 * - 线程私有: 程序计数器、虚拟机栈、本地方法栈
 * - 线程共享: 堆、方法区
 * 
 * 2. 堆内存:
 * - 新生代: Eden + S0 + S1
 * - 老年代: 长期存活对象
 * - 默认比例: 新生代:老年代 = 1:2
 * 
 * 3. 方法区:
 * - JDK7: 永久代 (PermGen)
 * - JDK8+: 元空间 (Metaspace)
 * - 存储类信息、常量池、静态变量
 * 
 * 4. JMM:
 * - 线程与主内存的交互规则
 * - 保证原子性、可见性、有序性
 * 
 * 🏃 练习:
 * 1. 使用 -Xmx 和 -Xms 设置不同大小，观察效果
 * 2. 使用 jvisualvm 查看 JVM 内存
 * 3. 模拟堆内存不足的情况
 */
