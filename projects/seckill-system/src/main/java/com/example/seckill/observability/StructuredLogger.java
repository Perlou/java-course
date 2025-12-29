package com.example.seckill.observability;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Phase 18: 结构化日志组件
 * 
 * 生成 JSON 格式的结构化日志，便于：
 * - ELK Stack 解析和索引
 * - 日志聚合和分析
 * - 链路追踪关联
 */
@Component
public class StructuredLogger {

    private static final Logger log = LoggerFactory.getLogger(StructuredLogger.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final String serviceName;

    public StructuredLogger() {
        this.serviceName = "seckill-system";
    }

    // ==================== 日志级别方法 ====================

    public void info(String message, Object... args) {
        logStructured("INFO", message, null, args);
    }

    public void warn(String message, Object... args) {
        logStructured("WARN", message, null, args);
    }

    public void error(String message, Throwable throwable, Object... args) {
        logStructured("ERROR", message, throwable, args);
    }

    public void debug(String message, Object... args) {
        logStructured("DEBUG", message, null, args);
    }

    // ==================== 业务事件日志 ====================

    /**
     * 记录业务事件
     */
    public void logEvent(String eventType, Map<String, Object> data) {
        Map<String, Object> logEntry = createBaseEntry("INFO");
        logEntry.put("event_type", eventType);
        logEntry.put("event_data", data);
        outputLog(logEntry);
    }

    /**
     * 记录 API 请求日志
     */
    public void logApiRequest(String method, String path, int status, long durationMs,
            String clientIp, String userAgent) {
        Map<String, Object> logEntry = createBaseEntry("INFO");
        logEntry.put("log_type", "api_request");
        logEntry.put("http_method", method);
        logEntry.put("http_path", path);
        logEntry.put("http_status", status);
        logEntry.put("duration_ms", durationMs);
        logEntry.put("client_ip", clientIp);
        logEntry.put("user_agent", userAgent);
        outputLog(logEntry);
    }

    /**
     * 记录秒杀事件
     */
    public void logSeckillEvent(String action, Long userId, Long goodsId, boolean success, String message) {
        Map<String, Object> logEntry = createBaseEntry(success ? "INFO" : "WARN");
        logEntry.put("log_type", "seckill");
        logEntry.put("action", action);
        logEntry.put("user_id", userId);
        logEntry.put("goods_id", goodsId);
        logEntry.put("success", success);
        logEntry.put("message", message);
        outputLog(logEntry);
    }

    /**
     * 记录数据库操作日志
     */
    public void logDbOperation(String operation, String table, long durationMs, boolean success) {
        Map<String, Object> logEntry = createBaseEntry(success ? "INFO" : "ERROR");
        logEntry.put("log_type", "database");
        logEntry.put("operation", operation);
        logEntry.put("table", table);
        logEntry.put("duration_ms", durationMs);
        logEntry.put("success", success);
        outputLog(logEntry);
    }

    /**
     * 记录缓存操作日志
     */
    public void logCacheOperation(String operation, String key, boolean hit) {
        Map<String, Object> logEntry = createBaseEntry("DEBUG");
        logEntry.put("log_type", "cache");
        logEntry.put("operation", operation);
        logEntry.put("cache_key", key);
        logEntry.put("cache_hit", hit);
        outputLog(logEntry);
    }

    /**
     * 记录错误日志
     */
    public void logError(String errorType, String message, Throwable throwable) {
        Map<String, Object> logEntry = createBaseEntry("ERROR");
        logEntry.put("log_type", "error");
        logEntry.put("error_type", errorType);
        logEntry.put("error_message", message);
        if (throwable != null) {
            logEntry.put("exception_class", throwable.getClass().getName());
            logEntry.put("exception_message", throwable.getMessage());
            logEntry.put("stack_trace", getStackTrace(throwable, 5));
        }
        outputLog(logEntry);
    }

    // ==================== 私有方法 ====================

    private void logStructured(String level, String message, Throwable throwable, Object... args) {
        Map<String, Object> logEntry = createBaseEntry(level);
        logEntry.put("message", String.format(message.replace("{}", "%s"), args));

        if (throwable != null) {
            logEntry.put("exception", throwable.getClass().getName());
            logEntry.put("exception_message", throwable.getMessage());
        }

        outputLog(logEntry);
    }

    private Map<String, Object> createBaseEntry(String level) {
        Map<String, Object> entry = new LinkedHashMap<>();

        // 基础字段
        entry.put("@timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        entry.put("level", level);
        entry.put("service", serviceName);

        // 链路追踪信息
        TraceContext context = TraceContext.getContext();
        if (context != null) {
            entry.put("trace_id", context.getTraceId());
            entry.put("span_id", context.getSpanId());
            if (context.getParentSpanId() != null) {
                entry.put("parent_span_id", context.getParentSpanId());
            }
        }

        // 线程信息
        entry.put("thread", Thread.currentThread().getName());

        return entry;
    }

    private void outputLog(Map<String, Object> logEntry) {
        try {
            String json = objectMapper.writeValueAsString(logEntry);
            // 输出到标准日志（生产环境会被 Filebeat 采集）
            String level = (String) logEntry.get("level");
            switch (level) {
                case "ERROR" -> log.error(json);
                case "WARN" -> log.warn(json);
                case "DEBUG" -> log.debug(json);
                default -> log.info(json);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize log entry", e);
        }
    }

    private String getStackTrace(Throwable throwable, int maxLines) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = throwable.getStackTrace();
        for (int i = 0; i < Math.min(maxLines, trace.length); i++) {
            sb.append(trace[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
