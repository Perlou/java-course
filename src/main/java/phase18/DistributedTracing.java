package phase18;

/**
 * Phase 18: 分布式链路追踪
 * 
 * 本课程涵盖：
 * 1. 链路追踪基本概念
 * 2. OpenTelemetry 标准
 * 3. SkyWalking / Jaeger 实践
 * 4. 上下文传播机制
 */
public class DistributedTracing {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          🔗 Phase 18: 分布式链路追踪                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        tracingConcepts();
        openTelemetryStandard();
        contextPropagation();
        toolComparison();
        implementation();
    }

    // ==================== 1. 链路追踪概念 ====================

    private static void tracingConcepts() {
        System.out.println("\n📌 1. 链路追踪基本概念\n");

        System.out.println("分布式环境下，一个请求可能跨越多个服务：");
        System.out.println();
        System.out.println("┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│                     请求链路示例                               │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                                │");
        System.out.println("│  用户 ──→ 网关 ──→ 订单服务 ──→ 库存服务                      │");
        System.out.println("│           │          │            │                            │");
        System.out.println("│           │          ↓            ↓                            │");
        System.out.println("│           │       支付服务     数据库                          │");
        System.out.println("│                                                                │");
        System.out.println("│  问题: 请求慢了？在哪个服务？哪个环节？                        │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("核心概念：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  概念          │  说明                                       │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│  Trace         │  完整的请求链路，由多个 Span 组成           │");
        System.out.println("│  Span          │  一个操作单元（如 HTTP 请求、DB 查询）      │");
        System.out.println("│  Trace ID      │  全局唯一标识，贯穿整个链路                 │");
        System.out.println("│  Span ID       │  当前操作的唯一标识                         │");
        System.out.println("│  Parent Span   │  父操作，用于构建调用树                     │");
        System.out.println("│  Baggage       │  跨服务传递的上下文数据                     │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("链路追踪可视化：");
        System.out.println();
        System.out.println("Trace: abc123");
        System.out.println("├── Span: 网关 [0ms - 150ms]");
        System.out.println("│   └── Span: 订单服务 [10ms - 140ms]");
        System.out.println("│       ├── Span: 支付服务 [20ms - 80ms]");
        System.out.println("│       └── Span: 库存服务 [85ms - 130ms]");
        System.out.println("│           └── Span: MySQL 查询 [90ms - 120ms]");
    }

    // ==================== 2. OpenTelemetry 标准 ====================

    private static void openTelemetryStandard() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. OpenTelemetry 标准\n");

        System.out.println("OpenTelemetry (OTel) 是 CNCF 的可观测性标准：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                   OpenTelemetry 架构                         │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   ┌────────────┐    ┌────────────┐    ┌──────────────┐     │");
        System.out.println("│   │   应用     │ →  │ OTel SDK   │ →  │  Collector   │      │");
        System.out.println("│   │   代码     │    │  (Agent)   │    │  (收集器)    │      │");
        System.out.println("│   └────────────┘    └────────────┘    └──────────────┘     │");
        System.out.println("│                                              │              │");
        System.out.println("│                          ┌───────────────────┼───────┐     │");
        System.out.println("│                          ↓                   ↓       ↓     │");
        System.out.println("│                    ┌─────────┐  ┌─────────┐  ┌─────────┐  │");
        System.out.println("│                    │ Jaeger  │  │Zipkin   │  │SkyWalking│  │");
        System.out.println("│                    └─────────┘  └─────────┘  └─────────┘  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("三大支柱：");
        System.out.println("  📊 Traces  - 请求链路追踪");
        System.out.println("  📈 Metrics - 指标监控");
        System.out.println("  📝 Logs    - 日志收集");
        System.out.println();

        System.out.println("W3C Trace Context 格式（HTTP Header）：");
        System.out.println("```");
        System.out.println("traceparent: 00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01");
        System.out.println("             │  │                                │                │");
        System.out.println("             │  │                                │                └─ flags");
        System.out.println("             │  │                                └─ parent-id (span-id)");
        System.out.println("             │  └─ trace-id");
        System.out.println("             └─ version");
        System.out.println("```");
    }

    // ==================== 3. 上下文传播 ====================

    private static void contextPropagation() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. 上下文传播机制\n");

        System.out.println("跨服务传播 Trace 上下文：");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ HTTP 传播                                                    │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ // 发送端：注入上下文到 Header                               │");
        System.out.println("│ HttpRequest request = ...;                                   │");
        System.out.println("│ request.setHeader(\"X-Trace-Id\", traceId);                   │");
        System.out.println("│ request.setHeader(\"X-Span-Id\", spanId);                     │");
        System.out.println("│                                                              │");
        System.out.println("│ // 接收端：从 Header 提取上下文                              │");
        System.out.println("│ String traceId = request.getHeader(\"X-Trace-Id\");           │");
        System.out.println("│ String parentSpanId = request.getHeader(\"X-Span-Id\");       │");
        System.out.println("│ Span span = tracer.spanBuilder(\"handle-request\")            │");
        System.out.println("│     .setParent(Context.current().with(parentSpan))           │");
        System.out.println("│     .startSpan();                                            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 消息队列传播                                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ // 生产者：注入上下文到消息属性                              │");
        System.out.println("│ message.setHeader(\"traceparent\", span.toW3CTraceContext()); │");
        System.out.println("│                                                              │");
        System.out.println("│ // 消费者：从消息属性提取上下文                              │");
        System.out.println("│ String traceParent = message.getHeader(\"traceparent\");      │");
        System.out.println("│ Context context = W3CTraceContextPropagator.extract(headers);│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("代码示例 - Spring 拦截器：");
        System.out.println("```java");
        System.out.println("@Component");
        System.out.println("public class TraceInterceptor implements HandlerInterceptor {");
        System.out.println("    @Override");
        System.out.println("    public boolean preHandle(HttpServletRequest request, ...) {");
        System.out.println("        String traceId = request.getHeader(\"X-Trace-Id\");");
        System.out.println("        if (traceId == null) {");
        System.out.println("            traceId = UUID.randomUUID().toString();");
        System.out.println("        }");
        System.out.println("        MDC.put(\"traceId\", traceId);  // 日志关联");
        System.out.println("        return true;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
    }

    // ==================== 4. 工具对比 ====================

    private static void toolComparison() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. 追踪工具对比\n");

        System.out.println("┌────────────┬──────────────┬──────────────┬──────────────┐");
        System.out.println("│   特性     │   Jaeger     │   Zipkin     │  SkyWalking  │");
        System.out.println("├────────────┼──────────────┼──────────────┼──────────────┤");
        System.out.println("│ 开发语言   │ Go           │ Java         │ Java         │");
        System.out.println("│ 部署复杂度 │ 中等         │ 简单         │ 中等         │");
        System.out.println("│ 存储后端   │ ES/Cassandra │ ES/MySQL     │ ES/H2/MySQL  │");
        System.out.println("│ Agent 方式 │ SDK 集成     │ SDK 集成     │ Java Agent   │");
        System.out.println("│ 无侵入性   │ ✗            │ ✗            │ ✓ (字节码)   │");
        System.out.println("│ 服务拓扑   │ ✓            │ ✓            │ ✓ (更强)     │");
        System.out.println("│ 性能分析   │ 基础         │ 基础         │ 强           │");
        System.out.println("│ 适用场景   │ 云原生       │ Spring 生态  │ Java 企业应用│");
        System.out.println("└────────────┴──────────────┴──────────────┴──────────────┘");
        System.out.println();

        System.out.println("💡 推荐选择：");
        System.out.println("   • Java 应用 → SkyWalking（无侵入，功能全面）");
        System.out.println("   • 云原生/K8s → Jaeger（CNCF 项目，生态好）");
        System.out.println("   • 快速上手 → Zipkin（简单易用）");
    }

    // ==================== 5. 实现示例 ====================

    private static void implementation() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 实现示例\n");

        System.out.println("SkyWalking Java Agent 配置：");
        System.out.println();
        System.out.println("```bash");
        System.out.println("# 启动应用时添加 Java Agent");
        System.out.println("java -javaagent:/path/to/skywalking-agent.jar \\");
        System.out.println("     -Dskywalking.agent.service_name=order-service \\");
        System.out.println("     -Dskywalking.collector.backend_service=localhost:11800 \\");
        System.out.println("     -jar app.jar");
        System.out.println("```");
        System.out.println();

        System.out.println("自定义 Span（业务埋点）：");
        System.out.println("```java");
        System.out.println("@Trace  // SkyWalking 注解");
        System.out.println("public Order createOrder(Long userId, Long productId) {");
        System.out.println("    // 添加自定义标签");
        System.out.println("    ActiveSpan.tag(\"user.id\", String.valueOf(userId));");
        System.out.println("    ActiveSpan.tag(\"product.id\", String.valueOf(productId));");
        System.out.println("    ");
        System.out.println("    // 业务逻辑...");
        System.out.println("    ");
        System.out.println("    return order;");
        System.out.println("}");
        System.out.println("```");
        System.out.println();

        System.out.println("💡 最佳实践：");
        System.out.println("   1. 采样率设置 - 生产环境建议 10%~50%");
        System.out.println("   2. 关键业务 100% 采样");
        System.out.println("   3. 合理设置 Span 粒度");
        System.out.println("   4. 异常 Span 自动标记");
    }
}
