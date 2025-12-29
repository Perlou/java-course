package com.example.seckill.observability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;

/**
 * Phase 18: 链路追踪上下文
 * 
 * 实现分布式链路追踪的核心功能：
 * - 生成和传递 Trace ID
 * - 管理 Span 上下文
 * - 支持跨服务传播
 * 
 * 基于 W3C Trace Context 规范
 */
public class TraceContext {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String PARENT_SPAN_ID_KEY = "parentSpanId";

    // 使用 ThreadLocal 存储当前线程的追踪上下文
    private static final ThreadLocal<TraceContext> CONTEXT = new ThreadLocal<>();

    private final String traceId;
    private final String spanId;
    private final String parentSpanId;
    private final long startTime;
    private final Map<String, String> baggage;

    private TraceContext(String traceId, String spanId, String parentSpanId) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.startTime = System.currentTimeMillis();
        this.baggage = new HashMap<>();
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建新的追踪上下文（作为根 Span）
     */
    public static TraceContext create() {
        String traceId = generateId();
        String spanId = generateId();
        TraceContext context = new TraceContext(traceId, spanId, null);
        setContext(context);
        return context;
    }

    /**
     * 从现有追踪创建子 Span
     */
    public static TraceContext createChild() {
        TraceContext current = getContext();
        if (current == null) {
            return create();
        }
        String spanId = generateId();
        TraceContext child = new TraceContext(current.traceId, spanId, current.spanId);
        setContext(child);
        return child;
    }

    /**
     * 从传入的 Header 恢复上下文
     */
    public static TraceContext fromHeaders(Map<String, String> headers) {
        String traceId = headers.getOrDefault("X-Trace-Id", generateId());
        String parentSpanId = headers.get("X-Span-Id");
        String spanId = generateId();

        TraceContext context = new TraceContext(traceId, spanId, parentSpanId);
        setContext(context);
        return context;
    }

    // ==================== 上下文管理 ====================

    /**
     * 获取当前上下文
     */
    public static TraceContext getContext() {
        return CONTEXT.get();
    }

    /**
     * 设置当前上下文
     */
    public static void setContext(TraceContext context) {
        CONTEXT.set(context);
        // 同步到 MDC（日志上下文）
        if (context != null) {
            MDC.put(TRACE_ID_KEY, context.traceId);
            MDC.put(SPAN_ID_KEY, context.spanId);
            if (context.parentSpanId != null) {
                MDC.put(PARENT_SPAN_ID_KEY, context.parentSpanId);
            }
        }
    }

    /**
     * 清除当前上下文
     */
    public static void clear() {
        CONTEXT.remove();
        MDC.remove(TRACE_ID_KEY);
        MDC.remove(SPAN_ID_KEY);
        MDC.remove(PARENT_SPAN_ID_KEY);
    }

    /**
     * 获取当前 Trace ID（静态方法）
     */
    public static String currentTraceId() {
        TraceContext context = getContext();
        return context != null ? context.traceId : null;
    }

    // ==================== 实例方法 ====================

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 添加 Baggage（跨服务传递的数据）
     */
    public void setBaggage(String key, String value) {
        baggage.put(key, value);
    }

    public String getBaggage(String key) {
        return baggage.get(key);
    }

    /**
     * 生成传播用的 Headers
     */
    public Map<String, String> toHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Trace-Id", traceId);
        headers.put("X-Span-Id", spanId);
        if (parentSpanId != null) {
            headers.put("X-Parent-Span-Id", parentSpanId);
        }
        // 传递 Baggage
        baggage.forEach((k, v) -> headers.put("X-Baggage-" + k, v));
        return headers;
    }

    /**
     * 生成 W3C Trace Context 格式的 traceparent
     */
    public String toW3CTraceParent() {
        // 格式: version-trace_id-parent_id-flags
        return String.format("00-%s-%s-01", traceId, spanId);
    }

    @Override
    public String toString() {
        return String.format("TraceContext{traceId='%s', spanId='%s', parentSpanId='%s', duration=%dms}",
                traceId, spanId, parentSpanId, getDuration());
    }

    // ==================== 工具方法 ====================

    private static String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 执行带追踪的操作
     */
    public static <T> T traced(String operationName, java.util.function.Supplier<T> action) {
        TraceContext context = createChild();
        try {
            return action.get();
        } finally {
            clear();
        }
    }

    /**
     * 执行带追踪的操作（无返回值）
     */
    public static void traced(String operationName, Runnable action) {
        TraceContext context = createChild();
        try {
            action.run();
        } finally {
            clear();
        }
    }
}
