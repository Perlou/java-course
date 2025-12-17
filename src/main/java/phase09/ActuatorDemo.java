package phase09;

/**
 * Phase 9 - Lesson 5: Actuator 监控
 * 
 * 🎯 学习目标:
 * 1. 理解 Actuator 的作用
 * 2. 掌握常用端点
 * 3. 学会自定义健康检查
 */
public class ActuatorDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 9 - Lesson 5: Actuator 监控");
        System.out.println("=".repeat(60));

        // 1. Actuator 简介
        System.out.println("\n【1. Actuator 简介】");
        System.out.println("""
                Spring Boot Actuator 提供生产级监控能力:

                ┌─────────────────────────────────────────────────────────┐
                │                    Actuator                             │
                │                                                         │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │              内置端点 (Endpoints)                │   │
                │  │  /health  /info  /metrics  /env  /beans ...     │   │
                │  └─────────────────────────────────────────────────┘   │
                │                                                         │
                │  功能:                                                  │
                │  - 应用健康检查                                        │
                │  - 运行指标收集                                        │
                │  - 环境信息查看                                        │
                │  - 日志级别动态调整                                    │
                │  - 线程堆栈查看                                        │
                └─────────────────────────────────────────────────────────┘

                添加依赖:
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-actuator</artifactId>
                </dependency>
                """);

        // 2. 常用端点
        System.out.println("=".repeat(60));
        System.out.println("【2. 常用端点】");
        System.out.println("""
                ┌────────────────────┬────────────────────────────────────┐
                │      端点          │           说明                     │
                ├────────────────────┼────────────────────────────────────┤
                │ /actuator/health   │ 应用健康状态                       │
                │ /actuator/info     │ 应用信息                           │
                │ /actuator/metrics  │ 指标列表                           │
                │ /actuator/env      │ 环境变量                           │
                │ /actuator/beans    │ 所有 Bean 列表                     │
                │ /actuator/mappings │ 所有请求映射                       │
                │ /actuator/configprops│ 配置属性                        │
                │ /actuator/loggers  │ 日志配置 (可动态修改)              │
                │ /actuator/threaddump│ 线程堆栈                         │
                │ /actuator/heapdump │ 堆转储                             │
                │ /actuator/shutdown │ 关闭应用 (默认禁用)                │
                └────────────────────┴────────────────────────────────────┘
                """);

        // 3. 配置端点
        System.out.println("=".repeat(60));
        System.out.println("【3. 端点配置】");
        System.out.println("""
                application.yml:

                management:
                  endpoints:
                    web:
                      exposure:
                        include: "*"  # 暴露所有端点
                        # include: health,info,metrics
                        # exclude: env
                      base-path: /actuator  # 端点基础路径

                  endpoint:
                    health:
                      show-details: always  # 健康详情
                    shutdown:
                      enabled: true  # 启用关闭端点

                  server:
                    port: 8081  # 管理端口独立

                安全建议:
                - 生产环境限制暴露的端点
                - 使用独立端口
                - 添加认证保护
                """);

        // 4. 健康检查
        System.out.println("=".repeat(60));
        System.out.println("【4. 健康检查】");
        System.out.println("""
                /actuator/health 响应:

                {
                    "status": "UP",
                    "components": {
                        "db": {
                            "status": "UP",
                            "details": {
                                "database": "MySQL",
                                "validationQuery": "SELECT 1"
                            }
                        },
                        "diskSpace": {
                            "status": "UP",
                            "details": {
                                "total": 500000000000,
                                "free": 300000000000
                            }
                        },
                        "redis": {
                            "status": "UP"
                        }
                    }
                }

                内置健康指示器:
                - DataSourceHealthIndicator (数据库)
                - DiskSpaceHealthIndicator (磁盘)
                - RedisHealthIndicator (Redis)
                - MongoHealthIndicator (MongoDB)
                - RabbitHealthIndicator (RabbitMQ)
                """);

        // 5. 自定义健康检查
        System.out.println("=".repeat(60));
        System.out.println("【5. 自定义健康检查】");
        System.out.println("""
                @Component
                public class MyServiceHealthIndicator implements HealthIndicator {

                    @Autowired
                    private ExternalService externalService;

                    @Override
                    public Health health() {
                        try {
                            boolean isUp = externalService.ping();
                            if (isUp) {
                                return Health.up()
                                    .withDetail("service", "Available")
                                    .withDetail("latency", "10ms")
                                    .build();
                            } else {
                                return Health.down()
                                    .withDetail("error", "Service not responding")
                                    .build();
                            }
                        } catch (Exception e) {
                            return Health.down(e).build();
                        }
                    }
                }

                访问 /actuator/health:
                {
                    "status": "UP",
                    "components": {
                        "myService": {
                            "status": "UP",
                            "details": {
                                "service": "Available",
                                "latency": "10ms"
                            }
                        }
                    }
                }
                """);

        // 6. Metrics 指标
        System.out.println("=".repeat(60));
        System.out.println("【6. Metrics 指标】");
        System.out.println("""
                /actuator/metrics 返回可用指标列表:
                {
                    "names": [
                        "jvm.memory.used",
                        "jvm.memory.max",
                        "jvm.gc.pause",
                        "http.server.requests",
                        "system.cpu.usage",
                        ...
                    ]
                }

                查看具体指标:
                /actuator/metrics/jvm.memory.used

                {
                    "name": "jvm.memory.used",
                    "measurements": [
                        {"statistic": "VALUE", "value": 123456789}
                    ],
                    "availableTags": [
                        {"tag": "area", "values": ["heap", "nonheap"]}
                    ]
                }

                自定义指标:

                @Component
                public class OrderMetrics {
                    private final Counter orderCounter;

                    public OrderMetrics(MeterRegistry registry) {
                        this.orderCounter = Counter.builder("orders.created")
                            .description("Number of orders created")
                            .register(registry);
                    }

                    public void orderCreated() {
                        orderCounter.increment();
                    }
                }
                """);

        // 7. 与监控系统集成
        System.out.println("=".repeat(60));
        System.out.println("【7. 监控系统集成】");
        System.out.println("""
                Prometheus + Grafana:

                <dependency>
                    <groupId>io.micrometer</groupId>
                    <artifactId>micrometer-registry-prometheus</artifactId>
                </dependency>

                访问 /actuator/prometheus:
                # HELP jvm_memory_used_bytes
                # TYPE jvm_memory_used_bytes gauge
                jvm_memory_used_bytes{area="heap"} 1.23456789E8
                jvm_memory_used_bytes{area="nonheap"} 5.6789E7

                其他监控系统:
                - Datadog
                - New Relic
                - InfluxDB
                - Dynatrace
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Actuator 提供生产级监控能力");
        System.out.println("💡 生产环境注意端点安全");
        System.out.println("💡 配合 Prometheus + Grafana 实现可视化监控");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Actuator 端点:
 * - health, metrics, info, env
 * - beans, mappings, loggers
 * 
 * 2. 健康检查:
 * - 内置健康指示器
 * - 自定义 HealthIndicator
 * 
 * 3. Metrics:
 * - JVM/HTTP 指标
 * - 自定义指标
 * - Prometheus 集成
 */
