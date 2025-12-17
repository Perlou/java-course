package phase06;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Phase 6 - 实战项目: JVM 监控报告生成器
 * 
 * 🎯 项目目标:
 * 1. 综合运用 JVM 知识
 * 2. 实现 JVM 状态监控
 * 3. 生成 JVM 诊断报告
 * 4. 模拟常见内存问题
 */
public class JvmMonitor {

    private static final long MB = 1024 * 1024;

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   🖥️ JVM 监控报告生成器 v1.0");
        System.out.println("=".repeat(60));

        Scanner scanner = new Scanner(System.in);

        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> generateReport();
                case "2" -> monitorMemory();
                case "3" -> monitorGC();
                case "4" -> monitorThreads();
                case "5" -> simulateMemoryLeak();
                case "6" -> testGC();
                case "7" -> showJvmArgs();
                case "0" -> {
                    System.out.println("👋 再见!");
                    return;
                }
                default -> System.out.println("无效选项");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("1. 生成 JVM 诊断报告");
        System.out.println("2. 实时内存监控");
        System.out.println("3. GC 监控");
        System.out.println("4. 线程监控");
        System.out.println("5. 模拟内存泄漏");
        System.out.println("6. GC 压力测试");
        System.out.println("7. 查看 JVM 参数");
        System.out.println("0. 退出");
        System.out.print("请选择 > ");
    }

    /**
     * 生成完整的 JVM 诊断报告
     */
    private static void generateReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              JVM 诊断报告");
        System.out.println("              " + new Date());
        System.out.println("=".repeat(60));

        // 1. JVM 基本信息
        System.out.println("\n【1. JVM 基本信息】");
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM 名称:    " + runtime.getVmName());
        System.out.println("JVM 版本:    " + runtime.getVmVersion());
        System.out.println("JVM 供应商:  " + runtime.getVmVendor());
        System.out.println("启动时间:    " + formatUptime(runtime.getUptime()));
        System.out.println("进程 PID:    " + runtime.getPid());

        // 2. 操作系统信息
        System.out.println("\n【2. 操作系统信息】");
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("OS 名称:     " + os.getName());
        System.out.println("OS 版本:     " + os.getVersion());
        System.out.println("OS 架构:     " + os.getArch());
        System.out.println("可用处理器:  " + os.getAvailableProcessors());
        System.out.println("系统负载:    " + String.format("%.2f", os.getSystemLoadAverage()));

        // 3. 内存信息
        System.out.println("\n【3. 内存信息】");
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memory.getHeapMemoryUsage();
        MemoryUsage nonHeap = memory.getNonHeapMemoryUsage();

        System.out.println("堆内存:");
        printMemoryUsage("  ", heap);
        System.out.println("非堆内存:");
        printMemoryUsage("  ", nonHeap);

        // 内存使用率
        double heapUsage = (double) heap.getUsed() / heap.getMax() * 100;
        System.out.printf("\n堆内存使用率: %.1f%%%n", heapUsage);
        if (heapUsage > 80) {
            System.out.println("⚠️  警告: 堆内存使用率超过 80%!");
        }

        // 4. 内存池详情
        System.out.println("\n【4. 内存池详情】");
        System.out.printf("%-30s %12s %12s %10s%n", "内存池", "已使用", "最大", "使用率");
        System.out.println("-".repeat(70));

        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage usage = pool.getUsage();
            String maxStr = usage.getMax() == -1 ? "无限" : formatSize(usage.getMax());
            double usagePercent = usage.getMax() == -1 ? 0 : (double) usage.getUsed() / usage.getMax() * 100;
            System.out.printf("%-30s %12s %12s %9.1f%%%n",
                    pool.getName(),
                    formatSize(usage.getUsed()),
                    maxStr,
                    usagePercent);
        }

        // 5. GC 信息
        System.out.println("\n【5. GC 信息】");
        long totalGcCount = 0;
        long totalGcTime = 0;

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("收集器: " + gc.getName());
            System.out.println("  收集次数: " + gc.getCollectionCount());
            System.out.println("  收集耗时: " + gc.getCollectionTime() + " ms");
            System.out.println("  管理内存: " + String.join(", ", gc.getMemoryPoolNames()));
            totalGcCount += gc.getCollectionCount();
            totalGcTime += gc.getCollectionTime();
        }

        System.out.println("\nGC 汇总:");
        System.out.println("  总 GC 次数: " + totalGcCount);
        System.out.println("  总 GC 耗时: " + totalGcTime + " ms");
        double gcRatio = runtime.getUptime() > 0 ? (double) totalGcTime / runtime.getUptime() * 100 : 0;
        System.out.printf("  GC 时间占比: %.2f%%%n", gcRatio);
        if (gcRatio > 5) {
            System.out.println("⚠️  警告: GC 时间占比超过 5%!");
        }

        // 6. 线程信息
        System.out.println("\n【6. 线程信息】");
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        System.out.println("当前线程数:   " + thread.getThreadCount());
        System.out.println("峰值线程数:   " + thread.getPeakThreadCount());
        System.out.println("守护线程数:   " + thread.getDaemonThreadCount());
        System.out.println("总启动线程:   " + thread.getTotalStartedThreadCount());

        // 死锁检测
        long[] deadlockedThreads = thread.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            System.out.println("⚠️  检测到死锁! 线程数: " + deadlockedThreads.length);
        } else {
            System.out.println("✅ 未检测到死锁");
        }

        // 7. 类加载信息
        System.out.println("\n【7. 类加载信息】");
        ClassLoadingMXBean classLoading = ManagementFactory.getClassLoadingMXBean();
        System.out.println("已加载类数:   " + classLoading.getLoadedClassCount());
        System.out.println("总加载类数:   " + classLoading.getTotalLoadedClassCount());
        System.out.println("已卸载类数:   " + classLoading.getUnloadedClassCount());

        // 8. 诊断建议
        System.out.println("\n【8. 诊断建议】");
        provideDiagnosticAdvice(heap, gcRatio, thread.getThreadCount());

        System.out.println("\n" + "=".repeat(60));
        System.out.println("              报告生成完毕");
        System.out.println("=".repeat(60));
    }

    /**
     * 实时内存监控
     */
    private static void monitorMemory() {
        System.out.println("\n实时内存监控 (按 Ctrl+C 停止):");
        System.out.println("-".repeat(60));
        System.out.printf("%-8s %12s %12s %12s %10s%n",
                "时间", "已使用", "已分配", "最大", "使用率");

        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();

        for (int i = 0; i < 20; i++) {
            MemoryUsage heap = memory.getHeapMemoryUsage();
            double usage = (double) heap.getUsed() / heap.getMax() * 100;

            System.out.printf("%-8s %12s %12s %12s %9.1f%%%n",
                    String.format("%ds", i),
                    formatSize(heap.getUsed()),
                    formatSize(heap.getCommitted()),
                    formatSize(heap.getMax()),
                    usage);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * GC 监控
     */
    private static void monitorGC() {
        System.out.println("\nGC 监控 (20秒):");
        System.out.println("-".repeat(60));

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        long[] prevCounts = new long[gcBeans.size()];
        long[] prevTimes = new long[gcBeans.size()];

        for (int i = 0; i < gcBeans.size(); i++) {
            prevCounts[i] = gcBeans.get(i).getCollectionCount();
            prevTimes[i] = gcBeans.get(i).getCollectionTime();
        }

        for (int sec = 0; sec < 20; sec++) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%2ds: ", sec));

            for (int i = 0; i < gcBeans.size(); i++) {
                GarbageCollectorMXBean gc = gcBeans.get(i);
                long count = gc.getCollectionCount() - prevCounts[i];
                long time = gc.getCollectionTime() - prevTimes[i];

                if (count > 0) {
                    sb.append(String.format("[%s: %d次/%dms] ", gc.getName(), count, time));
                }

                prevCounts[i] = gc.getCollectionCount();
                prevTimes[i] = gc.getCollectionTime();
            }

            if (sb.length() > 5) {
                System.out.println(sb);
            } else {
                System.out.println(sec + "s: (无 GC)");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * 线程监控
     */
    private static void monitorThreads() {
        System.out.println("\n线程监控:");
        System.out.println("-".repeat(60));

        ThreadMXBean thread = ManagementFactory.getThreadMXBean();

        // 获取所有线程
        long[] threadIds = thread.getAllThreadIds();
        ThreadInfo[] threadInfos = thread.getThreadInfo(threadIds);

        // 按状态统计
        Map<Thread.State, Integer> stateCount = new EnumMap<>(Thread.State.class);
        for (Thread.State state : Thread.State.values()) {
            stateCount.put(state, 0);
        }

        for (ThreadInfo info : threadInfos) {
            if (info != null) {
                stateCount.merge(info.getThreadState(), 1, Integer::sum);
            }
        }

        System.out.println("线程状态分布:");
        for (Map.Entry<Thread.State, Integer> entry : stateCount.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.printf("  %-15s: %d%n", entry.getKey(), entry.getValue());
            }
        }

        System.out.println("\n前 10 个线程:");
        System.out.printf("%-5s %-30s %-15s%n", "ID", "名称", "状态");
        System.out.println("-".repeat(55));

        int count = 0;
        for (ThreadInfo info : threadInfos) {
            if (info != null && count++ < 10) {
                System.out.printf("%-5d %-30s %-15s%n",
                        info.getThreadId(),
                        truncate(info.getThreadName(), 30),
                        info.getThreadState());
            }
        }
    }

    /**
     * 模拟内存泄漏
     */
    private static void simulateMemoryLeak() {
        System.out.println("\n模拟内存泄漏 (观察内存增长):");
        System.out.println("⚠️  这会持续消耗内存!");

        List<byte[]> leak = new ArrayList<>();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();

        for (int i = 0; i < 50; i++) {
            leak.add(new byte[1024 * 1024]); // 每次 1MB

            MemoryUsage heap = memory.getHeapMemoryUsage();
            double usage = (double) heap.getUsed() / heap.getMax() * 100;

            System.out.printf("分配 %3d MB, 堆使用: %s / %s (%.1f%%)%n",
                    i + 1,
                    formatSize(heap.getUsed()),
                    formatSize(heap.getMax()),
                    usage);

            if (usage > 90) {
                System.out.println("⚠️  内存使用率超过 90%，停止模拟");
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }

        // 清理
        leak.clear();
        System.gc();
        System.out.println("✅ 已清理模拟的内存泄漏");
    }

    /**
     * GC 压力测试
     */
    private static void testGC() {
        System.out.println("\nGC 压力测试:");
        System.out.println("持续分配和释放对象，观察 GC 行为");

        long startGcCount = getGCCount();
        long startGcTime = getGCTime();
        long startTime = System.currentTimeMillis();

        int iterations = 100;
        int objectsPerIteration = 10000;

        for (int i = 0; i < iterations; i++) {
            List<byte[]> temp = new ArrayList<>();
            for (int j = 0; j < objectsPerIteration; j++) {
                temp.add(new byte[100]); // 100 字节对象
            }
            // temp 离开作用域，变为垃圾

            if ((i + 1) % 20 == 0) {
                System.out.printf("进度: %d/%d%n", i + 1, iterations);
            }
        }

        long endTime = System.currentTimeMillis();
        long gcCount = getGCCount() - startGcCount;
        long gcTime = getGCTime() - startGcTime;

        System.out.println("\nGC 压力测试结果:");
        System.out.println("  总耗时:    " + (endTime - startTime) + " ms");
        System.out.println("  GC 次数:   " + gcCount);
        System.out.println("  GC 耗时:   " + gcTime + " ms");
        System.out.printf("  GC 占比:   %.2f%%%n", (double) gcTime / (endTime - startTime) * 100);
    }

    /**
     * 显示 JVM 参数
     */
    private static void showJvmArgs() {
        System.out.println("\nJVM 参数:");
        System.out.println("-".repeat(60));

        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        System.out.println("输入参数:");
        for (String arg : runtime.getInputArguments()) {
            System.out.println("  " + arg);
        }

        System.out.println("\n系统属性:");
        System.out.println("  java.home: " + System.getProperty("java.home"));
        System.out.println("  java.version: " + System.getProperty("java.version"));
        System.out.println("  java.vm.name: " + System.getProperty("java.vm.name"));
        System.out.println("  user.dir: " + System.getProperty("user.dir"));
    }

    // ==================== 工具方法 ====================

    private static void printMemoryUsage(String prefix, MemoryUsage usage) {
        System.out.println(prefix + "初始值:  " + formatSize(usage.getInit()));
        System.out.println(prefix + "已使用:  " + formatSize(usage.getUsed()));
        System.out.println(prefix + "已分配: " + formatSize(usage.getCommitted()));
        System.out.println(prefix + "最大值:  " + formatSize(usage.getMax()));
    }

    private static String formatSize(long bytes) {
        if (bytes < 0)
            return "N/A";
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < MB)
            return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * MB)
            return String.format("%.1f MB", bytes / (double) MB);
        return String.format("%.1f GB", bytes / (1024.0 * MB));
    }

    private static String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0)
            return String.format("%dd %dh %dm", days, hours % 24, minutes % 60);
        if (hours > 0)
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        if (minutes > 0)
            return String.format("%dm %ds", minutes, seconds % 60);
        return String.format("%ds", seconds);
    }

    private static String truncate(String s, int maxLen) {
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 3) + "...";
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

    private static void provideDiagnosticAdvice(MemoryUsage heap, double gcRatio, int threadCount) {
        List<String> advice = new ArrayList<>();

        double heapUsage = (double) heap.getUsed() / heap.getMax() * 100;

        if (heapUsage > 80) {
            advice.add("• 堆内存使用率高 (>80%)，考虑增大 -Xmx 或优化内存使用");
        }

        if (gcRatio > 5) {
            advice.add("• GC 时间占比高 (>5%)，可能存在频繁 GC，检查对象分配");
        }

        if (threadCount > 500) {
            advice.add("• 线程数较多 (>500)，考虑使用线程池控制");
        }

        if (heap.getMax() - heap.getUsed() < 100 * MB) {
            advice.add("• 剩余堆内存不足 100MB，可能很快 OOM");
        }

        if (advice.isEmpty()) {
            System.out.println("✅ 系统运行状态良好，未发现明显问题");
        } else {
            System.out.println("⚠️  发现以下潜在问题:");
            for (String a : advice) {
                System.out.println(a);
            }
        }
    }
}

/*
 * 📚 项目总结:
 * 
 * 本项目综合运用了 Phase 6 的核心知识:
 * - JVM 内存模型理解
 * - MXBean 监控 API
 * - GC 相关知识
 * - 内存问题分析
 * 
 * 🎯 扩展任务:
 * 1. 添加内存趋势图 (ASCII)
 * 2. 导出报告为 HTML 格式
 * 3. 添加阈值告警功能
 * 4. 集成到 Web 界面
 */
