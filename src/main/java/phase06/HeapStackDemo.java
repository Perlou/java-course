package phase06;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 6 - Lesson 2: 堆与栈
 * 
 * 🎯 学习目标:
 * 1. 深入理解堆内存
 * 2. 深入理解栈内存
 * 3. 了解常见的 OOM 和栈溢出
 * 4. 学会分析内存问题
 */
public class HeapStackDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 6 - Lesson 2: 堆与栈");
        System.out.println("=".repeat(60));

        // 1. 堆 vs 栈
        System.out.println("\n【1. 堆 vs 栈对比】");
        System.out.println("""
                | 特性     | 堆 (Heap)        | 栈 (Stack)        |
                |----------|------------------|-------------------|
                | 存储内容 | 对象实例、数组    | 局部变量、方法调用 |
                | 共享性   | 线程共享          | 线程私有          |
                | 大小     | 大 (-Xmx)        | 小 (-Xss)         |
                | 分配速度 | 慢               | 快                |
                | 内存管理 | GC 自动回收      | 方法结束自动释放   |
                | 生命周期 | 对象引用决定      | 方法执行周期      |
                | 异常     | OutOfMemoryError | StackOverflowError|
                """);

        // 2. 堆内存分配演示
        System.out.println("【2. 堆内存分配演示】");

        // 对象在堆中分配
        Object obj1 = new Object(); // 堆中分配
        Object obj2 = new Object(); // 堆中分配

        int[] array = new int[100]; // 数组在堆中
        String str = new String("Hello"); // String 对象在堆中

        System.out.println("obj1 hashCode: " + System.identityHashCode(obj1));
        System.out.println("obj2 hashCode: " + System.identityHashCode(obj2));
        System.out.println("array length: " + array.length);
        System.out.println("str: " + str);

        // 3. 栈内存演示
        System.out.println("\n【3. 栈内存演示】");

        int localVar = 10; // 基本类型存在栈中
        double localDouble = 3.14; // 基本类型在栈中
        Object localRef = new Object(); // 引用在栈中，对象在堆中

        System.out.println("localVar (栈中): " + localVar);
        System.out.println("localDouble (栈中): " + localDouble);
        System.out.println("localRef 引用在栈中，指向堆中对象");

        System.out.println("""

                内存布局图示:

                栈 (Stack)              堆 (Heap)
                ┌──────────────┐        ┌──────────────────┐
                │ localVar: 10 │        │ ┌──────────────┐ │
                │ localDouble  │        │ │  Object 实例 │ │
                │ localRef ────┼───────>│ └──────────────┘ │
                └──────────────┘        │ ┌──────────────┐ │
                                        │ │  int[100]    │ │
                                        │ └──────────────┘ │
                                        └──────────────────┘
                """);

        // 4. 对象创建过程
        System.out.println("【4. 对象创建过程】");
        System.out.println("""
                new Object() 执行过程:

                1. 类加载检查
                   - 检查类是否已加载
                   - 未加载则先执行类加载

                2. 分配内存
                   - 指针碰撞 (内存规整)
                   - 空闲列表 (内存不规整)

                3. 初始化零值
                   - 实例变量设为零值
                   - 数值类型: 0
                   - 引用类型: null
                   - boolean: false

                4. 设置对象头
                   - Mark Word (hashCode, GC年龄, 锁状态)
                   - 类型指针

                5. 执行 <init> 方法
                   - 执行构造方法
                   - 初始化实例变量
                """);

        // 5. 对象内存布局
        System.out.println("【5. 对象内存布局】");
        System.out.println("""
                ┌─────────────────────────────────────┐
                │            对象头 (Header)           │
                │  ┌─────────────────────────────────┐│
                │  │ Mark Word (8字节/64位)          ││
                │  │ - hashCode (25位)               ││
                │  │ - GC 分代年龄 (4位)             ││
                │  │ - 锁标志位 (2位)                ││
                │  └─────────────────────────────────┘│
                │  ┌─────────────────────────────────┐│
                │  │ Class Pointer (4/8字节)         ││
                │  │ - 指向类元数据                  ││
                │  └─────────────────────────────────┘│
                │  ┌─────────────────────────────────┐│
                │  │ 数组长度 (仅数组对象, 4字节)    ││
                │  └─────────────────────────────────┘│
                ├─────────────────────────────────────┤
                │            实例数据                  │
                │   (Instance Data)                   │
                │   - 对象的字段数据                  │
                ├─────────────────────────────────────┤
                │            对齐填充                  │
                │   (Padding)                         │
                │   - 保证 8 字节对齐                 │
                └─────────────────────────────────────┘
                """);

        // 6. 查看对象大小
        System.out.println("【6. 内存使用情况】");

        Runtime runtime = Runtime.getRuntime();
        long mb = 1024 * 1024;

        System.out.println("分配大量对象前:");
        printMemory(runtime);

        // 分配大量对象
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new byte[1024 * 1024]); // 每次分配 1MB
        }

        System.out.println("\n分配 10MB 后:");
        printMemory(runtime);

        // 释放引用
        list.clear();
        list = null;
        System.gc(); // 建议 GC，不保证立即执行

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        System.out.println("\nGC 后:");
        printMemory(runtime);

        // 7. 栈深度测试
        System.out.println("\n【7. 栈深度测试】");

        System.out.println("测试最大递归深度 (默认栈大小)...");
        StackDepthTest test = new StackDepthTest();
        try {
            test.recursiveCall();
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError at depth: " + test.depth);
        }

        // 8. 常见 OOM 场景
        System.out.println("\n【8. 常见内存异常场景】");
        System.out.println("""
                1. java.lang.OutOfMemoryError: Java heap space
                   原因: 堆内存不足
                   场景: 大量对象创建、内存泄漏
                   解决: 增大 -Xmx 或优化代码

                2. java.lang.OutOfMemoryError: Metaspace
                   原因: 元空间不足
                   场景: 大量动态类加载
                   解决: 增大 -XX:MaxMetaspaceSize

                3. java.lang.OutOfMemoryError: GC overhead limit exceeded
                   原因: GC 耗时过长 (超过98%时间)
                   场景: 内存紧张，频繁 Full GC
                   解决: 优化代码或增大内存

                4. java.lang.StackOverflowError
                   原因: 栈深度超限
                   场景: 无限递归、深层调用
                   解决: 检查递归逻辑或增大 -Xss

                5. java.lang.OutOfMemoryError: Direct buffer memory
                   原因: 直接内存不足
                   场景: NIO 大量使用 DirectByteBuffer
                   解决: 增大 -XX:MaxDirectMemorySize
                """);

        // 9. 逃逸分析
        System.out.println("【9. 逃逸分析】");
        System.out.println("""
                逃逸分析: JIT 编译器的优化技术
                判断对象是否只在方法内部使用

                如果对象不逃逸 (只在方法内使用):
                - 栈上分配: 对象分配在栈上，方法结束自动释放
                - 标量替换: 对象拆解为基本类型
                - 同步消除: 去除没必要的同步

                JVM 参数:
                -XX:+DoEscapeAnalysis  开启逃逸分析 (默认开启)
                -XX:+EliminateAllocations  开启标量替换
                -XX:+EliminateLocks  开启同步消除
                """);

        // 逃逸分析示例
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            allocatePoint(); // 对象可能在栈上分配
        }
        System.out.println("循环分配 1000万个临时对象: " +
                (System.currentTimeMillis() - start) + " ms");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 对象在堆中分配，引用在栈中");
        System.out.println("💡 栈大小有限，注意递归深度");
        System.out.println("💡 逃逸分析可以优化对象分配");
        System.out.println("=".repeat(60));
    }

    private static void printMemory(Runtime runtime) {
        long mb = 1024 * 1024;
        long used = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("  已使用: " + used / mb + " MB");
        System.out.println("  空闲:   " + runtime.freeMemory() / mb + " MB");
        System.out.println("  总计:   " + runtime.totalMemory() / mb + " MB");
    }

    /**
     * 可能被逃逸分析优化的方法
     */
    private static int allocatePoint() {
        Point p = new Point(1, 2); // 对象不逃逸
        return p.x + p.y; // 返回基本类型
    }

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

/**
 * 栈深度测试
 */
class StackDepthTest {
    int depth = 0;

    void recursiveCall() {
        depth++;
        recursiveCall(); // 无限递归
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 堆内存:
 * - 存储对象和数组
 * - GC 管理
 * - 线程共享
 * 
 * 2. 栈内存:
 * - 存储基本类型和引用
 * - 自动分配和释放
 * - 线程私有
 * 
 * 3. 对象布局:
 * - 对象头 (Mark Word + Class Pointer)
 * - 实例数据
 * - 对齐填充
 * 
 * 4. 逃逸分析:
 * - 栈上分配
 * - 标量替换
 * - 同步消除
 * 
 * 🏃 练习:
 * 1. 修改 -Xss 参数观察栈深度变化
 * 2. 模拟 OOM 并导出 heap dump
 * 3. 使用 jmap 查看堆内存使用
 */
