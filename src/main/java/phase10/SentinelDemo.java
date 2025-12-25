package phase10;

/**
 * Phase 10 - Lesson 5: Sentinel 熔断与限流
 * 
 * 🎯 学习目标:
 * 1. 理解服务雪崩问题
 * 2. 掌握 Sentinel 的核心功能
 * 3. 学会配置熔断降级和限流规则
 */
public class SentinelDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 10 - Lesson 5: Sentinel 熔断与限流");
        System.out.println("=".repeat(60));

        // 1. 服务雪崩问题
        System.out.println("\n【1. 服务雪崩问题】");
        System.out.println("""
                场景: 服务调用链

                ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐
                │ 网关   │──▶│ 订单   │──▶│ 库存   │──▶│ 数据库 │
                └────────┘   └────────┘   └────────┘   └────────┘
                                                           ❌ 故障

                雪崩过程:
                1. 数据库故障，库存服务阻塞
                2. 订单服务等待库存响应，线程耗尽
                3. 网关等待订单响应，请求堆积
                4. 整个系统不可用

                ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐
                │ 网关 ❌│──▶│ 订单 ❌│──▶│ 库存 ❌│──▶│ 数据库❌│
                └────────┘   └────────┘   └────────┘   └────────┘
                  请求堆积     线程耗尽     阻塞等待       故障源

                解决方案:
                ┌─────────────────────────────────────────────────────────┐
                │ 1. 熔断降级 - 快速失败，不等待                          │
                │ 2. 限流 - 控制请求速率                                   │
                │ 3. 隔离 - 隔离故障服务                                   │
                │ 4. 超时 - 设置合理超时时间                               │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. Sentinel 简介
        System.out.println("=".repeat(60));
        System.out.println("【2. Sentinel 简介】");
        System.out.println("""
                Sentinel = 面向分布式微服务的流量控制组件

                核心功能:
                ┌─────────────────────────────────────────────────────────┐
                │                     Sentinel                             │
                ├─────────────────┬─────────────────┬─────────────────────┤
                │     流量控制    │     熔断降级     │      系统保护       │
                ├─────────────────┼─────────────────┼─────────────────────┤
                │ - QPS 限流      │ - 慢调用比例    │ - CPU 使用率        │
                │ - 线程数限流    │ - 异常比例      │ - 系统负载           │
                │ - 排队等待      │ - 异常数        │ - 入口 QPS          │
                └─────────────────┴─────────────────┴─────────────────────┘

                对比 Hystrix:
                ┌──────────────────┬────────────┬────────────┐
                │       特性       │  Sentinel  │  Hystrix   │
                ├──────────────────┼────────────┼────────────┤
                │ 熔断策略         │ 多种       │ 仅异常比例 │
                │ 限流             │ ✅ 丰富    │ ❌ 不支持  │
                │ 控制台           │ ✅ 可视化  │ ❌ 简陋    │
                │ 规则持久化       │ ✅ 多种    │ ❌ 不支持  │
                │ 维护状态         │ 活跃       │ 停止维护   │
                └──────────────────┴────────────┴────────────┘
                """);

        // 3. 快速开始
        System.out.println("=".repeat(60));
        System.out.println("【3. 快速开始】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
                </dependency>

                2. 配置 Sentinel 控制台

                spring:
                  cloud:
                    sentinel:
                      transport:
                        dashboard: localhost:8080  # 控制台地址
                        port: 8719                 # 客户端端口

                3. 启动 Sentinel 控制台

                # 下载
                wget https://github.com/alibaba/Sentinel/releases/download/1.8.6/sentinel-dashboard-1.8.6.jar

                # 启动
                java -Dserver.port=8080 -jar sentinel-dashboard-1.8.6.jar

                # 访问
                http://localhost:8080
                # 默认账号: sentinel / sentinel

                4. 定义资源 (自动)

                @RestController
                public class OrderController {

                    @GetMapping("/orders/{id}")
                    public Order getOrder(@PathVariable Long id) {
                        // 接口自动成为 Sentinel 资源
                        return orderService.findById(id);
                    }
                }
                """);

        // 4. 流量控制 (限流)
        System.out.println("=".repeat(60));
        System.out.println("【4. 流量控制 (限流)】");
        System.out.println("""
                限流策略:

                1. QPS 限流 - 每秒请求数
                   ┌─────────────────────────────────────────────────┐
                   │  阈值: 100 QPS                                  │
                   │  第 1-100 请求 → 通过                           │
                   │  第 101+ 请求 → 拒绝 (快速失败)                 │
                   └─────────────────────────────────────────────────┘

                2. 线程数限流 - 并发线程数
                   ┌─────────────────────────────────────────────────┐
                   │  阈值: 10 线程                                  │
                   │  当前有 10 个请求在处理                         │
                   │  新请求 → 拒绝                                  │
                   │  适用: 慢接口保护                               │
                   └─────────────────────────────────────────────────┘

                流控效果:

                1. 快速失败 (默认)
                   超过阈值直接拒绝

                2. Warm Up (预热)
                   冷启动，逐渐增加阈值
                   适用: 秒杀场景，防止瞬间流量冲击

                3. 排队等待
                   请求排队等待，匀速通过
                   适用: 削峰填谷

                代码配置:

                @PostConstruct
                public void initFlowRules() {
                    List<FlowRule> rules = new ArrayList<>();

                    FlowRule rule = new FlowRule();
                    rule.setResource("GET:/orders/{id}");
                    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
                    rule.setCount(100);  // QPS 阈值

                    rules.add(rule);
                    FlowRuleManager.loadRules(rules);
                }
                """);

        // 5. 熔断降级
        System.out.println("=".repeat(60));
        System.out.println("【5. 熔断降级】");
        System.out.println("""
                熔断器状态:

                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │   ┌────────┐  触发条件   ┌────────┐  熔断时间结束      │
                │   │  关闭  │ ──────────▶ │  打开  │ ──────────┐       │
                │   │ CLOSED │             │  OPEN  │           │       │
                │   └────────┘             └────────┘           │       │
                │       ▲                       │               │       │
                │       │                       │               ▼       │
                │       │                  ┌─────────┐                  │
                │       └────────────────── │ 半开   │                  │
                │         探测请求成功       │HALF-OPEN│ ◀──────────────┘│
                │                           └─────────┘                  │
                │                              │                         │
                │                    探测请求失败 → 回到 OPEN             │
                └─────────────────────────────────────────────────────────┘

                熔断策略:

                1. 慢调用比例
                   - 统计慢调用比例
                   - 超过阈值触发熔断
                   - 适用: 关注响应时间

                   rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
                   rule.setCount(0.5);           // 比例阈值 50%
                   rule.setSlowRatioThreshold(1000); // 慢调用响应时间 1s

                2. 异常比例
                   - 统计异常请求比例
                   - 超过阈值触发熔断

                   rule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
                   rule.setCount(0.5);  // 异常比例 50%

                3. 异常数
                   - 统计异常请求数量
                   - 超过阈值触发熔断

                   rule.setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType());
                   rule.setCount(5);  // 异常数 5

                熔断参数:
                - 熔断时长: 熔断后多久尝试恢复
                - 最小请求数: 触发熔断的最小请求数
                - 统计时长: 统计窗口时间
                """);

        // 6. 注解方式
        System.out.println("=".repeat(60));
        System.out.println("【6. 注解方式】");
        System.out.println("""
                使用 @SentinelResource 定义资源:

                @Service
                public class OrderService {

                    @SentinelResource(
                        value = "createOrder",
                        blockHandler = "createOrderBlockHandler",
                        fallback = "createOrderFallback"
                    )
                    public Order createOrder(OrderRequest request) {
                        // 业务逻辑
                        return orderRepository.save(order);
                    }

                    // BlockHandler: 限流或熔断时调用
                    // 参数需要与原方法相同，多一个 BlockException
                    public Order createOrderBlockHandler(OrderRequest request, BlockException e) {
                        throw new ServiceException("系统繁忙，请稍后再试");
                    }

                    // Fallback: 业务异常时调用
                    // 参数需要与原方法相同，可多一个 Throwable
                    public Order createOrderFallback(OrderRequest request, Throwable t) {
                        log.error("创建订单失败", t);
                        throw new ServiceException("创建订单失败，请稍后再试");
                    }
                }

                Handler 可以放在单独的类中:

                public class OrderBlockHandler {

                    // 必须是 static 方法
                    public static Order createOrderBlockHandler(OrderRequest request, BlockException e) {
                        ...
                    }
                }

                @SentinelResource(
                    value = "createOrder",
                    blockHandlerClass = OrderBlockHandler.class,
                    blockHandler = "createOrderBlockHandler"
                )
                """);

        // 7. Feign 整合 Sentinel
        System.out.println("=".repeat(60));
        System.out.println("【7. Feign 整合 Sentinel】");
        System.out.println("""
                1. 开启 Feign 对 Sentinel 的支持

                feign:
                  sentinel:
                    enabled: true

                2. 定义 Fallback

                @Component
                public class UserClientFallback implements UserClient {

                    @Override
                    public User findById(Long id) {
                        return User.builder()
                            .id(id)
                            .name("服务降级: 用户信息暂不可用")
                            .build();
                    }
                }

                @FeignClient(name = "user-service", fallback = UserClientFallback.class)
                public interface UserClient {
                    @GetMapping("/users/{id}")
                    User findById(@PathVariable Long id);
                }

                3. 使用 FallbackFactory 获取异常信息

                @Component
                public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

                    @Override
                    public UserClient create(Throwable cause) {
                        return new UserClient() {
                            @Override
                            public User findById(Long id) {
                                log.error("调用 user-service 失败", cause);

                                if (cause instanceof FlowException) {
                                    throw new ServiceException("请求过于频繁");
                                } else if (cause instanceof DegradeException) {
                                    throw new ServiceException("服务熔断中");
                                }

                                throw new ServiceException("服务暂不可用");
                            }
                        };
                    }
                }

                @FeignClient(name = "user-service", fallbackFactory = UserClientFallbackFactory.class)
                public interface UserClient {
                    // ...
                }
                """);

        // 8. 规则持久化
        System.out.println("=".repeat(60));
        System.out.println("【8. 规则持久化】");
        System.out.println("""
                默认规则保存在内存中，重启后丢失

                持久化到 Nacos:

                1. 添加依赖

                <dependency>
                    <groupId>com.alibaba.csp</groupId>
                    <artifactId>sentinel-datasource-nacos</artifactId>
                </dependency>

                2. 配置数据源

                spring:
                  cloud:
                    sentinel:
                      datasource:
                        flow:
                          nacos:
                            server-addr: localhost:8848
                            dataId: ${spring.application.name}-flow-rules
                            groupId: SENTINEL_GROUP
                            data-type: json
                            rule-type: flow
                        degrade:
                          nacos:
                            server-addr: localhost:8848
                            dataId: ${spring.application.name}-degrade-rules
                            groupId: SENTINEL_GROUP
                            data-type: json
                            rule-type: degrade

                3. 在 Nacos 中创建配置

                Data ID: order-service-flow-rules
                Group: SENTINEL_GROUP
                内容:
                [
                  {
                    "resource": "GET:/orders/{id}",
                    "limitApp": "default",
                    "grade": 1,
                    "count": 100,
                    "strategy": 0,
                    "controlBehavior": 0
                  }
                ]

                规则会自动从 Nacos 拉取，修改 Nacos 配置会即时生效
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Sentinel 提供限流、熔断、系统保护");
        System.out.println("💡 Feign 整合 Sentinel 实现服务降级");
        System.out.println("💡 生产环境务必持久化规则到 Nacos");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 服务雪崩:
 * - 链路故障蔓延
 * - 熔断降级保护
 * 
 * 2. 限流策略:
 * - QPS 限流
 * - 线程数限流
 * - 预热、排队
 * 
 * 3. 熔断策略:
 * - 慢调用比例
 * - 异常比例
 * - 异常数
 * 
 * 4. 使用方式:
 * - @SentinelResource
 * - Feign fallback
 * - Nacos 持久化
 */
