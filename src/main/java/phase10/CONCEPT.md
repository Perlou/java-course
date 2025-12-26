```markdown
# Spring Cloud 深入解析：从零开始

> 本文档从零开始，系统性地介绍 Spring Cloud 微服务架构的核心概念、组件原理和实战应用。

## 目录

- [一、微服务架构基础](#一微服务架构基础)
- [二、Spring Cloud 概述](#二spring-cloud-概述)
- [三、服务注册与发现](#三服务注册与发现)
- [四、负载均衡](#四负载均衡)
- [五、服务调用 (OpenFeign)](#五服务调用-openfeign)
- [六、服务熔断与降级 (Sentinel)](#六服务熔断与降级-sentinel)
- [七、服务网关 (Spring Cloud Gateway)](#七服务网关-spring-cloud-gateway)
- [八、配置中心 (Nacos Config)](#八配置中心-nacos-config)
- [九、链路追踪](#九链路追踪)
- [十、消息驱动 (Spring Cloud Stream)](#十消息驱动-spring-cloud-stream)
- [十一、完整项目架构](#十一完整项目架构)
- [十二、总结](#十二总结)

---

## 一、微服务架构基础

### 1.1 从单体到微服务的演进
```

┌─────────────────────────────────────────────────────────────────────┐
│ 架构演进路线 │
├─────────────────────────────────────────────────────────────────────┤
│ │
│ ┌──────────┐ ┌──────────────┐ ┌─────────────────────┐ │
│ │ 单体架构 │ ──→ │ 垂直架构 │ ──→ │ 分布式/SOA 架构 │ │
│ │ │ │ │ │ │ │
│ │ All in │ │ 按业务拆分 │ │ 服务化+ESB 总线 │ │
│ │ One │ │ 多个应用 │ │ │ │
│ └──────────┘ └──────────────┘ └─────────────────────┘ │
│ │ │
│ ▼ │
│ ┌─────────────────────┐ │
│ │ 微服务架构 │ │
│ │ │ │
│ │ 更细粒度的服务拆分 │ │
│ │ 独立部署、独立扩展 │ │
│ └─────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘

```

### 1.2 微服务架构的核心问题

```

┌─────────────────────────────────────────────────────────────────┐
│ 微服务需要解决的问题 │
├─────────────────────────────────────────────────────────────────┤
│ │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│ │ 服务注册发现 │ │ 负载均衡 │ │ 服务调用 │ │
│ │ 服务在哪里？ │ │ 调用哪个实例？│ │ 如何调用？ │ │
│ └─────────────┘ └─────────────┘ └─────────────┘ │
│ │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│ │ 熔断降级 │ │ API 网关 │ │ 配置管理 │ │
│ │ 故障怎么办？ │ │ 统一入口？ │ │ 配置如何管？│ │
│ └─────────────┘ └─────────────┘ └─────────────┘ │
│ │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│ │ 链路追踪 │ │ 消息驱动 │ │ 分布式事务 │ │
│ │ 问题在哪里？ │ │ 异步通信？ │ │ 数据一致性？│ │
│ └─────────────┘ └─────────────┘ └─────────────┘ │
│ │
└─────────────────────────────────────────────────────────────────┘

```

---

## 二、Spring Cloud 概述

### 2.1 什么是 Spring Cloud

```

┌─────────────────────────────────────────────────────────────────────┐
│ Spring Cloud 定位 │
├─────────────────────────────────────────────────────────────────────┤
│ │
│ Spring Cloud 是一套微服务解决方案的规范和标准 │
│ │
│ ┌───────────────────────────────────────────────────────────────┐ │
│ │ Spring Cloud（规范层） │ │
│ ├───────────────────────────────────────────────────────────────┤ │
│ │ │ │
│ │ ┌─────────────────────┐ ┌─────────────────────┐ │ │
│ │ │ Spring Cloud Netflix│ │ Spring Cloud Alibaba│ │ │
│ │ │ (第一代) │ │ (第二代) │ │ │
│ │ ├─────────────────────┤ ├─────────────────────┤ │ │
│ │ │ • Eureka │ │ • Nacos │ │ │
│ │ │ • Ribbon │ │ • Sentinel │ │ │
│ │ │ • Hystrix │ │ • Seata │ │ │
│ │ │ • Zuul │ │ • RocketMQ │ │ │
│ │ │ • Feign │ │ │ │ │
│ │ └─────────────────────┘ └─────────────────────┘ │ │
│ │ │ │
│ └───────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌───────────────────────────────────────────────────────────────┐ │
│ │ Spring Boot（基础层） │ │
│ └───────────────────────────────────────────────────────────────┘ │
│ │
└─────────────────────────────────────────────────────────────────────┘

```

### 2.2 版本对应关系

| Spring Cloud | Spring Boot | 发布时间 |
|--------------|-------------|----------|
| 2023.0.x (Leyton) | 3.2.x | 2023.11 |
| 2022.0.x (Kilburn) | 3.0.x, 3.1.x | 2022.12 |
| 2021.0.x (Jubilee) | 2.6.x, 2.7.x | 2021.12 |
| 2020.0.x (Ilford) | 2.4.x, 2.5.x | 2020.12 |
| Hoxton | 2.2.x, 2.3.x | 2019.11 |
| Greenwich | 2.1.x | 2019.01 |
| Finchley | 2.0.x | 2018.06 |

> ⚠️ **注意**：版本不匹配会导致各种奇怪问题！

### 2.3 组件演进对比

| 功能 | 第一代(Netflix) | 第二代(Alibaba/官方) |
|------|-----------------|---------------------|
| 服务注册 | Eureka | Nacos / Consul |
| 服务调用 | Feign | OpenFeign |
| 负载均衡 | Ribbon | LoadBalancer |
| 熔断降级 | Hystrix | Sentinel / Resilience4j |
| 服务网关 | Zuul | Spring Cloud Gateway |
| 配置中心 | Config | Nacos Config |
| 消息驱动 | Bus+RabbitMQ | RocketMQ |
| 链路追踪 | Sleuth+Zipkin | Micrometer Tracing |
| 分布式事务 | 无 | Seata |

---

## 三、服务注册与发现

### 3.1 核心原理

```

┌─────────────────────────────────────────────────────────────────────┐
│ 服务注册与发现原理 │
├─────────────────────────────────────────────────────────────────────┤
│ │
│ ┌─────────────────────────────────────────┐ │
│ │ 注册中心 (Nacos/Eureka) │ │
│ │ ┌─────────────────────────────────┐ │ │
│ │ │ 服务注册表 │ │ │
│ │ │ ┌─────────────────────────┐ │ │ │
│ │ │ │ user-service: │ │ │ │
│ │ │ │ - 192.168.1.10:8080 │ │ │ │
│ │ │ │ - 192.168.1.11:8080 │ │ │ │
│ │ │ │ order-service: │ │ │ │
│ │ │ │ - 192.168.1.20:8081 │ │ │ │
│ │ │ └─────────────────────────┘ │ │ │
│ │ └─────────────────────────────────┘ │ │
│ └──────────────┬──────────────┬──────────┘ │
│ ▲ │ │ ▲ │
│ ① 注册 │ │ ③ 拉取列表 │ │ ① 注册 │
│ ② 心跳 │ │ ▼ │ ② 心跳 │
│ │ │ │ │
│ ┌──────────┴───┐ │ ┌──────────────┴───┐ │
│ │ User Service │ │ │ Order Service │ │
│ │ (服务提供者) │◄───┴────────│ (服务消费者) │ │
│ │ │ ④ 调用服务 │ │ │
│ └──────────────┘ └──────────────────┘ │
│ │
│ 执行流程： │
│ ① 服务启动时向注册中心注册自己的信息 │
│ ② 定时发送心跳，证明自己还活着 │
│ ③ 服务消费者从注册中心拉取服务列表 │
│ ④ 根据负载均衡算法选择一个实例进行调用 │
│ │
└─────────────────────────────────────────────────────────────────────┘

````

### 3.2 Nacos 实战

#### 3.2.1 添加依赖

```xml
<!-- pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<properties>
    <spring-cloud.version>2021.0.8</spring-cloud.version>
    <spring-cloud-alibaba.version>2021.0.5.0</spring-cloud-alibaba.version>
</properties>

<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Nacos Discovery -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>${spring-cloud-alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
````

#### 3.2.2 配置文件

```yaml
# application.yml
server:
  port: 8080

spring:
  application:
    name: user-service # 服务名称，非常重要！
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 # Nacos 地址
        namespace: dev # 命名空间（可选）
        group: DEFAULT_GROUP # 分组（可选）
        cluster-name: BJ # 集群名称（可选）
```

**Nacos 核心概念层级：**

```
Namespace (命名空间) - 环境隔离
  └── Group (分组) - 业务隔离
       └── Service (服务) - 服务名
            └── Cluster (集群) - 机房
                 └── Instance (实例)
```

#### 3.2.3 启动类

```java
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务发现（可省略，自动配置）
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

### 3.3 Nacos 与 Eureka 对比

| 特性       | Nacos          | Eureka   |
| ---------- | -------------- | -------- |
| CAP 模型   | 支持 AP 和 CP  | AP       |
| 配置中心   | 支持           | 不支持   |
| 动态刷新   | 支持           | 不支持   |
| 健康检查   | TCP/HTTP/MySQL | 仅心跳   |
| 雪崩保护   | 支持           | 支持     |
| 管理界面   | 功能丰富       | 简单     |
| 社区活跃度 | 活跃           | 停止维护 |

---

## 四、负载均衡

### 4.1 负载均衡原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                    客户端负载均衡 vs 服务端负载均衡                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  【服务端负载均衡】                  【客户端负载均衡】                 │
│                                                                      │
│  ┌────────┐                         ┌────────┐                      │
│  │ Client │                         │ Client │                      │
│  └───┬────┘                         │  ┌───────────┐                │
│      │                              │  │ 服务列表   │                │
│      ▼                              │  │ • A:8080  │                │
│  ┌──────────┐                       │  │ • A:8081  │                │
│  │   Nginx  │                       │  │ • A:8082  │                │
│  │ 负载均衡 │                        │  └───────────┘                │
│  └────┬─────┘                       └───────┬────┘                  │
│       │                                     │ 本地决策               │
│  ┌────┴────────────┐               ┌───────┴────────────┐          │
│  ▼        ▼        ▼               ▼        ▼           ▼          │
│ ┌──┐    ┌──┐    ┌──┐             ┌──┐    ┌──┐       ┌──┐          │
│ │A │    │A │    │A │             │A │    │A │       │A │          │
│ └──┘    └──┘    └──┘             └──┘    └──┘       └──┘          │
│                                                                      │
│  由Nginx决定调用哪个               由客户端自己决定调用哪个             │
│  • 集中式                          • 分布式                          │
│  • 单点问题                        • 无单点问题                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.2 Spring Cloud LoadBalancer

```java
// 配置负载均衡的 RestTemplate
@Configuration
public class LoadBalancerConfig {

    @Bean
    @LoadBalanced  // 开启负载均衡
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// 使用示例
@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;

    public User getUser(Long userId) {
        // 使用服务名代替具体的IP地址
        // user-service 会被解析为具体的实例地址
        String url = "http://user-service/users/" + userId;
        return restTemplate.getForObject(url, User.class);
    }
}
```

### 4.3 负载均衡策略

```java
// 自定义负载均衡策略
@Configuration
public class CustomLoadBalancerConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> customLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {

        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);

        return new RandomLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name
        );
    }
}

// 指定某个服务使用特定的负载均衡策略
@LoadBalancerClient(name = "user-service", configuration = CustomLoadBalancerConfig.class)
public class UserServiceLoadBalancerConfig {
}
```

**常用负载均衡策略：**

| 策略           | 描述                           |
| -------------- | ------------------------------ |
| RoundRobin     | 轮询（默认）：依次选择每个实例 |
| Random         | 随机：随机选择一个实例         |
| WeightedRandom | 加权随机：按权重随机选择       |
| BestAvailable  | 最优实例：选择并发连接最少的   |

---

## 五、服务调用 (OpenFeign)

### 5.1 OpenFeign 原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                     OpenFeign 工作原理                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┐                                                   │
│  │ @FeignClient │  ① 启动时扫描所有 @FeignClient 注解                │
│  │    接口       │     生成动态代理对象                               │
│  └──────┬───────┘                                                   │
│         │                                                            │
│         ▼                                                            │
│  ┌──────────────┐                                                   │
│  │  JDK Proxy   │  ② 调用方法时，代理对象拦截                        │
│  │   动态代理    │     解析方法上的注解，构建 HTTP 请求               │
│  └──────┬───────┘                                                   │
│         │                                                            │
│         ▼                                                            │
│  ┌──────────────┐                                                   │
│  │ LoadBalancer │  ③ 从注册中心获取服务实例列表                      │
│  │   负载均衡    │     根据策略选择一个实例                           │
│  └──────┬───────┘                                                   │
│         │                                                            │
│         ▼                                                            │
│  ┌──────────────┐                                                   │
│  │  HTTP Client │  ④ 发送实际的 HTTP 请求                           │
│  │  (OkHttp等)  │     获取响应并反序列化                             │
│  └──────────────┘                                                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.2 OpenFeign 实战

#### 5.2.1 添加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 5.2.2 启用 Feign

```java
@SpringBootApplication
@EnableFeignClients  // 开启 Feign 客户端
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

#### 5.2.3 定义 Feign 客户端

```java
/**
 * 声明式的服务调用客户端
 * name: 目标服务名（与 Nacos 中注册的服务名一致）
 * path: 统一的请求前缀
 */
@FeignClient(
    name = "user-service",
    path = "/api/users",
    fallbackFactory = UserClientFallbackFactory.class  // 降级工厂
)
public interface UserClient {

    /**
     * 根据ID查询用户
     * 会转化为: GET http://user-service/api/users/{id}
     */
    @GetMapping("/{id}")
    User findById(@PathVariable("id") Long id);

    /**
     * 查询用户列表
     * 会转化为: GET http://user-service/api/users?ids=1,2,3
     */
    @GetMapping
    List<User> findByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 创建用户
     * 会转化为: POST http://user-service/api/users
     * Body: JSON格式的User对象
     */
    @PostMapping
    User create(@RequestBody User user);

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    User update(@PathVariable("id") Long id, @RequestBody User user);

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") Long id);
}
```

#### 5.2.4 使用 Feign 客户端

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    // 直接注入，像调用本地方法一样调用远程服务
    private final UserClient userClient;

    public OrderVO createOrder(OrderDTO orderDTO) {
        // 调用用户服务获取用户信息
        User user = userClient.findById(orderDTO.getUserId());

        // 业务逻辑...
        Order order = new Order();
        order.setUserId(user.getId());
        order.setUserName(user.getName());
        // ...

        return convertToVO(order, user);
    }
}
```

### 5.3 Feign 高级配置

```yaml
# application.yml
feign:
  client:
    config:
      default: # 全局配置
        connectTimeout: 5000 # 连接超时时间
        readTimeout: 10000 # 读取超时时间
        loggerLevel: FULL # 日志级别
      user-service: # 针对特定服务的配置
        connectTimeout: 3000
        readTimeout: 5000

  compression:
    request:
      enabled: true # 开启请求压缩
      mime-types: application/json # 压缩类型
      min-request-size: 2048 # 最小压缩大小
    response:
      enabled: true # 开启响应压缩

  httpclient:
    enabled: true # 使用 Apache HttpClient
    max-connections: 200 # 最大连接数
    max-connections-per-route: 50 # 每个路由最大连接数
```

**日志级别说明：**

- `NONE`: 无日志（默认）
- `BASIC`: 仅记录请求方法、URL、响应状态码、执行时间
- `HEADERS`: 在 BASIC 基础上，记录请求和响应的头信息
- `FULL`: 记录所有请求和响应的详细信息

```java
// 自定义 Feign 配置
@Configuration
public class FeignConfig {

    /**
     * 自定义日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 请求拦截器：添加认证信息
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 从当前请求中获取Token，传递给下游服务
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("Authorization");
                if (StringUtils.hasText(token)) {
                    template.header("Authorization", token);
                }
            }
        };
    }

    /**
     * 自定义错误解码器
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new NotFoundException("资源不存在");
            }
            if (response.status() == 503) {
                return new ServiceUnavailableException("服务不可用");
            }
            return new RuntimeException("服务调用异常: " + response.status());
        };
    }
}
```

---

## 六、服务熔断与降级 (Sentinel)

### 6.1 熔断降级原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                     为什么需要熔断降级？                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  服务雪崩效应：                                                       │
│                                                                      │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐          │
│  │ 服务 A  │───▶│ 服务 B  │───▶│ 服务 C  │───▶│ 服务 D  │          │
│  └─────────┘    └─────────┘    └─────────┘    └────┬────┘          │
│       │              │              │              │ 故障           │
│       │              │              │              ▼                │
│       │              │              │         ┌─────────┐          │
│       │              │              │         │   💥    │          │
│       │              │              │         └─────────┘          │
│       ▼              ▼              ▼                               │
│  请求堆积 ──────▶ 请求堆积 ──────▶ 请求堆积 ──────▶ 整体崩溃！       │
│                                                                      │
│  解决方案：熔断 + 降级                                                │
│                                                                      │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐          │
│  │ 服务 A  │───▶│ 服务 B  │───▶│ 服务 C  │──╳ │ 服务 D  │          │
│  └─────────┘    └─────────┘    └────┬────┘    └─────────┘          │
│                                     │ 熔断                          │
│                                     ▼                               │
│                              ┌─────────────┐                        │
│                              │  降级处理    │                        │
│                              │  返回默认值   │                        │
│                              └─────────────┘                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.2 熔断器状态机

```
┌─────────────────────────────────────────────────────────────────────┐
│                        熔断器状态转换                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                    ┌────────────────┐                               │
│                    │                │                               │
│            ┌───────│    CLOSED      │◄─────────────────┐           │
│            │       │    (关闭)      │                   │           │
│            │       └───────┬────────┘                   │           │
│            │               │                            │           │
│            │     失败率超过阈值                   探测请求成功         │
│            │               │                            │           │
│            │               ▼                            │           │
│            │       ┌────────────────┐                   │           │
│            │       │                │                   │           │
│   一直正常 │       │     OPEN       │────────────┐     │           │
│            │       │    (打开)      │            │     │           │
│            │       └────────────────┘            │     │           │
│            │               │                     │     │           │
│            │               │                     │     │           │
│            │        超时时间到达              熔断期间    │           │
│            │               │               快速失败     │           │
│            │               ▼                     │     │           │
│            │       ┌────────────────┐            │     │           │
│            │       │                │            │     │           │
│            └───────│   HALF_OPEN    │────────────┘     │           │
│                    │   (半开)       │                   │           │
│                    │                │───────────────────┘           │
│                    └────────────────┘                               │
│                            │                                        │
│                       探测请求失败                                   │
│                            │                                        │
│                            ▼                                        │
│                    ┌────────────────┐                               │
│                    │     OPEN       │                               │
│                    └────────────────┘                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.3 Sentinel 实战

#### 6.3.1 添加依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<!-- Sentinel 与 Feign 整合 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-sentinel-datasource</artifactId>
</dependency>
```

#### 6.3.2 配置

```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080 # Sentinel 控制台地址
        port: 8719 # 与控制台通信的端口
      web-context-unify: false # 链路模式必须关闭

# 开启 Feign 对 Sentinel 的支持
feign:
  sentinel:
    enabled: true
```

#### 6.3.3 使用注解方式

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    /**
     * @SentinelResource 核心注解
     * value: 资源名称（唯一标识）
     * blockHandler: 流控或熔断时的处理方法
     * fallback: 业务异常时的降级方法
     */
    @GetMapping("/{id}")
    @SentinelResource(
        value = "getOrder",
        blockHandler = "getOrderBlockHandler",
        fallback = "getOrderFallback"
    )
    public Order getOrder(@PathVariable Long id) {
        // 模拟可能的异常
        if (id < 0) {
            throw new IllegalArgumentException("ID不能为负数");
        }
        return orderService.findById(id);
    }

    /**
     * 流控/熔断处理方法
     * 参数必须与原方法一致，最后加 BlockException 参数
     */
    public Order getOrderBlockHandler(Long id, BlockException ex) {
        log.warn("请求被流控或熔断: {}", ex.getMessage());
        return new Order().setId(id).setStatus("BLOCKED");
    }

    /**
     * 业务异常降级方法
     * 参数必须与原方法一致，最后加 Throwable 参数
     */
    public Order getOrderFallback(Long id, Throwable ex) {
        log.error("业务异常，执行降级: {}", ex.getMessage());
        return new Order().setId(id).setStatus("FALLBACK");
    }
}
```

#### 6.3.4 Feign 降级

```java
/**
 * FallbackFactory 方式（推荐）
 * 可以获取异常信息，便于日志记录和问题排查
 */
@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        log.error("UserClient 调用失败: {}", cause.getMessage());

        return new UserClient() {
            @Override
            public User findById(Long id) {
                log.warn("findById 降级处理, id: {}", id);
                return new User()
                    .setId(id)
                    .setName("未知用户")
                    .setStatus("DEGRADED");
            }

            @Override
            public List<User> findByIds(List<Long> ids) {
                log.warn("findByIds 降级处理");
                return Collections.emptyList();
            }

            @Override
            public User create(User user) {
                throw new ServiceException("用户服务暂不可用，请稍后重试");
            }

            @Override
            public User update(Long id, User user) {
                throw new ServiceException("用户服务暂不可用，请稍后重试");
            }

            @Override
            public void delete(Long id) {
                throw new ServiceException("用户服务暂不可用，请稍后重试");
            }
        };
    }
}
```

### 6.4 Sentinel 规则配置

```java
/**
 * 通过代码配置规则（也可通过控制台配置）
 */
@Configuration
public class SentinelRulesConfig {

    @PostConstruct
    public void init() {
        initFlowRules();
        initDegradeRules();
    }

    /**
     * 流控规则
     */
    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // QPS 限流
        FlowRule qpsRule = new FlowRule();
        qpsRule.setResource("getOrder");
        qpsRule.setGrade(RuleConstant.FLOW_GRADE_QPS);  // QPS 模式
        qpsRule.setCount(100);  // 阈值：100 QPS
        qpsRule.setLimitApp("default");
        rules.add(qpsRule);

        // 线程数限流
        FlowRule threadRule = new FlowRule();
        threadRule.setResource("createOrder");
        threadRule.setGrade(RuleConstant.FLOW_GRADE_THREAD);  // 线程数模式
        threadRule.setCount(10);  // 最多10个并发线程
        rules.add(threadRule);

        FlowRuleManager.loadRules(rules);
    }

    /**
     * 熔断规则
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // 慢调用比例熔断
        DegradeRule slowCallRule = new DegradeRule();
        slowCallRule.setResource("getOrder");
        slowCallRule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        slowCallRule.setCount(0.5);  // 慢调用比例阈值 50%
        slowCallRule.setTimeWindow(10);  // 熔断时长 10 秒
        slowCallRule.setSlowRatioThreshold(1000);  // 慢调用定义：响应时间 > 1000ms
        slowCallRule.setMinRequestAmount(5);  // 最小请求数
        slowCallRule.setStatIntervalMs(10000);  // 统计时长 10 秒
        rules.add(slowCallRule);

        // 异常比例熔断
        DegradeRule errorRatioRule = new DegradeRule();
        errorRatioRule.setResource("createOrder");
        errorRatioRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        errorRatioRule.setCount(0.5);  // 异常比例阈值 50%
        errorRatioRule.setTimeWindow(30);  // 熔断时长 30 秒
        errorRatioRule.setMinRequestAmount(10);
        rules.add(errorRatioRule);

        DegradeRuleManager.loadRules(rules);
    }
}
```

**Sentinel 核心概念：**

| 流量控制策略 | 描述                     |
| ------------ | ------------------------ |
| 直接拒绝     | 超过阈值直接拒绝         |
| Warm Up 预热 | 逐渐增加阈值，避免冷启动 |
| 排队等待     | 请求排队等待，匀速通过   |

| 熔断策略   | 描述                    |
| ---------- | ----------------------- |
| 慢调用比例 | 响应时间>阈值的请求占比 |
| 异常比例   | 失败率>阈值             |
| 异常数     | 失败次数>阈值           |

---

## 七、服务网关 (Spring Cloud Gateway)

### 7.1 网关架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        网关在架构中的位置                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                           ┌─────────────┐                           │
│                           │   Client    │                           │
│                           │   (Web/App) │                           │
│                           └──────┬──────┘                           │
│                                  │                                   │
│                                  ▼                                   │
│                    ┌─────────────────────────┐                      │
│                    │     Spring Cloud        │                      │
│                    │       Gateway           │                      │
│                    │                         │                      │
│                    │  • 统一入口              │                      │
│                    │  • 路由转发              │                      │
│                    │  • 身份认证              │                      │
│                    │  • 权限校验              │                      │
│                    │  • 限流熔断              │                      │
│                    │  • 日志监控              │                      │
│                    │  • 请求改写              │                      │
│                    └───────────┬─────────────┘                      │
│                                │                                     │
│            ┌───────────────────┼───────────────────┐                │
│            │                   │                   │                │
│            ▼                   ▼                   ▼                │
│     ┌────────────┐     ┌────────────┐     ┌────────────┐           │
│     │   User     │     │   Order    │     │  Product   │           │
│     │  Service   │     │  Service   │     │  Service   │           │
│     └────────────┘     └────────────┘     └────────────┘           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 7.2 Gateway 三大核心概念

| 概念                 | 描述                                                |
| -------------------- | --------------------------------------------------- |
| **Route (路由)**     | 网关最基本的组件，由 ID、目标 URI、断言和过滤器组成 |
| **Predicate (断言)** | 匹配 HTTP 请求的条件，满足条件才进行路由            |
| **Filter (过滤器)**  | 在请求发送之前和之后执行的逻辑                      |

**常用断言：**

- `Path` - 路径匹配
- `Method` - 请求方法
- `Header` - 请求头匹配
- `Query` - 请求参数
- `Host` - 主机名匹配
- `Cookie` - Cookie 值
- `After/Before/Between` - 时间匹配

### 7.3 Gateway 配置

```yaml
# application.yml
server:
  port: 8080

spring:
  application:
    name: gateway-service

  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848

    gateway:
      # 开启从注册中心动态创建路由
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true # 服务名小写

      # 路由配置
      routes:
        # 用户服务路由
        - id: user-service
          uri: lb://user-service # lb: 表示负载均衡
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1 # 去掉第一层路径

        # 订单服务路由
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
            - Method=GET,POST # 只匹配 GET 和 POST
          filters:
            - StripPrefix=1
            - AddRequestHeader=X-Request-Source, gateway

        # 产品服务路由（复杂断言示例）
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
            - Header=X-Request-Id, \d+ # 必须有数字格式的请求ID
            - Query=token # 必须有token参数
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter # 限流过滤器
              args:
                redis-rate-limiter.replenishRate: 10 # 每秒10个请求
                redis-rate-limiter.burstCapacity: 20 # 最大突发20个
                key-resolver: "#{@ipKeyResolver}" # 限流key解析器

      # 全局跨域配置
      globalcors:
        cors-configurations:
          "[/**]":
            allowedOrigins: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
```

### 7.4 自定义过滤器

#### 7.4.1 全局认证过滤器

```java
@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/public/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单直接放行
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = request.getHeaders().getFirst("Authorization");

        // Token 为空
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange, "未提供认证信息");
        }

        // 验证 Token
        try {
            // 去掉 Bearer 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 从 Redis 验证 Token
            String userInfoJson = (String) redisTemplate.opsForValue().get("token:" + token);
            if (userInfoJson == null) {
                return unauthorized(exchange, "Token已过期");
            }

            // 解析用户信息
            UserInfo userInfo = JSON.parseObject(userInfoJson, UserInfo.class);

            // 将用户信息传递给下游服务
            ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userInfo.getUserId()))
                .header("X-User-Name", URLEncoder.encode(userInfo.getUserName(), "UTF-8"))
                .header("X-User-Roles", String.join(",", userInfo.getRoles()))
                .build();

            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return unauthorized(exchange, "Token验证失败");
        }
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result = Result.fail(401, message);
        byte[] bytes = JSON.toJSONBytes(result);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 判断是否在白名单
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> {
            if (pattern.endsWith("/**")) {
                return path.startsWith(pattern.replace("/**", ""));
            }
            return pattern.equals(path);
        });
    }

    @Override
    public int getOrder() {
        return -100;  // 数字越小，优先级越高
    }
}
```

#### 7.4.2 请求日志过滤器

```java
@Component
@Slf4j
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        log.info("====> 请求开始 [{}] {} {} from {}",
            requestId,
            request.getMethod(),
            request.getURI(),
            request.getRemoteAddress()
        );

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();

            log.info("<==== 请求结束 [{}] 状态: {} 耗时: {}ms",
                requestId,
                response.getStatusCode(),
                duration
            );
        }));
    }

    @Override
    public int getOrder() {
        return -200;  // 最先执行
    }
}
```

---

## 八、配置中心 (Nacos Config)

### 8.1 配置中心的作用

```
┌─────────────────────────────────────────────────────────────────────┐
│                     传统配置 vs 配置中心                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  【传统方式】                                                        │
│  每个服务实例都有自己的配置文件                                        │
│  修改配置需要重启所有实例，无法保证配置一致性                           │
│                                                                      │
│  【配置中心方式】                                                     │
│  配置集中存储在 Nacos，服务自动获取并动态刷新                          │
│                                                                      │
│  优势：                                                              │
│  ✓ 配置集中管理     ✓ 动态刷新（无需重启）                            │
│  ✓ 版本控制        ✓ 环境隔离                                       │
│  ✓ 权限控制        ✓ 配置监听                                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 8.2 Nacos Config 配置

```xml
<!-- 依赖 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>

<!-- Spring Cloud 2020 之后需要添加 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

```yaml
# bootstrap.yml（优先于 application.yml 加载）
spring:
  application:
    name: user-service

  profiles:
    active: dev

  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml # 配置文件格式
        namespace: dev # 命名空间
        group: DEFAULT_GROUP # 分组

        # 共享配置
        shared-configs:
          - data-id: common.yaml
            group: DEFAULT_GROUP
            refresh: true

        # 扩展配置
        extension-configs:
          - data-id: redis.yaml
            group: DEFAULT_GROUP
            refresh: true
          - data-id: mysql.yaml
            group: DEFAULT_GROUP
            refresh: true
```

**配置文件命名规则：**

```
${spring.application.name}-${spring.profiles.active}.${file-extension}
例如：user-service-dev.yaml
```

### 8.3 配置文件示例

```yaml
# Nacos 中的配置: user-service-dev.yaml

server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/user_db
    username: root
    password: 123456

  redis:
    host: localhost
    port: 6379
    password:
    database: 0

# 自定义配置
app:
  config:
    maxRetry: 3
    timeout: 5000
    features:
      enableCache: true
      enableAsync: true
```

### 8.4 动态刷新配置

```java
/**
 * 方式一：@RefreshScope + @Value
 */
@RestController
@RefreshScope  // 开启配置刷新
@RequestMapping("/config")
public class ConfigController {

    @Value("${app.config.maxRetry:3}")
    private Integer maxRetry;

    @Value("${app.config.timeout:5000}")
    private Integer timeout;

    @GetMapping
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxRetry", maxRetry);
        config.put("timeout", timeout);
        return config;
    }
}

/**
 * 方式二：@ConfigurationProperties（推荐）
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "app.config")
public class AppConfig {

    private Integer maxRetry = 3;
    private Integer timeout = 5000;
    private Features features = new Features();

    @Data
    public static class Features {
        private boolean enableCache = true;
        private boolean enableAsync = true;
    }
}

// 使用
@Service
@RequiredArgsConstructor
public class SomeService {

    private final AppConfig appConfig;

    public void doSomething() {
        if (appConfig.getFeatures().isEnableCache()) {
            // 使用缓存
        }

        int maxRetry = appConfig.getMaxRetry();
        // ...
    }
}

/**
 * 方式三：监听配置变化
 */
@Component
@Slf4j
public class ConfigChangeListener implements ApplicationListener<RefreshScopeRefreshedEvent> {

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        log.info("配置已更新，执行相关操作...");
        // 可以在这里做一些配置变更后的处理
    }
}
```

---

## 九、链路追踪

### 9.1 分布式链路追踪原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                      链路追踪原理                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  问题：请求在微服务间流转，如何追踪完整调用链路？                         │
│                                                                      │
│  解决：TraceId + SpanId                                              │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                TraceId: abc-123 (整条链路唯一)                │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│   Client ──▶ Gateway ──▶ Order ──▶ User ──▶ Product                 │
│              SpanId:1    SpanId:2   SpanId:3  SpanId:4              │
│                 │           │          │         │                   │
│                 ▼           ▼          ▼         ▼                   │
│              ┌────┐      ┌────┐     ┌────┐    ┌────┐                │
│              │ 10ms│     │ 15ms│    │ 8ms│    │ 5ms│                │
│              └────┘      └────┘     └────┘    └────┘                │
│                                                                      │
│  通过 TraceId 可以追踪完整请求链路                                     │
│  通过 SpanId 可以定位每个服务的耗时                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 9.2 使用 Micrometer Tracing + Zipkin

```xml
<!-- Spring Cloud 2022+ 使用 Micrometer Tracing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Tracing -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Zipkin 上报 -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>

<!-- Feign 链路追踪支持 -->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-micrometer</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  application:
    name: order-service

management:
  tracing:
    sampling:
      probability: 1.0 # 采样率：1.0 = 100%，生产环境建议 0.1
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans

logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

```java
/**
 * 日志中会自动包含 TraceId 和 SpanId
 *
 * 示例日志：
 * INFO [order-service,abc123,span456] c.e.OrderService : 处理订单...
 */
@Service
@Slf4j
public class OrderService {

    public void processOrder(Long orderId) {
        log.info("开始处理订单: {}", orderId);
        // ...
        log.info("订单处理完成");
    }
}
```

---

## 十、消息驱动 (Spring Cloud Stream)

### 10.1 消息驱动架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                     消息驱动架构                                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  同步调用的问题：耦合度高，任一服务失败影响整个流程                       │
│                                                                      │
│  异步消息解耦：                                                       │
│  ┌─────────┐         ┌─────────────────┐         ┌─────────┐       │
│  │ Order   │ ──────▶ │  Message Queue  │ ──────▶ │  Stock  │       │
│  │ Service │         │  (RocketMQ等)    │         │ Service │       │
│  └─────────┘         └────────┬────────┘         └─────────┘       │
│                               │                                      │
│                               └──────────────────▶ ┌─────────┐      │
│                                                    │ Notify  │      │
│                                                    │ Service │      │
│                                                    └─────────┘      │
│                                                                      │
│  优势：                                                              │
│  ✓ 服务解耦     ✓ 异步处理     ✓ 流量削峰                            │
│  ✓ 失败重试     ✓ 消息持久化                                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 10.2 Spring Cloud Stream + RocketMQ

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rocketmq</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  cloud:
    stream:
      rocketmq:
        binder:
          name-server: localhost:9876
      bindings:
        # 输出通道（生产者）
        order-output:
          destination: order-topic # Topic名称
          content-type: application/json
          producer:
            group: order-producer-group

        # 输入通道（消费者）
        order-input:
          destination: order-topic
          content-type: application/json
          group: order-consumer-group
          consumer:
            maxAttempts: 3 # 最大重试次数
```

```java
/**
 * 定义消息通道接口
 */
public interface OrderChannel {

    String OUTPUT = "order-output";
    String INPUT = "order-input";

    @Output(OUTPUT)
    MessageChannel output();

    @Input(INPUT)
    SubscribableChannel input();
}

/**
 * 消息生产者
 */
@Service
@RequiredArgsConstructor
@EnableBinding(OrderChannel.class)
@Slf4j
public class OrderMessageProducer {

    private final OrderChannel orderChannel;

    public void sendOrderMessage(OrderEvent event) {
        Message<OrderEvent> message = MessageBuilder
            .withPayload(event)
            .setHeader("eventType", event.getType())
            .build();

        boolean result = orderChannel.output().send(message);
        log.info("发送订单消息: {}, 结果: {}", event.getOrderId(), result);
    }
}

/**
 * 消息消费者
 */
@Service
@EnableBinding(OrderChannel.class)
@Slf4j
public class OrderMessageConsumer {

    @StreamListener(OrderChannel.INPUT)
    public void handleOrderMessage(OrderEvent event) {
        log.info("收到订单消息: {}", event);

        try {
            // 处理订单事件
            switch (event.getType()) {
                case "CREATE":
                    handleOrderCreate(event);
                    break;
                case "PAY":
                    handleOrderPay(event);
                    break;
                case "CANCEL":
                    handleOrderCancel(event);
                    break;
            }
        } catch (Exception e) {
            log.error("处理订单消息失败: {}", e.getMessage());
            throw e;  // 抛出异常会触发重试
        }
    }

    private void handleOrderCreate(OrderEvent event) {
        // 扣减库存、发送通知等
    }

    private void handleOrderPay(OrderEvent event) {
        // 更新订单状态等
    }

    private void handleOrderCancel(OrderEvent event) {
        // 恢复库存等
    }
}

/**
 * 订单事件
 */
@Data
@Builder
public class OrderEvent {
    private Long orderId;
    private String type;  // CREATE, PAY, CANCEL
    private Long userId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
```

---

## 十一、完整项目架构

### 11.1 项目结构

```
microservice-demo/
├── pom.xml                              # 父 POM
│
├── common/                              # 公共模块
│   ├── common-core/                     # 核心工具类
│   ├── common-redis/                    # Redis 配置
│   ├── common-mysql/                    # MySQL 配置
│   └── common-feign/                    # Feign 配置
│
├── gateway/                             # 网关服务
│   └── src/main/
│       ├── java/
│       │   └── com.example.gateway/
│       │       ├── GatewayApplication.java
│       │       ├── filter/              # 过滤器
│       │       └── config/              # 配置类
│       └── resources/
│           └── application.yml
│
├── service/                             # 业务服务
│   ├── user-service/                    # 用户服务
│   │   └── src/main/
│   │       ├── java/
│   │       │   └── com.example.user/
│   │       │       ├── UserApplication.java
│   │       │       ├── controller/
│   │       │       ├── service/
│   │       │       ├── mapper/
│   │       │       └── entity/
│   │       └── resources/
│   │           └── bootstrap.yml
│   │
│   ├── order-service/                   # 订单服务
│   │   └── ...
│   │
│   └── product-service/                 # 产品服务
│       └── ...
│
├── service-api/                         # API 模块（Feign 接口定义）
│   ├── user-api/
│   │   └── src/main/java/
│   │       └── com.example.user.api/
│   │           ├── UserClient.java      # Feign 客户端
│   │           ├── dto/                 # DTO
│   │           └── fallback/            # 降级实现
│   │
│   ├── order-api/
│   └── product-api/
│
└── auth/                                # 认证服务
    └── ...
```

### 11.2 核心依赖配置

```xml
<!-- 父 pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>microservice-demo</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <properties>
        <java.version>17</java.version>
        <spring-boot.version>2.7.18</spring-boot.version>
        <spring-cloud.version>2021.0.8</spring-cloud.version>
        <spring-cloud-alibaba.version>2021.0.5.0</spring-cloud-alibaba.version>
    </properties>

    <modules>
        <module>common</module>
        <module>gateway</module>
        <module>service</module>
        <module>service-api</module>
        <module>auth</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

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

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 11.3 技术选型总结

| 层级             | 技术选型                  |
| ---------------- | ------------------------- |
| **基础设施层**   | Docker, K8s, Jenkins, Git |
| **服务注册发现** | Nacos                     |
| **配置中心**     | Nacos Config              |
| **服务网关**     | Spring Cloud Gateway      |
| **负载均衡**     | LoadBalancer              |
| **服务调用**     | OpenFeign                 |
| **熔断降级**     | Sentinel                  |
| **链路追踪**     | Micrometer + Zipkin       |
| **消息队列**     | RocketMQ                  |
| **分布式事务**   | Seata                     |
| **数据层**       | MySQL, Redis, ES, MongoDB |

---

## 十二、总结

### 12.1 学习路线

**阶段一：基础**

- Spring Boot 基础
- 微服务架构概念
- Docker 基础

**阶段二：核心组件**

- Nacos 服务注册与配置
- OpenFeign 服务调用
- Sentinel 熔断降级
- Gateway 网关

**阶段三：进阶**

- 分布式事务 (Seata)
- 消息队列 (RocketMQ)
- 链路追踪
- 日志聚合 (ELK)

**阶段四：实战**

- 完整项目实战
- 性能优化
- 容器化部署 (K8s)
- CI/CD 流程

### 12.2 最佳实践

1. **服务拆分**：按业务领域拆分，避免过度拆分
2. **接口设计**：使用 RESTful 风格，做好版本管理
3. **配置管理**：敏感配置加密，区分环境
4. **异常处理**：统一异常处理，合理的降级策略
5. **监控告警**：完善的监控体系，及时发现问题
6. **文档管理**：接口文档、架构文档及时更新
7. **安全防护**：网关统一认证，服务间 mTLS

---

> 📌 **提示**：本文档基于 Spring Cloud 2021.x 版本编写，如使用其他版本请注意配置差异。

> 📚 **参考资料**：
>
> - [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
> - [Spring Cloud Alibaba 官方文档](https://github.com/alibaba/spring-cloud-alibaba)
> - [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
> - [Sentinel 官方文档](https://sentinelguard.io/zh-cn/)

```

---

以上就是完整的 Markdown 文档，您可以直接复制保存为 `.md` 文件使用。
```
