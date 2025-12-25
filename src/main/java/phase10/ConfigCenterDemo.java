package phase10;

/**
 * Phase 10 - Lesson 7: 配置中心
 * 
 * 🎯 学习目标:
 * 1. 理解配置中心的作用
 * 2. 掌握 Nacos Config 的使用
 * 3. 学会动态配置刷新
 */
public class ConfigCenterDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 10 - Lesson 7: 配置中心");
        System.out.println("=".repeat(60));

        // 1. 为什么需要配置中心
        System.out.println("\n【1. 为什么需要配置中心】");
        System.out.println("""
                传统配置方式的问题:

                ┌────────────────────────────────────────────────────────┐
                │  每个服务都有自己的配置文件                            │
                │                                                        │
                │  user-service/                                         │
                │  └── application.yml  ← 配置打包在 Jar 中              │
                │                                                        │
                │  order-service/                                        │
                │  └── application.yml  ← 修改需要重新部署               │
                │                                                        │
                │  问题:                                                 │
                │  1. 配置分散，难以统一管理                             │
                │  2. 修改配置需要重启服务                               │
                │  3. 敏感信息暴露在代码仓库                             │
                │  4. 多环境配置管理困难                                 │
                └────────────────────────────────────────────────────────┘

                配置中心解决方案:

                ┌──────────────────────────────────────────────────────────┐
                │                                                          │
                │           ┌──────────────┐                              │
                │           │  配置中心     │                              │
                │           │  Nacos Config │                              │
                │           └──────┬───────┘                              │
                │                  │ 配置下发                              │
                │      ┌───────────┼───────────┐                          │
                │      ▼           ▼           ▼                          │
                │  ┌────────┐ ┌────────┐ ┌────────┐                      │
                │  │ user   │ │ order  │ │ product│                      │
                │  │ service│ │ service│ │ service│                      │
                │  └────────┘ └────────┘ └────────┘                      │
                │                                                          │
                │  优点:                                                   │
                │  - 配置集中管理                                         │
                │  - 动态刷新，无需重启                                   │
                │  - 配置版本管理                                         │
                │  - 环境隔离                                             │
                │  - 敏感配置加密                                         │
                └──────────────────────────────────────────────────────────┘
                """);

        // 2. Nacos Config 简介
        System.out.println("=".repeat(60));
        System.out.println("【2. Nacos Config 简介】");
        System.out.println("""
                Nacos = 服务发现 + 配置管理

                配置管理核心概念:

                ┌─────────────────────────────────────────────────────────┐
                │  Namespace (命名空间) - 环境隔离                        │
                │  ├── dev                                                │
                │  ├── test                                               │
                │  └── prod                                               │
                │                                                         │
                │  Group (分组) - 项目/业务隔离                          │
                │  ├── DEFAULT_GROUP                                      │
                │  ├── ORDER_GROUP                                        │
                │  └── PAYMENT_GROUP                                      │
                │                                                         │
                │  Data ID (配置文件ID) - 配置标识                        │
                │  格式: ${prefix}-${spring.profiles.active}.${file-extension}
                │  例如: user-service-dev.yaml                            │
                └─────────────────────────────────────────────────────────┘

                配置文件定位规则:
                1. ${prefix} 默认为 spring.application.name
                2. ${file-extension} 默认为 properties
                3. 可以添加 spring.profiles.active 后缀
                """);

        // 3. 快速开始
        System.out.println("=".repeat(60));
        System.out.println("【3. 快速开始】");
        System.out.println("""
                1. 添加依赖

                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
                </dependency>

                <!-- Spring Cloud 2021+ 需要 bootstrap -->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-bootstrap</artifactId>
                </dependency>

                2. 创建 bootstrap.yml (而不是 application.yml)

                spring:
                  application:
                    name: user-service
                  profiles:
                    active: dev
                  cloud:
                    nacos:
                      config:
                        server-addr: localhost:8848
                        file-extension: yaml  # 配置文件格式
                        namespace: dev        # 命名空间 ID
                        group: DEFAULT_GROUP  # 分组

                ⚠️ 注意: 必须使用 bootstrap.yml，因为配置中心需要在应用启动前加载

                3. 在 Nacos 控制台创建配置

                Data ID: user-service-dev.yaml
                Group: DEFAULT_GROUP
                配置格式: YAML
                配置内容:
                server:
                  port: 8080

                spring:
                  datasource:
                    url: jdbc:mysql://localhost:3306/user_db
                    username: root
                    password: 123456

                app:
                  name: User Service
                  version: 1.0.0
                """);

        // 4. 读取配置
        System.out.println("=".repeat(60));
        System.out.println("【4. 读取配置】");
        System.out.println("""
                方式 1: @Value 注入

                @RestController
                public class ConfigController {

                    @Value("${app.name}")
                    private String appName;

                    @Value("${app.version}")
                    private String appVersion;

                    @GetMapping("/config")
                    public String getConfig() {
                        return appName + " v" + appVersion;
                    }
                }

                方式 2: @ConfigurationProperties (推荐)

                @Data
                @Component
                @ConfigurationProperties(prefix = "app")
                public class AppConfig {
                    private String name;
                    private String version;
                }

                @RestController
                @RequiredArgsConstructor
                public class ConfigController {
                    private final AppConfig appConfig;

                    @GetMapping("/config")
                    public AppConfig getConfig() {
                        return appConfig;
                    }
                }
                """);

        // 5. 动态刷新
        System.out.println("=".repeat(60));
        System.out.println("【5. 动态刷新配置】");
        System.out.println("""
                配置修改后自动刷新，无需重启服务

                方式 1: @RefreshScope (适用于 @Value)

                @RestController
                @RefreshScope  // 关键注解
                public class ConfigController {

                    @Value("${app.name}")
                    private String appName;

                    @GetMapping("/config")
                    public String getConfig() {
                        return appName;  // 配置修改后自动更新
                    }
                }

                方式 2: @ConfigurationProperties (自动刷新)

                @Data
                @Component
                @ConfigurationProperties(prefix = "app")
                public class AppConfig {
                    private String name;   // 自动刷新
                    private String version;
                }

                ⚠️ @ConfigurationProperties 默认支持动态刷新
                ⚠️ @Value 需要配合 @RefreshScope 使用

                监听配置变更:

                @Component
                public class ConfigChangeListener {

                    @NacosConfigListener(dataId = "user-service-dev.yaml", groupId = "DEFAULT_GROUP")
                    public void onConfigChange(String config) {
                        log.info("配置变更: {}", config);
                        // 自定义处理逻辑
                    }
                }
                """);

        // 6. 多配置文件
        System.out.println("=".repeat(60));
        System.out.println("【6. 多配置文件】");
        System.out.println("""
                一个服务可以加载多个配置文件:

                spring:
                  cloud:
                    nacos:
                      config:
                        server-addr: localhost:8848
                        file-extension: yaml
                        # 默认配置文件: user-service-dev.yaml

                        # 扩展配置 (优先级高于默认)
                        extension-configs:
                          - data-id: common.yaml
                            group: DEFAULT_GROUP
                            refresh: true

                          - data-id: database.yaml
                            group: DEFAULT_GROUP
                            refresh: true

                        # 共享配置 (优先级最低)
                        shared-configs:
                          - data-id: shared-redis.yaml
                            group: DEFAULT_GROUP
                            refresh: true

                配置优先级 (从低到高):
                1. shared-configs (共享配置)
                2. extension-configs (扩展配置)
                3. ${application.name}-${profile}.${extension} (默认配置)
                4. ${application.name}.${extension} (主配置)

                典型场景:
                - shared-configs: 所有服务共享的配置 (Redis, 日志等)
                - extension-configs: 本服务特定的配置
                - 默认配置: 服务主配置
                """);

        // 7. 环境隔离
        System.out.println("=".repeat(60));
        System.out.println("【7. 多环境配置】");
        System.out.println("""
                使用 Namespace 隔离环境:

                在 Nacos 控制台创建命名空间:
                - dev (开发环境)
                - test (测试环境)
                - prod (生产环境)

                bootstrap.yml:

                spring:
                  profiles:
                    active: dev  # 当前环境
                  cloud:
                    nacos:
                      config:
                        server-addr: localhost:8848
                        namespace: ${NACOS_NAMESPACE:dev}  # 从环境变量读取

                不同环境启动:

                # 开发环境
                java -jar app.jar --spring.profiles.active=dev

                # 生产环境
                java -jar app.jar \\
                  --spring.profiles.active=prod \\
                  -DNACOS_NAMESPACE=prod_namespace_id

                配置结构:
                ┌─────────────────────────────────────────────────────────┐
                │  dev 命名空间                                          │
                │  ├── user-service-dev.yaml                             │
                │  └── order-service-dev.yaml                            │
                │                                                         │
                │  prod 命名空间                                         │
                │  ├── user-service-prod.yaml                            │
                │  └── order-service-prod.yaml                           │
                └─────────────────────────────────────────────────────────┘
                """);

        // 8. 敏感配置加密
        System.out.println("=".repeat(60));
        System.out.println("【8. 敏感配置处理】");
        System.out.println("""
                方案 1: 使用环境变量

                在 Nacos 配置中使用占位符:
                spring:
                  datasource:
                    password: ${DB_PASSWORD}

                启动时传入:
                java -jar app.jar -DDB_PASSWORD=secret123

                方案 2: 使用 Jasypt 加密

                1. 添加依赖
                <dependency>
                    <groupId>com.github.ulisesbocchio</groupId>
                    <artifactId>jasypt-spring-boot-starter</artifactId>
                    <version>3.0.5</version>
                </dependency>

                2. 配置加密密钥
                jasypt:
                  encryptor:
                    password: ${JASYPT_PASSWORD}  # 加密密钥

                3. 加密敏感值
                # 使用 jasypt 加密
                java -cp jasypt.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \\
                  algorithm=PBEWITHHMACSHA512ANDAES_256 \\
                  password=mySecretKey \\
                  input=realPassword

                4. 配置使用 ENC() 包裹
                spring:
                  datasource:
                    password: ENC(加密后的密文)

                方案 3: Nacos 配置加密 (企业版)

                Nacos 企业版支持配置加密存储
                """);

        // 9. 最佳实践
        System.out.println("=".repeat(60));
        System.out.println("【9. 最佳实践】");
        System.out.println("""
                1. 配置分层

                shared-configs:     # 全局共享
                  - redis.yaml     # Redis 配置
                  - logging.yaml   # 日志配置

                extension-configs:  # 服务特定
                  - database.yaml  # 数据库配置

                默认配置:           # 业务配置
                  - user-service.yaml

                2. 配置命名规范

                ${服务名}-${环境}.yaml
                例: user-service-prod.yaml

                3. 敏感信息外部化

                数据库密码、API Key 等使用环境变量

                4. 配置变更审计

                Nacos 支持配置历史查看和回滚

                5. 灰度发布配置

                先在部分实例验证，再全量推送

                6. 配置监控

                监听配置变更，记录日志
                重要配置变更发送告警
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 配置中心实现配置集中管理和动态刷新");
        System.out.println("💡 使用 bootstrap.yml 配置 Nacos Config");
        System.out.println("💡 @ConfigurationProperties 自动支持动态刷新");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 配置中心作用:
 * - 集中管理
 * - 动态刷新
 * - 环境隔离
 * 
 * 2. Nacos Config:
 * - Namespace 环境隔离
 * - Group 项目隔离
 * - Data ID 配置标识
 * 
 * 3. 配置加载:
 * - bootstrap.yml 配置
 * - 多配置文件支持
 * - 优先级规则
 * 
 * 4. 动态刷新:
 * - @RefreshScope
 * - @ConfigurationProperties
 */
