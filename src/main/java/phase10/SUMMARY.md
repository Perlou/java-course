# Phase 10: 微服务架构 - 学习总结

> **完成状态**: ✅ 已完成  
> **学习时间**: 2025 年 12 月

---

## 🎯 学习目标达成

| 目标                               | 状态 |
| ---------------------------------- | ---- |
| 理解微服务架构设计原则             | ✅   |
| 掌握服务注册与发现 (Nacos)         | ✅   |
| 熟练使用声明式服务调用 (OpenFeign) | ✅   |
| 理解负载均衡原理                   | ✅   |
| 掌握服务熔断与降级 (Sentinel)      | ✅   |
| 熟悉 API 网关 (Gateway)            | ✅   |
| 理解分布式配置中心                 | ✅   |
| 能够设计和开发微服务系统           | ✅   |

---

## 📚 核心知识点

### 1. 架构演进 ([MicroserviceIntro.java](./MicroserviceIntro.java))

```
单体架构 → 垂直架构 → SOA → 微服务
   ↓          ↓        ↓       ↓
 一个应用   按业务拆分  ESB总线  独立服务
```

**微服务核心特点:**

| 特点     | 说明               |
| -------- | ------------------ |
| 单一职责 | 每个服务只做一件事 |
| 独立部署 | 服务可独立发布     |
| 技术异构 | 可用不同技术栈     |
| 去中心化 | 无单点故障         |

---

### 2. 服务注册与发现 ([NacosDiscovery.java](./NacosDiscovery.java))

**Nacos = Naming + Configuration**

```
服务提供者 ──注册──▶ Nacos ◀──发现── 服务消费者
                      │
                  服务列表
                  健康检查
```

**核心配置:**

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
```

**隔离模型:** `Namespace` (环境) → `Group` (业务) → `Service` (服务)

---

### 3. 声明式服务调用 ([OpenFeignDemo.java](./OpenFeignDemo.java))

**Feign 让远程调用像本地方法一样简单**

```java
@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    User findById(@PathVariable("id") Long id);
}
```

**参数传递:**

| 注解              | 用途                   |
| ----------------- | ---------------------- |
| `@PathVariable`   | 路径变量 `/users/{id}` |
| `@RequestParam`   | 查询参数 `?name=xxx`   |
| `@RequestBody`    | JSON 请求体            |
| `@SpringQueryMap` | 对象转查询参数         |

---

### 4. 负载均衡 ([LoadBalancerDemo.java](./LoadBalancerDemo.java))

**客户端负载均衡 vs 服务端负载均衡**

```
┌────────────────────┐         ┌─────────┐
│      Client        │         │ Server1 │
│ ┌────────────────┐ │    ┌───▶│         │
│ │  LoadBalancer  │─┼────┤    └─────────┘
│ └────────────────┘ │    │    ┌─────────┐
│ (内置在客户端)     │    └───▶│ Server2 │
└────────────────────┘         └─────────┘
```

**负载策略:**

- **轮询** (Round Robin) - 默认
- **随机** (Random)
- **加权** (Weighted)
- **最少连接** (Least Connections)

---

### 5. 熔断与限流 ([SentinelDemo.java](./SentinelDemo.java))

**服务雪崩问题:**

```
A ──▶ B ──▶ C ──▶ D (故障)
     阻塞   阻塞
     ↓      ↓
   整个系统不可用
```

**Sentinel 核心功能:**

| 功能     | 策略                         |
| -------- | ---------------------------- |
| 流量控制 | QPS 限流、线程数限流、预热   |
| 熔断降级 | 慢调用比例、异常比例、异常数 |
| 系统保护 | CPU、负载、入口 QPS          |

**熔断器状态:** `CLOSED` ⇄ `OPEN` ⇄ `HALF-OPEN`

---

### 6. API 网关 ([GatewayDemo.java](./GatewayDemo.java))

**网关 = 微服务的统一入口**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-route
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
```

**核心概念:**

- **Route** - 路由规则
- **Predicate** - 断言 (匹配条件)
- **Filter** - 过滤器 (处理请求/响应)

**常用功能:** 路由转发、认证鉴权、限流熔断、跨域处理

---

### 7. 配置中心 ([ConfigCenterDemo.java](./ConfigCenterDemo.java))

**集中管理、动态刷新**

```yaml
# bootstrap.yml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml
```

**动态刷新:**

- `@RefreshScope` + `@Value`
- `@ConfigurationProperties` (自动刷新)

**配置优先级:** shared-configs < extension-configs < 默认配置

---

## 🎯 实战项目: 微服务电商系统

[MicroserviceProject.java](./MicroserviceProject.java) 提供了完整的实战指南：

### 项目架构

```
┌─────────────┐
│   Gateway   │ ← 统一入口
└──────┬──────┘
       │
┌──────┼──────┬──────────┬──────────┐
▼      ▼      ▼          ▼          ▼
User  Order  Product  Inventory  Payment
       │
       └────▶ Nacos + Sentinel
```

### 服务划分

| 服务              | 职责                 |
| ----------------- | -------------------- |
| gateway-service   | API 网关、路由、认证 |
| user-service      | 用户注册、登录       |
| product-service   | 商品管理             |
| order-service     | 订单处理             |
| inventory-service | 库存管理             |

---

## 🔑 关键收获

1. **微服务不是银弹** - 带来新的复杂性，需权衡利弊
2. **Spring Cloud Alibaba** - 国内主流方案 (Nacos + Sentinel)
3. **服务治理三件套** - 注册中心 + 配置中心 + 熔断限流
4. **Feign + LoadBalancer** - 声明式调用 + 负载均衡
5. **Gateway 统一入口** - 认证、路由、限流一站式处理
6. **渐进式拆分** - 先模块化，再微服务化

---

## 🛠 环境准备命令

```bash
# 启动 Nacos
docker run -d --name nacos -p 8848:8848 -p 9848:9848 \
  -e MODE=standalone nacos/nacos-server:v2.2.3

# 启动 Sentinel 控制台
java -Dserver.port=8080 -jar sentinel-dashboard-1.8.6.jar

# 访问地址
# Nacos: http://localhost:8848/nacos (nacos/nacos)
# Sentinel: http://localhost:8080 (sentinel/sentinel)
```

---

## 📈 进阶方向

- [ ] 学习分布式事务 (Seata)
- [ ] 掌握消息队列 (RocketMQ)
- [ ] 了解链路追踪 (SkyWalking)
- [ ] 进入 Phase 11: 容器与 DevOps

---

> 📝 Phase 10 完成！接下来进入 [Phase 11: 容器与 DevOps](../phase11/README.md)
