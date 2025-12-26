package phase13;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Phase 13: 连接池调优
 * 
 * 本课程涵盖：
 * 1. 连接池原理
 * 2. HikariCP 配置
 * 3. 线程池配置
 * 4. 最佳实践
 */
public class ConnectionPoolTuning {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 13: 连接池调优");
        System.out.println("=".repeat(60));

        connectionPoolBasics();
        hikariConfiguration();
        threadPoolConfiguration();
        monitoringDemo();
    }

    private static void connectionPoolBasics() {
        System.out.println("\n【1. 连接池原理】");
        System.out.println("-".repeat(50));

        String basics = """

                ═══════════════════════════════════════════════════════════
                                  为什么需要连接池
                ═══════════════════════════════════════════════════════════

                没有连接池：
                ┌─────────┐    创建连接(~50ms)    ┌─────────┐
                │  应用   │ ──────────────────→   │ 数据库  │
                │         │    执行SQL(~5ms)      │         │
                │         │ ──────────────────→   │         │
                │         │    关闭连接(~5ms)     │         │
                └─────────┘ ──────────────────→   └─────────┘

                ⚠️ 问题：每次请求都要创建/销毁连接，开销巨大

                使用连接池：
                ┌─────────┐    获取连接(~1ms)     ┌─────────┐
                │  应用   │ ──────────────────→   │ 连接池  │ ←→ 数据库
                │         │    归还连接            │  (复用) │
                └─────────┘ ──────────────────→   └─────────┘

                ✅ 连接被复用，避免重复创建开销


                ═══════════════════════════════════════════════════════════
                                  连接池核心参数
                ═══════════════════════════════════════════════════════════

                │ 参数              │ 说明                          │
                ├───────────────────┼───────────────────────────────┤
                │ maximumPoolSize   │ 最大连接数                    │
                │ minimumIdle       │ 最小空闲连接数                │
                │ connectionTimeout │ 获取连接的等待超时            │
                │ idleTimeout       │ 空闲连接存活时间              │
                │ maxLifetime       │ 连接最大生命周期              │


                ═══════════════════════════════════════════════════════════
                                  连接池大小公式
                ═══════════════════════════════════════════════════════════

                最佳连接数 ≈ (CPU核心数 × 2) + 有效磁盘数

                示例：4 核 CPU + 1 个 SSD
                最佳连接数 = (4 × 2) + 1 = 9 ~ 10

                ⚠️ 更多连接不一定更好！
                • 连接过多 → 上下文切换增加，性能下降
                • 连接过少 → 请求排队等待
                • 需要根据压测调整
                """;
        System.out.println(basics);
    }

    private static void hikariConfiguration() {
        System.out.println("\n【2. HikariCP 配置】");
        System.out.println("-".repeat(50));

        String hikari = """

                ═══════════════════════════════════════════════════════════
                             HikariCP 推荐配置 (Spring Boot)
                ═══════════════════════════════════════════════════════════

                spring:
                  datasource:
                    url: jdbc:mysql://localhost:3306/mydb
                    username: root
                    password: password
                    driver-class-name: com.mysql.cj.jdbc.Driver

                    hikari:
                      # 连接池大小
                      maximum-pool-size: 10      # 生产一般 10-20
                      minimum-idle: 10           # 建议与 max 相同

                      # 超时配置
                      connection-timeout: 30000  # 获取连接超时 30s
                      idle-timeout: 600000       # 空闲超时 10min
                      max-lifetime: 1800000      # 最大生命周期 30min

                      # 连接验证
                      connection-test-query: SELECT 1
                      validation-timeout: 5000

                      # 其他
                      pool-name: MyHikariPool
                      auto-commit: true


                ═══════════════════════════════════════════════════════════
                             MySQL 驱动推荐配置
                ═══════════════════════════════════════════════════════════

                jdbc:mysql://host:3306/db?
                  useSSL=false
                  &serverTimezone=Asia/Shanghai
                  &characterEncoding=UTF-8
                  &useUnicode=true
                  &rewriteBatchedStatements=true   # 批量插入优化
                  &cachePrepStmts=true             # 缓存预编译语句
                  &prepStmtCacheSize=250
                  &prepStmtCacheSqlLimit=2048


                ═══════════════════════════════════════════════════════════
                             常见问题
                ═══════════════════════════════════════════════════════════

                问题1: Connection is not available
                ─────────────────────────────────────────────────────────
                原因: 连接池耗尽，获取连接超时
                解决:
                • 增大 maximum-pool-size
                • 检查是否有连接泄漏（未释放）
                • 增大 connection-timeout

                问题2: Connection marked as broken
                ─────────────────────────────────────────────────────────
                原因: 连接被数据库断开（空闲过久）
                解决:
                • 设置 max-lifetime < 数据库 wait_timeout
                • 添加 connection-test-query
                """;
        System.out.println(hikari);
    }

    private static void threadPoolConfiguration() {
        System.out.println("\n【3. 线程池配置】");
        System.out.println("-".repeat(50));

        String threadPool = """

                ═══════════════════════════════════════════════════════════
                             核心参数
                ═══════════════════════════════════════════════════════════

                ThreadPoolExecutor(
                    corePoolSize,      // 核心线程数
                    maximumPoolSize,   // 最大线程数
                    keepAliveTime,     // 空闲线程存活时间
                    unit,              // 时间单位
                    workQueue,         // 工作队列
                    threadFactory,     // 线程工厂
                    handler            // 拒绝策略
                );


                ═══════════════════════════════════════════════════════════
                             线程池大小公式
                ═══════════════════════════════════════════════════════════

                CPU 密集型任务：
                线程数 = CPU 核心数 + 1

                IO 密集型任务：
                线程数 = CPU 核心数 × 2
                或
                线程数 = CPU 核心数 × (1 + 等待时间/计算时间)

                示例：8 核 CPU，IO 操作占 80% 时间
                线程数 = 8 × (1 + 80/20) = 8 × 5 = 40


                ═══════════════════════════════════════════════════════════
                             最佳实践配置
                ═══════════════════════════════════════════════════════════

                // IO 密集型线程池
                int cpuCores = Runtime.getRuntime().availableProcessors();

                ThreadPoolExecutor ioPool = new ThreadPoolExecutor(
                    cpuCores * 2,                    // 核心线程
                    cpuCores * 4,                    // 最大线程
                    60L, TimeUnit.SECONDS,           // 空闲存活
                    new LinkedBlockingQueue<>(1000), // 有界队列
                    new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
                );

                // CPU 密集型线程池
                ThreadPoolExecutor cpuPool = new ThreadPoolExecutor(
                    cpuCores + 1,
                    cpuCores + 1,  // 核心=最大
                    0L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(100),
                    new ThreadPoolExecutor.AbortPolicy()
                );


                ═══════════════════════════════════════════════════════════
                             拒绝策略
                ═══════════════════════════════════════════════════════════

                AbortPolicy        抛出异常（默认）
                CallerRunsPolicy   调用者线程执行（推荐）
                DiscardPolicy      静默丢弃
                DiscardOldestPolicy 丢弃最旧任务
                """;
        System.out.println(threadPool);
    }

    private static void monitoringDemo() {
        System.out.println("\n【4. 线程池监控演示】");
        System.out.println("-".repeat(50));

        // 创建线程池
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                cpuCores,
                cpuCores * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());

        System.out.println("线程池配置:");
        System.out.printf("  核心线程数: %d%n", executor.getCorePoolSize());
        System.out.printf("  最大线程数: %d%n", executor.getMaximumPoolSize());
        System.out.printf("  队列容量: %d%n", 100);

        // 提交任务
        System.out.println("\n提交 20 个任务...");
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 监控状态
        System.out.println("\n线程池状态:");
        System.out.printf("  活跃线程数: %d%n", executor.getActiveCount());
        System.out.printf("  当前线程数: %d%n", executor.getPoolSize());
        System.out.printf("  队列中任务: %d%n", executor.getQueue().size());
        System.out.printf("  已完成任务: %d%n", executor.getCompletedTaskCount());

        // 关闭线程池
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n最终统计:");
        System.out.printf("  总任务数: %d%n", executor.getTaskCount());
        System.out.printf("  已完成: %d%n", executor.getCompletedTaskCount());

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 连接池调优课程完成！");
        System.out.println("Phase 13 全部完成！");
        System.out.println("=".repeat(60));
    }
}
