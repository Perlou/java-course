# Phase 9: Spring Boot

> **目标**：掌握 Spring Boot 快速开发  
> **预计时长**：2 周  
> **前置条件**：Phase 8 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解 Spring Boot 自动配置原理
2. 掌握 Starter 机制
3. 熟练使用配置管理和多环境配置
4. 掌握 Spring Data JPA
5. 了解 Actuator 监控
6. 理解 Spring Security 和 JWT 认证
7. 能够独立开发 RESTful API 项目

---

## 📚 核心概念

### Spring Boot 特性

```
┌─────────────────────────────────────────────────────────┐
│              Spring Boot 核心特性                        │
├─────────────────────────────────────────────────────────┤
│  🚀 约定优于配置     自动配置，开箱即用                  │
│  📦 Starter 依赖     一站式依赖管理                      │
│  🔧 嵌入式服务器     Tomcat/Jetty/Undertow              │
│  📊 Actuator        生产级监控                          │
│  🔐 Security        认证与授权                          │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 文件列表

| #   | 文件                                               | 描述            | 知识点                   |
| --- | -------------------------------------------------- | --------------- | ------------------------ |
| 1   | [AutoConfigDemo.java](./AutoConfigDemo.java)       | 自动配置原理    | @SpringBootApplication   |
| 2   | [StarterDemo.java](./StarterDemo.java)             | Starter 机制    | 依赖管理, 自定义 Starter |
| 3   | [ConfigurationDemo.java](./ConfigurationDemo.java) | 配置管理        | YAML, Profile, 多环境    |
| 4   | [JpaDemo.java](./JpaDemo.java)                     | Spring Data JPA | Entity, Repository, 查询 |
| 5   | [ActuatorDemo.java](./ActuatorDemo.java)           | Actuator 监控   | 健康检查, Metrics        |
| 6   | [SecurityDemo.java](./SecurityDemo.java)           | Security & JWT  | 认证授权, Token          |
| 7   | [SpringBootProject.java](./SpringBootProject.java) | 🎯 **实战项目** | RESTful API 开发         |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行自动配置
mvn exec:java -Dexec.mainClass="phase09.AutoConfigDemo"

# 运行 Starter 机制
mvn exec:java -Dexec.mainClass="phase09.StarterDemo"

# 运行实战项目指南
mvn exec:java -Dexec.mainClass="phase09.SpringBootProject"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: AutoConfigDemo - 自动配置原理
2. **Day 3-4**: StarterDemo - Starter 机制
3. **Day 5-6**: ConfigurationDemo - 配置管理
4. **Day 7-8**: JpaDemo - Spring Data JPA
5. **Day 9-10**: ActuatorDemo - 监控
6. **Day 11-12**: SecurityDemo - 安全认证
7. **Day 13-14**: SpringBootProject - 实战项目

### 实战练习

理论学习后，建议：

1. 访问 [Spring Initializr](https://start.spring.io) 创建项目
2. 按照 `SpringBootProject.java` 指南开发 API
3. 部署到服务器或云平台

---

## ✅ 完成检查

- [ ] 理解 @SpringBootApplication 三个注解
- [ ] 了解 Starter 的作用和结构
- [ ] 掌握 YAML 配置和多环境配置
- [ ] 能够使用 JPA 进行数据库操作
- [ ] 了解 Actuator 端点
- [ ] 理解 JWT 认证流程
- [ ] 完成 RESTful API 项目

---

## 🎯 实战项目: RESTful API

`SpringBootProject.java` 是本阶段的实战指南，包含：

| 内容       | 说明                   |
| ---------- | ---------------------- |
| 项目创建   | Spring Initializr 使用 |
| 项目结构   | 分层架构规范           |
| 实体定义   | JPA Entity             |
| Repository | 数据访问层             |
| Service    | 业务逻辑层             |
| Controller | RESTful API            |
| 异常处理   | 全局统一处理           |
| 配置文件   | 多环境配置             |

---

## 🔗 快速开始 Spring Boot 项目

```bash
# 使用 Spring Initializr 创建项目
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,h2,actuator,validation,lombok \
  -d name=my-api \
  -d type=maven-project \
  -d javaVersion=17 \
  -o my-api.zip

# 解压并启动
unzip my-api.zip -d my-api
cd my-api
./mvnw spring-boot:run
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 10: 微服务与分布式](../phase10/README.md)
