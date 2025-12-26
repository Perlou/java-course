package phase13;

/**
 * Phase 13: GC 日志分析
 * 
 * 本课程涵盖：
 * 1. GC 日志配置
 * 2. GC 日志格式解读
 * 3. 常见 GC 问题分析
 * 4. GC 日志分析工具
 */
public class GCAnalysis {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 13: GC 日志分析");
        System.out.println("=".repeat(60));

        gcLogConfiguration();
        gcLogFormat();
        gcLogAnalysis();
        gcAnalysisTools();
        triggerGCDemo();
    }

    private static void gcLogConfiguration() {
        System.out.println("\n【1. GC 日志配置】");
        System.out.println("-".repeat(50));

        String config = """

                ═══════════════════════════════════════════════════════════
                                JDK 9+ GC 日志配置 (推荐)
                ═══════════════════════════════════════════════════════════

                # 基础配置
                -Xlog:gc*:file=gc.log

                # 完整配置（带时间戳和轮转）
                -Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=100m

                参数说明：
                • gc*          - 记录所有 GC 相关日志
                • file=gc.log  - 输出到文件
                • time         - 添加时间戳
                • uptime       - 添加运行时间
                • filecount=5  - 保留 5 个日志文件
                • filesize=100m - 每个文件最大 100MB


                ═══════════════════════════════════════════════════════════
                                JDK 8 GC 日志配置
                ═══════════════════════════════════════════════════════════

                -XX:+PrintGCDetails          # 打印详细信息
                -XX:+PrintGCDateStamps       # 打印日期时间
                -XX:+PrintGCTimeStamps       # 打印相对时间
                -XX:+PrintGCApplicationStoppedTime  # STW 时间
                -Xloggc:gc.log               # 输出到文件
                -XX:+UseGCLogFileRotation    # 日志轮转
                -XX:NumberOfGCLogFiles=5     # 保留文件数
                -XX:GCLogFileSize=100m       # 单文件大小
                """;
        System.out.println(config);
    }

    private static void gcLogFormat() {
        System.out.println("\n【2. GC 日志格式解读】");
        System.out.println("-".repeat(50));

        String format = """

                ═══════════════════════════════════════════════════════════
                                  G1 GC 日志示例
                ═══════════════════════════════════════════════════════════

                [2024-01-15T10:30:45.123+0800][info][gc] GC(12) Pause Young (Normal)
                                                        (G1 Evacuation Pause)
                [2024-01-15T10:30:45.125+0800][info][gc] GC(12)
                                            -> 256M(4096M) 2.345ms

                解读：
                ─────────────────────────────────────────────────────────
                GC(12)       - 第 12 次 GC
                Pause Young  - Young GC（年轻代）
                (Normal)     - 正常触发（非强制）
                256M         - GC 后堆使用量
                (4096M)      - 堆总容量
                2.345ms      - GC 耗时 ⭐ 关键指标


                ═══════════════════════════════════════════════════════════
                                  Full GC 日志示例
                ═══════════════════════════════════════════════════════════

                [info][gc] GC(45) Pause Full (Metadata GC Threshold)
                [info][gc] GC(45) 2048M->512M(4096M) 1234.567ms

                ⚠️ 警告信号：
                • "Pause Full" - 发生了 Full GC
                • 1234ms      - 停顿时间过长！
                • 原因 "Metadata GC Threshold" - 元空间触发


                ═══════════════════════════════════════════════════════════
                                  需要关注的 GC 原因
                ═══════════════════════════════════════════════════════════

                ✅ 正常原因：
                • G1 Evacuation Pause     - 正常 Young GC
                • G1 Concurrent Cycle     - 并发标记周期

                ⚠️ 需要关注：
                • Allocation Failure      - 分配失败触发
                • System.gc()             - 代码显式调用
                • Metadata GC Threshold   - 元空间不足
                • Ergonomics              - 自适应调整

                ❌ 严重问题：
                • To-space exhausted      - Survivor 空间不足
                • Evacuation Failure      - 晋升失败
                """;
        System.out.println(format);
    }

    private static void gcLogAnalysis() {
        System.out.println("\n【3. GC 问题分析】");
        System.out.println("-".repeat(50));

        String analysis = """

                ═══════════════════════════════════════════════════════════
                             问题1: 频繁 Young GC
                ═══════════════════════════════════════════════════════════

                现象：每秒多次 Young GC

                可能原因：
                • 年轻代设置过小
                • 对象创建速率过高
                • 存活对象过多

                解决方案：
                • 增大年轻代: -Xmn 或 -XX:G1NewSizePercent
                • 检查代码是否有不必要的对象创建
                • 使用对象池复用对象


                ═══════════════════════════════════════════════════════════
                             问题2: Full GC 频繁
                ═══════════════════════════════════════════════════════════

                现象：Full GC 次数持续增加

                可能原因：
                • 老年代空间不足
                • 内存泄漏
                • 大对象直接进入老年代

                排查命令：
                jstat -gcutil <pid> 1000   # 观察 O(老年代) 占比
                jmap -histo <pid>          # 查看对象分布


                ═══════════════════════════════════════════════════════════
                             问题3: GC 停顿过长
                ═══════════════════════════════════════════════════════════

                现象：单次 GC 停顿超过 200ms

                解决方案：
                • 使用 G1: -XX:MaxGCPauseMillis=200
                • 使用 ZGC: -XX:+UseZGC (JDK 15+)
                • 减小堆大小（减少标记/清理范围）
                """;
        System.out.println(analysis);
    }

    private static void gcAnalysisTools() {
        System.out.println("\n【4. GC 分析工具】");
        System.out.println("-".repeat(50));

        String tools = """

                ═══════════════════════════════════════════════════════════
                                在线分析工具（推荐）
                ═══════════════════════════════════════════════════════════

                1. GCEasy (https://gceasy.io)
                   • 上传 GC 日志自动分析
                   • 生成可视化报告
                   • 提供优化建议

                2. GCViewer
                   • 开源桌面工具
                   • 图形化展示 GC 指标
                   • 支持多种 GC 日志格式

                3. JClarity Censum
                   • 专业 GC 分析工具
                   • 自动检测问题


                ═══════════════════════════════════════════════════════════
                                命令行快速分析
                ═══════════════════════════════════════════════════════════

                # 统计 Full GC 次数
                grep "Pause Full" gc.log | wc -l

                # 查看 GC 停顿时间分布
                grep -oE "[0-9]+\\.[0-9]+ms" gc.log | sort -n | tail -20

                # 查看 GC 原因分布
                grep -oE "\\(.*\\)" gc.log | sort | uniq -c | sort -rn


                ═══════════════════════════════════════════════════════════
                                关键指标检查清单
                ═══════════════════════════════════════════════════════════

                □ Young GC 频率: < 10次/秒
                □ Young GC 耗时: < 50ms
                □ Full GC 频率: < 1次/小时
                □ Full GC 耗时: < 1秒
                □ GC 总时间占比: < 5%
                """;
        System.out.println(tools);
    }

    private static void triggerGCDemo() {
        System.out.println("\n【5. GC 触发演示】");
        System.out.println("-".repeat(50));

        System.out.println("正在分配内存触发 GC...\n");

        // 记录初始状态
        Runtime runtime = Runtime.getRuntime();
        long beforeUsed = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("GC 前已使用内存: %.2f MB%n", beforeUsed / 1024.0 / 1024.0);

        // 分配一些内存
        byte[][] arrays = new byte[100][];
        for (int i = 0; i < 100; i++) {
            arrays[i] = new byte[1024 * 1024]; // 1MB per array
        }

        long afterAlloc = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("分配后已使用内存: %.2f MB%n", afterAlloc / 1024.0 / 1024.0);

        // 清除引用并触发 GC
        arrays = null;
        System.gc(); // 建议 JVM 进行 GC

        // 短暂等待 GC 完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long afterGC = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("GC 后已使用内存: %.2f MB%n", afterGC / 1024.0 / 1024.0);
        System.out.printf("释放内存: %.2f MB%n", (afterAlloc - afterGC) / 1024.0 / 1024.0);

        System.out.println("\n💡 提示: 使用以下命令运行可看到 GC 日志:");
        System.out.println("java -Xlog:gc* -Xms256m -Xmx256m phase13.GCAnalysis");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ GC 日志分析课程完成！下一课: MemoryAnalysis.java");
        System.out.println("=".repeat(60));
    }
}
