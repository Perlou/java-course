package phase10;

/**
 * Phase 10 - Lesson 2: Nacos 服务注册与发现
 * 
 * 🎯 学习目标:
 * 1. 理解服务注册与发现原理
 * 2. 掌握 Nacos 的使用
 * 3. 了解服务健康检查
 */
public class NacosDiscovery {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 10 - Lesson 2: Nacos 服务注册与发现");
        System.out.println("=".repeat(60));

        // 1. 为什么需要服务注册与发现
        System.out.println("\n【1. 为什么需要服务注册与发现】");
        System.out.println("""
                传统调用方式的问题:

                ┌────────────┐     硬编码地址     ┌────────────┐
                │  服务 A    │ ─────────────────▶ │  服务 B    │
                │            │  http://192.168.1.100:8080
                └────────────┘                    └────────────┘

                问题:
                1. 服务 B 的 IP 写死在代码中
                2. 服务 B 扩容后，A 不知道新实例
                3. 服务 B 宕机后，A 无法感知

                解决方案 - 服务注册中心:

                ┌────────────┐                    ┌────────────┐
                │  服务 A    │                    │  服务 B-1  │
                │            │    ┌──────────┐   │ :8080      │
                │ 1.查询服务 │───▶│ 注册中心 │◀──│ 2.注册     │
                │   地址列表 │    │  Nacos   │   └────────────┘
                │            │◀───│          │   ┌────────────┐
                │ 3.调用     │    └──────────┘   │  服务 B-2  │
                │   服务B    │                   │ :8081      │
                └──────┬─────┘                   │ 2.注册     │
                       │                          └────────────┘
                       └─────────────▶ 负载均衡调用 B-1 或 B-2
                """);

        // 2. Nacos 简介
        System.out.println("=".repeat(60));
        System.out.println("【2. Nacos 简介】");
        System.out.println("""
                Nacos = Naming + Configuration Service

                功能:
                ┌─────────────────────────────────────────────────────────┐
                │                      Nacos                              │
                ├──────────────────────┬──────────────────────────────────┤
                │  服务发现 (Naming)    │  配置管理 (Configuration)       │
                ├──────────────────────┼──────────────────────────────────┤
                │  - 服务注册           │  - 配置存储                      │
                │  - 服务发现           │  - 配置推送                      │
                │  - 健康检查           │  - 配置回滚                      │
                │  - 负载均衡           │  - 多环境支持                    │
                └──────────────────────┴──────────────────────────────────┘

                对比:
                ┌──────────────┬────────────┬────────────┬────────────┐
                │    特性      │   Nacos    │  Eureka    │   Consul   │
                ├──────────────┼────────────┼────────────┼────────────┤
                │ 服务发现     │     ✅     │     ✅     │     ✅     │
                │ 配置中心     │     ✅     │     ❌     │     ✅     │
                │ 健康检查     │   TCP/HTTP │   心跳     │  TCP/HTTP  │
                │ CAP 模型     │   AP/CP    │     AP     │     CP     │
                │ 控制台       │     ✅     │     ✅     │     ✅     │
                │ 社区活跃度   │     高     │     低     │     中     │
                └──────────────┴────────────┴────────────┴────────────┘
                """);

        // 3. 启动 Nacos
        System.out.println("=".repeat(60));
        System.out.println("【3. 启动 Nacos】");
        System.out.println("""
                方式 1: Docker (推荐)

                # 单机模式
                docker run -d --name nacos \\
                  -p 8848:8848 \\
                  -p 9848:9848 \\
                  -e MODE=standalone \\
                  nacos/nacos-server:v2.2.3

                方式 2: 下载安装

                # 下载
                wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.zip

                # 解压
                unzip nacos-server-2.2.3.zip
                cd nacos/bin

                # 启动 (单机模式)
                sh startup.sh -m standalone

                访问控制台:
                http://localhost:8848/nacos
                默认账号: nacos / nacos
                """);

        // 4. 服务注册
        System.out.println("=".repeat(60));
        System.out.println("【4. 服务提供者注册】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
                </dependency>

                <!-- 需要在 parent 或 dependencyManagement 中引入 -->
                <dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>com.alibaba.cloud</groupId>
                            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                            <version>2022.0.0.0</version>
                            <type>pom</type>
                            <scope>import</scope>
                        </dependency>
                    </dependencies>
                </dependencyManagement>

                2. 配置 application.yml

                spring:
                  application:
                    name: user-service  # 服务名称
                  cloud:
                    nacos:
                      discovery:
                        server-addr: localhost:8848  # Nacos 地址
                        namespace: dev               # 命名空间 (可选)
                        group: DEFAULT_GROUP         # 分组 (可选)

                server:
                  port: 8080

                3. 启动类开启服务发现

                @SpringBootApplication
                @EnableDiscoveryClient  // 开启服务发现 (可省略)
                public class UserServiceApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(UserServiceApplication.class, args);
                    }
                }
                """);

        // 5. 服务发现
        System.out.println("=".repeat(60));
        System.out.println("【5. 服务消费者发现】");
        System.out.println("""
                1. 同样添加 Nacos 依赖和配置

                2. 使用 DiscoveryClient 发现服务

                @RestController
                @RequiredArgsConstructor
                public class OrderController {

                    private final DiscoveryClient discoveryClient;

                    @GetMapping("/discovery")
                    public List<String> discovery() {
                        // 获取 user-service 的所有实例
                        List<ServiceInstance> instances =
                            discoveryClient.getInstances("user-service");

                        return instances.stream()
                            .map(instance -> instance.getHost() + ":" + instance.getPort())
                            .toList();
                    }
                }

                3. 使用 RestTemplate + 负载均衡调用

                @Configuration
                public class RestConfig {
                    @Bean
                    @LoadBalanced  // 开启负载均衡
                    public RestTemplate restTemplate() {
                        return new RestTemplate();
                    }
                }

                @Service
                public class OrderService {
                    @Autowired
                    private RestTemplate restTemplate;

                    public User getUser(Long id) {
                        // 使用服务名替代 IP:端口
                        String url = "http://user-service/users/" + id;
                        return restTemplate.getForObject(url, User.class);
                    }
                }
                """);

        // 6. 服务分组和命名空间
        System.out.println("=".repeat(60));
        System.out.println("【6. 服务分组和命名空间】");
        System.out.println("""
                Nacos 的服务隔离模型:

                ┌─────────────────────────────────────────────────────────┐
                │                      Nacos                              │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │            Namespace (命名空间)                  │   │
                │  │            用于环境隔离                          │   │
                │  │  ┌────────────────────────────────────────────┐ │   │
                │  │  │              Group (分组)                   │ │   │
                │  │  │              用于服务分组                   │ │   │
                │  │  │  ┌─────────────────────────────────────┐   │ │   │
                │  │  │  │          Service (服务)              │   │ │   │
                │  │  │  │  ┌────────┐ ┌────────┐ ┌────────┐   │   │ │   │
                │  │  │  │  │Instance│ │Instance│ │Instance│   │   │ │   │
                │  │  │  │  └────────┘ └────────┘ └────────┘   │   │ │   │
                │  │  │  └─────────────────────────────────────┘   │ │   │
                │  │  └────────────────────────────────────────────┘ │   │
                │  └─────────────────────────────────────────────────┘   │
                └─────────────────────────────────────────────────────────┘

                使用场景:

                命名空间 (环境隔离):
                - dev (开发环境)
                - test (测试环境)
                - prod (生产环境)

                分组 (业务隔离):
                - DEFAULT_GROUP
                - ORDER_GROUP
                - PAYMENT_GROUP

                配置示例:
                spring:
                  cloud:
                    nacos:
                      discovery:
                        server-addr: localhost:8848
                        namespace: dev_namespace_id  # 命名空间 ID
                        group: ORDER_GROUP           # 分组名
                """);

        // 7. 健康检查
        System.out.println("=".repeat(60));
        System.out.println("【7. 健康检查机制】");
        System.out.println("""
                Nacos 健康检查方式:

                1. 临时实例 (Ephemeral) - 默认
                   - 客户端心跳检测
                   - 每 5 秒发送心跳
                   - 15 秒未收到心跳标记为不健康
                   - 30 秒未收到心跳剔除实例
                   - 适合: 自动部署的服务

                2. 持久实例 (Persistent)
                   - 服务端主动探测
                   - TCP/HTTP 探测
                   - 服务下线不会自动删除
                   - 适合: 需要手动管理的服务

                配置:
                spring:
                  cloud:
                    nacos:
                      discovery:
                        ephemeral: true  # true=临时实例, false=持久实例
                        heart-beat-interval: 5000     # 心跳间隔 (ms)
                        heart-beat-timeout: 15000     # 心跳超时 (ms)
                        ip-delete-timeout: 30000      # 删除超时 (ms)

                权重配置 (用于负载均衡):
                spring:
                  cloud:
                    nacos:
                      discovery:
                        weight: 1  # 权重 0-100，数字越大流量越多
                """);

        // 8. 集群部署
        System.out.println("=".repeat(60));
        System.out.println("【8. Nacos 集群部署】");
        System.out.println("""
                生产环境推荐集群部署:

                ┌─────────────────────────────────────────────────────────┐
                │                   Nacos 集群架构                        │
                │                                                         │
                │   客户端                                                │
                │     │                                                   │
                │     ▼                                                   │
                │  ┌──────────┐                                          │
                │  │  Nginx   │  负载均衡                                 │
                │  │  / SLB   │                                          │
                │  └────┬─────┘                                          │
                │       │                                                 │
                │  ┌────┴────┬────────┬────────┐                         │
                │  ▼         ▼        ▼        ▼                         │
                │ Nacos1  Nacos2   Nacos3   (至少3节点)                  │
                │  └─────────┴────────┴────────┘                         │
                │              ▼                                          │
                │         MySQL 集群                                     │
                │         (数据持久化)                                    │
                └─────────────────────────────────────────────────────────┘

                集群配置 cluster.conf:
                # IP:PORT
                192.168.1.101:8848
                192.168.1.102:8848
                192.168.1.103:8848

                客户端配置:
                spring:
                  cloud:
                    nacos:
                      discovery:
                        server-addr: 192.168.1.101:8848,192.168.1.102:8848,192.168.1.103:8848
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Nacos = 服务发现 + 配置中心");
        System.out.println("💡 使用 @LoadBalanced RestTemplate 实现负载均衡");
        System.out.println("💡 命名空间隔离环境，分组隔离业务");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 服务注册与发现:
 * - 服务提供者注册到 Nacos
 * - 服务消费者从 Nacos 获取地址
 * - 负载均衡调用
 * 
 * 2. Nacos 配置:
 * - spring.cloud.nacos.discovery.server-addr
 * - namespace, group 隔离
 * 
 * 3. 服务调用:
 * - DiscoveryClient 获取实例
 * - @LoadBalanced RestTemplate
 * 
 * 4. 健康检查:
 * - 临时实例: 心跳检测
 * - 持久实例: 主动探测
 */
