package phase13;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 13: 内存问题排查
 * 
 * 本课程涵盖：
 * 1. OOM 类型与原因
 * 2. 内存泄漏检测
 * 3. 堆转储分析
 * 4. 常用排查命令
 */
public class MemoryAnalysis {

    // 模拟内存泄漏的静态集合
    private static final List<byte[]> leakList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 13: 内存问题排查");
        System.out.println("=".repeat(60));

        oomTypes();
        memoryLeakPatterns();
        heapDumpAnalysis();
        troubleshootingCommands();
        memoryMonitoringDemo();
    }

    private static void oomTypes() {
        System.out.println("\n【1. OOM 类型与原因】");
        System.out.println("-".repeat(50));

        String types = """

                ═══════════════════════════════════════════════════════════
                                 常见 OOM 类型
                ═══════════════════════════════════════════════════════════

                1️⃣ java.lang.OutOfMemoryError: Java heap space
                ─────────────────────────────────────────────────────────
                原因：堆内存不足

                常见场景：
                • 一次加载过多数据到内存
                • 内存泄漏导致对象无法回收
                • 堆内存配置过小

                解决方案：
                • 增大堆内存: -Xmx
                • 分析堆转储找到大对象
                • 使用分页/流式处理大数据


                2️⃣ java.lang.OutOfMemoryError: Metaspace
                ─────────────────────────────────────────────────────────
                原因：元空间（类元数据）不足

                常见场景：
                • 动态生成大量类（CGLIB、动态代理）
                • 类加载器泄漏
                • 反复部署应用（热部署）

                解决方案：
                • 增大元空间: -XX:MaxMetaspaceSize=512m
                • 检查类加载器是否正确关闭


                3️⃣ java.lang.OutOfMemoryError: Direct buffer memory
                ─────────────────────────────────────────────────────────
                原因：直接内存（堆外内存）不足

                常见场景：
                • NIO ByteBuffer.allocateDirect 过多
                • Netty 等框架使用堆外内存

                解决方案：
                • 增大直接内存: -XX:MaxDirectMemorySize
                • 及时释放 DirectByteBuffer


                4️⃣ java.lang.OutOfMemoryError: Unable to create new native thread
                ─────────────────────────────────────────────────────────
                原因：无法创建新线程

                常见场景：
                • 创建的线程数超过系统限制
                • 每个线程栈占用内存过大

                解决方案：
                • 使用线程池复用线程
                • 减小线程栈: -Xss256k
                • 增加系统线程数限制: ulimit -u
                """;
        System.out.println(types);
    }

    private static void memoryLeakPatterns() {
        System.out.println("\n【2. 常见内存泄漏模式】");
        System.out.println("-".repeat(50));

        String patterns = """

                ═══════════════════════════════════════════════════════════
                                内存泄漏常见模式
                ═══════════════════════════════════════════════════════════

                1. 静态集合持有对象引用
                ─────────────────────────────────────────────────────────
                ❌ 错误示例:
                private static List<User> cache = new ArrayList<>();

                public void addUser(User user) {
                    cache.add(user);  // 永远不会被回收！
                }

                ✅ 正确做法: 使用弱引用或定期清理


                2. 未关闭的资源
                ─────────────────────────────────────────────────────────
                ❌ 错误示例:
                InputStream is = new FileInputStream("file.txt");
                // 忘记 close()

                ✅ 正确做法: 使用 try-with-resources
                try (InputStream is = new FileInputStream("file.txt")) {
                    // ...
                }


                3. ThreadLocal 未清理
                ─────────────────────────────────────────────────────────
                ❌ 错误示例:
                threadLocal.set(largeObject);
                // 线程复用但未调用 remove()

                ✅ 正确做法:
                try {
                    threadLocal.set(value);
                    // 业务逻辑
                } finally {
                    threadLocal.remove();  // 必须清理！
                }


                4. 监听器/回调未注销
                ─────────────────────────────────────────────────────────
                ❌ 错误示例:
                eventBus.register(this);
                // 对象销毁时未 unregister

                ✅ 正确做法: 使用弱引用或确保注销
                """;
        System.out.println(patterns);
    }

    private static void heapDumpAnalysis() {
        System.out.println("\n【3. 堆转储分析】");
        System.out.println("-".repeat(50));

        String analysis = """

                ═══════════════════════════════════════════════════════════
                                获取堆转储的方法
                ═══════════════════════════════════════════════════════════

                1. OOM 时自动生成（推荐生产环境配置）
                -XX:+HeapDumpOnOutOfMemoryError
                -XX:HeapDumpPath=/path/to/heapdump.hprof

                2. 使用 jmap 手动生成
                jmap -dump:format=b,file=heap.hprof <pid>
                ⚠️ 注意：会触发 Full GC，生产环境慎用！

                3. 使用 jcmd（推荐）
                jcmd <pid> GC.heap_dump /path/to/heap.hprof

                4. 使用 Arthas
                heapdump /path/to/heap.hprof


                ═══════════════════════════════════════════════════════════
                                堆转储分析工具
                ═══════════════════════════════════════════════════════════

                1. Eclipse MAT (Memory Analyzer Tool) 【推荐】
                   • 自动检测内存泄漏
                   • Dominator Tree 分析
                   • Histogram 对象统计
                   • 下载: https://eclipse.dev/mat/

                2. VisualVM
                   • JDK 自带（JDK 8）或单独下载
                   • 实时监控 + 堆分析

                3. JProfiler / YourKit
                   • 商业专业工具
                   • 功能强大


                ═══════════════════════════════════════════════════════════
                                MAT 分析步骤
                ═══════════════════════════════════════════════════════════

                1. 打开堆转储文件
                2. 运行 "Leak Suspects" 报告
                3. 查看 Dominator Tree 找最大对象
                4. 使用 OQL 查询可疑对象
                5. 检查 GC Roots 引用链
                """;
        System.out.println(analysis);
    }

    private static void troubleshootingCommands() {
        System.out.println("\n【4. 内存排查命令】");
        System.out.println("-".repeat(50));

        String commands = """

                ═══════════════════════════════════════════════════════════
                                快速排查命令
                ═══════════════════════════════════════════════════════════

                # 1. 查看堆内存使用情况
                jmap -heap <pid>

                # 2. 查看对象实例数和大小（占用内存 Top 20）
                jmap -histo <pid> | head -25

                # 3. 只统计存活对象（会触发 Full GC）
                jmap -histo:live <pid> | head -25

                # 4. 实时监控内存
                jstat -gcutil <pid> 1000

                输出字段说明：
                S0/S1  - Survivor 区使用率
                E      - Eden 区使用率
                O      - Old 区使用率 ⭐ 关键
                M      - Metaspace 使用率
                YGC    - Young GC 次数
                FGC    - Full GC 次数 ⭐ 关键


                ═══════════════════════════════════════════════════════════
                                使用 Arthas 排查
                ═══════════════════════════════════════════════════════════

                # 启动 Arthas
                java -jar arthas-boot.jar

                # 查看内存使用
                memory

                # 查看对象实例
                vmtool --action getInstances --className java.util.HashMap

                # 搜索类
                sc *MemoryLeak*

                # 导出堆转储
                heapdump /tmp/heap.hprof
                """;
        System.out.println(commands);
    }

    private static void memoryMonitoringDemo() {
        System.out.println("\n【5. 内存监控演示】");
        System.out.println("-".repeat(50));

        // 获取内存 MXBean
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        System.out.println("\n当前内存使用情况:");

        // 堆内存
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        System.out.println("\n堆内存 (Heap):");
        System.out.printf("  已使用: %.2f MB%n", heapUsage.getUsed() / 1024.0 / 1024.0);
        System.out.printf("  已提交: %.2f MB%n", heapUsage.getCommitted() / 1024.0 / 1024.0);
        System.out.printf("  最大: %.2f MB%n", heapUsage.getMax() / 1024.0 / 1024.0);
        System.out.printf("  使用率: %.2f%%%n", heapUsage.getUsed() * 100.0 / heapUsage.getMax());

        // 非堆内存
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        System.out.println("\n非堆内存 (Non-Heap / Metaspace):");
        System.out.printf("  已使用: %.2f MB%n", nonHeapUsage.getUsed() / 1024.0 / 1024.0);
        System.out.printf("  已提交: %.2f MB%n", nonHeapUsage.getCommitted() / 1024.0 / 1024.0);

        // 内存池详情
        System.out.println("\n各内存池使用情况:");
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            MemoryUsage usage = pool.getUsage();
            System.out.printf("  %-25s: %.2f MB%n",
                    pool.getName(),
                    usage.getUsed() / 1024.0 / 1024.0);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 内存问题排查课程完成！下一课: SqlOptimization.java");
        System.out.println("=".repeat(60));
    }
}
