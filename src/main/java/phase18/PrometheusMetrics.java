package phase18;

/**
 * Phase 18: Prometheus 指标监控
 * 
 * 本课程涵盖：
 * 1. Prometheus 数据模型
 * 2. 四种核心指标类型
 * 3. Micrometer 集成
 * 4. PromQL 查询语言
 */
public class PrometheusMetrics {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          📊 Phase 18: Prometheus 指标监控                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        prometheusOverview();
        metricTypes();
        micrometerIntegration();
        promqlBasics();
        bestPractices();
    }

    // ==================== 1. Prometheus 概述 ====================

    private static void prometheusOverview() {
        System.out.println("\n📌 1. Prometheus 概述\n");

        System.out.println("Prometheus 是一个开源的监控和告警系统：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                     Prometheus 架构                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   ┌──────────┐      ┌──────────────┐      ┌──────────┐     │");
        System.out.println("│   │ 应用服务 │ ──→ │  Prometheus  │ ──→ │  Grafana │      │");
        System.out.println("│   │ /metrics │      │   (拉取)     │      │  (可视化) │     │");
        System.out.println("│   └──────────┘      └──────────────┘      └──────────┘     │");
        System.out.println("│                            │                                │");
        System.out.println("│                            ↓                                │");
        System.out.println("│                    ┌──────────────┐                        │");
        System.out.println("│                    │  Alertmanager │                        │");
        System.out.println("│                    │   (告警通知)  │                        │");
        System.out.println("│                    └──────────────┘                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("核心特点:");
        System.out.println("  • Pull 模式 - Prometheus 主动拉取指标");
        System.out.println("  • 多维数据模型 - 指标名 + 标签");
        System.out.println("  • PromQL - 强大的查询语言");
        System.out.println("  • 时序数据库 - 高效存储时间序列");
    }

    // ==================== 2. 四种核心指标类型 ====================

    private static void metricTypes() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. 四种核心指标类型\n");

        // Counter
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 1. Counter (计数器)                                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 特点: 只增不减，重启归零                                     │");
        System.out.println("│ 场景: 请求数、错误数、处理任务数                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   http_requests_total{method=\"GET\", path=\"/api\"} → 12345   │");
        System.out.println("│                                                              │");
        System.out.println("│ Java 代码:                                                   │");
        System.out.println("│   Counter counter = Counter.builder(\"http_requests_total\")  │");
        System.out.println("│       .tag(\"method\", \"GET\")                                 │");
        System.out.println("│       .register(registry);                                   │");
        System.out.println("│   counter.increment();                                       │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        // Gauge
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 2. Gauge (仪表盘)                                            │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 特点: 可增可减，表示瞬时值                                   │");
        System.out.println("│ 场景: 当前连接数、队列大小、温度                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   active_connections{service=\"api\"} → 42                    │");
        System.out.println("│                                                              │");
        System.out.println("│ Java 代码:                                                   │");
        System.out.println("│   Gauge.builder(\"active_connections\", queue, Queue::size)   │");
        System.out.println("│       .register(registry);                                   │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        // Histogram
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 3. Histogram (直方图)                                        │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 特点: 记录分布，预定义桶 (bucket)                            │");
        System.out.println("│ 场景: 响应时间分布、请求大小分布                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例 (自动生成):                                             │");
        System.out.println("│   http_latency_bucket{le=\"0.1\"} → 1000                      │");
        System.out.println("│   http_latency_bucket{le=\"0.5\"} → 1500                      │");
        System.out.println("│   http_latency_bucket{le=\"1.0\"} → 1800                      │");
        System.out.println("│   http_latency_sum → 450.5                                   │");
        System.out.println("│   http_latency_count → 2000                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        // Summary
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 4. Summary (摘要)                                            │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 特点: 客户端计算分位数                                       │");
        System.out.println("│ 场景: 精确的分位数统计                                       │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   http_latency{quantile=\"0.5\"} → 0.025                      │");
        System.out.println("│   http_latency{quantile=\"0.9\"} → 0.085                      │");
        System.out.println("│   http_latency{quantile=\"0.99\"} → 0.250                     │");
        System.out.println("│                                                              │");
        System.out.println("│ ⚠️  注意: Summary 不支持聚合，Histogram 更推荐              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ==================== 3. Micrometer 集成 ====================

    private static void micrometerIntegration() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. Micrometer 集成\n");

        System.out.println("Micrometer 是 Java 应用的指标门面（类似 SLF4J 之于日志）");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ Spring Boot 集成                                             │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│ 1. 添加依赖:                                                 │");
        System.out.println("│    <dependency>                                              │");
        System.out.println("│      <groupId>io.micrometer</groupId>                        │");
        System.out.println("│      <artifactId>micrometer-registry-prometheus</artifactId> │");
        System.out.println("│    </dependency>                                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 配置 application.yml:                                     │");
        System.out.println("│    management:                                               │");
        System.out.println("│      endpoints:                                              │");
        System.out.println("│        web:                                                  │");
        System.out.println("│          exposure:                                           │");
        System.out.println("│            include: prometheus,health,info                   │");
        System.out.println("│      metrics:                                                │");
        System.out.println("│        tags:                                                 │");
        System.out.println("│          application: my-app                                 │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 访问 /actuator/prometheus 获取指标                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("代码示例:");
        System.out.println();
        System.out.println("```java");
        System.out.println("@Service");
        System.out.println("public class MetricService {");
        System.out.println("    private final MeterRegistry registry;");
        System.out.println("    private final Counter requestCounter;");
        System.out.println("    private final Timer requestTimer;");
        System.out.println();
        System.out.println("    public MetricService(MeterRegistry registry) {");
        System.out.println("        this.registry = registry;");
        System.out.println("        this.requestCounter = Counter.builder(\"api.requests\")");
        System.out.println("            .description(\"API 请求计数\")");
        System.out.println("            .register(registry);");
        System.out.println("        this.requestTimer = Timer.builder(\"api.latency\")");
        System.out.println("            .description(\"API 响应时间\")");
        System.out.println("            .register(registry);");
        System.out.println("    }");
        System.out.println();
        System.out.println("    public void recordRequest(String api, long durationMs) {");
        System.out.println("        requestCounter.increment();");
        System.out.println("        requestTimer.record(durationMs, TimeUnit.MILLISECONDS);");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
    }

    // ==================== 4. PromQL 基础 ====================

    private static void promqlBasics() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. PromQL 查询语言\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 基础查询                                                     │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ # 查询指标                                                   │");
        System.out.println("│ http_requests_total                                          │");
        System.out.println("│                                                              │");
        System.out.println("│ # 标签过滤                                                   │");
        System.out.println("│ http_requests_total{method=\"GET\", status=\"200\"}            │");
        System.out.println("│                                                              │");
        System.out.println("│ # 正则匹配                                                   │");
        System.out.println("│ http_requests_total{path=~\"/api/.*\"}                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 常用函数                                                     │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ # QPS (每秒请求数)                                           │");
        System.out.println("│ rate(http_requests_total[5m])                                │");
        System.out.println("│                                                              │");
        System.out.println("│ # 增长量                                                     │");
        System.out.println("│ increase(http_requests_total[1h])                            │");
        System.out.println("│                                                              │");
        System.out.println("│ # P99 延迟                                                   │");
        System.out.println("│ histogram_quantile(0.99, rate(http_latency_bucket[5m]))      │");
        System.out.println("│                                                              │");
        System.out.println("│ # 平均值                                                     │");
        System.out.println("│ avg(rate(http_latency_sum[5m]) / rate(http_latency_count[5m]))│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 告警规则示例                                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ groups:                                                      │");
        System.out.println("│   - name: api_alerts                                         │");
        System.out.println("│     rules:                                                   │");
        System.out.println("│       - alert: HighErrorRate                                 │");
        System.out.println("│         expr: |                                              │");
        System.out.println("│           rate(http_requests_total{status=~\"5..\"}[5m])      │");
        System.out.println("│           / rate(http_requests_total[5m]) > 0.05             │");
        System.out.println("│         for: 5m                                              │");
        System.out.println("│         labels:                                              │");
        System.out.println("│           severity: critical                                 │");
        System.out.println("│         annotations:                                         │");
        System.out.println("│           summary: \"错误率超过 5%\"                           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ==================== 5. 最佳实践 ====================

    private static void bestPractices() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 最佳实践\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ✅ 指标命名规范                                              │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 使用小写字母和下划线                                      │");
        System.out.println("│    ✓ http_requests_total                                     │");
        System.out.println("│    ✗ HttpRequestsTotal                                       │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. Counter 以 _total 结尾                                    │");
        System.out.println("│    ✓ http_requests_total                                     │");
        System.out.println("│    ✗ http_requests                                           │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 使用有意义的标签                                          │");
        System.out.println("│    ✓ {method=\"GET\", path=\"/api/users\", status=\"200\"}      │");
        System.out.println("│    ✗ {m=\"G\", p=\"/a/u\", s=\"2\"}                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 避免高基数标签                                            │");
        System.out.println("│    ✗ {user_id=\"12345\"}  // 用户ID导致指标爆炸              │");
        System.out.println("│    ✓ {user_type=\"premium\"}                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("💡 RED 方法 - 服务监控黄金指标:");
        System.out.println("   • Rate    - 请求速率 (QPS)");
        System.out.println("   • Errors  - 错误率");
        System.out.println("   • Duration - 响应时间");
        System.out.println();

        System.out.println("💡 USE 方法 - 资源监控:");
        System.out.println("   • Utilization - 使用率");
        System.out.println("   • Saturation  - 饱和度");
        System.out.println("   • Errors      - 错误数");
    }
}
