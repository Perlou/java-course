package phase18;

/**
 * Phase 18: 告警规则设计
 * 
 * 本课程涵盖：
 * 1. 告警设计原则
 * 2. 告警规则类型
 * 3. 告警分级与处理
 * 4. On-Call 体系
 */
public class AlertRules {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          🚨 Phase 18: 告警规则设计                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        alertingPrinciples();
        alertRuleTypes();
        alertLevels();
        onCallSystem();
        alertFatigue();
    }

    // ==================== 1. 告警设计原则 ====================

    private static void alertingPrinciples() {
        System.out.println("\n📌 1. 告警设计原则\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 告警的核心目标                                               │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   \"每一条告警都应该是 Actionable 的\"                         │");
        System.out.println("│                                                              │");
        System.out.println("│   即：收到告警后，你需要采取行动                             │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("告警设计四原则：");
        System.out.println();
        System.out.println("  1️⃣  精准性 - 减少误报 (False Positive)");
        System.out.println("       • 告警必须反映真实问题");
        System.out.println("       • 误报率 < 5%");
        System.out.println();
        System.out.println("  2️⃣  及时性 - 快速检测");
        System.out.println("       • 检测延迟 < 5 分钟");
        System.out.println("       • 通知延迟 < 1 分钟");
        System.out.println();
        System.out.println("  3️⃣  可操作性 - 明确处理步骤");
        System.out.println("       • 告警包含上下文信息");
        System.out.println("       • 关联 Runbook（处理手册）");
        System.out.println();
        System.out.println("  4️⃣  分级明确 - 区分紧急程度");
        System.out.println("       • Critical: 立即处理");
        System.out.println("       • Warning: 工作时间处理");
        System.out.println("       • Info: 仅记录");
    }

    // ==================== 2. 告警规则类型 ====================

    private static void alertRuleTypes() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 2. 告警规则类型\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 1. 阈值告警 (Threshold-based)                                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 规则: 当指标超过/低于阈值时触发                              │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   • CPU > 80% 持续 5 分钟                                    │");
        System.out.println("│   • 可用内存 < 10%                                           │");
        System.out.println("│   • 错误率 > 5%                                              │");
        System.out.println("│   • 响应时间 P99 > 1 秒                                      │");
        System.out.println("│                                                              │");
        System.out.println("│ Prometheus 规则:                                             │");
        System.out.println("│   - alert: HighErrorRate                                     │");
        System.out.println("│     expr: rate(http_errors_total[5m]) / rate(http_total[5m])│");
        System.out.println("│           > 0.05                                             │");
        System.out.println("│     for: 5m                                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 2. 趋势告警 (Trend-based)                                    │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 规则: 基于历史数据预测趋势                                   │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   • 磁盘预计 4 小时后耗尽                                    │");
        System.out.println("│   • 请求量比上周同期增长 200%                                │");
        System.out.println("│                                                              │");
        System.out.println("│ Prometheus 规则 (磁盘预测):                                  │");
        System.out.println("│   - alert: DiskWillFillIn4Hours                              │");
        System.out.println("│     expr: predict_linear(disk_free_bytes[1h], 4*3600) < 0    │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 3. 异常检测 (Anomaly Detection)                              │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 规则: 基于机器学习检测异常                                   │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   • 流量突然下降（可能是故障）                               │");
        System.out.println("│   • 响应时间突然增加                                         │");
        System.out.println("│   • 异常的请求模式（可能是攻击）                             │");
        System.out.println("│                                                              │");
        System.out.println("│ 优点: 无需手动设置阈值，自动适应                             │");
        System.out.println("│ 缺点: 需要足够的历史数据，可能误报                           │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 4. 心跳告警 (Heartbeat)                                      │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 规则: 检测服务存活状态                                       │");
        System.out.println("│                                                              │");
        System.out.println("│ 示例:                                                        │");
        System.out.println("│   • 服务 1 分钟内没有上报指标                                │");
        System.out.println("│   • 健康检查失败                                             │");
        System.out.println("│                                                              │");
        System.out.println("│ Prometheus 规则:                                             │");
        System.out.println("│   - alert: ServiceDown                                       │");
        System.out.println("│     expr: up == 0                                            │");
        System.out.println("│     for: 1m                                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ==================== 3. 告警分级 ====================

    private static void alertLevels() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 3. 告警分级与响应\n");

        System.out.println("┌────────────┬─────────────────────────────────────────────────┐");
        System.out.println("│   级别     │   描述                          │   响应时间   │");
        System.out.println("├────────────┼─────────────────────────────────┼──────────────┤");
        System.out.println("│ 🔴 P0     │ 核心服务完全不可用              │   5 分钟     │");
        System.out.println("│ Critical  │ 影响大量用户                    │   立即响应   │");
        System.out.println("│           │ 数据丢失风险                    │   唤醒值班   │");
        System.out.println("├────────────┼─────────────────────────────────┼──────────────┤");
        System.out.println("│ 🟠 P1     │ 服务降级，功能部分可用          │   30 分钟    │");
        System.out.println("│ High      │ 影响部分用户                    │   值班处理   │");
        System.out.println("├────────────┼─────────────────────────────────┼──────────────┤");
        System.out.println("│ 🟡 P2     │ 非核心功能异常                  │   4 小时     │");
        System.out.println("│ Medium    │ 不影响主流程                    │   工作时间   │");
        System.out.println("├────────────┼─────────────────────────────────┼──────────────┤");
        System.out.println("│ 🔵 P3     │ 轻微问题                        │   24 小时    │");
        System.out.println("│ Low       │ 优化建议                        │   下一迭代   │");
        System.out.println("└────────────┴─────────────────────────────────┴──────────────┘");
        System.out.println();

        System.out.println("通知渠道配置：");
        System.out.println("```yaml");
        System.out.println("# Alertmanager 配置");
        System.out.println("route:");
        System.out.println("  receiver: 'default'");
        System.out.println("  routes:");
        System.out.println("    - match:");
        System.out.println("        severity: critical");
        System.out.println("      receiver: 'pagerduty'");
        System.out.println("      continue: true");
        System.out.println("    - match:");
        System.out.println("        severity: warning");
        System.out.println("      receiver: 'slack'");
        System.out.println();
        System.out.println("receivers:");
        System.out.println("  - name: 'pagerduty'");
        System.out.println("    pagerduty_configs:");
        System.out.println("      - service_key: '<key>'");
        System.out.println("  - name: 'slack'");
        System.out.println("    slack_configs:");
        System.out.println("      - channel: '#alerts'");
        System.out.println("```");
    }

    // ==================== 4. On-Call 体系 ====================

    private static void onCallSystem() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 4. On-Call 值班体系\n");

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                      On-Call 流程                            │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                              │");
        System.out.println("│   告警触发                                                   │");
        System.out.println("│      │                                                       │");
        System.out.println("│      ↓                                                       │");
        System.out.println("│   通知主要值班人 (5 分钟内响应)                              │");
        System.out.println("│      │                                                       │");
        System.out.println("│      ├── 响应 → 处理问题                                     │");
        System.out.println("│      │                                                       │");
        System.out.println("│      └── 未响应                                              │");
        System.out.println("│             │                                                │");
        System.out.println("│             ↓                                                │");
        System.out.println("│   Escalation: 通知备份值班人                                 │");
        System.out.println("│             │                                                │");
        System.out.println("│             └── 仍未响应 → 通知团队负责人                    │");
        System.out.println("│                                                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("值班轮换最佳实践：");
        System.out.println();
        System.out.println("  • 轮换周期: 每周轮换");
        System.out.println("  • 交接时间: 工作日上午 10:00");
        System.out.println("  • 备份机制: 主备两人同时值班");
        System.out.println("  • 假期处理: 提前安排替补");
        System.out.println("  • 工具支持: PagerDuty / Opsgenie");
        System.out.println();

        System.out.println("On-Call 职责：");
        System.out.println("  1. 15 分钟内响应 P0/P1 告警");
        System.out.println("  2. 初步诊断和止血");
        System.out.println("  3. 必要时升级到专家");
        System.out.println("  4. 事后编写 Postmortem");
    }

    // ==================== 5. 告警疲劳 ====================

    private static void alertFatigue() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("\n📌 5. 避免告警疲劳\n");

        System.out.println("告警疲劳症状：");
        System.out.println("  ❌ 告警太多，开始忽略");
        System.out.println("  ❌ 误报频繁，失去信任");
        System.out.println("  ❌ 告警信息不清楚，无法行动");
        System.out.println();

        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ 解决方案                                                     │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 告警抑制 (Inhibition)                                     │");
        System.out.println("│    当更严重的告警触发时，抑制相关的次要告警                  │");
        System.out.println("│    例: 服务器宕机时，抑制其上的服务告警                      │");
        System.out.println("│                                                              │");
        System.out.println("│ 2. 告警分组 (Grouping)                                       │");
        System.out.println("│    相同类型的告警合并为一条通知                              │");
        System.out.println("│    例: 10 个 Pod 的相同告警合并为 1 条                       │");
        System.out.println("│                                                              │");
        System.out.println("│ 3. 静默 (Silence)                                            │");
        System.out.println("│    维护期间临时静默告警                                      │");
        System.out.println("│                                                              │");
        System.out.println("│ 4. 定期审查                                                  │");
        System.out.println("│    每月审查告警规则，删除无效告警                            │");
        System.out.println("│    目标: 每周值班告警 < 20 条                                │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("💡 黄金法则：");
        System.out.println("   \"如果收到告警后你不需要采取任何行动，");
        System.out.println("    那这个告警就不应该存在\"");
    }
}
