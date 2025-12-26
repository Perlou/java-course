# Spring Boot 深入解析（从 0 到 1）

## 📚 目录导航

```
一、Spring Boot 概述
二、快速入门
三、核心注解解析
四、自动配置原理（核心重点）
五、启动流程深度剖析
六、配置文件机制
七、Web 开发
八、数据访问
九、自定义 Starter
十、监控与运维
十一、面试高频问题
```

---

## 一、Spring Boot 概述

### 1.1 什么是 Spring Boot？

```
┌─────────────────────────────────────────────────────────────────┐
│                     传统 Spring 开发痛点                          │
├─────────────────────────────────────────────────────────────────┤
│  ❌ 配置繁琐：大量 XML 配置文件                                    │
│  ❌ 依赖管理：版本冲突、依赖地狱                                   │
│  ❌ 部署复杂：需要外部 Tomcat、打 WAR 包                           │
│  ❌ 开发效率低：搭建项目耗时长                                     │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                    Spring Boot 应运而生
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     Spring Boot 解决方案                          │
├─────────────────────────────────────────────────────────────────┤
│  ✅ 自动配置：约定优于配置，减少样板代码                            │
│  ✅ 起步依赖：一站式依赖管理，版本自动协调                          │
│  ✅ 内嵌容器：直接运行 JAR，无需外部服务器                          │
│  ✅ 快速开发：分钟级项目搭建                                       │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 核心特性一览

```java
/*
 * Spring Boot 四大核心特性
 */
┌────────────────────────────────────────────────────────────────────┐
│                                                                    │
│   ┌──────────────┐    ┌──────────────┐                            │
│   │  自动配置     │    │  起步依赖    │                            │
│   │ Auto-Config  │    │  Starters    │                            │
│   │              │    │              │                            │
│   │ 根据类路径    │    │ 一个依赖     │                            │
│   │ 自动装配Bean  │    │ 搞定一切     │                            │
│   └──────────────┘    └──────────────┘                            │
│                                                                    │
│   ┌──────────────┐    ┌──────────────┐                            │
│   │  内嵌容器     │    │  Actuator   │                            │
│   │ Embedded     │    │  生产就绪    │                            │
│   │              │    │              │                            │
│   │ Tomcat/Jetty │    │ 健康检查     │                            │
│   │ Undertow     │    │ 指标监控     │                            │
│   └──────────────┘    └──────────────┘                            │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

---

## 二、快速入门

### 2.1 第一个 Spring Boot 应用

```java
// 1. pom.xml - 引入父工程和起步依赖
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <!-- 继承 Spring Boot 父工程 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <dependencies>
        <!-- Web 起步依赖：自动引入 Spring MVC + 内嵌 Tomcat + JSON 等 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// 2. 主启动类
@SpringBootApplication  // 核心注解，开启一切魔法
public class MyApplication {
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(MyApplication.class, args);
    }
}

// 3. Controller
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

### 2.2 项目结构规范

```
my-spring-boot-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       ├── DemoApplication.java      # 主启动类（放在根包下）
│   │   │       ├── controller/               # 控制层
│   │   │       ├── service/                  # 业务层
│   │   │       ├── repository/               # 数据访问层
│   │   │       ├── entity/                   # 实体类
│   │   │       └── config/                   # 配置类
│   │   └── resources/
│   │       ├── application.yml               # 主配置文件
│   │       ├── application-dev.yml           # 开发环境配置
│   │       ├── application-prod.yml          # 生产环境配置
│   │       ├── static/                       # 静态资源
│   │       └── templates/                    # 模板文件
│   └── test/
│       └── java/                             # 测试类
├── pom.xml
└── README.md
```

---

## 三、核心注解深度解析

### 3.1 @SpringBootApplication 拆解

```java
@SpringBootApplication  // 这是一个组合注解
public class MyApplication { }

// 等价于以下三个注解的组合：
┌─────────────────────────────────────────────────────────────────────┐
│  @SpringBootApplication                                             │
│  ├── @SpringBootConfiguration                                       │
│  │   └── @Configuration          // 标识这是一个配置类               │
│  │       └── @Component          // 本质上也是一个 Bean              │
│  │                                                                  │
│  ├── @EnableAutoConfiguration    // 开启自动配置（核心！）            │
│  │   ├── @AutoConfigurationPackage                                  │
│  │   │   └── @Import(AutoConfigurationPackages.Registrar.class)    │
│  │   └── @Import(AutoConfigurationImportSelector.class)            │
│  │                                                                  │
│  └── @ComponentScan              // 组件扫描（扫描主类所在包及子包）   │
│      └── basePackages = 主类所在包                                  │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 条件注解详解

```java
/**
 * Spring Boot 自动配置的核心：条件注解
 * 只有满足特定条件，配置才会生效
 */
@Configuration
public class ConditionalDemo {

    // ==================== 类/Bean 存在性条件 ====================

    @Bean
    @ConditionalOnClass(DataSource.class)
    // 当类路径存在 DataSource 类时生效
    public MyBean bean1() { return new MyBean(); }

    @Bean
    @ConditionalOnMissingClass("com.example.SomeClass")
    // 当类路径不存在某类时生效
    public MyBean bean2() { return new MyBean(); }

    @Bean
    @ConditionalOnBean(DataSource.class)
    // 当容器中存在 DataSource Bean 时生效
    public MyBean bean3() { return new MyBean(); }

    @Bean
    @ConditionalOnMissingBean(MyService.class)
    // 当容器中不存在 MyService Bean 时生效（常用于默认配置）
    public MyService defaultService() { return new DefaultMyService(); }

    // ==================== 属性条件 ====================

    @Bean
    @ConditionalOnProperty(
        prefix = "my.feature",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false  // 属性不存在时是否匹配
    )
    // 当 my.feature.enabled=true 时生效
    public FeatureBean featureBean() { return new FeatureBean(); }

    // ==================== Web 环境条件 ====================

    @Bean
    @ConditionalOnWebApplication(type = Type.SERVLET)
    // Servlet Web 环境时生效
    public WebBean webBean() { return new WebBean(); }

    @Bean
    @ConditionalOnNotWebApplication
    // 非 Web 环境时生效
    public NonWebBean nonWebBean() { return new NonWebBean(); }

    // ==================== 资源/表达式条件 ====================

    @Bean
    @ConditionalOnResource(resources = "classpath:my-config.xml")
    // 当资源存在时生效
    public ResourceBean resourceBean() { return new ResourceBean(); }

    @Bean
    @ConditionalOnExpression("${my.enabled:true} and ${other.enabled:false}")
    // SpEL 表达式为 true 时生效
    public ExpressionBean expressionBean() { return new ExpressionBean(); }
}
```

### 3.3 条件注解执行流程图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        条件注解判断流程                                   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
                    ┌───────────────────────────────┐
                    │    加载自动配置类              │
                    │  (从 spring.factories 读取)   │
                    └───────────────────────────────┘
                                    │
                                    ▼
              ┌─────────────────────────────────────────────┐
              │  @ConditionalOnClass - 类路径检查           │
              │  检查必需的类是否存在于 classpath           │
              └─────────────────────────────────────────────┘
                         │                    │
                     存在 ✓                不存在 ✗
                         ▼                    ▼
              ┌──────────────────┐    ┌──────────────────┐
              │  继续下一个条件   │    │  跳过此配置类     │
              └──────────────────┘    └──────────────────┘
                         │
                         ▼
              ┌─────────────────────────────────────────────┐
              │  @ConditionalOnProperty - 属性检查          │
              │  检查配置文件中属性值是否匹配                │
              └─────────────────────────────────────────────┘
                         │                    │
                     匹配 ✓                不匹配 ✗
                         ▼                    ▼
              ┌──────────────────┐    ┌──────────────────┐
              │  继续下一个条件   │    │  跳过此配置类     │
              └──────────────────┘    └──────────────────┘
                         │
                         ▼
              ┌─────────────────────────────────────────────┐
              │  @ConditionalOnMissingBean - 容器检查       │
              │  检查容器中是否已存在同类型 Bean             │
              └─────────────────────────────────────────────┘
                         │                    │
                    不存在 ✓                存在 ✗
                         ▼                    ▼
              ┌──────────────────┐    ┌──────────────────┐
              │  ✅ 创建 Bean     │    │  ❌ 不创建 Bean  │
              │  注册到容器中     │    │  使用已有 Bean   │
              └──────────────────┘    └──────────────────┘
```

---

## 四、自动配置原理（核心重点）

### 4.1 自动配置全景图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Spring Boot 自动配置全景图                            │
└─────────────────────────────────────────────────────────────────────────────┘

     pom.xml                                       application.yml
        │                                                │
        ▼                                                ▼
┌───────────────┐                                ┌───────────────────┐
│ spring-boot-  │                                │ server.port=8080  │
│ starter-web   │                                │ spring.datasource │
└───────────────┘                                └───────────────────┘
        │                                                │
        │ 引入依赖                                        │ 提供配置值
        ▼                                                ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                   @EnableAutoConfiguration                          │  │
│   │                                                                     │  │
│   │  1. 读取 META-INF/spring/                                           │  │
│   │     org.springframework.boot.autoconfigure.AutoConfiguration.imports│  │
│   │                                                                     │  │
│   │  2. 加载所有自动配置类（约 150+ 个）                                  │  │
│   │     ├── DataSourceAutoConfiguration                                 │  │
│   │     ├── WebMvcAutoConfiguration                                     │  │
│   │     ├── JpaRepositoriesAutoConfiguration                            │  │
│   │     └── ...                                                         │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                              │                                              │
│                              ▼                                              │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                        条件过滤                                      │  │
│   │                                                                     │  │
│   │  @ConditionalOnClass      → 检查类路径依赖                           │  │
│   │  @ConditionalOnProperty   → 检查配置属性                             │  │
│   │  @ConditionalOnMissingBean→ 检查容器中是否已有                        │  │
│   │                                                                     │  │
│   │  结果：约 20-30 个配置类真正生效                                      │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                              │                                              │
│                              ▼                                              │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                     创建并注册 Bean                                   │  │
│   │                                                                     │  │
│   │  DispatcherServlet、TomcatServletWebServerFactory、                  │  │
│   │  DataSource、JdbcTemplate、TransactionManager ...                   │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 自动配置类源码分析

```java
/**
 * 以 DataSourceAutoConfiguration 为例，深入理解自动配置
 */
@AutoConfiguration(before = SqlInitializationAutoConfiguration.class)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
// ↑ 条件1：类路径必须存在 DataSource 类（引入了 JDBC 依赖）
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
// ↑ 条件2：不是响应式数据库连接
@EnableConfigurationProperties(DataSourceProperties.class)
// ↑ 绑定配置属性：将 spring.datasource.* 绑定到 DataSourceProperties
@Import({
    DataSourcePoolMetadataProvidersConfiguration.class,
    DataSourceInitializationConfiguration.class
})
public class DataSourceAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Conditional(EmbeddedDatabaseCondition.class)
    @ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
    @Import(EmbeddedDataSourceConfiguration.class)
    protected static class EmbeddedDatabaseConfiguration {
        // 如果没有配置数据源，使用内嵌数据库（H2、HSQL 等）
    }

    @Configuration(proxyBeanMethods = false)
    @Conditional(PooledDataSourceCondition.class)
    @ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
    @Import({
        HikariDataSourceConfiguration.class,  // 默认使用 HikariCP
        // Tomcat JDBC、DBCP2 等作为候选
    })
    protected static class PooledDataSourceConfiguration {
        // 配置连接池数据源
    }
}

/**
 * HikariCP 数据源配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HikariDataSource.class)  // 类路径存在 HikariDataSource
@ConditionalOnMissingBean(DataSource.class)   // 用户没有自定义 DataSource
@ConditionalOnProperty(
    name = "spring.datasource.type",
    havingValue = "com.zaxxer.hikari.HikariDataSource",
    matchIfMissing = true  // 默认就使用 HikariCP
)
class HikariDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(
            properties, HikariDataSource.class);
        // 绑定 spring.datasource.hikari.* 配置
        return dataSource;
    }
}
```

### 4.3 配置属性绑定机制

```java
/**
 * @ConfigurationProperties 将配置文件属性绑定到 Java 对象
 */
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private Class<? extends DataSource> type;

    // Getters & Setters
}

// application.yml 配置：
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb     # → url 属性
    username: root                             # → username 属性
    password: 123456                           # → password 属性
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:                                    # → HikariCP 特有配置
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     配置属性绑定过程                                      │
└─────────────────────────────────────────────────────────────────────────┘

  application.yml                         Java Bean
       │                                      │
       ▼                                      ▼
┌──────────────────┐                 ┌──────────────────────┐
│ spring:          │                 │ @ConfigurationProps  │
│   datasource:    │   ─────────────▶│ DataSourceProperties │
│     url: xxx     │    Binder       │   String url         │
│     username: xxx│                 │   String username    │
│     password: xxx│                 │   String password    │
└──────────────────┘                 └──────────────────────┘
                                              │
                                              ▼
                                     ┌──────────────────────┐
                                     │  自动配置类使用       │
                                     │  properties.getUrl() │
                                     │  创建 DataSource     │
                                     └──────────────────────┘
```

### 4.4 自动配置加载机制（Spring Boot 3.x）

```java
/**
 * Spring Boot 3.x 使用新的自动配置加载机制
 * 配置文件位置：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */

// 文件内容示例（每行一个配置类全限定名）：
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration
org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration
org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
... (约 150+ 个配置类)
```

---

## 五、启动流程深度剖析

### 5.1 启动流程全景图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Spring Boot 启动流程                                  │
└─────────────────────────────────────────────────────────────────────────────┘

main() 方法
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ ① SpringApplication.run(MyApp.class, args)                                 │
└─────────────────────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ ② new SpringApplication(primarySources)                                    │
│    ├── 推断 Web 应用类型（SERVLET / REACTIVE / NONE）                        │
│    ├── 加载 BootstrapRegistryInitializer                                   │
│    ├── 加载 ApplicationContextInitializer（上下文初始化器）                  │
│    ├── 加载 ApplicationListener（应用监听器）                                │
│    └── 推断主类（包含 main 方法的类）                                        │
└─────────────────────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ ③ run() 方法执行                                                            │
│    │                                                                        │
│    ├── 3.1 获取 SpringApplicationRunListeners                              │
│    │        发布 ApplicationStartingEvent                                  │
│    │                                                                        │
│    ├── 3.2 准备环境 prepareEnvironment()                                   │
│    │        ├── 创建 Environment（读取配置文件、环境变量、系统属性）           │
│    │        ├── 发布 ApplicationEnvironmentPreparedEvent                   │
│    │        └── 绑定 spring.main.* 属性                                    │
│    │                                                                        │
│    ├── 3.3 打印 Banner（Spring Boot Logo）                                 │
│    │                                                                        │
│    ├── 3.4 创建 ApplicationContext 容器                                    │
│    │        ├── SERVLET → AnnotationConfigServletWebServerApplicationContext│
│    │        ├── REACTIVE → AnnotationConfigReactiveWebServerApplicationContext│
│    │        └── NONE → AnnotationConfigApplicationContext                  │
│    │                                                                        │
│    ├── 3.5 准备上下文 prepareContext()                                     │
│    │        ├── 设置 Environment                                           │
│    │        ├── 执行 ApplicationContextInitializer                         │
│    │        ├── 发布 ApplicationContextInitializedEvent                    │
│    │        ├── 注册主类 BeanDefinition                                    │
│    │        └── 发布 ApplicationPreparedEvent                              │
│    │                                                                        │
│    ├── 3.6 刷新上下文 refreshContext() ⭐ 核心步骤                          │
│    │        └── 调用 AbstractApplicationContext.refresh()                  │
│    │            ├── invokeBeanFactoryPostProcessors (扫描+自动配置)         │
│    │            ├── registerBeanPostProcessors                             │
│    │            ├── onRefresh (创建 Web 服务器)                             │
│    │            └── finishBeanFactoryInitialization (实例化所有单例 Bean)   │
│    │                                                                        │
│    ├── 3.7 发布 ApplicationStartedEvent                                    │
│    │                                                                        │
│    ├── 3.8 执行 Runners                                                    │
│    │        ├── ApplicationRunner                                          │
│    │        └── CommandLineRunner                                          │
│    │                                                                        │
│    └── 3.9 发布 ApplicationReadyEvent（应用启动完成！）                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 核心源码逐行解析

```java
public class SpringApplication {

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class<?>[] { primarySource }, args);
    }

    public ConfigurableApplicationContext run(String... args) {
        // 记录启动时间
        long startTime = System.nanoTime();

        // 创建 BootstrapContext（引导上下文）
        DefaultBootstrapContext bootstrapContext = createBootstrapContext();
        ConfigurableApplicationContext context = null;

        // 设置 java.awt.headless 模式
        configureHeadlessProperty();

        // ① 获取 SpringApplicationRunListeners（启动监听器）
        SpringApplicationRunListeners listeners = getRunListeners(args);
        // 发布 ApplicationStartingEvent
        listeners.starting(bootstrapContext, this.mainApplicationClass);

        try {
            // 封装命令行参数
            ApplicationArguments applicationArguments =
                new DefaultApplicationArguments(args);

            // ② 准备环境（加载配置文件）
            ConfigurableEnvironment environment =
                prepareEnvironment(listeners, bootstrapContext, applicationArguments);

            // ③ 打印 Banner
            Banner printedBanner = printBanner(environment);

            // ④ 创建 Spring 容器
            context = createApplicationContext();
            context.setApplicationStartup(this.applicationStartup);

            // ⑤ 准备上下文
            prepareContext(bootstrapContext, context, environment,
                listeners, applicationArguments, printedBanner);

            // ⑥ 刷新上下文 ⭐ 核心：Bean 扫描、自动配置、依赖注入
            refreshContext(context);

            // ⑦ 后置处理（空方法，供子类扩展）
            afterRefresh(context, applicationArguments);

            // 计算启动耗时
            Duration timeTakenToStartup = Duration.ofNanos(
                System.nanoTime() - startTime);
            if (this.logStartupInfo) {
                new StartupInfoLogger(this.mainApplicationClass)
                    .logStarted(getApplicationLog(), timeTakenToStartup);
            }

            // 发布 ApplicationStartedEvent
            listeners.started(context, timeTakenToStartup);

            // ⑧ 执行 Runners
            callRunners(context, applicationArguments);
        }
        catch (Throwable ex) {
            handleRunFailure(context, ex, listeners);
            throw new IllegalStateException(ex);
        }

        try {
            // 发布 ApplicationReadyEvent
            Duration timeTakenToReady = Duration.ofNanos(
                System.nanoTime() - startTime);
            listeners.ready(context, timeTakenToReady);
        }
        catch (Throwable ex) {
            handleRunFailure(context, ex, null);
            throw new IllegalStateException(ex);
        }

        return context;
    }
}
```

### 5.3 refresh() 核心流程

```java
/**
 * AbstractApplicationContext.refresh() - Spring 容器的核心刷新方法
 */
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {

        // ① 准备刷新：设置启动时间、激活状态、初始化属性源
        prepareRefresh();

        // ② 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // ③ 准备 BeanFactory：设置类加载器、表达式解析器等
        prepareBeanFactory(beanFactory);

        try {
            // ④ 子类扩展点：Web 容器会注册 Web 相关的 Scope
            postProcessBeanFactory(beanFactory);

            // ⑤ 执行 BeanFactoryPostProcessor ⭐
            //    - ConfigurationClassPostProcessor 扫描 @Component
            //    - 解析 @Import、@Bean
            //    - 处理自动配置类
            invokeBeanFactoryPostProcessors(beanFactory);

            // ⑥ 注册 BeanPostProcessor
            registerBeanPostProcessors(beanFactory);

            // ⑦ 初始化消息源（国际化）
            initMessageSource();

            // ⑧ 初始化事件广播器
            initApplicationEventMulticaster();

            // ⑨ 子类扩展点：Web 容器创建内嵌服务器 ⭐
            onRefresh();

            // ⑩ 注册监听器
            registerListeners();

            // ⑪ 实例化所有非懒加载的单例 Bean ⭐
            finishBeanFactoryInitialization(beanFactory);

            // ⑫ 发布容器刷新完成事件
            finishRefresh();
        }
        catch (BeansException ex) {
            // 销毁已创建的 Bean
            destroyBeans();
            // 取消刷新
            cancelRefresh(ex);
            throw ex;
        }
        finally {
            // 清除缓存
            resetCommonCaches();
        }
    }
}
```

### 5.4 启动过程时序图

```
┌────────┐ ┌──────────────┐ ┌───────────────┐ ┌─────────────────┐ ┌──────────────┐
│  Main  │ │SpringApplic  │ │ Environment   │ │ApplicationContext│ │ Web Server   │
└────┬───┘ └──────┬───────┘ └──────┬────────┘ └────────┬────────┘ └──────┬───────┘
     │            │                │                   │                 │
     │  run()     │                │                   │                 │
     │───────────>│                │                   │                 │
     │            │                │                   │                 │
     │            │ create         │                   │                 │
     │            │───────────────>│                   │                 │
     │            │                │                   │                 │
     │            │ load configs   │                   │                 │
     │            │<───────────────│                   │                 │
     │            │                │                   │                 │
     │            │ create context │                   │                 │
     │            │───────────────────────────────────>│                 │
     │            │                │                   │                 │
     │            │                │    refresh()      │                 │
     │            │                │                   │────────┐        │
     │            │                │                   │        │        │
     │            │                │                   │<───────┘        │
     │            │                │                   │                 │
     │            │                │                   │  onRefresh()    │
     │            │                │                   │────────────────>│
     │            │                │                   │                 │
     │            │                │                   │    start()      │
     │            │                │                   │<────────────────│
     │            │                │                   │                 │
     │  context   │                │                   │                 │
     │<───────────│                │                   │                 │
     │            │                │                   │                 │
```

---

## 六、配置文件机制

### 6.1 配置文件类型与优先级

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        配置文件加载优先级（从高到低）                          │
└─────────────────────────────────────────────────────────────────────────────┘

优先级    配置来源                              示例
─────────────────────────────────────────────────────────────────────────────
   1     命令行参数                            java -jar app.jar --server.port=9090
   2     SPRING_APPLICATION_JSON               环境变量中的 JSON
   3     ServletConfig 初始化参数
   4     ServletContext 初始化参数
   5     java:comp/env 的 JNDI 属性
   6     Java 系统属性                         -Dserver.port=9090
   7     操作系统环境变量                       SERVER_PORT=9090
   8     RandomValuePropertySource            random.* 属性
   9     jar 包外的 profile 配置               config/application-{profile}.yml
  10     jar 包内的 profile 配置               application-{profile}.yml
  11     jar 包外的 application 配置           config/application.yml
  12     jar 包内的 application 配置           application.yml
  13     @PropertySource 注解
  14     默认属性                              SpringApplication.setDefaultProperties()
─────────────────────────────────────────────────────────────────────────────

配置文件位置搜索顺序（从高到低）：
  1. file:./config/                    # 项目根目录/config/
  2. file:./config/*/                  # 项目根目录/config/子目录/
  3. file:./                           # 项目根目录
  4. classpath:/config/                # 类路径/config/
  5. classpath:/                       # 类路径根目录
```

### 6.2 多环境配置

```yaml
# application.yml - 主配置文件
spring:
  profiles:
    active: dev  # 激活开发环境

# 公共配置
server:
  servlet:
    context-path: /api

---
# application-dev.yml - 开发环境
spring:
  config:
    activate:
      on-profile: dev

server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dev_db
    username: dev_user
    password: dev_pass

logging:
  level:
    root: DEBUG
    com.example: DEBUG

---
# application-prod.yml - 生产环境
spring:
  config:
    activate:
      on-profile: prod

server:
  port: 80

spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/prod_db
    username: ${DB_USER}      # 从环境变量读取
    password: ${DB_PASSWORD}  # 敏感信息不硬编码

logging:
  level:
    root: WARN
    com.example: INFO
```

### 6.3 配置绑定高级用法

```java
/**
 * 复杂配置绑定示例
 */
@ConfigurationProperties(prefix = "myapp")
@Validated  // 启用 JSR-303 校验
public class MyAppProperties {

    @NotNull
    private String name;

    @Min(1)
    @Max(65535)
    private int port = 8080;  // 默认值

    private Duration timeout = Duration.ofSeconds(30);  // 支持 Duration

    private DataSize maxFileSize = DataSize.ofMegabytes(10);  // 支持 DataSize

    private List<String> servers = new ArrayList<>();  // 支持 List

    private Map<String, String> headers = new HashMap<>();  // 支持 Map

    private final Security security = new Security();  // 嵌套对象

    // 嵌套配置类
    public static class Security {
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>();
        // Getters & Setters
    }

    // Getters & Setters
}

// application.yml
myapp:
  name: MyApplication
  port: 9090
  timeout: 30s           # 支持多种时间格式：30s、30000ms、1m
  max-file-size: 10MB    # 支持多种大小格式：10MB、10240KB
  servers:
    - server1.example.com
    - server2.example.com
  headers:
    X-Custom-Header: custom-value
    X-App-Name: ${myapp.name}   # 支持占位符引用
  security:
    username: admin
    password: ${ADMIN_PASSWORD:default123}  # 环境变量 + 默认值
    roles:
      - ADMIN
      - USER
```

```java
/**
 * 启用配置属性
 */
@Configuration
@EnableConfigurationProperties(MyAppProperties.class)
public class MyAppConfig {

    private final MyAppProperties properties;

    public MyAppConfig(MyAppProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MyService myService() {
        return new MyService(
            properties.getName(),
            properties.getTimeout()
        );
    }
}
```

### 6.4 配置文件占位符与表达式

```yaml
# 占位符使用
app:
  name: MyApp
  description: ${app.name} is a Spring Boot application
  version: @project.version@  # Maven 资源过滤

# 随机值
random:
  secret: ${random.value}           # 随机字符串
  number: ${random.int}             # 随机整数
  bignumber: ${random.long}         # 随机长整数
  uuid: ${random.uuid}              # 随机 UUID
  range: ${random.int[1024,65535]}  # 范围内随机整数

# 默认值
server:
  port: ${SERVER_PORT:8080}         # 环境变量不存在时使用 8080
  address: ${SERVER_ADDRESS:localhost}
```

---

## 七、Web 开发

### 7.1 内嵌 Web 服务器

```java
/**
 * Spring Boot 支持三种内嵌服务器
 */
// 默认使用 Tomcat
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

// 切换到 Jetty
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>

// 切换到 Undertow（性能最好）
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

### 7.2 Web 服务器配置

```yaml
server:
  port: 8080
  address: 0.0.0.0
  servlet:
    context-path: /api
    session:
      timeout: 30m

  # Tomcat 特有配置
  tomcat:
    max-threads: 200 # 最大工作线程数
    min-spare-threads: 10 # 最小空闲线程
    max-connections: 10000 # 最大连接数
    accept-count: 100 # 等待队列长度
    connection-timeout: 20000 # 连接超时
    uri-encoding: UTF-8

  # 压缩配置
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,application/json
    min-response-size: 1024

  # SSL 配置
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
```

### 7.3 MVC 自动配置

```java
/**
 * WebMvcAutoConfiguration 自动配置的内容
 */
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Spring MVC 自动配置                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ✅ DispatcherServlet              - 前端控制器                             │
│  ✅ RequestMappingHandlerMapping   - 请求映射处理器                          │
│  ✅ RequestMappingHandlerAdapter   - 请求处理适配器                          │
│  ✅ HandlerExceptionResolver       - 异常处理器                             │
│  ✅ ViewResolver                   - 视图解析器                             │
│  ✅ HttpMessageConverters          - 消息转换器 (JSON/XML)                   │
│  ✅ ContentNegotiationManager      - 内容协商管理器                          │
│  ✅ 静态资源处理                    - /static, /public, /resources          │
│  ✅ Favicon 处理                   - /favicon.ico                           │
│  ✅ 欢迎页处理                      - index.html                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

/**
 * 自定义 MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**");
    }

    // 自定义消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 添加自定义转换器
    }

    // 自定义视图控制器
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    // 跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 静态资源配置
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:/data/files/")
                .setCachePeriod(3600);
    }
}
```

### 7.4 统一异常处理

```java
/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    // 处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    // 处理资源未找到
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(ResourceNotFoundException e) {
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    // 处理所有未捕获异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SYSTEM_ERROR", "系统繁忙，请稍后重试"));
    }
}

// 统一响应格式
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private long timestamp = System.currentTimeMillis();

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

---

## 八、数据访问

### 8.1 数据源自动配置

```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

    # HikariCP 连接池配置（默认）
    hikari:
      pool-name: MyHikariPool
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 30000
      connection-test-query: SELECT 1
```

### 8.2 JPA 整合

```java
// 依赖
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

// 实体类
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}

// Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 方法名派生查询
    Optional<User> findByUsername(String username);

    List<User> findByStatusAndCreatedAtAfter(UserStatus status, LocalDateTime date);

    // 分页查询
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // 自定义 JPQL
    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword%")
    List<User> searchByEmail(@Param("keyword") String keyword);

    // 原生 SQL
    @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
    List<User> findRecentUsers(@Param("date") LocalDateTime date);

    // 修改操作
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") UserStatus status);
}
```

### 8.3 MyBatis 整合

```java
// 依赖
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

// 配置
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    lazy-loading-enabled: true

// Mapper 接口
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO users(username, email) VALUES(#{username}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // 复杂查询使用 XML
    List<User> findByCondition(UserQuery query);

    // 动态 SQL（注解方式）
    @SelectProvider(type = UserSqlProvider.class, method = "selectByCondition")
    List<User> selectByCondition(UserQuery query);
}
```

```xml
<!-- mapper/UserMapper.xml -->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.UserMapper">

    <resultMap id="UserResultMap" type="User">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="email" column="email"/>
        <result property="createdAt" column="created_at"/>
        <collection property="orders" ofType="Order">
            <id property="id" column="order_id"/>
            <result property="amount" column="amount"/>
        </collection>
    </resultMap>

    <select id="findByCondition" resultMap="UserResultMap">
        SELECT * FROM users
        <where>
            <if test="username != null">
                AND username LIKE CONCAT('%', #{username}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
            <if test="startDate != null">
                AND created_at >= #{startDate}
            </if>
        </where>
        ORDER BY created_at DESC
        <if test="limit != null">
            LIMIT #{limit}
        </if>
    </select>

</mapper>
```

---

## 九、自定义 Starter

### 9.1 Starter 结构

```
自定义 Starter 命名规范：
  - 官方：spring-boot-starter-xxx
  - 第三方：xxx-spring-boot-starter

项目结构：
my-spring-boot-starter/
├── pom.xml
└── src/main/java/
    └── com/example/mystarter/
        ├── MyAutoConfiguration.java      # 自动配置类
        ├── MyProperties.java             # 配置属性类
        └── MyService.java                # 核心服务类
└── src/main/resources/
    └── META-INF/
        └── spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

### 9.2 完整实现示例

```java
/**
 * 1. 配置属性类
 */
@ConfigurationProperties(prefix = "my.starter")
public class MyStarterProperties {

    private boolean enabled = true;
    private String prefix = "[MyStarter] ";
    private String suffix = "";

    // Getters & Setters
}

/**
 * 2. 核心服务类
 */
public class MyStarterService {

    private final MyStarterProperties properties;

    public MyStarterService(MyStarterProperties properties) {
        this.properties = properties;
    }

    public String wrap(String content) {
        if (!properties.isEnabled()) {
            return content;
        }
        return properties.getPrefix() + content + properties.getSuffix();
    }
}

/**
 * 3. 自动配置类
 */
@AutoConfiguration
@ConditionalOnClass(MyStarterService.class)
@EnableConfigurationProperties(MyStarterProperties.class)
@ConditionalOnProperty(
    prefix = "my.starter",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class MyStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean  // 允许用户自定义覆盖
    public MyStarterService myStarterService(MyStarterProperties properties) {
        return new MyStarterService(properties);
    }
}

/**
 * 4. 配置提示（可选）
 * resources/META-INF/spring-configuration-metadata.json
 */
{
  "properties": [
    {
      "name": "my.starter.enabled",
      "type": "java.lang.Boolean",
      "description": "是否启用 MyStarter",
      "defaultValue": true
    },
    {
      "name": "my.starter.prefix",
      "type": "java.lang.String",
      "description": "内容前缀",
      "defaultValue": "[MyStarter] "
    }
  ]
}

/**
 * 5. 注册自动配置
 * resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */
com.example.mystarter.MyStarterAutoConfiguration
```

### 9.3 使用自定义 Starter

```java
// 引入依赖
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

// 配置
my:
  starter:
    enabled: true
    prefix: ">>> "
    suffix: " <<<"

// 使用
@RestController
public class DemoController {

    @Autowired
    private MyStarterService myStarterService;

    @GetMapping("/wrap")
    public String wrap(@RequestParam String content) {
        return myStarterService.wrap(content);  // >>> content <<<
    }
}
```

---

## 十、监控与运维

### 10.1 Actuator 端点

```yaml
# 依赖
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

# 配置
management:
  endpoints:
    web:
      exposure:
        include: "*"  # 暴露所有端点（生产环境谨慎配置）
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    shutdown:
      enabled: true  # 允许优雅关闭
```

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        常用 Actuator 端点                                    │
├──────────────────┬──────────────────────────────────────────────────────────┤
│   端点           │   描述                                                   │
├──────────────────┼──────────────────────────────────────────────────────────┤
│   /health        │   应用健康状态                                           │
│   /info          │   应用信息                                               │
│   /metrics       │   应用指标（内存、CPU、请求数等）                          │
│   /env           │   环境属性                                               │
│   /configprops   │   配置属性                                               │
│   /beans         │   所有 Bean 信息                                         │
│   /mappings      │   所有请求映射                                           │
│   /loggers       │   日志级别（可动态修改）                                   │
│   /threaddump    │   线程转储                                               │
│   /heapdump      │   堆转储（下载 hprof 文件）                               │
│   /shutdown      │   优雅关闭应用                                           │
│   /prometheus    │   Prometheus 格式指标（需引入 micrometer-registry-prometheus）│
└──────────────────┴──────────────────────────────────────────────────────────┘
```

### 10.2 自定义健康检查

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT 1");
            ps.executeQuery();
            return Health.up()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "Connected")
                    .build();
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

### 10.3 自定义 Metrics

```java
@Component
public class OrderMetrics {

    private final Counter orderCounter;
    private final Timer orderProcessTimer;

    public OrderMetrics(MeterRegistry registry) {
        // 计数器
        this.orderCounter = Counter.builder("orders.created")
                .description("Number of orders created")
                .tag("type", "online")
                .register(registry);

        // 计时器
        this.orderProcessTimer = Timer.builder("orders.process.time")
                .description("Order processing time")
                .register(registry);
    }

    public void recordOrderCreated() {
        orderCounter.increment();
    }

    public void recordProcessTime(Runnable action) {
        orderProcessTimer.record(action);
    }
}
```

---

## 十一、面试高频问题

### 11.1 核心概念类

```
Q1: Spring Boot 的核心特性是什么？
─────────────────────────────────────────
A: 四大核心特性：
   ① 自动配置：根据 classpath 依赖自动装配 Bean
   ② 起步依赖：一站式依赖管理，避免版本冲突
   ③ 内嵌容器：无需外部服务器，直接运行 JAR
   ④ Actuator：生产就绪特性，健康检查、指标监控

Q2: @SpringBootApplication 做了什么？
─────────────────────────────────────────
A: 它是组合注解，包含：
   ① @SpringBootConfiguration：标识配置类
   ② @EnableAutoConfiguration：开启自动配置
   ③ @ComponentScan：组件扫描主类所在包

Q3: 自动配置原理？
─────────────────────────────────────────
A: ① 读取 META-INF/spring/*.imports 文件
   ② 加载所有自动配置类
   ③ 通过条件注解过滤（@ConditionalOnClass 等）
   ④ 符合条件的配置类生效，注册 Bean

Q4: 如何禁用特定的自动配置？
─────────────────────────────────────────
A: 三种方式：
   ① @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
   ② spring.autoconfigure.exclude=xxx.AutoConfiguration
   ③ 自定义同类型 Bean（@ConditionalOnMissingBean 失效）
```

### 11.2 实战问题类

```
Q5: 配置文件加载顺序？
─────────────────────────────────────────
A: 命令行参数 > 环境变量 > config/ 目录 > classpath
   profile 配置 > 主配置文件
   外部配置 > 内部配置

Q6: 如何实现多环境配置？
─────────────────────────────────────────
A: ① 创建 application-{profile}.yml
   ② 设置 spring.profiles.active=dev/prod/test
   ③ 命令行：--spring.profiles.active=prod

Q7: Spring Boot 启动时做了什么？
─────────────────────────────────────────
A: ① 创建 SpringApplication 对象
   ② 加载配置文件，创建 Environment
   ③ 创建 ApplicationContext 容器
   ④ 执行自动配置，注册 Bean
   ⑤ 创建并启动内嵌 Web 服务器
   ⑥ 发布 ApplicationReadyEvent

Q8: 如何自定义 Starter？
─────────────────────────────────────────
A: ① 创建配置属性类 @ConfigurationProperties
   ② 创建自动配置类 @AutoConfiguration + 条件注解
   ③ 注册到 META-INF/spring/*.imports
   ④ 打包发布
```

---

## 知识体系总结

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Spring Boot 知识体系                                  │
└─────────────────────────────────────────────────────────────────────────────┘

                           Spring Boot
                               │
       ┌───────────────────────┼───────────────────────┐
       │                       │                       │
   ┌───▼───┐             ┌─────▼─────┐            ┌────▼────┐
   │ 核心  │             │   开发    │            │  运维   │
   │ 原理  │             │   功能    │            │  监控   │
   └───┬───┘             └─────┬─────┘            └────┬────┘
       │                       │                       │
       ├── 自动配置            ├── Web 开发            ├── Actuator
       │   └── 条件注解        │   ├── 内嵌容器        │   ├── 健康检查
       │   └── SPI 机制        │   ├── 静态资源        │   ├── 指标监控
       │                       │   └── 异常处理        │   └── 日志管理
       ├── 启动流程            │                       │
       │   └── SpringApp       ├── 数据访问            ├── 优雅关闭
       │   └── refresh()       │   ├── DataSource      │
       │                       │   ├── JPA/MyBatis     ├── 配置刷新
       ├── 配置机制            │   └── 事务            │
       │   └── 加载顺序        │                       │
       │   └── 属性绑定        ├── 安全认证            │
       │   └── 多环境          │   └── Spring Security │
       │                       │                       │
       └── 自定义 Starter      └── 缓存/消息           │
                                   ├── Redis           │
                                   └── Kafka/RabbitMQ  │
                                                       │
                               ┌───────────────────────┘
                               │
                           ┌───▼───┐
                           │ 部署  │
                           └───┬───┘
                               │
                               ├── Docker 容器化
                               ├── Kubernetes 编排
                               └── CI/CD 流水线
```

---

如需深入了解某个具体主题（如 Spring Security 整合、异步处理、响应式编程等），请告诉我！
