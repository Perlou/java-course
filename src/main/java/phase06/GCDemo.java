package phase06;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 6 - Lesson 3: 垃圾回收 (GC)
 * 
 * 🎯 学习目标:
 * 1. 理解垃圾回收的基本原理
 * 2. 掌握常见的 GC 算法
 * 3. 了解各种垃圾收集器
 * 4. 学会分析 GC 日志
 */
public class GCDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 6 - Lesson 3: 垃圾回收 (GC)");
        System.out.println("=".repeat(60));

        // 1. 什么是垃圾
        System.out.println("\n【1. 什么是垃圾】");
        System.out.println("""
                垃圾: 没有任何引用指向的对象

                判断对象是否为垃圾:

                1. 引用计数法 (Reference Counting)
                   - 每个对象维护一个引用计数
                   - 引用 +1，取消引用 -1
                   - 计数为 0 即为垃圾
                   - ❌ 问题: 无法解决循环引用

                2. 可达性分析 (Reachability Analysis) ✅
                   - 从 GC Roots 出发，遍历所有可达对象
                   - 不可达的对象即为垃圾
                   - Java 使用此方法
                """);

        // 2. GC Roots 演示
        System.out.println("【2. GC Roots】");
        System.out.println("""
                可作为 GC Roots 的对象:

                1. 虚拟机栈中引用的对象
                   - 方法参数、局部变量

                2. 方法区中静态变量引用的对象
                   - static 变量

                3. 方法区中常量引用的对象
                   - static final 常量

                4. 本地方法栈中 JNI 引用的对象
                   - native 方法

                5. 同步锁持有的对象
                   - synchronized 锁对象

                6. JVM 内部引用
                   - 基本类型的 Class 对象
                   - 异常对象
                   - 系统类加载器
                """);

        // 演示 GC Roots
        demonstrateGCRoots();

        // 3. GC 算法
        System.out.println("\n【3. GC 算法】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                    标记-清除算法                         │
                │  Mark-Sweep                                             │
                ├─────────────────────────────────────────────────────────┤
                │  标记前: [A][B][C][D][E]                                │
                │  标记:   [A][ ][C][ ][E]  (B、D 为垃圾)                 │
                │  清除:   [A][ ][C][ ][E]  (空间碎片化)                  │
                │                                                         │
                │  优点: 简单                                             │
                │  缺点: 内存碎片                                         │
                └─────────────────────────────────────────────────────────┘

                ┌─────────────────────────────────────────────────────────┐
                │                      复制算法                            │
                │  Copying                                                │
                ├─────────────────────────────────────────────────────────┤
                │  From:  [A][B][C][D][E]                                 │
                │  复制存活对象到 To 区:                                  │
                │  To:    [A][C][E]                                       │
                │  清空 From 区                                           │
                │                                                         │
                │  优点: 无碎片、分配快                                   │
                │  缺点: 内存只能用一半                                   │
                │  应用: 新生代 (Eden → Survivor)                        │
                └─────────────────────────────────────────────────────────┘

                ┌─────────────────────────────────────────────────────────┐
                │                   标记-整理算法                          │
                │  Mark-Compact                                           │
                ├─────────────────────────────────────────────────────────┤
                │  标记前: [A][B][C][D][E]                                │
                │  标记:   [A][ ][C][ ][E]                                │
                │  整理:   [A][C][E][ ][ ]  (存活对象向一端移动)          │
                │                                                         │
                │  优点: 无碎片                                           │
                │  缺点: 移动对象成本高                                   │
                │  应用: 老年代                                           │
                └─────────────────────────────────────────────────────────┘
                """);

        // 4. 分代收集
        System.out.println("【4. 分代收集】");
        System.out.println("""
                为什么分代?
                - 大部分对象朝生夕死 (98%)
                - 少部分对象长期存活
                - 针对不同特点使用不同算法

                ┌──────────────────────────────────────────────────────┐
                │                       堆内存                          │
                ├─────────────────────────────┬────────────────────────┤
                │        新生代 (1/3)         │      老年代 (2/3)      │
                ├──────────┬────────┬────────┤                        │
                │   Eden   │   S0   │   S1   │                        │
                │   80%    │  10%   │  10%   │                        │
                ├──────────┴────────┴────────┼────────────────────────┤
                │  复制算法                   │     标记-整理算法      │
                │  Minor GC (频繁、快速)      │   Major GC (较慢)      │
                └─────────────────────────────┴────────────────────────┘

                对象晋升老年代的条件:
                1. 年龄到达阈值 (默认15次, -XX:MaxTenuringThreshold)
                2. 动态年龄判断 (相同年龄对象大小超过 Survivor 50%)
                3. 大对象直接进入老年代 (-XX:PretenureSizeThreshold)
                """);

        // 5. 垃圾收集器
        System.out.println("【5. 垃圾收集器】");
        System.out.println("""
                新生代收集器:
                ┌────────────────────────────────────────────────────────┐
                │  Serial      单线程，STW                               │
                │              -XX:+UseSerialGC                          │
                ├────────────────────────────────────────────────────────┤
                │  ParNew      多线程版 Serial                           │
                │              -XX:+UseParNewGC                          │
                ├────────────────────────────────────────────────────────┤
                │  Parallel    吞吐量优先                                │
                │  Scavenge    -XX:+UseParallelGC                        │
                └────────────────────────────────────────────────────────┘

                老年代收集器:
                ┌────────────────────────────────────────────────────────┐
                │  Serial Old   单线程，标记-整理                        │
                ├────────────────────────────────────────────────────────┤
                │  Parallel     多线程，标记-整理                        │
                │  Old          -XX:+UseParallelOldGC                    │
                ├────────────────────────────────────────────────────────┤
                │  CMS          并发标记清除，低延迟                     │
                │               -XX:+UseConcMarkSweepGC                  │
                │               已废弃 (JDK9+)                           │
                └────────────────────────────────────────────────────────┘

                全堆收集器 (推荐):
                ┌────────────────────────────────────────────────────────┐
                │  G1           Region 分区，可预测停顿                  │
                │               -XX:+UseG1GC (JDK9+ 默认)                │
                ├────────────────────────────────────────────────────────┤
                │  ZGC          超低延迟 (<10ms)                         │
                │               -XX:+UseZGC (JDK11+)                     │
                ├────────────────────────────────────────────────────────┤
                │  Shenandoah   低延迟，RedHat                           │
                │               -XX:+UseShenandoahGC                     │
                └────────────────────────────────────────────────────────┘
                """);

        // 6. 查看当前 GC 信息
        System.out.println("【6. 当前 GC 信息】");

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println("收集器: " + gcBean.getName());
            System.out.println("  收集次数: " + gcBean.getCollectionCount());
            System.out.println("  收集耗时: " + gcBean.getCollectionTime() + " ms");
        }

        // 7. 触发 GC 并观察
        System.out.println("\n【7. 触发 GC 并观察】");

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        System.out.println("GC 前:");
        printMemoryUsage(memoryBean);

        // 分配一些对象
        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new byte[1024 * 1024]); // 10MB
        }

        System.out.println("\n分配 10MB 后:");
        printMemoryUsage(memoryBean);

        // 清除引用并 GC
        list.clear();
        list = null;
        System.gc(); // 建议 GC

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        System.out.println("\nGC 后:");
        printMemoryUsage(memoryBean);

        // 打印 GC 次数变化
        System.out.println("\nGC 统计:");
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println("  " + gcBean.getName() + ": " +
                    gcBean.getCollectionCount() + " 次, " +
                    gcBean.getCollectionTime() + " ms");
        }

        // 8. 引用类型
        System.out.println("\n【8. Java 引用类型】");
        System.out.println("""
                引用强度: 强 > 软 > 弱 > 虚

                1. 强引用 (Strong Reference)
                   Object obj = new Object();
                   - 不回收，宁可 OOM

                2. 软引用 (Soft Reference)
                   SoftReference<Object> soft = new SoftReference<>(obj);
                   - 内存不足时回收
                   - 适合缓存

                3. 弱引用 (Weak Reference)
                   WeakReference<Object> weak = new WeakReference<>(obj);
                   - 下次 GC 就回收
                   - WeakHashMap

                4. 虚引用 (Phantom Reference)
                   PhantomReference<Object> phantom = new PhantomReference<>(obj, queue);
                   - 无法获取对象
                   - 用于跟踪对象被回收
                """);

        // 演示软引用和弱引用
        demonstrateReferences();

        // 9. GC 调优参数
        System.out.println("\n【9. 常用 GC 调优参数】");
        System.out.println("""
                基础参数:
                -Xms1g          初始堆大小
                -Xmx1g          最大堆大小 (建议相等避免动态调整)
                -Xmn256m        新生代大小

                G1 参数:
                -XX:+UseG1GC                  使用 G1
                -XX:MaxGCPauseMillis=200      目标停顿时间
                -XX:G1HeapRegionSize=4m       Region 大小

                GC 日志 (JDK9+):
                -Xlog:gc*:gc.log:time,tags    输出 GC 日志
                -Xlog:gc+heap=debug           堆详情

                GC 日志 (JDK8):
                -XX:+PrintGCDetails           详细 GC 日志
                -XX:+PrintGCDateStamps        打印时间戳
                -Xloggc:gc.log                输出到文件
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 GC 使用可达性分析判断垃圾");
        System.out.println("💡 新生代用复制算法，老年代用标记-整理");
        System.out.println("💡 G1 是 JDK9+ 默认收集器，ZGC 延迟更低");
        System.out.println("=".repeat(60));
    }

    private static void printMemoryUsage(MemoryMXBean memoryBean) {
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        long mb = 1024 * 1024;
        System.out.println("  堆使用: " + heap.getUsed() / mb + " MB / " + heap.getMax() / mb + " MB");
    }

    /**
     * 演示 GC Roots
     */
    private static void demonstrateGCRoots() {
        // 1. 栈帧中的局部变量
        Object localVar = new Object(); // GC Root

        // 2. 看 staticRef 字段
        staticRef = new Object(); // GC Root (静态变量)

        System.out.println("GC Roots 示例:");
        System.out.println("  局部变量 localVar: " + localVar);
        System.out.println("  静态变量 staticRef: " + staticRef);
    }

    private static Object staticRef; // 静态变量作为 GC Root

    /**
     * 演示软引用和弱引用
     */
    private static void demonstrateReferences() {
        System.out.println("\n软引用和弱引用演示:");

        // 软引用
        Object strongObj = new Object();
        SoftReference<Object> softRef = new SoftReference<>(strongObj);
        strongObj = null; // 移除强引用

        System.out.println("软引用 (GC前): " + softRef.get());
        System.gc();
        System.out.println("软引用 (GC后): " + softRef.get() + " (内存充足时不回收)");

        // 弱引用
        Object weakObj = new Object();
        WeakReference<Object> weakRef = new WeakReference<>(weakObj);
        weakObj = null;

        System.out.println("弱引用 (GC前): " + weakRef.get());
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        System.out.println("弱引用 (GC后): " + weakRef.get() + " (被回收)");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 垃圾判断:
 * - 引用计数法 (有循环引用问题)
 * - 可达性分析 (Java 使用)
 * 
 * 2. GC 算法:
 * - 标记-清除: 简单但有碎片
 * - 复制算法: 无碎片但空间减半
 * - 标记-整理: 无碎片但需移动对象
 * 
 * 3. 分代收集:
 * - 新生代: 复制算法 (Eden + S0 + S1)
 * - 老年代: 标记-整理
 * 
 * 4. 垃圾收集器:
 * - Serial: 单线程
 * - Parallel: 吞吐量优先
 * - G1: 可预测停顿 (默认)
 * - ZGC: 超低延迟
 * 
 * 🏃 练习:
 * 1. 开启 GC 日志运行程序
 * 2. 使用不同 GC 收集器对比
 * 3. 模拟 Full GC 的触发
 */
