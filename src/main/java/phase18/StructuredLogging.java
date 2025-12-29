package phase18;

/**
 * Phase 18: 结构化日志
 * 
 * 本课程涵盖：
 * 1. 结构化日志概念
 * 2. JSON 日志格式
 * 3. ELK Stack 集成
 * 4. 日志与链路追踪关联
 */
public class StructuredLogging {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          📝 Phase 18: 结构化日志                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        structuredLoggingConcept();
        jsonLogFormat();
        elkStackIntegration();
        loggingBestPractices();
    }

    // ==================== 1. 结构化日志概念 ====================

    private static void structuredLoggingConcept() {
        System.out.println("\n📌 1. 结构化日志概念\n");

        System.out.println("传统日志 vs 结构化日志：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 传统日志 (文本格式)                                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 2024-01-15 10:30:45 INFO  OrderService - User 123 created   │");
        System.out.println("│ order 456 for product 789, total: $99.99                    │");
        System.out.println("│                                                              │");
        System.out.println("│ ⚠️  问题:                                                    │");
        System.out.println("│   • 难以解析和检索                                           │");
        System.out.println("│   • 格式不统一                                               │");
        System.out.println("│   • 难以做数据分析                                           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 结构化日志 (JSON 格式)                                       │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ {                                                            │");
        System.out.println("│   \"@timestamp\": \"2024-01-15T10:30:45.123Z\",                │");
        System.out.println("│   \"level\": \"INFO\",                                         │");
        System.out.println("│   \"service\": \"order-service\",                              │");
        System.out.println("│   \"trace_id\": \"abc123def456\",                              │");
        System.out.println("│   \"event\": \"order.created\",                                │");
        System.out.println("│   \"user_id\": 123,                                           │");
        System.out.println("│   \"order_id\": 456,                                          │");
        System.out.println("│   \"product_id\": 789,                                        │");
        System.out.println("│   \"total\": 99.99                                            │");
        System.out.println("│ }                                                            │");
        System.out.println("│                                                              │");
        System.out.println("│ ✅ 优点:                                                     │");
        System.out.println("│   • 机器可解析，易于检索                                     │");
        System.out.println("│   • 字段统一，便于聚合分析                                   │");
        System.out.println("│   • 可关联链路追踪                                           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ==================== 2. JSON 日志格式 ====================

    private static void jsonLogFormat() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. JSON 日志格式配置\n");

        System.out.println("Logback JSON 配置 (logback-spring.xml)：");
        System.out.println();
        System.out.println("```xml");
        System.out.println("<configuration>");
        System.out.println("  <appender name=\"JSON\" class=\"ch.qos.logback.core.ConsoleAppender\">");
        System.out.println("    <encoder class=\"net.logstash.logback.encoder.LogstashEncoder\">");
        System.out.println("      <includeMdcKeyName>traceId</includeMdcKeyName>");
        System.out.println("      <includeMdcKeyName>spanId</includeMdcKeyName>");
        System.out.println("      <customFields>");
        System.out.println("        {\"service\":\"order-service\",\"env\":\"${ENV}\"}");
        System.out.println("      </customFields>");
        System.out.println("    </encoder>");
        System.out.println("  </appender>");
        System.out.println();
        System.out.println("  <root level=\"INFO\">");
        System.out.println("    <appender-ref ref=\"JSON\" />");
        System.out.println("  </root>");
        System.out.println("</configuration>");
        System.out.println("```");
        System.out.println();

        System.out.println("Maven 依赖：");
        System.out.println("```xml");
        System.out.println("<dependency>");
        System.out.println("  <groupId>net.logstash.logback</groupId>");
        System.out.println("  <artifactId>logstash-logback-encoder</artifactId>");
        System.out.println("  <version>7.4</version>");
        System.out.println("</dependency>");
        System.out.println("```");
        System.out.println();

        System.out.println("代码中使用 MDC 添加上下文：");
        System.out.println("```java");
        System.out.println("import org.slf4j.MDC;");
        System.out.println();
        System.out.println("// 请求开始时设置");
        System.out.println("MDC.put(\"traceId\", traceId);");
        System.out.println("MDC.put(\"userId\", userId);");
        System.out.println();
        System.out.println("// 正常日志记录");
        System.out.println("log.info(\"Order created\");  // 自动包含 MDC 字段");
        System.out.println();
        System.out.println("// 请求结束时清理");
        System.out.println("MDC.clear();");
        System.out.println("```");
    }

    // ==================== 3. ELK Stack 集成 ====================

    private static void elkStackIntegration() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. ELK Stack 日志平台\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      ELK Stack 架构                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   应用日志                                                   │");
        System.out.println("│      │                                                       │");
        System.out.println("│      ↓                                                       │");
        System.out.println("│   ┌──────────┐    ┌──────────┐    ┌───────────────┐        │");
        System.out.println("│   │ Filebeat │ →  │ Logstash │ →  │ Elasticsearch │         │");
        System.out.println("│   │ (采集)   │    │ (处理)   │    │   (存储)      │         │");
        System.out.println("│   └──────────┘    └──────────┘    └───────────────┘        │");
        System.out.println("│                                          │                  │");
        System.out.println("│                                          ↓                  │");
        System.out.println("│                                    ┌──────────┐            │");
        System.out.println("│                                    │  Kibana  │            │");
        System.out.println("│                                    │ (可视化) │            │");
        System.out.println("│                                    └──────────┘            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("组件职责：");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 组件           │ 职责                                        │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Filebeat       │ 轻量级日志采集，运行在每个节点              │");
        System.out.println("│ Logstash       │ 日志解析、过滤、转换                        │");
        System.out.println("│ Elasticsearch  │ 分布式搜索引擎，存储和索引日志              │");
        System.out.println("│ Kibana         │ 可视化界面，查询和分析日志                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("Filebeat 配置示例 (filebeat.yml)：");
        System.out.println("```yaml");
        System.out.println("filebeat.inputs:");
        System.out.println("  - type: log");
        System.out.println("    paths:");
        System.out.println("      - /var/log/app/*.log");
        System.out.println("    json.keys_under_root: true");
        System.out.println("    json.add_error_key: true");
        System.out.println();
        System.out.println("output.elasticsearch:");
        System.out.println("  hosts: [\"elasticsearch:9200\"]");
        System.out.println("  index: \"app-logs-%{+yyyy.MM.dd}\"");
        System.out.println("```");
    }

    // ==================== 4. 最佳实践 ====================

    private static void loggingBestPractices() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. 日志最佳实践\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ✅ 日志级别使用规范                                          │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ ERROR  │ 系统错误，需要立即处理                              │");
        System.out.println("│        │ 例: 数据库连接失败、外部服务不可用                  │");
        System.out.println("│────────┼────────────────────────────────────────────────────│");
        System.out.println("│ WARN   │ 警告信息，可能导致问题                              │");
        System.out.println("│        │ 例: 重试成功、配置使用默认值                        │");
        System.out.println("│────────┼────────────────────────────────────────────────────│");
        System.out.println("│ INFO   │ 重要业务事件                                        │");
        System.out.println("│        │ 例: 订单创建、用户登录、支付完成                    │");
        System.out.println("│────────┼────────────────────────────────────────────────────│");
        System.out.println("│ DEBUG  │ 调试信息，开发环境使用                              │");
        System.out.println("│        │ 例: 方法入参、中间结果                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ ✅ 日志内容规范                                              │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 包含关键业务字段                                          │");
        System.out.println("│    ✓ log.info(\"Order created\", kv(\"orderId\", orderId));     │");
        System.out.println("│    ✗ log.info(\"Order created: \" + order);                   │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 避免敏感信息                                              │");
        System.out.println("│    ✗ log.info(\"User login: password=\" + password);          │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 关联 Trace ID                                             │");
        System.out.println("│    所有日志自动包含 traceId，便于问题追踪                    │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 记录上下文                                                │");
        System.out.println("│    包含 userId、requestId、duration 等                       │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("💡 关键原则：");
        System.out.println("   1. 日志是可观测性的基础");
        System.out.println("   2. 结构化便于机器处理");
        System.out.println("   3. 与 Trace 关联便于排查");
        System.out.println("   4. 采样控制日志量");
    }
}
