package phase10;

/**
 * Phase 10 - Lesson 4: 负载均衡
 * 
 * 🎯 学习目标:
 * 1. 理解负载均衡的概念和类型
 * 2. 掌握 Spring Cloud LoadBalancer
 * 3. 了解常见负载均衡策略
 */
public class LoadBalancerDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 10 - Lesson 4: 负载均衡");
        System.out.println("=".repeat(60));

        // 1. 什么是负载均衡
        System.out.println("\n【1. 什么是负载均衡】");
        System.out.println("""
                负载均衡 = 将请求分发到多个服务实例

                场景:
                ┌─────────────────────────────────────────────────────────┐
                │                                                         │
                │  user-service 部署了 3 个实例:                          │
                │    - 192.168.1.101:8080                                │
                │    - 192.168.1.102:8080                                │
                │    - 192.168.1.103:8080                                │
                │                                                         │
                │  order-service 需要调用 user-service                    │
                │  问题: 调用哪个实例?                                    │
                │                                                         │
                │  解决方案 - 负载均衡器:                                  │
                │                                                         │
                │   order-service                                         │
                │       │                                                 │
                │       ▼                                                 │
                │  ┌────────────┐                                        │
                │  │ LoadBalancer│  根据策略选择实例                      │
                │  └─────┬──────┘                                        │
                │        │                                                │
                │   ┌────┴────┬──────────┐                               │
                │   ▼         ▼          ▼                               │
                │ :8080     :8080      :8080                             │
                │ user-1    user-2     user-3                            │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 负载均衡类型
        System.out.println("=".repeat(60));
        System.out.println("【2. 负载均衡类型】");
        System.out.println("""
                1. 服务端负载均衡 (Server-Side)

                ┌────────┐    ┌───────────────┐    ┌─────────┐
                │ Client │───▶│   Nginx/F5   │───▶│ Server  │
                └────────┘    │ (独立硬件/软件)│    └─────────┘
                              └───────────────┘

                特点:
                - 独立的负载均衡器
                - Nginx, HAProxy, F5
                - 客户端不知道后端有多少实例

                2. 客户端负载均衡 (Client-Side) ← 微服务常用

                ┌────────────────────┐         ┌─────────┐
                │      Client        │         │ Server1 │
                │ ┌────────────────┐ │    ┌───▶│         │
                │ │  LoadBalancer  │─┼────┤    └─────────┘
                │ └────────────────┘ │    │    ┌─────────┐
                │ (内置在客户端)     │    └───▶│ Server2 │
                └────────────────────┘         └─────────┘

                特点:
                - 负载均衡逻辑在客户端
                - 从注册中心获取服务列表
                - 自己选择调用哪个实例
                - Spring Cloud LoadBalancer, Ribbon
                """);

        // 3. 负载均衡策略
        System.out.println("=".repeat(60));
        System.out.println("【3. 常见负载均衡策略】");
        System.out.println("""
                ┌──────────────────┬────────────────────────────────────┐
                │      策略         │              说明                  │
                ├──────────────────┼────────────────────────────────────┤
                │ Round Robin      │ 轮询，依次调用每个实例             │
                │ Random           │ 随机选择一个实例                   │
                │ Weighted         │ 加权轮询，按权重分配               │
                │ Least Connections│ 最少连接，选择连接数最少的实例     │
                │ IP Hash          │ 根据 IP 哈希，同一 IP 固定实例    │
                │ Response Time    │ 响应时间加权，快的实例多分配       │
                └──────────────────┴────────────────────────────────────┘

                轮询示例 (Round Robin):
                请求 1 → 实例 A
                请求 2 → 实例 B
                请求 3 → 实例 C
                请求 4 → 实例 A  (循环)

                加权轮询示例 (权重 A:3, B:2, C:1):
                请求 1-3 → 实例 A
                请求 4-5 → 实例 B
                请求 6   → 实例 C
                """);

        // 4. Spring Cloud LoadBalancer
        System.out.println("=".repeat(60));
        System.out.println("【4. Spring Cloud LoadBalancer】");
        System.out.println("""
                Spring Cloud LoadBalancer 替代了 Netflix Ribbon

                1. 添加依赖 (通常已包含在 nacos-discovery 中)

                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
                </dependency>

                2. 使用 @LoadBalanced RestTemplate

                @Configuration
                public class RestConfig {

                    @Bean
                    @LoadBalanced  // 关键注解，开启负载均衡
                    public RestTemplate restTemplate() {
                        return new RestTemplate();
                    }
                }

                @Service
                public class OrderService {

                    @Autowired
                    private RestTemplate restTemplate;

                    public User getUser(Long id) {
                        // 使用服务名，LoadBalancer 自动负载均衡
                        String url = "http://user-service/users/" + id;
                        return restTemplate.getForObject(url, User.class);
                    }
                }

                3. 使用 WebClient (响应式)

                @Bean
                @LoadBalanced
                public WebClient.Builder webClientBuilder() {
                    return WebClient.builder();
                }

                @Service
                public class UserService {
                    @Autowired
                    private WebClient.Builder webClientBuilder;

                    public Mono<User> getUser(Long id) {
                        return webClientBuilder.build()
                            .get()
                            .uri("http://user-service/users/{id}", id)
                            .retrieve()
                            .bodyToMono(User.class);
                    }
                }
                """);

        // 5. 自定义负载均衡策略
        System.out.println("=".repeat(60));
        System.out.println("【5. 自定义负载均衡策略】");
        System.out.println("""
                默认策略是轮询 (RoundRobinLoadBalancer)

                切换为随机策略:

                @Configuration
                public class LoadBalancerConfig {

                    @Bean
                    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
                            Environment environment,
                            LoadBalancerClientFactory factory) {

                        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);

                        return new RandomLoadBalancer(
                            factory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                            name
                        );
                    }
                }

                为特定服务指定策略:

                @Configuration
                @LoadBalancerClient(name = "user-service", configuration = UserLoadBalancerConfig.class)
                public class AppConfig {
                }

                // 或者为多个服务指定
                @LoadBalancerClients({
                    @LoadBalancerClient(name = "user-service", configuration = RandomConfig.class),
                    @LoadBalancerClient(name = "order-service", configuration = RoundRobinConfig.class)
                })
                public class AppConfig {
                }
                """);

        // 6. 自定义负载均衡实现
        System.out.println("=".repeat(60));
        System.out.println("【6. 实现自定义策略】");
        System.out.println("""
                基于权重的负载均衡器:

                public class WeightedLoadBalancer implements ReactorServiceInstanceLoadBalancer {

                    private final String serviceId;
                    private final ObjectProvider<ServiceInstanceListSupplier> supplier;

                    public WeightedLoadBalancer(
                            ObjectProvider<ServiceInstanceListSupplier> supplier,
                            String serviceId) {
                        this.serviceId = serviceId;
                        this.supplier = supplier;
                    }

                    @Override
                    public Mono<Response<ServiceInstance>> choose(Request request) {
                        ServiceInstanceListSupplier provider = supplier.getIfAvailable();
                        return provider.get()
                            .next()
                            .map(this::selectByWeight);
                    }

                    private Response<ServiceInstance> selectByWeight(List<ServiceInstance> instances) {
                        if (instances.isEmpty()) {
                            return new EmptyResponse();
                        }

                        // 计算总权重
                        int totalWeight = 0;
                        for (ServiceInstance instance : instances) {
                            String weight = instance.getMetadata().get("weight");
                            totalWeight += weight != null ? Integer.parseInt(weight) : 1;
                        }

                        // 随机选择
                        int random = ThreadLocalRandom.current().nextInt(totalWeight);
                        int current = 0;

                        for (ServiceInstance instance : instances) {
                            String weight = instance.getMetadata().get("weight");
                            current += weight != null ? Integer.parseInt(weight) : 1;
                            if (random < current) {
                                return new DefaultResponse(instance);
                            }
                        }

                        return new DefaultResponse(instances.get(0));
                    }
                }

                在 Nacos 中设置权重:
                spring:
                  cloud:
                    nacos:
                      discovery:
                        weight: 10  # 权重
                        metadata:
                          weight: 10
                """);

        // 7. Feign 集成负载均衡
        System.out.println("=".repeat(60));
        System.out.println("【7. Feign 集成负载均衡】");
        System.out.println("""
                Feign 默认集成了 LoadBalancer:

                @FeignClient("user-service")  // 服务名
                public interface UserClient {
                    @GetMapping("/users/{id}")
                    User findById(@PathVariable Long id);
                }

                工作流程:
                1. Feign 发起请求: http://user-service/users/1
                2. LoadBalancer 拦截请求
                3. 从 Nacos 获取 user-service 的实例列表
                4. 根据策略选择一个实例
                5. 替换 URL: http://192.168.1.101:8080/users/1
                6. 发起 HTTP 请求

                查看实际调用地址:

                feign:
                  client:
                    config:
                      default:
                        logger-level: FULL

                logging:
                  level:
                    com.example.feign: DEBUG
                """);

        // 8. 健康检查和实例过滤
        System.out.println("=".repeat(60));
        System.out.println("【8. 健康检查和实例过滤】");
        System.out.println("""
                LoadBalancer 会自动过滤不健康的实例:

                ┌────────────────┐
                │   Nacos        │
                │ ┌────────────┐ │
                │ │ user-1 ✅  │ │
                │ │ user-2 ❌  │ │  ← 心跳超时，不健康
                │ │ user-3 ✅  │ │
                │ └────────────┘ │
                └────────────────┘
                        │
                        ▼ 获取服务列表
                ┌────────────────┐
                │ LoadBalancer   │
                │ 只返回健康实例: │
                │ [user-1, user-3]│
                └────────────────┘

                自定义实例过滤器:

                @Bean
                public ServiceInstanceListSupplier customSupplier(
                        DiscoveryClient discoveryClient) {
                    return new DiscoveryClientServiceInstanceListSupplier(discoveryClient)
                        // 添加健康检查
                        .withHealthCheck()
                        // 添加缓存
                        .withCaching();
                }

                配置健康检查:
                spring:
                  cloud:
                    loadbalancer:
                      health-check:
                        initial-delay: 0   # 初始延迟
                        interval: 5s       # 检查间隔
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 微服务使用客户端负载均衡");
        System.out.println("💡 @LoadBalanced 开启负载均衡");
        System.out.println("💡 默认轮询策略，可自定义");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 负载均衡类型:
 * - 服务端负载均衡: Nginx
 * - 客户端负载均衡: LoadBalancer
 * 
 * 2. 常见策略:
 * - 轮询 (默认)
 * - 随机
 * - 加权
 * 
 * 3. 使用方式:
 * - @LoadBalanced + RestTemplate
 * - Feign 自动集成
 * 
 * 4. 自定义策略:
 * - @LoadBalancerClient
 * - 实现 ReactorServiceInstanceLoadBalancer
 */
