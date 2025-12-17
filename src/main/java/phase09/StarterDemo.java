package phase09;

/**
 * Phase 9 - Lesson 2: Starter 机制
 * 
 * 🎯 学习目标:
 * 1. 理解 Starter 的作用
 * 2. 掌握常用 Starter
 * 3. 学会创建自定义 Starter
 */
public class StarterDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 9 - Lesson 2: Starter 机制");
        System.out.println("=".repeat(60));

        // 1. Starter 简介
        System.out.println("\n【1. Starter 简介】");
        System.out.println("""
                Starter 是什么?

                ┌─────────────────────────────────────────────────────────┐
                │ Starter = 依赖集合 + 自动配置                           │
                │                                                         │
                │ 传统方式:                                               │
                │ - 手动添加多个依赖                                      │
                │ - 手动写配置类                                          │
                │ - 手动配置属性                                          │
                │                                                         │
                │ Starter 方式:                                           │
                │ - 一个依赖搞定                                          │
                │ - 自动配置                                              │
                │ - 合理默认值                                            │
                └─────────────────────────────────────────────────────────┘

                命名规范:
                - 官方: spring-boot-starter-{name}
                - 三方: {name}-spring-boot-starter
                """);

        // 2. 常用 Starter
        System.out.println("=".repeat(60));
        System.out.println("【2. 常用 Starter】");
        System.out.println("""
                核心 Starter:
                ┌──────────────────────────────────┬────────────────────────┐
                │            Starter               │         功能           │
                ├──────────────────────────────────┼────────────────────────┤
                │ spring-boot-starter              │ 核心，包含自动配置     │
                │ spring-boot-starter-web          │ Web 应用，含 Tomcat    │
                │ spring-boot-starter-json         │ JSON 处理              │
                │ spring-boot-starter-logging      │ Logback 日志           │
                │ spring-boot-starter-test         │ 测试支持               │
                └──────────────────────────────────┴────────────────────────┘

                数据访问:
                ┌──────────────────────────────────┬────────────────────────┐
                │ spring-boot-starter-data-jpa     │ Spring Data JPA        │
                │ spring-boot-starter-data-redis   │ Redis 支持             │
                │ spring-boot-starter-data-mongodb │ MongoDB 支持           │
                │ spring-boot-starter-jdbc         │ JDBC 支持              │
                └──────────────────────────────────┴────────────────────────┘

                其他常用:
                ┌──────────────────────────────────┬────────────────────────┐
                │ spring-boot-starter-security     │ Spring Security        │
                │ spring-boot-starter-validation   │ 数据校验               │
                │ spring-boot-starter-actuator     │ 监控和管理             │
                │ spring-boot-starter-mail         │ 邮件发送               │
                │ spring-boot-starter-cache        │ 缓存支持               │
                └──────────────────────────────────┴────────────────────────┘
                """);

        // 3. Starter 内部结构
        System.out.println("=".repeat(60));
        System.out.println("【3. Starter 内部结构】");
        System.out.println("""
                以 spring-boot-starter-web 为例:

                spring-boot-starter-web
                ├── spring-boot-starter (核心)
                │   ├── spring-boot
                │   ├── spring-boot-autoconfigure
                │   └── spring-core, spring-context...
                ├── spring-boot-starter-json
                │   └── jackson-databind
                ├── spring-boot-starter-tomcat
                │   └── tomcat-embed-core
                ├── spring-web
                └── spring-webmvc

                关键点:
                - Starter 本身不包含代码
                - 只是聚合依赖
                - 自动配置在 spring-boot-autoconfigure 中
                """);

        // 4. 创建自定义 Starter
        System.out.println("=".repeat(60));
        System.out.println("【4. 创建自定义 Starter】");
        System.out.println("""
                项目结构:

                my-starter/
                ├── my-starter-autoconfigure/  (自动配置模块)
                │   ├── pom.xml
                │   └── src/
                │       └── main/
                │           ├── java/
                │           │   └── com/example/
                │           │       ├── MyService.java
                │           │       ├── MyProperties.java
                │           │       └── MyAutoConfiguration.java
                │           └── resources/
                │               └── META-INF/
                │                   └── spring/
                │                       └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
                │
                └── my-spring-boot-starter/    (Starter 模块)
                    └── pom.xml  (只依赖 autoconfigure 模块)

                步骤:
                1. 创建服务类
                2. 创建属性配置类
                3. 创建自动配置类
                4. 注册自动配置
                5. 创建 Starter 模块聚合依赖
                """);

        // 5. 自定义 Starter 代码示例
        System.out.println("=".repeat(60));
        System.out.println("【5. 自定义 Starter 代码】");
        System.out.println("""
                // 1. 服务类
                public class GreetingService {
                    private final String message;

                    public GreetingService(String message) {
                        this.message = message;
                    }

                    public String greet(String name) {
                        return message + ", " + name + "!";
                    }
                }

                // 2. 属性配置类
                @ConfigurationProperties(prefix = "greeting")
                public class GreetingProperties {
                    private String message = "Hello";

                    // getter/setter
                }

                // 3. 自动配置类
                @AutoConfiguration
                @ConditionalOnClass(GreetingService.class)
                @EnableConfigurationProperties(GreetingProperties.class)
                public class GreetingAutoConfiguration {

                    @Bean
                    @ConditionalOnMissingBean
                    public GreetingService greetingService(GreetingProperties props) {
                        return new GreetingService(props.getMessage());
                    }
                }

                // 4. 使用
                @RestController
                public class HelloController {
                    @Autowired
                    private GreetingService greetingService;

                    @GetMapping("/hello")
                    public String hello(@RequestParam String name) {
                        return greetingService.greet(name);
                    }
                }

                // application.yml
                greeting:
                  message: "Welcome"
                """);

        // 6. 依赖管理
        System.out.println("=".repeat(60));
        System.out.println("【6. 依赖管理】");
        System.out.println("""
                Spring Boot 依赖管理:

                <parent>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-parent</artifactId>
                    <version>3.2.0</version>
                </parent>

                好处:
                - 统一版本管理
                - 无需指定版本号
                - 避免依赖冲突

                不用 parent 的方式:
                <dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-dependencies</artifactId>
                            <version>3.2.0</version>
                            <type>pom</type>
                            <scope>import</scope>
                        </dependency>
                    </dependencies>
                </dependencyManagement>
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Starter = 依赖集合 + 自动配置");
        System.out.println("💡 官方 Starter: spring-boot-starter-*");
        System.out.println("💡 三方 Starter: *-spring-boot-starter");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Starter 作用:
 * - 简化依赖管理
 * - 自动配置
 * 
 * 2. 常用 Starter:
 * - web, data-jpa, security
 * - test, actuator
 * 
 * 3. 自定义 Starter:
 * - autoconfigure 模块
 * - starter 模块
 * - 注册 AutoConfiguration
 */
