package phase10;

/**
 * Phase 10 - Lesson 3: OpenFeign 声明式服务调用
 * 
 * 🎯 学习目标:
 * 1. 理解 Feign 的作用和原理
 * 2. 掌握 OpenFeign 的使用
 * 3. 了解 Feign 的高级配置
 */
public class OpenFeignDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 10 - Lesson 3: OpenFeign 声明式服务调用");
        System.out.println("=".repeat(60));

        // 1. 为什么需要 Feign
        System.out.println("\n【1. 为什么需要 Feign】");
        System.out.println("""
                传统 RestTemplate 调用的问题:

                @Service
                public class OrderService {
                    @Autowired
                    private RestTemplate restTemplate;

                    public User getUser(Long id) {
                        String url = "http://user-service/users/" + id;
                        return restTemplate.getForObject(url, User.class);
                    }

                    public User createUser(User user) {
                        String url = "http://user-service/users";
                        return restTemplate.postForObject(url, user, User.class);
                    }
                }

                问题:
                1. URL 硬编码，容易出错
                2. 需要手动拼接参数
                3. 代码冗余，不够优雅
                4. 缺乏类型安全

                Feign 解决方案:

                @FeignClient("user-service")
                public interface UserClient {

                    @GetMapping("/users/{id}")
                    User getUser(@PathVariable Long id);

                    @PostMapping("/users")
                    User createUser(@RequestBody User user);
                }

                优点:
                ✅ 接口定义，类似 Controller
                ✅ 声明式调用，代码简洁
                ✅ 与 Spring MVC 注解一致
                ✅ 集成负载均衡
                """);

        // 2. 快速开始
        System.out.println("=".repeat(60));
        System.out.println("【2. 快速开始】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-openfeign</artifactId>
                </dependency>

                2. 开启 Feign 客户端

                @SpringBootApplication
                @EnableFeignClients  // 开启 Feign
                public class OrderServiceApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(OrderServiceApplication.class, args);
                    }
                }

                3. 定义 Feign 接口

                @FeignClient(name = "user-service")  // 服务名
                public interface UserClient {

                    @GetMapping("/users/{id}")
                    User findById(@PathVariable("id") Long id);

                    @GetMapping("/users")
                    List<User> findAll();

                    @PostMapping("/users")
                    User create(@RequestBody User user);

                    @PutMapping("/users/{id}")
                    User update(@PathVariable("id") Long id, @RequestBody User user);

                    @DeleteMapping("/users/{id}")
                    void delete(@PathVariable("id") Long id);
                }

                4. 使用 Feign 客户端

                @RestController
                @RequiredArgsConstructor
                public class OrderController {

                    private final UserClient userClient;

                    @GetMapping("/orders/{orderId}/user")
                    public User getOrderUser(@PathVariable Long orderId) {
                        Order order = orderService.findById(orderId);
                        // 像本地方法一样调用远程服务
                        return userClient.findById(order.getUserId());
                    }
                }
                """);

        // 3. 参数传递
        System.out.println("=".repeat(60));
        System.out.println("【3. 参数传递方式】");
        System.out.println("""
                1. 路径变量 @PathVariable

                @GetMapping("/users/{id}")
                User findById(@PathVariable("id") Long id);

                2. 请求参数 @RequestParam

                @GetMapping("/users")
                List<User> search(@RequestParam("name") String name,
                                  @RequestParam("age") Integer age);

                3. 请求体 @RequestBody

                @PostMapping("/users")
                User create(@RequestBody User user);

                4. 请求头 @RequestHeader

                @GetMapping("/users/me")
                User getCurrentUser(@RequestHeader("Authorization") String token);

                5. 表单数据 @SpringQueryMap

                @GetMapping("/users/search")
                List<User> search(@SpringQueryMap UserQuery query);

                // UserQuery 会被展开为查询参数
                public class UserQuery {
                    private String name;
                    private Integer age;
                    private String status;
                }
                // 请求: /users/search?name=xxx&age=18&status=ACTIVE
                """);

        // 4. 自定义配置
        System.out.println("=".repeat(60));
        System.out.println("【4. 自定义配置】");
        System.out.println("""
                配置类方式:

                @Configuration
                public class FeignConfig {

                    // 日志级别
                    @Bean
                    public Logger.Level feignLoggerLevel() {
                        return Logger.Level.FULL;
                        // NONE: 不记录
                        // BASIC: 请求方法、URL、响应状态码、执行时间
                        // HEADERS: BASIC + 请求/响应头
                        // FULL: HEADERS + 请求/响应体
                    }

                    // 连接超时和读取超时
                    @Bean
                    public Request.Options options() {
                        return new Request.Options(
                            5, TimeUnit.SECONDS,   // 连接超时
                            10, TimeUnit.SECONDS,  // 读取超时
                            true                   // 重定向
                        );
                    }

                    // 自定义拦截器 (添加请求头)
                    @Bean
                    public RequestInterceptor authInterceptor() {
                        return template -> {
                            template.header("X-Service-Name", "order-service");
                            // 传递认证信息
                            String token = getTokenFromContext();
                            if (token != null) {
                                template.header("Authorization", "Bearer " + token);
                            }
                        };
                    }
                }

                YAML 配置方式:

                feign:
                  client:
                    config:
                      default:  # 全局配置
                        connect-timeout: 5000
                        read-timeout: 10000
                        logger-level: FULL
                      user-service:  # 针对特定服务
                        connect-timeout: 3000
                        read-timeout: 5000
                """);

        // 5. 日志配置
        System.out.println("=".repeat(60));
        System.out.println("【5. 日志配置】");
        System.out.println("""
                1. 配置 Feign 日志级别

                feign:
                  client:
                    config:
                      default:
                        logger-level: FULL

                2. 配置 Spring 日志级别

                logging:
                  level:
                    com.example.feign: DEBUG  # Feign 接口所在包

                3. 日志输出示例

                ---> POST http://user-service/users HTTP/1.1
                Content-Type: application/json
                {"name":"张三","email":"zhangsan@example.com"}

                <--- HTTP/1.1 201 Created (120ms)
                Content-Type: application/json
                {"id":1,"name":"张三","email":"zhangsan@example.com"}
                """);

        // 6. 降级处理
        System.out.println("=".repeat(60));
        System.out.println("【6. 降级处理 (Fallback)】");
        System.out.println("""
                当服务调用失败时，执行降级逻辑:

                1. 开启 Sentinel 支持

                feign:
                  sentinel:
                    enabled: true

                2. 定义 Fallback 类

                @Component
                public class UserClientFallback implements UserClient {

                    @Override
                    public User findById(Long id) {
                        // 返回默认值
                        return User.builder()
                            .id(id)
                            .name("未知用户")
                            .build();
                    }

                    @Override
                    public List<User> findAll() {
                        return Collections.emptyList();
                    }

                    @Override
                    public User create(User user) {
                        throw new ServiceUnavailableException("用户服务不可用");
                    }
                }

                3. 指定 Fallback

                @FeignClient(name = "user-service",
                             fallback = UserClientFallback.class)
                public interface UserClient {
                    // ...
                }

                4. 获取异常信息 (FallbackFactory)

                @Component
                public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

                    @Override
                    public UserClient create(Throwable cause) {
                        return new UserClient() {
                            @Override
                            public User findById(Long id) {
                                log.error("调用 user-service 失败: {}", cause.getMessage());
                                return null;
                            }
                        };
                    }
                }

                @FeignClient(name = "user-service",
                             fallbackFactory = UserClientFallbackFactory.class)
                public interface UserClient {
                    // ...
                }
                """);

        // 7. 最佳实践
        System.out.println("=".repeat(60));
        System.out.println("【7. 最佳实践】");
        System.out.println("""
                1. 独立模块管理 Feign 接口

                项目结构:
                ├── user-service-api/          (接口模块)
                │   ├── src/main/java/
                │   │   └── com/example/user/api/
                │   │       ├── UserClient.java
                │   │       └── dto/
                │   │           ├── UserRequest.java
                │   │           └── UserResponse.java
                │   └── pom.xml
                │
                ├── user-service/              (服务提供者)
                │   └── 依赖 user-service-api
                │
                └── order-service/             (服务消费者)
                    └── 依赖 user-service-api

                好处:
                - 接口复用
                - 类型安全
                - 更新方便

                2. 合理设置超时

                feign:
                  client:
                    config:
                      default:
                        connect-timeout: 2000  # 连接超时 2s
                        read-timeout: 5000     # 读取超时 5s
                      payment-service:
                        read-timeout: 30000    # 支付服务可以更长

                3. 避免循环依赖

                ❌ A 调用 B，B 调用 A
                ✅ 引入中间服务或消息队列解耦
                """);

        // 8. 常见问题
        System.out.println("=".repeat(60));
        System.out.println("【8. 常见问题】");
        System.out.println("""
                1. @PathVariable 需要指定 value

                // 错误
                User findById(@PathVariable Long id);

                // 正确
                User findById(@PathVariable("id") Long id);

                2. GET 请求不能使用 @RequestBody

                // 错误
                @GetMapping("/users")
                List<User> search(@RequestBody UserQuery query);

                // 正确 - 使用 @SpringQueryMap
                @GetMapping("/users")
                List<User> search(@SpringQueryMap UserQuery query);

                3. 文件上传

                @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
                String upload(@RequestPart("file") MultipartFile file);

                4. 继承同一个接口

                // API 模块
                public interface UserApi {
                    @GetMapping("/users/{id}")
                    User findById(@PathVariable("id") Long id);
                }

                // 消费者 (Feign)
                @FeignClient("user-service")
                public interface UserClient extends UserApi {}

                // 提供者 (Controller)
                @RestController
                public class UserController implements UserApi {
                    @Override
                    public User findById(Long id) { ... }
                }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Feign 让服务调用像调用本地方法一样简单");
        System.out.println("💡 记得配置超时和降级处理");
        System.out.println("💡 推荐独立模块管理 Feign 接口");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. OpenFeign 使用:
 * - @EnableFeignClients 开启
 * - @FeignClient 定义接口
 * - 使用 Spring MVC 注解
 * 
 * 2. 参数传递:
 * - @PathVariable, @RequestParam
 * - @RequestBody, @RequestHeader
 * - @SpringQueryMap
 * 
 * 3. 配置:
 * - 日志级别
 * - 超时时间
 * - 拦截器
 * 
 * 4. 降级处理:
 * - fallback
 * - fallbackFactory
 */
