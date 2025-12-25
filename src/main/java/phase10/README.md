# Phase 10: 微服务架构

> **目标**：掌握 Spring Cloud 微服务开发  
> **预计时长**：2 周  
> **前置条件**：Phase 9 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解微服务架构设计原则
2. 掌握服务注册与发现 (Nacos)
3. 熟练使用声明式服务调用 (OpenFeign)
4. 理解负载均衡原理
5. 掌握服务熔断与降级 (Sentinel)
6. 熟悉 API 网关 (Gateway)
7. 理解分布式配置中心
8. 能够设计和开发微服务系统

---

## 📚 核心概念

### 微服务架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      微服务架构全景图                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  客户端 ──▶ [API Gateway] ──▶ [服务A] ◀──┐                      │
│                   │                      │                      │
│                   ├──▶ [服务B] ◀─────────┼── [注册中心]          │
│                   │                      │     Nacos            │
│                   └──▶ [服务C] ◀─────────┘                      │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │ 配置中心    │  │ 熔断降级    │  │ 链路追踪    │              │
│  │ Nacos Config│  │ Sentinel    │  │ SkyWalking  │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

### Spring Cloud Alibaba 组件

| 组件         | 功能                     |
| ------------ | ------------------------ |
| Nacos        | 服务注册、发现、配置中心 |
| OpenFeign    | 声明式 HTTP 客户端       |
| LoadBalancer | 客户端负载均衡           |
| Sentinel     | 流量控制、熔断降级       |
| Gateway      | API 网关                 |
| Seata        | 分布式事务               |

---

## 📁 文件列表

| #   | 文件                                                   | 描述            | 知识点             |
| --- | ------------------------------------------------------ | --------------- | ------------------ |
| 1   | [MicroserviceIntro.java](./MicroserviceIntro.java)     | 微服务架构概述  | 架构演进, 设计原则 |
| 2   | [NacosDiscovery.java](./NacosDiscovery.java)           | 服务注册与发现  | Nacos, 服务注册    |
| 3   | [OpenFeignDemo.java](./OpenFeignDemo.java)             | 声明式服务调用  | Feign, 服务间通信  |
| 4   | [LoadBalancerDemo.java](./LoadBalancerDemo.java)       | 负载均衡        | 负载策略, Ribbon   |
| 5   | [SentinelDemo.java](./SentinelDemo.java)               | 熔断与限流      | 熔断, 降级, 限流   |
| 6   | [GatewayDemo.java](./GatewayDemo.java)                 | API 网关        | 路由, 过滤器       |
| 7   | [ConfigCenterDemo.java](./ConfigCenterDemo.java)       | 配置中心        | 动态配置, 热更新   |
| 8   | [MicroserviceProject.java](./MicroserviceProject.java) | 🎯 **实战项目** | 微服务电商系统     |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行微服务概述
mvn exec:java -Dexec.mainClass="phase10.MicroserviceIntro"

# 运行 Nacos 服务发现
mvn exec:java -Dexec.mainClass="phase10.NacosDiscovery"

# 运行实战项目指南
mvn exec:java -Dexec.mainClass="phase10.MicroserviceProject"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: MicroserviceIntro - 微服务架构概述
2. **Day 3-4**: NacosDiscovery - 服务注册与发现
3. **Day 5-6**: OpenFeignDemo - 声明式服务调用
4. **Day 7-8**: LoadBalancerDemo - 负载均衡
5. **Day 9-10**: SentinelDemo - 熔断与限流
6. **Day 11-12**: GatewayDemo - API 网关
7. **Day 13-14**: MicroserviceProject - 实战项目

### 环境准备

```bash
# 1. 安装 Docker (用于运行 Nacos)
# macOS
brew install docker

# 2. 启动 Nacos
docker run -d --name nacos -p 8848:8848 -e MODE=standalone nacos/nacos-server:v2.2.3

# 3. 访问 Nacos 控制台
http://localhost:8848/nacos
# 默认账号: nacos/nacos
```

---

## ✅ 完成检查

- [ ] 理解微服务与单体架构的区别
- [ ] 掌握 Nacos 服务注册与发现
- [ ] 能够使用 OpenFeign 进行服务调用
- [ ] 理解负载均衡策略
- [ ] 掌握 Sentinel 熔断降级配置
- [ ] 能够配置 Gateway 网关路由
- [ ] 理解配置中心的作用
- [ ] 完成微服务电商项目

---

## 🎯 实战项目: 微服务电商系统

`MicroserviceProject.java` 是本阶段的综合实战指南：

| 服务              | 说明     |
| ----------------- | -------- |
| gateway-service   | API 网关 |
| user-service      | 用户服务 |
| product-service   | 商品服务 |
| order-service     | 订单服务 |
| inventory-service | 库存服务 |

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 11: 容器与 DevOps](../phase11/README.md)
