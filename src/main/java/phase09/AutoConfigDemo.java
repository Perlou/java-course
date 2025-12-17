package phase09;

/**
 * Phase 9 - Lesson 1: Spring Boot 自动配置
 * 
 * 🎯 学习目标:
 * 1. 理解 Spring Boot 的设计理念
 * 2. 掌握自动配置原理
 * 3. 了解 @SpringBootApplication 的作用
 */
public class AutoConfigDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 9 - Lesson 1: Spring Boot 自动配置");
        System.out.println("=".repeat(60));

        // 1. Spring Boot 简介
        System.out.println("\n【1. Spring Boot 简介】");
        System.out.println("""
                Spring Boot 设计理念:

                ┌─────────────────────────────────────────────────────────┐
                │ "约定优于配置" (Convention over Configuration)          │
                │                                                         │
                │ Spring:      需要大量 XML/注解配置                      │
                │ Spring Boot: 自动配置，开箱即用                        │
                └─────────────────────────────────────────────────────────┘

                Spring Boot 核心特性:
                1. 快速创建独立运行的 Spring 应用
                2. 内嵌 Tomcat/Jetty/Undertow
                3. 提供 starter 依赖简化配置
                4. 自动配置 Spring 和第三方库
                5. 无代码生成，无需 XML 配置
                6. 提供生产级监控和外部化配置
                """);

        // 2. @SpringBootApplication
        System.out.println("=".repeat(60));
        System.out.println("【2. @SpringBootApplication 注解】");
        System.out.println("""
                @SpringBootApplication 是一个组合注解:

                @SpringBootApplication
                = @SpringBootConfiguration   (Spring Boot 配置类)
                + @EnableAutoConfiguration   (启用自动配置)
                + @ComponentScan             (组件扫描)

                ┌─────────────────────────────────────────────────────────┐
                │  @SpringBootApplication                                 │
                │  ├── @SpringBootConfiguration                          │
                │  │   └── @Configuration (标识配置类)                   │
                │  ├── @EnableAutoConfiguration                          │
                │  │   └── @Import(AutoConfigurationImportSelector.class)│
                │  └── @ComponentScan                                    │
                │       └── 扫描当前包及子包下的组件                     │
                └─────────────────────────────────────────────────────────┘

                示例:
                @SpringBootApplication
                public class MyApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(MyApplication.class, args);
                    }
                }
                """);

        // 3. 自动配置原理
        System.out.println("=".repeat(60));
        System.out.println("【3. 自动配置原理】");
        System.out.println("""
                自动配置流程:

                1. SpringApplication.run() 启动
                        ↓
                2. 加载 META-INF/spring.factories (Spring Boot 2.x)
                   或 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (3.x)
                        ↓
                3. 获取所有 AutoConfiguration 类
                        ↓
                4. 根据 @Conditional 条件过滤
                        ↓
                5. 加载满足条件的配置类

                spring.factories 示例:
                ┌─────────────────────────────────────────────────────────┐
                │ # spring-boot-autoconfigure.jar                        │
                │ org.springframework.boot.autoconfigure.EnableAutoConfiguration=\\
                │ org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\\
                │ org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\\
                │ org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
                └─────────────────────────────────────────────────────────┘
                """);

        // 4. 条件装配
        System.out.println("=".repeat(60));
        System.out.println("【4. 条件装配 @Conditional】");
        System.out.println("""
                常用条件注解:

                ┌──────────────────────────┬────────────────────────────────┐
                │        注解              │           条件                 │
                ├──────────────────────────┼────────────────────────────────┤
                │ @ConditionalOnClass      │ 类路径存在指定类               │
                │ @ConditionalOnMissingClass│ 类路径不存在指定类            │
                │ @ConditionalOnBean       │ 容器中存在指定 Bean            │
                │ @ConditionalOnMissingBean│ 容器中不存在指定 Bean          │
                │ @ConditionalOnProperty   │ 指定属性有特定值               │
                │ @ConditionalOnExpression │ SpEL 表达式为 true             │
                │ @ConditionalOnResource   │ 类路径存在指定资源             │
                │ @ConditionalOnWebApplication│ Web 环境                   │
                └──────────────────────────┴────────────────────────────────┘

                示例 - DataSource 自动配置:

                @Configuration
                @ConditionalOnClass(DataSource.class)  // 有 DataSource 类
                @ConditionalOnProperty(name = "spring.datasource.url")  // 配置了 url
                public class DataSourceAutoConfiguration {

                    @Bean
                    @ConditionalOnMissingBean  // 用户没有自定义时才创建
                    public DataSource dataSource(DataSourceProperties props) {
                        return props.initializeDataSourceBuilder().build();
                    }
                }
                """);

        // 5. 自定义自动配置
        System.out.println("=".repeat(60));
        System.out.println("【5. 自定义自动配置】");
        System.out.println("""
                创建自己的 AutoConfiguration:

                1. 创建配置类
                @Configuration
                @ConditionalOnClass(MyService.class)
                @EnableConfigurationProperties(MyProperties.class)
                public class MyAutoConfiguration {

                    @Bean
                    @ConditionalOnMissingBean
                    public MyService myService(MyProperties props) {
                        return new MyService(props.getName());
                    }
                }

                2. 配置属性类
                @ConfigurationProperties(prefix = "my.service")
                public class MyProperties {
                    private String name = "default";
                    // getter/setter
                }

                3. 注册自动配置 (Spring Boot 3.x)
                META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports:
                com.example.MyAutoConfiguration
                """);

        // 6. 查看自动配置报告
        System.out.println("=".repeat(60));
        System.out.println("【6. 自动配置调试】");
        System.out.println("""
                查看哪些自动配置生效:

                方法 1: 启动参数
                java -jar app.jar --debug

                方法 2: 配置文件
                debug=true

                方法 3: Actuator
                /actuator/conditions

                输出示例:
                ┌─────────────────────────────────────────────────────────┐
                │ Positive matches: (满足条件，已加载)                    │
                │ - DataSourceAutoConfiguration matched:                  │
                │   - @ConditionalOnClass found required class            │
                │                                                         │
                │ Negative matches: (不满足条件，未加载)                  │
                │ - MongoAutoConfiguration:                              │
                │   - @ConditionalOnClass did not find required class    │
                └─────────────────────────────────────────────────────────┘
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Spring Boot 核心: 约定优于配置");
        System.out.println("💡 @SpringBootApplication = 配置 + 自动配置 + 扫描");
        System.out.println("💡 使用 @Conditional 实现条件装配");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Spring Boot 核心:
 * - 约定优于配置
 * - 自动配置 + Starter
 * 
 * 2. @SpringBootApplication:
 * - @Configuration
 * - @EnableAutoConfiguration
 * - @ComponentScan
 * 
 * 3. 自动配置原理:
 * - 加载 spring.factories / AutoConfiguration.imports
 * - @Conditional 条件过滤
 * - 创建符合条件的 Bean
 * 
 * 4. 常用条件注解:
 * - @ConditionalOnClass
 * - @ConditionalOnMissingBean
 * - @ConditionalOnProperty
 */
