package phase10;

/**
 * Phase 10 - 实战项目: 微服务电商系统
 * 
 * 🎯 项目目标:
 * 1. 综合运用微服务各组件
 * 2. 实现一个完整的电商系统
 * 3. 掌握微服务项目结构和开发流程
 */
public class MicroserviceProject {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   🚀 微服务电商系统 - 实战项目指南");
        System.out.println("=".repeat(60));

        // 1. 项目架构
        System.out.println("\n【1. 项目架构】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────────────┐
                │                    微服务电商系统架构                            │
                ├─────────────────────────────────────────────────────────────────┤
                │                                                                 │
                │    ┌─────────────┐                                             │
                │    │   客户端    │  Web / App / 小程序                          │
                │    └──────┬──────┘                                             │
                │           │                                                     │
                │           ▼                                                     │
                │    ┌─────────────┐                                             │
                │    │  Gateway    │  路由、认证、限流                            │
                │    │   :8080     │                                             │
                │    └──────┬──────┘                                             │
                │           │                                                     │
                │    ┌──────┼──────┬──────────┬──────────┐                       │
                │    ▼      ▼      ▼          ▼          ▼                       │
                │ ┌──────┐┌──────┐┌──────┐┌────────┐┌────────┐                  │
                │ │ User ││Order ││Product││Inventory││ Pay    │                 │
                │ │:8081 ││:8082 ││:8083  ││ :8084  ││ :8085  │                 │
                │ └──────┘└──────┘└──────┘└────────┘└────────┘                  │
                │    │      │       │         │          │                       │
                │    └──────┴───────┴─────────┴──────────┘                       │
                │                      │                                          │
                │    ┌─────────────────┴─────────────────┐                       │
                │    ▼                                   ▼                       │
                │ ┌────────────────────┐  ┌────────────────────┐                 │
                │ │      Nacos         │  │     Sentinel       │                 │
                │ │ 注册中心 + 配置中心 │  │   熔断限流控制台   │                 │
                │ └────────────────────┘  └────────────────────┘                 │
                │                                                                 │
                │ ┌─────────────────────────────────────────────────────────┐    │
                │ │                   基础设施                               │    │
                │ │  MySQL  │  Redis  │  RocketMQ  │  Elasticsearch        │    │
                │ └─────────────────────────────────────────────────────────┘    │
                └─────────────────────────────────────────────────────────────────┘
                """);

        // 2. 服务规划
        System.out.println("=".repeat(60));
        System.out.println("【2. 服务规划】");
        System.out.println("""
                ┌────────────────────┬────────────────────────────────────┐
                │       服务         │              职责                  │
                ├────────────────────┼────────────────────────────────────┤
                │ gateway-service    │ API 网关，路由转发，认证          │
                │ user-service       │ 用户注册、登录、信息管理          │
                │ product-service    │ 商品管理、分类、搜索              │
                │ order-service      │ 订单创建、查询、状态管理          │
                │ inventory-service  │ 库存管理、扣减、预占              │
                │ payment-service    │ 支付处理、退款                    │
                │ notification-svc   │ 消息通知、短信、邮件              │
                └────────────────────┴────────────────────────────────────┘

                公共模块:
                ├── common-core      │ 通用工具类、异常、响应封装
                ├── common-security  │ JWT 认证、权限注解
                └── service-api      │ Feign 接口、DTO 定义
                """);

        // 3. 项目结构
        System.out.println("=".repeat(60));
        System.out.println("【3. 项目结构】");
        System.out.println("""
                mall-microservice/
                ├── pom.xml                          # 父 POM
                │
                ├── mall-common/                     # 公共模块
                │   ├── mall-common-core/            # 核心工具
                │   ├── mall-common-security/        # 安全模块
                │   └── mall-common-redis/           # Redis 封装
                │
                ├── mall-api/                        # Feign 接口
                │   ├── user-api/
                │   ├── product-api/
                │   ├── order-api/
                │   └── inventory-api/
                │
                ├── mall-gateway/                    # 网关服务
                │   ├── src/
                │   └── pom.xml
                │
                ├── mall-service/                    # 业务服务
                │   ├── user-service/
                │   │   ├── src/main/java/
                │   │   │   └── com/mall/user/
                │   │   │       ├── UserApplication.java
                │   │   │       ├── controller/
                │   │   │       ├── service/
                │   │   │       ├── repository/
                │   │   │       ├── entity/
                │   │   │       └── config/
                │   │   └── src/main/resources/
                │   │       └── bootstrap.yml
                │   │
                │   ├── product-service/
                │   ├── order-service/
                │   ├── inventory-service/
                │   └── payment-service/
                │
                └── doc/                             # 文档
                    ├── sql/
                    └── api/
                """);

        // 4. 父 POM 配置
        System.out.println("=".repeat(60));
        System.out.println("【4. 父 POM 配置】");
        System.out.println("""
                <?xml version="1.0" encoding="UTF-8"?>
                <project>
                    <modelVersion>4.0.0</modelVersion>

                    <parent>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-parent</artifactId>
                        <version>3.2.0</version>
                    </parent>

                    <groupId>com.mall</groupId>
                    <artifactId>mall-microservice</artifactId>
                    <version>1.0.0</version>
                    <packaging>pom</packaging>

                    <properties>
                        <java.version>17</java.version>
                        <spring-cloud.version>2023.0.0</spring-cloud.version>
                        <spring-cloud-alibaba.version>2022.0.0.0</spring-cloud-alibaba.version>
                    </properties>

                    <dependencyManagement>
                        <dependencies>
                            <!-- Spring Cloud -->
                            <dependency>
                                <groupId>org.springframework.cloud</groupId>
                                <artifactId>spring-cloud-dependencies</artifactId>
                                <version>${spring-cloud.version}</version>
                                <type>pom</type>
                                <scope>import</scope>
                            </dependency>

                            <!-- Spring Cloud Alibaba -->
                            <dependency>
                                <groupId>com.alibaba.cloud</groupId>
                                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                                <version>${spring-cloud-alibaba.version}</version>
                                <type>pom</type>
                                <scope>import</scope>
                            </dependency>
                        </dependencies>
                    </dependencyManagement>

                    <modules>
                        <module>mall-common</module>
                        <module>mall-api</module>
                        <module>mall-gateway</module>
                        <module>mall-service</module>
                    </modules>
                </project>
                """);

        // 5. 用户服务示例
        System.out.println("=".repeat(60));
        System.out.println("【5. 用户服务示例】");
        System.out.println("""
                // UserApplication.java
                @SpringBootApplication
                @EnableDiscoveryClient
                @EnableFeignClients
                public class UserApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(UserApplication.class, args);
                    }
                }

                // entity/User.java
                @Entity
                @Table(name = "users")
                @Data
                public class User {
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                    @Column(unique = true)
                    private String username;

                    private String password;

                    private String phone;

                    private String email;

                    @Enumerated(EnumType.STRING)
                    private UserStatus status;

                    private LocalDateTime createdAt;
                }

                // controller/UserController.java
                @RestController
                @RequestMapping("/users")
                @RequiredArgsConstructor
                public class UserController {

                    private final UserService userService;

                    @PostMapping("/register")
                    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
                        return Result.success(userService.register(request));
                    }

                    @PostMapping("/login")
                    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
                        return Result.success(userService.login(request));
                    }

                    @GetMapping("/{id}")
                    public Result<UserVO> findById(@PathVariable Long id) {
                        return Result.success(userService.findById(id));
                    }
                }

                // bootstrap.yml
                spring:
                  application:
                    name: user-service
                  cloud:
                    nacos:
                      discovery:
                        server-addr: ${NACOS_ADDR:localhost:8848}
                      config:
                        server-addr: ${NACOS_ADDR:localhost:8848}
                        file-extension: yaml
                """);

        // 6. Feign 接口模块
        System.out.println("=".repeat(60));
        System.out.println("【6. Feign 接口模块】");
        System.out.println("""
                // user-api 模块

                // UserClient.java
                @FeignClient(name = "user-service", fallbackFactory = UserClientFallbackFactory.class)
                public interface UserClient {

                    @GetMapping("/users/{id}")
                    Result<UserVO> findById(@PathVariable("id") Long id);

                    @GetMapping("/users/batch")
                    Result<List<UserVO>> findByIds(@RequestParam("ids") List<Long> ids);
                }

                // UserVO.java
                @Data
                public class UserVO {
                    private Long id;
                    private String username;
                    private String phone;
                    private String avatar;
                }

                // UserClientFallbackFactory.java
                @Component
                public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

                    @Override
                    public UserClient create(Throwable cause) {
                        return new UserClient() {
                            @Override
                            public Result<UserVO> findById(Long id) {
                                log.error("调用用户服务失败", cause);
                                return Result.fail("用户服务暂不可用");
                            }

                            @Override
                            public Result<List<UserVO>> findByIds(List<Long> ids) {
                                return Result.fail("用户服务暂不可用");
                            }
                        };
                    }
                }

                // 订单服务中使用
                @Service
                @RequiredArgsConstructor
                public class OrderServiceImpl implements OrderService {

                    private final UserClient userClient;
                    private final ProductClient productClient;
                    private final InventoryClient inventoryClient;

                    @Override
                    @Transactional
                    public OrderVO createOrder(CreateOrderRequest request) {
                        // 1. 查询用户信息
                        UserVO user = userClient.findById(request.getUserId()).getData();

                        // 2. 查询商品信息
                        ProductVO product = productClient.findById(request.getProductId()).getData();

                        // 3. 扣减库存
                        inventoryClient.deduct(request.getProductId(), request.getQuantity());

                        // 4. 创建订单
                        Order order = new Order();
                        order.setUserId(user.getId());
                        order.setProductId(product.getId());
                        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
                        orderRepository.save(order);

                        return toVO(order);
                    }
                }
                """);

        // 7. 网关配置
        System.out.println("=".repeat(60));
        System.out.println("【7. 网关配置】");
        System.out.println("""
                # gateway bootstrap.yml

                spring:
                  application:
                    name: gateway-service
                  cloud:
                    nacos:
                      discovery:
                        server-addr: ${NACOS_ADDR:localhost:8848}
                      config:
                        server-addr: ${NACOS_ADDR:localhost:8848}
                        file-extension: yaml
                    gateway:
                      routes:
                        - id: user-service
                          uri: lb://user-service
                          predicates:
                            - Path=/api/users/**
                          filters:
                            - StripPrefix=1

                        - id: product-service
                          uri: lb://product-service
                          predicates:
                            - Path=/api/products/**
                          filters:
                            - StripPrefix=1

                        - id: order-service
                          uri: lb://order-service
                          predicates:
                            - Path=/api/orders/**
                          filters:
                            - StripPrefix=1

                      globalcors:
                        cors-configurations:
                          '[/**]':
                            allowed-origins: "*"
                            allowed-methods: "*"
                            allowed-headers: "*"

                server:
                  port: 8080
                """);

        // 8. 启动顺序
        System.out.println("=".repeat(60));
        System.out.println("【8. 启动顺序】");
        System.out.println("""
                1. 基础设施

                # 启动 Nacos
                docker run -d --name nacos -p 8848:8848 -p 9848:9848 \\
                  -e MODE=standalone nacos/nacos-server:v2.2.3

                # 启动 MySQL
                docker run -d --name mysql -p 3306:3306 \\
                  -e MYSQL_ROOT_PASSWORD=123456 mysql:8.0

                # 启动 Redis
                docker run -d --name redis -p 6379:6379 redis:7

                # 启动 Sentinel 控制台
                java -jar sentinel-dashboard.jar

                2. 在 Nacos 中创建配置

                # 各服务配置
                user-service.yaml
                product-service.yaml
                order-service.yaml
                ...

                3. 启动微服务

                # 启动顺序
                1. gateway-service
                2. user-service
                3. product-service
                4. inventory-service
                5. order-service
                6. payment-service

                4. 验证

                # 查看服务列表
                http://localhost:8848/nacos

                # 测试 API
                curl http://localhost:8080/api/users/1
                """);

        // 9. 开发建议
        System.out.println("=".repeat(60));
        System.out.println("【9. 开发建议】");
        System.out.println("""
                1. 渐进式开发

                Phase 1: 基础服务
                - 搭建基础框架
                - 实现用户服务和商品服务
                - 配置网关路由

                Phase 2: 核心业务
                - 实现订单服务
                - 实现库存服务
                - 服务间调用

                Phase 3: 高可用
                - 添加 Sentinel 熔断限流
                - 配置降级处理
                - 压力测试

                Phase 4: 运维
                - 日志聚合
                - 链路追踪
                - 监控告警

                2. 代码规范

                - 统一异常处理
                - 统一响应格式
                - 参数校验
                - 日志规范

                3. 数据库设计

                - 每个服务独立数据库
                - 避免跨服务 JOIN
                - 通过 API 获取其他服务数据

                4. 分布式事务

                - 优先本地事务
                - 使用消息队列保证最终一致性
                - 关键业务使用 Seata
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 微服务项目需要渐进式开发");
        System.out.println("💡 使用 Feign API 模块实现类型安全调用");
        System.out.println("💡 每个服务独立数据库，避免耦合");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 项目总结:
 * 
 * 1. 项目架构:
 * - Gateway 统一入口
 * - 业务服务独立部署
 * - Nacos 服务治理
 * 
 * 2. 模块划分:
 * - common: 公共模块
 * - api: Feign 接口
 * - service: 业务服务
 * 
 * 3. 关键配置:
 * - bootstrap.yml
 * - 网关路由
 * - Feign + Sentinel
 * 
 * 4. 开发流程:
 * - 基础服务 → 核心业务 → 高可用 → 运维
 */
