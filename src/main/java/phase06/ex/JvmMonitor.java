package phase06.ex;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Scanner;

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
                // case "2" -> monitorMemory();
                // case "3" -> monitorGC();
                // case "4" -> monitorThreads();
                // case "5" -> simulateMemoryLeak();
                // case "6" -> testGC();
                // case "7" -> showJvmArgs();
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
        // System.out.println("启动时间: " + formatUptime(runtime.getUptime()));
        System.out.println("进程 PID:    " + runtime.getPid());

    }

}
