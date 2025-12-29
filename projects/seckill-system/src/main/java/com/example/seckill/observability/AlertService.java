package com.example.seckill.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Phase 18: 告警服务
 * 
 * 实现告警规则检查和通知：
 * - 阈值告警
 * - 异常检测
 * - 告警抑制（防止告警风暴）
 * - 多渠道通知
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    // 告警规则
    private final Map<String, AlertRule> rules = new ConcurrentHashMap<>();

    // 告警历史
    private final List<Alert> alertHistory = new CopyOnWriteArrayList<>();

    // 告警抑制（防止重复告警）
    private final Map<String, Long> lastAlertTime = new ConcurrentHashMap<>();

    // 通知处理器
    private final List<AlertNotifier> notifiers = new ArrayList<>();

    // 抑制时间（秒）
    private static final long SUPPRESS_SECONDS = 60;

    public AlertService() {
        // 初始化默认告警规则
        initDefaultRules();
        log.info("🚨 告警服务已初始化");
    }

    // ==================== 告警规则管理 ====================

    /**
     * 添加告警规则
     */
    public void addRule(AlertRule rule) {
        rules.put(rule.getName(), rule);
        log.info("➕ 添加告警规则: {}", rule.getName());
    }

    /**
     * 移除告警规则
     */
    public void removeRule(String ruleName) {
        rules.remove(ruleName);
        log.info("➖ 移除告警规则: {}", ruleName);
    }

    /**
     * 获取所有规则
     */
    public Collection<AlertRule> getRules() {
        return rules.values();
    }

    // ==================== 告警检查 ====================

    /**
     * 检查指标值是否触发告警
     */
    public void checkMetric(String metricName, double value, Map<String, String> labels) {
        rules.values().stream()
                .filter(rule -> rule.getMetricName().equals(metricName))
                .forEach(rule -> {
                    if (rule.shouldAlert(value)) {
                        triggerAlert(rule, value, labels);
                    }
                });
    }

    /**
     * 直接触发告警
     */
    public void triggerAlert(String ruleName, String message, AlertLevel level) {
        Alert alert = new Alert(
                UUID.randomUUID().toString(),
                ruleName,
                message,
                level,
                LocalDateTime.now(),
                Map.of());

        processAlert(alert);
    }

    // ==================== 告警处理 ====================

    private void triggerAlert(AlertRule rule, double value, Map<String, String> labels) {
        // 检查是否在抑制期内
        String alertKey = rule.getName() + ":" + labels;
        Long lastTime = lastAlertTime.get(alertKey);
        long now = System.currentTimeMillis();

        if (lastTime != null && (now - lastTime) < SUPPRESS_SECONDS * 1000) {
            log.debug("⏳ 告警被抑制: {} ({}s 内不重复)", rule.getName(), SUPPRESS_SECONDS);
            return;
        }

        // 创建告警
        Alert alert = new Alert(
                UUID.randomUUID().toString(),
                rule.getName(),
                String.format("%s: 当前值 %.2f %s 阈值 %.2f",
                        rule.getDescription(), value, rule.getOperator(), rule.getThreshold()),
                rule.getLevel(),
                LocalDateTime.now(),
                labels);

        // 更新抑制时间
        lastAlertTime.put(alertKey, now);

        // 处理告警
        processAlert(alert);
    }

    private void processAlert(Alert alert) {
        // 记录到历史
        alertHistory.add(alert);

        // 限制历史大小
        while (alertHistory.size() > 1000) {
            alertHistory.remove(0);
        }

        // 日志输出
        logAlert(alert);

        // 发送通知
        notifiers.forEach(notifier -> {
            try {
                notifier.notify(alert);
            } catch (Exception e) {
                log.error("告警通知失败: {}", e.getMessage());
            }
        });
    }

    private void logAlert(Alert alert) {
        String emoji = switch (alert.level()) {
            case CRITICAL -> "🔴";
            case WARNING -> "🟠";
            case INFO -> "🔵";
        };

        log.warn("{} [{}] {}: {}", emoji, alert.level(), alert.ruleName(), alert.message());
    }

    // ==================== 通知器管理 ====================

    /**
     * 添加通知器
     */
    public void addNotifier(AlertNotifier notifier) {
        notifiers.add(notifier);
    }

    // ==================== 告警历史查询 ====================

    /**
     * 获取最近的告警
     */
    public List<Alert> getRecentAlerts(int limit) {
        int size = alertHistory.size();
        int start = Math.max(0, size - limit);
        return new ArrayList<>(alertHistory.subList(start, size));
    }

    /**
     * 按级别获取告警
     */
    public List<Alert> getAlertsByLevel(AlertLevel level) {
        return alertHistory.stream()
                .filter(a -> a.level() == level)
                .toList();
    }

    /**
     * 获取告警统计
     */
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long criticalCount = alertHistory.stream().filter(a -> a.level() == AlertLevel.CRITICAL).count();
        long warningCount = alertHistory.stream().filter(a -> a.level() == AlertLevel.WARNING).count();
        long infoCount = alertHistory.stream().filter(a -> a.level() == AlertLevel.INFO).count();

        stats.put("total", alertHistory.size());
        stats.put("critical", criticalCount);
        stats.put("warning", warningCount);
        stats.put("info", infoCount);
        stats.put("rules_count", rules.size());

        return stats;
    }

    // ==================== 默认规则 ====================

    private void initDefaultRules() {
        // CPU 使用率告警
        addRule(new AlertRule("high_cpu", "system.cpu.usage", "CPU 使用率过高",
                ">", 80.0, AlertLevel.WARNING));

        // 内存使用率告警
        addRule(new AlertRule("high_memory", "jvm.memory.used.percent", "内存使用率过高",
                ">", 85.0, AlertLevel.WARNING));

        // 响应时间告警
        addRule(new AlertRule("slow_response", "http.latency.p99", "API 响应时间过长",
                ">", 1000.0, AlertLevel.WARNING));

        // 错误率告警
        addRule(new AlertRule("high_error_rate", "http.errors.rate", "错误率过高",
                ">", 5.0, AlertLevel.CRITICAL));

        // 秒杀失败率告警
        addRule(new AlertRule("seckill_fail_rate", "seckill.fail.rate", "秒杀失败率过高",
                ">", 30.0, AlertLevel.CRITICAL));
    }

    // ==================== 内部类 ====================

    public enum AlertLevel {
        INFO, WARNING, CRITICAL
    }

    public record Alert(
            String id,
            String ruleName,
            String message,
            AlertLevel level,
            LocalDateTime timestamp,
            Map<String, String> labels) {
    }

    public static class AlertRule {
        private final String name;
        private final String metricName;
        private final String description;
        private final String operator;
        private final double threshold;
        private final AlertLevel level;

        public AlertRule(String name, String metricName, String description,
                String operator, double threshold, AlertLevel level) {
            this.name = name;
            this.metricName = metricName;
            this.description = description;
            this.operator = operator;
            this.threshold = threshold;
            this.level = level;
        }

        public boolean shouldAlert(double value) {
            return switch (operator) {
                case ">" -> value > threshold;
                case ">=" -> value >= threshold;
                case "<" -> value < threshold;
                case "<=" -> value <= threshold;
                case "==" -> value == threshold;
                default -> false;
            };
        }

        public String getName() {
            return name;
        }

        public String getMetricName() {
            return metricName;
        }

        public String getDescription() {
            return description;
        }

        public String getOperator() {
            return operator;
        }

        public double getThreshold() {
            return threshold;
        }

        public AlertLevel getLevel() {
            return level;
        }
    }

    @FunctionalInterface
    public interface AlertNotifier {
        void notify(Alert alert);
    }
}
