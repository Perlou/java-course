package phase13;

/**
 * Phase 13: JVM 调优实践
 * 
 * 本课程涵盖：
 * 1. JVM 参数配置
 * 2. GC 收集器选择
 * 3. 常用调优参数
 * 4. 监控命令
 */
public class JvmTuning {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 13: JVM 调优实践");
        System.out.println("=".repeat(60));

        showJvmInfo();
        memoryConfiguration();
        gcCollectors();
        jvmParameters();
        monitoringCommands();
    }

    private static void showJvmInfo() {
        System.out.println("\n【1. 当前 JVM 信息】");
        System.out.println("-".repeat(50));

        Runtime runtime = Runtime.getRuntime();

        System.out.println("JVM 版本: " + System.getProperty("java.vm.version"));
        System.out.println("Java 版本: " + System.getProperty("java.version"));
        System.out.printf("最大内存 (-Xmx): %d MB%n", runtime.maxMemory() / 1024 / 1024);
        System.out.printf("总内存: %d MB%n", runtime.totalMemory() / 1024 / 1024);
        System.out.printf("空闲内存: %d MB%n", runtime.freeMemory() / 1024 / 1024);
        System.out.printf("可用处理器: %d%n", runtime.availableProcessors());
    }

    private static void memoryConfiguration() {
        System.out.println("\n【2. 内存配置指南】");
        System.out.println("-".repeat(50));

        String config = """

                ═══════════════════════════════════════════════════════════
                                  JVM 内存配置参数
                ═══════════════════════════════════════════════════════════

                1. 堆内存配置
                ─────────────────────────────────────────────────────────
                -Xms4g              # 初始堆大小（建议与 -Xmx 相同）
                -Xmx4g              # 最大堆大小
                -Xmn2g              # 年轻代大小

                💡 最佳实践：-Xms = -Xmx，避免堆动态扩展的开销

                2. 元空间配置 (JDK 8+)
                ─────────────────────────────────────────────────────────
                -XX:MetaspaceSize=256m       # 元空间初始大小
                -XX:MaxMetaspaceSize=512m    # 元空间最大大小

                3. 直接内存配置
                ─────────────────────────────────────────────────────────
                -XX:MaxDirectMemorySize=1g   # NIO 使用的堆外内存

                4. 线程栈配置
                ─────────────────────────────────────────────────────────
                -Xss256k            # 线程栈大小（默认 1MB）
                """;
        System.out.println(config);
    }

    private static void gcCollectors() {
        System.out.println("\n【3. GC 收集器选择】");
        System.out.println("-".repeat(50));

        String collectors = """

                ┌──────────────┬──────────────────────────────────────────┐
                │    收集器     │                   特点                   │
                ├──────────────┼──────────────────────────────────────────┤
                │ Serial       │ 单线程，-XX:+UseSerialGC                 │
                │ Parallel     │ 多线程吞吐量优先，JDK 8 默认              │
                │ G1 (推荐)    │ 分区收集，JDK 9+ 默认，-XX:+UseG1GC      │
                │ ZGC          │ 超低延迟 <10ms，JDK 15+，-XX:+UseZGC     │
                └──────────────┴──────────────────────────────────────────┘

                📊 选择建议：
                • 堆 < 4GB：Parallel
                • 堆 >= 4GB，平衡场景：G1
                • 超低延迟需求：ZGC
                """;
        System.out.println(collectors);
    }

    private static void jvmParameters() {
        System.out.println("\n【4. 生产环境推荐配置】");
        System.out.println("-".repeat(50));

        String params = """

                # ========== 基础配置 ==========
                -server
                -Xms4g -Xmx4g
                -XX:MetaspaceSize=256m

                # ========== G1 收集器 ==========
                -XX:+UseG1GC
                -XX:MaxGCPauseMillis=200

                # ========== GC 日志 (JDK 9+) ==========
                -Xlog:gc*:file=gc.log:time:filecount=5,filesize=100m

                # ========== OOM 时自动 dump ==========
                -XX:+HeapDumpOnOutOfMemoryError
                -XX:HeapDumpPath=/path/to/heapdump.hprof

                # ========== 完整启动命令示例 ==========
                java -server -Xms4g -Xmx4g \\
                  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \\
                  -XX:+HeapDumpOnOutOfMemoryError \\
                  -jar myapp.jar
                """;
        System.out.println(params);
    }

    private static void monitoringCommands() {
        System.out.println("\n【5. JVM 监控命令】");
        System.out.println("-".repeat(50));

        String commands = """

                ═══════════════════════════════════════════════════════════
                                 JDK 自带监控工具
                ═══════════════════════════════════════════════════════════

                jps -l                      # 查看 Java 进程
                jstat -gcutil <pid> 1000    # GC 统计（每秒1次）
                jmap -heap <pid>            # 堆内存概况
                jmap -histo <pid>           # 对象统计
                jstack <pid>                # 线程栈
                jcmd <pid> help             # 综合诊断工具

                ═══════════════════════════════════════════════════════════
                                   排查 CPU 100%
                ═══════════════════════════════════════════════════════════

                top -Hp <pid>           # 找到 CPU 高的线程 TID
                printf '%x\\n' <TID>     # 转换为十六进制
                jstack <pid> | grep <hex_tid> -A 30  # 查看线程栈

                ═══════════════════════════════════════════════════════════
                                   使用 Arthas
                ═══════════════════════════════════════════════════════════

                curl -O https://arthas.aliyun.com/arthas-boot.jar
                java -jar arthas-boot.jar

                thread -n 3     # CPU 最高的3个线程
                dashboard       # 实时面板
                heapdump        # 堆转储
                """;
        System.out.println(commands);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ JVM 调优课程完成！下一课: GCAnalysis.java");
        System.out.println("=".repeat(60));
    }
}
