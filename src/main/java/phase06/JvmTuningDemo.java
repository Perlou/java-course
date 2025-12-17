package phase06;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 6 - Lesson 5: JVM 调优
 * 
 * 🎯 学习目标:
 * 1. 掌握常用 JVM 监控工具
 * 2. 理解关键 JVM 参数
 * 3. 学会分析 GC 日志
 * 4. 掌握基本调优技巧
 */
public class JvmTuningDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(60));
        System.out.println("Phase 6 - Lesson 5: JVM 调优");
        System.out.println("=".repeat(60));

        // 1. JVM 信息
        System.out.println("\n【1. 当前 JVM 信息】");

        RuntimeMXBean runtimeMX = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM 名称: " + runtimeMX.getVmName());
        System.out.println("JVM 版本: " + runtimeMX.getVmVersion());
        System.out.println("JVM 供应商: " + runtimeMX.getVmVendor());
        System.out.println("启动时间: " + runtimeMX.getUptime() + " ms");

        // JVM 参数
        System.out.println("\n输入参数:");
        for (String arg : runtimeMX.getInputArguments()) {
            System.out.println("  " + arg);
        }

        // 2. 内存信息
        System.out.println("\n【2. 内存信息】");

        MemoryMXBean memoryMX = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMX.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMX.getNonHeapMemoryUsage();

        long mb = 1024 * 1024;
        System.out.println("堆内存:");
        System.out.println("  初始值:  " + heap.getInit() / mb + " MB");
        System.out.println("  使用量:  " + heap.getUsed() / mb + " MB");
        System.out.println("  已分配: " + heap.getCommitted() / mb + " MB");
        System.out.println("  最大值:  " + heap.getMax() / mb + " MB");

        System.out.println("\n非堆内存 (元空间等):");
        System.out.println("  使用量:  " + nonHeap.getUsed() / mb + " MB");
        System.out.println("  已分配: " + nonHeap.getCommitted() / mb + " MB");

        // 3. 内存池详情
        System.out.println("\n【3. 内存池详情】");

        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        System.out.printf("%-30s %10s %10s%n", "内存池", "使用量", "最大值");
        System.out.println("-".repeat(55));

        for (MemoryPoolMXBean pool : pools) {
            MemoryUsage usage = pool.getUsage();
            String maxStr = usage.getMax() == -1 ? "无限" : (usage.getMax() / mb) + " MB";
            System.out.printf("%-30s %10s %10s%n",
                    pool.getName(),
                    usage.getUsed() / mb + " MB",
                    maxStr);
        }

        // 4. GC 信息
        System.out.println("\n【4. GC 信息】");

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : gcBeans) {
            System.out.println("收集器: " + gc.getName());
            System.out.println("  收集次数: " + gc.getCollectionCount());
            System.out.println("  收集耗时: " + gc.getCollectionTime() + " ms");
            System.out.println("  内存池: " + String.join(", ", gc.getMemoryPoolNames()));
        }

        // 5. 线程信息
        System.out.println("\n【5. 线程信息】");

        ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
        System.out.println("当前线程数: " + threadMX.getThreadCount());
        System.out.println("峰值线程数: " + threadMX.getPeakThreadCount());
        System.out.println("总启动线程数: " + threadMX.getTotalStartedThreadCount());
        System.out.println("守护线程数: " + threadMX.getDaemonThreadCount());

        // 6. JVM 监控工具
        System.out.println("\n【6. JVM 监控工具】");
        System.out.println("""
                命令行工具 (JDK 自带):

                ┌────────────────────────────────────────────────────────┐
                │  jps                                                   │
                │  列出 Java 进程                                        │
                │  jps -l -v                                             │
                └────────────────────────────────────────────────────────┘
                ┌────────────────────────────────────────────────────────┐
                │  jinfo                                                 │
                │  查看/修改 JVM 参数                                    │
                │  jinfo -flags <pid>                                    │
                └────────────────────────────────────────────────────────┘
                ┌────────────────────────────────────────────────────────┐
                │  jstat                                                 │
                │  监控 GC 和类加载                                      |
                │  jstat -gc <pid> 1000 10  (每秒输出，共10次)           │
                │  jstat -gcutil <pid> 1000                              │
                └────────────────────────────────────────────────────────┘
                ┌────────────────────────────────────────────────────────┐
                │  jmap                                                  │
                │  生成堆 dump                                           │
                │  jmap -heap <pid>                                      │
                │  jmap -dump:format=b,file=heap.hprof <pid>             │
                └────────────────────────────────────────────────────────┘
                ┌────────────────────────────────────────────────────────┐
                │  jstack                                                │
                │  生成线程 dump                                         │
                │  jstack <pid>                                          │
                │  用于排查死锁、CPU 占用高等问题                        │
                └────────────────────────────────────────────────────────┘
                ┌────────────────────────────────────────────────────────┐
                │  jcmd                                                  │
                │  综合诊断工具 (推荐)                                   │
                │  jcmd <pid> VM.flags                                   │
                │  jcmd <pid> GC.heap_dump heap.hprof                    │
                │  jcmd <pid> Thread.print                               │
                └────────────────────────────────────────────────────────┘

                图形化工具:
                - jconsole: JDK 自带，简单监控
                - jvisualvm: 功能全面 (需单独安装)
                - JProfiler: 商业工具，功能强大
                - Arthas: 阿里开源，在线诊断
                """);

        // 7. 常用 JVM 参数
        System.out.println("【7. 常用 JVM 参数】");
        System.out.println("""
                内存参数:
                ┌────────────────────────────────────────────────────────┐
                │ -Xms512m        初始堆大小                             │
                │ -Xmx1024m       最大堆大小                             │
                │ -Xmn256m        新生代大小                             │
                │ -Xss256k        每个线程栈大小                         │
                │ -XX:MetaspaceSize=128m     初始元空间                  │
                │ -XX:MaxMetaspaceSize=256m  最大元空间                  │
                └────────────────────────────────────────────────────────┘

                GC 参数:
                ┌────────────────────────────────────────────────────────┐
                │ -XX:+UseG1GC               使用 G1 收集器              │
                │ -XX:MaxGCPauseMillis=200   G1 目标停顿时间             │
                │ -XX:+UseZGC                使用 ZGC (JDK11+)           │
                │ -XX:NewRatio=2             老年代:新生代 = 2:1         │
                │ -XX:SurvivorRatio=8        Eden:Survivor = 8:1:1       │
                │ -XX:MaxTenuringThreshold=15 晋升老年代的年龄           │
                └────────────────────────────────────────────────────────┘

                GC 日志 (JDK 9+):
                ┌────────────────────────────────────────────────────────┐
                │ -Xlog:gc*:file=gc.log:time,tags                        │
                │ -Xlog:gc+heap=debug                                    │
                └────────────────────────────────────────────────────────┘

                OOM 处理:
                ┌────────────────────────────────────────────────────────┐
                │ -XX:+HeapDumpOnOutOfMemoryError                        │
                │ -XX:HeapDumpPath=/path/dump.hprof                      │
                │ -XX:OnOutOfMemoryError="kill -9 %p"                    │
                └────────────────────────────────────────────────────────┘
                """);

        // 8. GC 日志分析
        System.out.println("【8. GC 日志分析】");
        System.out.println("""
                GC 日志示例 (G1 收集器):

                [0.123s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
                [0.123s][info][gc] GC(0) Pause Young (Normal) 24M->5M(128M) 3.456ms

                解读:
                - 时间戳: 0.123s (JVM 启动后)
                - GC 类型: Young GC
                - 原因: G1 Evacuation Pause
                - 堆变化: 24M -> 5M (总堆 128M)
                - 耗时: 3.456ms

                ┌────────────────────────────────────────────────────────┐
                │ GC 事件类型                                            │
                ├────────────────────────────────────────────────────────┤
                │ Young GC:  只回收新生代，频繁但快速                    │
                │ Mixed GC:  回收新生代+部分老年代 (G1)                 │
                │ Full GC:   全堆回收，耗时长，应尽量避免                │
                └────────────────────────────────────────────────────────┘

                GC 日志分析工具:
                - GCEasy (gceasy.io)
                - GCViewer
                - HPjmeter
                """);

        // 9. 调优实战建议
        System.out.println("【9. 调优实战建议】");
        System.out.println("""
                步骤 1: 确定目标
                - 吞吐量优先: 总GC时间占比 < 5%
                - 低延迟优先: 单次GC停顿 < 100ms
                - 内存占用: 尽量小的堆

                步骤 2: 收集数据
                - 开启 GC 日志
                - 监控工具持续观察
                - 压测环境模拟生产负载

                步骤 3: 分析问题
                - Full GC 频繁? → 老年代太小/内存泄漏
                - Young GC 太慢? → 新生代太大
                - 老年代增长快? → 对象过早晋升

                步骤 4: 调整参数
                ┌────────────────────────────────────────────────────────┐
                │ 问题             │  可能的调整                         │
                ├─────────────────┬──────────────────────────────────────┤
                │ Full GC 频繁    │ 增大老年代 / 检查内存泄漏           │
                │ Young GC 频繁   │ 增大新生代 / 检查对象创建           │
                │ GC 停顿长       │ 使用 G1/ZGC / 减小堆大小            │
                │ OOM             │ 增大堆 / 检查内存泄漏               │
                └─────────────────┴──────────────────────────────────────┘

                步骤 5: 验证效果
                - 对比调整前后的 GC 日志
                - 观察应用性能指标
                - 逐步调整，避免激进变更
                """);

        // 10. 性能测试
        System.out.println("【10. 简单性能测试】");

        int count = 100000;
        List<byte[]> list = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        long startGcCount = getGCCount();
        long startGcTime = getGCTime();

        System.out.println("分配 " + count + " 个 1KB 对象...");
        for (int i = 0; i < count; i++) {
            list.add(new byte[1024]);
        }

        long allocated = System.currentTimeMillis() - startTime;
        System.out.println("分配耗时: " + allocated + " ms");
        System.out.println("内存使用: " + list.size() * 1024 / mb + " MB");

        list.clear();
        list = null;
        System.gc();
        Thread.sleep(100);

        long gcCount = getGCCount() - startGcCount;
        long gcTime = getGCTime() - startGcTime;

        System.out.println("GC 次数: " + gcCount);
        System.out.println("GC 耗时: " + gcTime + " ms");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 使用 jstat/jmap/jstack 进行基本监控");
        System.out.println("💡 根据目标选择合适的 GC 收集器");
        System.out.println("💡 调优是循环过程：收集→分析→调整→验证");
        System.out.println("=".repeat(60));
    }

    private static long getGCCount() {
        long count = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            count += gc.getCollectionCount();
        }
        return count;
    }

    private static long getGCTime() {
        long time = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            time += gc.getCollectionTime();
        }
        return time;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 监控工具:
 * - jps/jinfo/jstat/jmap/jstack/jcmd
 * - jconsole/jvisualvm/Arthas
 * 
 * 2. 关键参数:
 * - 内存: -Xms, -Xmx, -Xmn, -Xss
 * - GC: -XX:+UseG1GC, -XX:MaxGCPauseMillis
 * - 诊断: -XX:+HeapDumpOnOutOfMemoryError
 * 
 * 3. 调优流程:
 * 确定目标 → 收集数据 → 分析问题 → 调整参数 → 验证效果
 * 
 * 4. 常见问题:
 * - Full GC 频繁: 老年代太小或内存泄漏
 * - GC 停顿长: 堆太大或收集器选择不当
 * 
 * 🏃 练习:
 * 1. 使用 jstat -gcutil 监控一个 Java 程序
 * 2. 生成 heap dump 并用 jvisualvm 分析
 * 3. 开启 GC 日志并分析
 */
