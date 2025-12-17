package phase09;

/**
 * Phase 9 - Lesson 3: 配置管理
 * 
 * 🎯 学习目标:
 * 1. 掌握 application.yml/properties
 * 2. 理解配置优先级
 * 3. 学会多环境配置
 */
public class ConfigurationDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 9 - Lesson 3: 配置管理");
        System.out.println("=".repeat(60));

        // 1. 配置文件类型
        System.out.println("\n【1. 配置文件类型】");
        System.out.println("""
                两种配置文件:

                application.properties:
                ┌─────────────────────────────────────────────────────────┐
                │ server.port=8080                                       │
                │ spring.datasource.url=jdbc:mysql://localhost:3306/db   │
                │ spring.datasource.username=root                        │
                │ spring.datasource.password=123456                      │
                └─────────────────────────────────────────────────────────┘

                application.yml (推荐):
                ┌─────────────────────────────────────────────────────────┐
                │ server:                                                │
                │   port: 8080                                           │
                │                                                         │
                │ spring:                                                │
                │   datasource:                                          │
                │     url: jdbc:mysql://localhost:3306/db                │
                │     username: root                                     │
                │     password: 123456                                   │
                └─────────────────────────────────────────────────────────┘

                YAML 优点:
                - 层次分明
                - 可读性好
                - 支持复杂类型
                """);

        // 2. 配置绑定
        System.out.println("=".repeat(60));
        System.out.println("【2. 配置绑定】");
        System.out.println("""
                方式 1: @Value 注入

                @Component
                public class MyConfig {
                    @Value("${server.port}")
                    private int port;

                    @Value("${app.name:DefaultApp}")  // 带默认值
                    private String appName;
                }

                方式 2: @ConfigurationProperties (推荐)

                @ConfigurationProperties(prefix = "app")
                @Component
                public class AppProperties {
                    private String name;
                    private String version;
                    private List<String> servers;

                    // getter/setter
                }

                对应配置:
                app:
                  name: MyApp
                  version: 1.0.0
                  servers:
                    - server1.com
                    - server2.com

                使用:
                @Autowired
                private AppProperties appProperties;
                """);

        // 3. 配置优先级
        System.out.println("=".repeat(60));
        System.out.println("【3. 配置优先级】");
        System.out.println("""
                从高到低:

                1. 命令行参数
                   java -jar app.jar --server.port=9090

                2. Java 系统属性
                   java -Dserver.port=9090 -jar app.jar

                3. 环境变量
                   export SERVER_PORT=9090 (注意下划线和大写)

                4. application-{profile}.yml
                   application-prod.yml

                5. application.yml
                   src/main/resources/application.yml

                6. @PropertySource
                   @PropertySource("classpath:custom.properties")

                7. 默认值
                   @Value("${port:8080}")
                """);

        // 4. 多环境配置 Profile
        System.out.println("=".repeat(60));
        System.out.println("【4. 多环境配置 (Profile)】");
        System.out.println("""
                配置文件:
                ├── application.yml           (公共配置)
                ├── application-dev.yml       (开发环境)
                ├── application-test.yml      (测试环境)
                └── application-prod.yml      (生产环境)

                application.yml:
                spring:
                  profiles:
                    active: dev  # 激活的环境

                application-dev.yml:
                server:
                  port: 8080
                spring:
                  datasource:
                    url: jdbc:mysql://localhost:3306/dev_db

                application-prod.yml:
                server:
                  port: 80
                spring:
                  datasource:
                    url: jdbc:mysql://prod-server:3306/prod_db

                激活方式:
                1. 配置文件: spring.profiles.active=prod
                2. 命令行: --spring.profiles.active=prod
                3. 环境变量: SPRING_PROFILES_ACTIVE=prod
                """);

        // 5. 多文档块
        System.out.println("=".repeat(60));
        System.out.println("【5. 单文件多环境配置】");
        System.out.println("""
                application.yml (使用 --- 分隔):

                # 公共配置
                spring:
                  application:
                    name: my-app

                ---
                # 开发环境
                spring:
                  config:
                    activate:
                      on-profile: dev
                server:
                  port: 8080

                ---
                # 生产环境
                spring:
                  config:
                    activate:
                      on-profile: prod
                server:
                  port: 80
                """);

        // 6. 敏感配置加密
        System.out.println("=".repeat(60));
        System.out.println("【6. 敏感配置处理】");
        System.out.println("""
                ⚠️ 敏感信息不要明文存储!

                方案 1: 环境变量
                spring:
                  datasource:
                    password: ${DB_PASSWORD}

                方案 2: Jasypt 加密
                spring:
                  datasource:
                    password: ENC(加密后的密文)

                方案 3: 配置中心
                - Spring Cloud Config
                - Nacos Config
                - Apollo

                方案 4: Vault
                - HashiCorp Vault
                - 专业的密钥管理
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 推荐使用 YAML 格式配置");
        System.out.println("💡 使用 @ConfigurationProperties 绑定配置");
        System.out.println("💡 使用 Profile 管理多环境配置");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 配置文件:
 * - application.properties
 * - application.yml (推荐)
 * 
 * 2. 配置绑定:
 * - @Value
 * - @ConfigurationProperties (推荐)
 * 
 * 3. 多环境配置:
 * - application-{profile}.yml
 * - spring.profiles.active
 * 
 * 4. 配置优先级:
 * 命令行 > 系统属性 > 环境变量 > 配置文件
 */
