package phase06.ex;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.List;

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
    }

}
