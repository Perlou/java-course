# Phase 9: Spring Boot - 学习总结

> **完成状态**: ✅ 已完成  
> **学习时间**: 2025 年 12 月

---

## 🎯 学习目标达成

| 目标                             | 状态 |
| -------------------------------- | ---- |
| 理解 Spring Boot 自动配置原理    | ✅   |
| 掌握 Starter 机制                | ✅   |
| 熟练使用配置管理和多环境配置     | ✅   |
| 掌握 Spring Data JPA             | ✅   |
| 了解 Actuator 监控               | ✅   |
| 理解 Spring Security 和 JWT 认证 | ✅   |
| 能够独立开发 RESTful API 项目    | ✅   |

---

## 📚 核心知识点

### 1. 自动配置原理 ([AutoConfigDemo.java](./AutoConfigDemo.java))

**@SpringBootApplication 组合注解:**

```
@SpringBootApplication
= @SpringBootConfiguration   (配置类)
+ @EnableAutoConfiguration   (启用自动配置)
+ @ComponentScan             (组件扫描)
```

**自动配置流程:**

```
SpringApplication.run()
       ↓
加载 META-INF/spring.factories 或 AutoConfiguration.imports
       ↓
获取所有 AutoConfiguration 类
       ↓
根据 @Conditional 条件过滤
       ↓
加载满足条件的配置类
```

**常用条件注解:**

| 注解                        | 条件                  |
| --------------------------- | --------------------- |
| `@ConditionalOnClass`       | 类路径存在指定类      |
| `@ConditionalOnMissingBean` | 容器中不存在指定 Bean |
| `@ConditionalOnProperty`    | 指定属性有特定值      |

---

### 2. Starter 机制 ([StarterDemo.java](./StarterDemo.java))

**Starter = 依赖集合 + 自动配置**

**命名规范:**

- 官方: `spring-boot-starter-{name}`
- 第三方: `{name}-spring-boot-starter`

**常用 Starter:**

| Starter                        | 功能                  |
| ------------------------------ | --------------------- |
| `spring-boot-starter-web`      | Web 应用，内嵌 Tomcat |
| `spring-boot-starter-data-jpa` | Spring Data JPA       |
| `spring-boot-starter-security` | Spring Security       |
| `spring-boot-starter-actuator` | 监控和管理            |
| `spring-boot-starter-test`     | 测试支持              |

---

### 3. 配置管理 ([ConfigurationDemo.java](./ConfigurationDemo.java))

**配置文件类型:**

- `application.properties` - 键值对格式
- `application.yml` - 层级结构 (推荐)

**配置绑定:**

| 方式                       | 推荐度  | 说明               |
| -------------------------- | ------- | ------------------ |
| `@Value`                   | ⚪ 可选 | 单个值注入         |
| `@ConfigurationProperties` | ✅ 推荐 | 批量绑定，类型安全 |

**多环境配置 (Profile):**

```
├── application.yml           (公共配置)
├── application-dev.yml       (开发环境)
├── application-test.yml      (测试环境)
└── application-prod.yml      (生产环境)
```

**配置优先级:** 命令行 > 系统属性 > 环境变量 > 配置文件

---

### 4. Spring Data JPA ([JpaDemo.java](./JpaDemo.java))

**架构层次:**

```
应用代码 → Spring Data JPA → JPA → Hibernate → JDBC → 数据库
```

**实体注解:**

| 注解              | 说明         |
| ----------------- | ------------ |
| `@Entity`         | 标识实体类   |
| `@Table`          | 指定表名     |
| `@Id`             | 主键         |
| `@GeneratedValue` | 主键生成策略 |
| `@Column`         | 列属性       |

**查询方法:**

| 方式       | 示例                             |
| ---------- | -------------------------------- |
| 方法名派生 | `findByUsername(String)`         |
| JPQL       | `@Query("SELECT u FROM User u")` |
| 原生 SQL   | `@Query(nativeQuery = true)`     |

**分页排序:**

```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<User> page = repository.findAll(pageable);
```

---

### 5. Actuator 监控 ([ActuatorDemo.java](./ActuatorDemo.java))

**常用端点:**

| 端点                | 说明                  |
| ------------------- | --------------------- |
| `/actuator/health`  | 应用健康状态          |
| `/actuator/metrics` | 指标列表              |
| `/actuator/info`    | 应用信息              |
| `/actuator/env`     | 环境变量              |
| `/actuator/loggers` | 日志配置 (可动态修改) |

**自定义健康检查:**

```java
@Component
public class MyHealthIndicator implements HealthIndicator {
    public Health health() {
        return Health.up().withDetail("status", "OK").build();
    }
}
```

**监控集成:** Prometheus + Grafana

---

### 6. Spring Security & JWT ([SecurityDemo.java](./SecurityDemo.java))

**认证 vs 授权:**

- **认证 (Authentication)**: 你是谁?
- **授权 (Authorization)**: 你能做什么?

**JWT 结构:**

```
Header.Payload.Signature

Header:  { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "user", "exp": 1516325422 }
Signature: HMACSHA256(header + payload, secret)
```

**JWT 认证流程:**

```
1. POST /login {username, password}
       ↓
2. 服务端验证，返回 JWT Token
       ↓
3. 客户端保存 Token
       ↓
4. 后续请求: Authorization: Bearer <token>
       ↓
5. 服务端验证 Token
```

**方法级安全:**

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }
```

---

## 🎯 实战项目: RESTful API ([SpringBootProject.java](./SpringBootProject.java))

### 项目结构

```
user-service/
├── controller/     ← 接口层
├── service/        ← 业务层
├── repository/     ← 数据层
├── entity/         ← 实体类
├── dto/            ← 数据传输对象
└── exception/      ← 异常处理
```

### 分层架构

```
Controller → Service → Repository → Database
     ↑           ↑          ↑
    DTO       Entity     JPA
```

### 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(Exception e) { }
}
```

---

## 🔑 关键收获

1. **约定优于配置**: Spring Boot 的核心设计理念
2. **自动配置原理**: @Conditional 条件装配
3. **Starter 机制**: 简化依赖管理和配置
4. **多环境配置**: Profile 管理不同环境
5. **JPA 数据访问**: Repository 方法名派生查询
6. **Actuator 监控**: 生产级健康检查和指标
7. **JWT 认证**: 无状态 API 认证方案
8. **分层架构**: Controller → Service → Repository

---

## 📈 快速开始

```bash
# 使用 Spring Initializr 创建项目
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,h2,actuator,validation,lombok \
  -d name=my-api -d javaVersion=17 -o my-api.zip

# 解压并启动
unzip my-api.zip -d my-api && cd my-api
./mvnw spring-boot:run
```

---

## 📈 进阶方向

- [ ] 学习 Spring Cloud 微服务
- [ ] 深入理解 Spring Security OAuth2
- [ ] 掌握 Spring Boot 性能优化
- [ ] 进入 Phase 10: 微服务与分布式

---

> 📝 Phase 9 完成！接下来进入 [Phase 10: 微服务与分布式](../phase10/README.md)
