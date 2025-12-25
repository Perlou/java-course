package phase10;

/**
 * Phase 10 - Lesson 6: Spring Cloud Gateway
 * 
 * 🎯 学习目标:
 * 1. 理解 API 网关的作用
 * 2. 掌握 Gateway 的路由配置
 * 3. 学会编写自定义过滤器
 */
public class GatewayDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 10 - Lesson 6: Spring Cloud Gateway");
        System.out.println("=".repeat(60));

        // 1. 为什么需要 API 网关
        System.out.println("\n【1. 为什么需要 API 网关】");
        System.out.println("""
                没有网关的问题:

                ┌────────┐   ┌──────────────┐   ┌────────────┐
                │ 客户端 │   │ user-service │   │直接暴露服务│
                │        │──▶│ :8081        │   │需要知道IP  │
                │        │   ├──────────────┤   │认证分散    │
                │        │──▶│ order-service│   │跨域问题    │
                │        │   │ :8082        │   │安全隐患    │
                │        │──▶├──────────────┤   │            │
                │        │   │ product-svc  │   └────────────┘
                └────────┘   │ :8083        │
                             └──────────────┘

                使用网关:

                ┌────────┐   ┌────────────┐   ┌──────────────┐
                │ 客户端 │──▶│  Gateway   │──▶│ user-service │
                │        │   │  :8080     │──▶│ order-service│
                │ 只需访问│   │            │──▶│ product-svc  │
                │ 网关地址│   │ 统一入口   │   └──────────────┘
                └────────┘   │ 统一认证   │
                             │ 统一鉴权   │
                             │ 路由转发   │
                             │ 限流熔断   │
                             │ 日志监控   │
                             └────────────┘

                API 网关核心功能:
                1. 路由转发 - 根据请求路径转发到对应服务
                2. 认证鉴权 - 统一的登录认证和权限校验
                3. 限流熔断 - 保护后端服务
                4. 日志监控 - 统一日志和链路追踪
                5. 跨域处理 - 统一处理 CORS
                """);

        // 2. Gateway 简介
        System.out.println("=".repeat(60));
        System.out.println("【2. Gateway 简介】");
        System.out.println("""
                Spring Cloud Gateway 是基于 WebFlux 的 API 网关

                核心概念:
                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │   Route (路由)                                          │
                │   ├── ID: 路由唯一标识                                 │
                │   ├── URI: 目标服务地址                                │
                │   ├── Predicates: 断言，匹配条件                       │
                │   └── Filters: 过滤器，处理请求/响应                   │
                │                                                         │
                │   工作流程:                                             │
                │   请求 → Predicate 匹配 → Filters 处理 → 转发到 URI    │
                │                                                         │
                └─────────────────────────────────────────────────────────┘

                对比 Zuul:
                ┌──────────────────┬─────────────────┬───────────────┐
                │       特性       │     Gateway     │     Zuul 1    │
                ├──────────────────┼─────────────────┼───────────────┤
                │ 底层框架         │ WebFlux (响应式)│ Servlet (阻塞)│
                │ 性能             │ 更高            │ 较低          │
                │ 长连接支持       │ WebSocket ✅   │ ❌            │
                │ 维护状态         │ 活跃            │ Zuul 2 活跃   │
                └──────────────────┴─────────────────┴───────────────┘
                """);

        // 3. 快速开始
        System.out.println("=".repeat(60));
        System.out.println("【3. 快速开始】");
        System.out.println("""
                1. 创建 Gateway 项目

                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-gateway</artifactId>
                </dependency>
                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
                </dependency>

                ⚠️ 注意: Gateway 基于 WebFlux，不能引入 spring-boot-starter-web

                2. 配置路由

                spring:
                  application:
                    name: gateway-service
                  cloud:
                    nacos:
                      discovery:
                        server-addr: localhost:8848
                    gateway:
                      routes:
                        # 用户服务路由
                        - id: user-service-route
                          uri: lb://user-service  # lb:// 表示负载均衡
                          predicates:
                            - Path=/api/users/**
                          filters:
                            - StripPrefix=1  # 去掉 /api 前缀

                        # 订单服务路由
                        - id: order-service-route
                          uri: lb://order-service
                          predicates:
                            - Path=/api/orders/**
                          filters:
                            - StripPrefix=1

                3. 请求示例

                客户端请求:
                GET http://gateway:8080/api/users/1

                Gateway 转发:
                GET http://user-service/users/1  (StripPrefix 去掉 /api)
                """);

        // 4. 路由断言 Predicates
        System.out.println("=".repeat(60));
        System.out.println("【4. 路由断言 (Predicates)】");
        System.out.println("""
                内置断言工厂:

                1. Path 路由 (最常用)
                   - Path=/users/**

                2. Method 请求方法
                   - Method=GET,POST

                3. Header 请求头
                   - Header=X-Request-Id, \\d+  # 正则匹配

                4. Query 查询参数
                   - Query=name, .+  # 参数名=name，值匹配正则

                5. Host 主机名
                   - Host=**.example.com

                6. After/Before/Between 时间
                   - After=2024-01-01T00:00:00+08:00[Asia/Shanghai]
                   - Before=2024-12-31T23:59:59+08:00[Asia/Shanghai]
                   - Between=..., ...

                7. Cookie
                   - Cookie=token, .+

                8. RemoteAddr 远程地址
                   - RemoteAddr=192.168.1.0/24

                组合使用:

                routes:
                  - id: user-route
                    uri: lb://user-service
                    predicates:
                      - Path=/api/users/**
                      - Method=GET,POST
                      - Header=X-Token, .+  # 所有条件都要满足
                """);

        // 5. 过滤器 Filters
        System.out.println("=".repeat(60));
        System.out.println("【5. 过滤器 (Filters)】");
        System.out.println("""
                内置过滤器:

                1. 路径相关
                   - StripPrefix=1     # 去掉前缀
                   - AddPrefix=/api    # 添加前缀
                   - RewritePath       # 重写路径

                2. 请求头相关
                   - AddRequestHeader=X-Request-Id, 123
                   - RemoveRequestHeader=Cookie
                   - SetRequestHeader=Host, example.com

                3. 响应头相关
                   - AddResponseHeader=X-Response-Time, 100
                   - RemoveResponseHeader=Server

                4. 重定向
                   - RedirectTo=302, http://example.com

                5. 限流
                   - RequestRateLimiter  # 需要配合 Redis

                6. 熔断
                   - CircuitBreaker=myBreaker

                7. 重试
                   - Retry=3

                示例:

                routes:
                  - id: user-route
                    uri: lb://user-service
                    predicates:
                      - Path=/api/users/**
                    filters:
                      - StripPrefix=1
                      - AddRequestHeader=X-Gateway, true
                      - AddResponseHeader=X-Response-Time, 100
                """);

        // 6. 全局过滤器
        System.out.println("=".repeat(60));
        System.out.println("【6. 全局过滤器】");
        System.out.println("""
                全局过滤器对所有路由生效

                配置方式:

                spring:
                  cloud:
                    gateway:
                      default-filters:  # 默认过滤器
                        - AddResponseHeader=X-Gateway-Version, 1.0

                自定义全局过滤器:

                @Component
                public class AuthGlobalFilter implements GlobalFilter, Ordered {

                    @Override
                    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                        ServerHttpRequest request = exchange.getRequest();
                        String path = request.getURI().getPath();

                        // 白名单路径
                        if (path.contains("/login") || path.contains("/register")) {
                            return chain.filter(exchange);
                        }

                        // 获取 Token
                        String token = request.getHeaders().getFirst("Authorization");

                        if (token == null || !token.startsWith("Bearer ")) {
                            // 未授权
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return response.setComplete();
                        }

                        // 验证 Token
                        try {
                            String userId = jwtUtil.parseToken(token.substring(7));
                            // 将用户信息传递给下游服务
                            ServerHttpRequest newRequest = request.mutate()
                                .header("X-User-Id", userId)
                                .build();
                            return chain.filter(exchange.mutate().request(newRequest).build());
                        } catch (Exception e) {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return response.setComplete();
                        }
                    }

                    @Override
                    public int getOrder() {
                        return -100;  // 值越小优先级越高
                    }
                }
                """);

        // 7. 自定义路由过滤器
        System.out.println("=".repeat(60));
        System.out.println("【7. 自定义路由过滤器】");
        System.out.println("""
                创建自定义过滤器工厂:

                @Component
                public class LoggingGatewayFilterFactory extends
                        AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

                    public LoggingGatewayFilterFactory() {
                        super(Config.class);
                    }

                    @Override
                    public GatewayFilter apply(Config config) {
                        return (exchange, chain) -> {
                            long startTime = System.currentTimeMillis();

                            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                                long endTime = System.currentTimeMillis();
                                if (config.isEnabled()) {
                                    log.info("请求: {} 耗时: {}ms",
                                        exchange.getRequest().getPath(),
                                        endTime - startTime);
                                }
                            }));
                        };
                    }

                    @Data
                    public static class Config {
                        private boolean enabled = true;
                    }
                }

                使用:

                routes:
                  - id: user-route
                    uri: lb://user-service
                    predicates:
                      - Path=/api/users/**
                    filters:
                      - name: Logging
                        args:
                          enabled: true
                """);

        // 8. 跨域配置
        System.out.println("=".repeat(60));
        System.out.println("【8. 跨域配置】");
        System.out.println("""
                YAML 配置:

                spring:
                  cloud:
                    gateway:
                      globalcors:
                        cors-configurations:
                          '[/**]':
                            allowed-origins:
                              - "http://localhost:3000"
                              - "https://example.com"
                            allowed-methods:
                              - GET
                              - POST
                              - PUT
                              - DELETE
                              - OPTIONS
                            allowed-headers: "*"
                            allow-credentials: true
                            max-age: 3600

                代码配置:

                @Configuration
                public class CorsConfig {

                    @Bean
                    public CorsWebFilter corsWebFilter() {
                        CorsConfiguration config = new CorsConfiguration();
                        config.addAllowedOrigin("*");
                        config.addAllowedMethod("*");
                        config.addAllowedHeader("*");

                        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                        source.registerCorsConfiguration("/**", config);

                        return new CorsWebFilter(source);
                    }
                }
                """);

        // 9. Gateway 整合 Sentinel
        System.out.println("=".repeat(60));
        System.out.println("【9. Gateway 整合 Sentinel】");
        System.out.println("""
                添加依赖:

                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>
                </dependency>

                配置:

                spring:
                  cloud:
                    sentinel:
                      transport:
                        dashboard: localhost:8080

                自定义限流响应:

                @Configuration
                public class GatewayConfig {

                    @PostConstruct
                    public void init() {
                        GatewayCallbackManager.setBlockHandler((exchange, t) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            String body = "{\\"code\\":429,\\"message\\":\\"请求过于频繁\\"}";

                            return response.writeWith(Mono.just(
                                response.bufferFactory().wrap(body.getBytes())
                            ));
                        });
                    }
                }

                在 Sentinel 控制台可以配置:
                - API 分组限流
                - 路由限流
                - 自定义 API 限流
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Gateway 是微服务的统一入口");
        System.out.println("💡 路由 = 断言 + 过滤器 + 目标 URI");
        System.out.println("💡 全局过滤器处理认证、日志等横切关注点");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 网关作用:
 * - 统一入口
 * - 认证鉴权
 * - 路由转发
 * - 限流熔断
 * 
 * 2. 核心概念:
 * - Route: 路由
 * - Predicate: 断言
 * - Filter: 过滤器
 * 
 * 3. 常用配置:
 * - Path 断言
 * - StripPrefix 过滤器
 * - 全局过滤器
 * 
 * 4. 高级功能:
 * - 跨域配置
 * - Sentinel 整合
 * - 自定义过滤器
 */
