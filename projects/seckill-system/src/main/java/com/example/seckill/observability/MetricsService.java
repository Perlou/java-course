package com.example.seckill.observability;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Phase 18: 指标收集服务
 * 
 * 基于 Micrometer 的指标收集服务，支持：
 * - Counter: 计数器（请求数、错误数）
 * - Gauge: 瞬时值（当前连接数、队列大小）
 * - Timer: 计时器（响应时间、处理时长）
 * - DistributionSummary: 分布统计（请求大小）
 * 
 * 自动集成 Prometheus 导出
 */
@Service
public class MetricsService {

    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    private final MeterRegistry meterRegistry;

    // 缓存已创建的指标
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    private final Map<String, DistributionSummary> summaries = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initSystemMetrics();
        log.info("📊 指标收集服务已初始化");
    }

    /**
     * 初始化系统指标
     */
    private void initSystemMetrics() {
        // JVM 内存指标
        new JvmMemoryMetrics().bindTo(meterRegistry);
        // JVM GC 指标
        new JvmGcMetrics().bindTo(meterRegistry);
        // JVM 线程指标
        new JvmThreadMetrics().bindTo(meterRegistry);
        // 进程 CPU 指标
        new ProcessorMetrics().bindTo(meterRegistry);
        // 文件描述符
        new FileDescriptorMetrics().bindTo(meterRegistry);

        log.info("✅ 系统指标绑定完成");
    }

    // ==================== Counter 计数器 ====================

    /**
     * 增加计数器
     */
    public void increment(String name, String... tags) {
        getOrCreateCounter(name, tags).increment();
    }

    /**
     * 增加指定数量
     */
    public void increment(String name, double amount, String... tags) {
        getOrCreateCounter(name, tags).increment(amount);
    }

    /**
     * 记录请求计数
     */
    public void recordRequest(String api, String method, String status) {
        Counter.builder("http.requests")
                .tag("api", api)
                .tag("method", method)
                .tag("status", status)
                .description("HTTP 请求计数")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录业务事件
     */
    public void recordEvent(String eventType, String result) {
        Counter.builder("business.events")
                .tag("type", eventType)
                .tag("result", result)
                .description("业务事件计数")
                .register(meterRegistry)
                .increment();
    }

    // ==================== Timer 计时器 ====================

    /**
     * 记录耗时
     */
    public void recordTime(String name, long duration, TimeUnit unit, String... tags) {
        getOrCreateTimer(name, tags).record(duration, unit);
    }

    /**
     * 计时执行（自动记录）
     */
    public <T> T recordTime(String name, Supplier<T> action, String... tags) {
        Timer timer = getOrCreateTimer(name, tags);
        return timer.record(action);
    }

    /**
     * 开始计时
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 停止计时并记录
     */
    public long stopTimer(Timer.Sample sample, String name, String... tags) {
        Timer timer = getOrCreateTimer(name, tags);
        return sample.stop(timer);
    }

    /**
     * 记录 API 响应时间
     */
    public void recordApiLatency(String api, String method, long durationMs) {
        Timer.builder("http.latency")
                .tag("api", api)
                .tag("method", method)
                .description("API 响应时间")
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

    // ==================== Gauge 瞬时值 ====================

    /**
     * 注册 Gauge 指标
     */
    public <T extends Number> void registerGauge(String name, T number, String... tags) {
        Gauge.builder(name, number, Number::doubleValue)
                .tags(tags)
                .register(meterRegistry);
    }

    /**
     * 注册动态 Gauge
     */
    public <T> void registerGauge(String name, T obj, java.util.function.ToDoubleFunction<T> valueFunction,
            String... tags) {
        Gauge.builder(name, obj, valueFunction)
                .tags(tags)
                .register(meterRegistry);
    }

    // ==================== Distribution Summary ====================

    /**
     * 记录分布值（如请求大小）
     */
    public void recordDistribution(String name, double value, String... tags) {
        getOrCreateSummary(name, tags).record(value);
    }

    // ==================== 秒杀业务指标 ====================

    /**
     * 记录秒杀请求
     */
    public void recordSeckillRequest(Long goodsId, boolean success) {
        Counter.builder("seckill.requests")
                .tag("goods_id", String.valueOf(goodsId))
                .tag("result", success ? "success" : "failed")
                .description("秒杀请求统计")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录库存变化
     */
    public void recordStockChange(Long goodsId, int stock) {
        Gauge.builder("seckill.stock", stock, Number::doubleValue)
                .tag("goods_id", String.valueOf(goodsId))
                .description("商品库存")
                .register(meterRegistry);
    }

    /**
     * 记录订单创建
     */
    public void recordOrderCreated(String status) {
        Counter.builder("orders.created")
                .tag("status", status)
                .description("订单创建统计")
                .register(meterRegistry)
                .increment();
    }

    // ==================== 获取指标统计 ====================

    /**
     * 获取所有指标摘要
     */
    public Map<String, Object> getMetricsSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        // 收集 Counter 统计
        meterRegistry.find("http.requests").counters().forEach(counter -> {
            String key = "requests." + counter.getId().getTag("api");
            summary.put(key, counter.count());
        });

        // 收集 Timer 统计
        meterRegistry.find("http.latency").timers().forEach(timer -> {
            String key = "latency." + timer.getId().getTag("api");
            summary.put(key + ".count", timer.count());
            summary.put(key + ".mean_ms", timer.mean(TimeUnit.MILLISECONDS));
            summary.put(key + ".max_ms", timer.max(TimeUnit.MILLISECONDS));
        });

        return summary;
    }

    // ==================== 私有方法 ====================

    private Counter getOrCreateCounter(String name, String... tags) {
        String key = name + String.join(",", tags);
        return counters.computeIfAbsent(key, k -> Counter.builder(name).tags(tags).register(meterRegistry));
    }

    private Timer getOrCreateTimer(String name, String... tags) {
        String key = name + String.join(",", tags);
        return timers.computeIfAbsent(key, k -> Timer.builder(name).tags(tags).register(meterRegistry));
    }

    private DistributionSummary getOrCreateSummary(String name, String... tags) {
        String key = name + String.join(",", tags);
        return summaries.computeIfAbsent(key,
                k -> DistributionSummary.builder(name).tags(tags).register(meterRegistry));
    }
}
