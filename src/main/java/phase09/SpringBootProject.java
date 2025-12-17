package phase09;

/**
 * Phase 9 - 实战项目: RESTful API 开发指南
 * 
 * 🎯 项目目标:
 * 1. 创建一个完整的 Spring Boot 项目
 * 2. 实现 RESTful API
 * 3. 集成数据库和认证
 * 
 * ⚠️ 本文件是项目开发指南，需要使用 Spring Initializr 创建实际项目
 */
public class SpringBootProject {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   🚀 Spring Boot 实战项目指南");
        System.out.println("=".repeat(60));

        // 1. 创建项目
        System.out.println("\n【1. 创建项目 - Spring Initializr】");
        System.out.println("""
                访问: https://start.spring.io

                配置:
                ┌─────────────────────────────────────────────────────────┐
                │ Project:      Maven                                    │
                │ Language:     Java                                     │
                │ Spring Boot:  3.2.x                                    │
                │ Group:        com.example                              │
                │ Artifact:     user-service                             │
                │ Packaging:    Jar                                      │
                │ Java:         17 或 21                                 │
                │                                                         │
                │ Dependencies:                                           │
                │ - Spring Web                                           │
                │ - Spring Data JPA                                      │
                │ - H2 Database (开发用)                                 │
                │ - Spring Boot Actuator                                 │
                │ - Spring Validation                                    │
                │ - Lombok                                               │
                └─────────────────────────────────────────────────────────┘

                或使用命令行:
                curl https://start.spring.io/starter.zip \\
                  -d dependencies=web,data-jpa,h2,actuator,validation,lombok \\
                  -d name=user-service \\
                  -d type=maven-project \\
                  -d javaVersion=17 \\
                  -o user-service.zip
                """);

        // 2. 项目结构
        System.out.println("=".repeat(60));
        System.out.println("【2. 项目结构】");
        System.out.println("""
                user-service/
                ├── src/main/java/com/example/userservice/
                │   ├── UserServiceApplication.java     (启动类)
                │   ├── config/                         (配置类)
                │   │   ├── SecurityConfig.java
                │   │   └── SwaggerConfig.java
                │   ├── controller/                     (控制器)
                │   │   └── UserController.java
                │   ├── service/                        (服务层)
                │   │   ├── UserService.java
                │   │   └── impl/
                │   │       └── UserServiceImpl.java
                │   ├── repository/                     (数据层)
                │   │   └── UserRepository.java
                │   ├── entity/                         (实体)
                │   │   └── User.java
                │   ├── dto/                            (数据传输对象)
                │   │   ├── UserRequest.java
                │   │   └── UserResponse.java
                │   └── exception/                      (异常处理)
                │       ├── GlobalExceptionHandler.java
                │       └── ResourceNotFoundException.java
                │
                └── src/main/resources/
                    ├── application.yml
                    └── application-dev.yml
                """);

        // 3. 实体定义
        System.out.println("=".repeat(60));
        System.out.println("【3. 实体定义】");
        System.out.println("""
                // entity/User.java

                @Entity
                @Table(name = "users")
                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                @Builder
                public class User {

                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                    @Column(nullable = false, length = 50)
                    private String username;

                    @Column(unique = true)
                    @Email
                    private String email;

                    @Column(nullable = false)
                    private String password;

                    @Enumerated(EnumType.STRING)
                    private UserStatus status = UserStatus.ACTIVE;

                    @CreatedDate
                    private LocalDateTime createdAt;

                    @LastModifiedDate
                    private LocalDateTime updatedAt;
                }

                public enum UserStatus {
                    ACTIVE, INACTIVE, DELETED
                }
                """);

        // 4. Repository
        System.out.println("=".repeat(60));
        System.out.println("【4. Repository】");
        System.out.println("""
                // repository/UserRepository.java

                @Repository
                public interface UserRepository extends JpaRepository<User, Long> {

                    Optional<User> findByUsername(String username);

                    Optional<User> findByEmail(String email);

                    boolean existsByEmail(String email);

                    List<User> findByStatus(UserStatus status);

                    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
                    List<User> findRecentUsers(@Param("date") LocalDateTime date);
                }
                """);

        // 5. Service
        System.out.println("=".repeat(60));
        System.out.println("【5. Service】");
        System.out.println("""
                // service/UserService.java

                public interface UserService {
                    UserResponse create(UserRequest request);
                    UserResponse findById(Long id);
                    List<UserResponse> findAll();
                    UserResponse update(Long id, UserRequest request);
                    void delete(Long id);
                }

                // service/impl/UserServiceImpl.java

                @Service
                @RequiredArgsConstructor
                @Transactional
                public class UserServiceImpl implements UserService {

                    private final UserRepository userRepository;
                    private final PasswordEncoder passwordEncoder;

                    @Override
                    public UserResponse create(UserRequest request) {
                        if (userRepository.existsByEmail(request.getEmail())) {
                            throw new DuplicateResourceException("Email already exists");
                        }

                        User user = User.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .build();

                        User saved = userRepository.save(user);
                        return toResponse(saved);
                    }

                    @Override
                    @Transactional(readOnly = true)
                    public UserResponse findById(Long id) {
                        User user = userRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("User", id));
                        return toResponse(user);
                    }

                    private UserResponse toResponse(User user) {
                        return UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .status(user.getStatus())
                            .createdAt(user.getCreatedAt())
                            .build();
                    }
                }
                """);

        // 6. Controller
        System.out.println("=".repeat(60));
        System.out.println("【6. Controller】");
        System.out.println("""
                // controller/UserController.java

                @RestController
                @RequestMapping("/api/users")
                @RequiredArgsConstructor
                @Validated
                public class UserController {

                    private final UserService userService;

                    @GetMapping
                    public ResponseEntity<List<UserResponse>> findAll() {
                        return ResponseEntity.ok(userService.findAll());
                    }

                    @GetMapping("/{id}")
                    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
                        return ResponseEntity.ok(userService.findById(id));
                    }

                    @PostMapping
                    public ResponseEntity<UserResponse> create(
                            @Valid @RequestBody UserRequest request) {
                        UserResponse response = userService.create(request);
                        return ResponseEntity
                            .created(URI.create("/api/users/" + response.getId()))
                            .body(response);
                    }

                    @PutMapping("/{id}")
                    public ResponseEntity<UserResponse> update(
                            @PathVariable Long id,
                            @Valid @RequestBody UserRequest request) {
                        return ResponseEntity.ok(userService.update(id, request));
                    }

                    @DeleteMapping("/{id}")
                    public ResponseEntity<Void> delete(@PathVariable Long id) {
                        userService.delete(id);
                        return ResponseEntity.noContent().build();
                    }
                }
                """);

        // 7. 异常处理
        System.out.println("=".repeat(60));
        System.out.println("【7. 全局异常处理】");
        System.out.println("""
                // exception/GlobalExceptionHandler.java

                @RestControllerAdvice
                @Slf4j
                public class GlobalExceptionHandler {

                    @ExceptionHandler(ResourceNotFoundException.class)
                    @ResponseStatus(HttpStatus.NOT_FOUND)
                    public ErrorResponse handleNotFound(ResourceNotFoundException e) {
                        log.warn("Resource not found: {}", e.getMessage());
                        return new ErrorResponse(404, e.getMessage());
                    }

                    @ExceptionHandler(MethodArgumentNotValidException.class)
                    @ResponseStatus(HttpStatus.BAD_REQUEST)
                    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
                        Map<String, String> errors = new HashMap<>();
                        e.getBindingResult().getFieldErrors().forEach(err ->
                            errors.put(err.getField(), err.getDefaultMessage())
                        );
                        return new ErrorResponse(400, "Validation failed", errors);
                    }

                    @ExceptionHandler(Exception.class)
                    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    public ErrorResponse handleAll(Exception e) {
                        log.error("Unexpected error", e);
                        return new ErrorResponse(500, "Internal server error");
                    }
                }

                @Data
                @AllArgsConstructor
                public class ErrorResponse {
                    private int code;
                    private String message;
                    private Object details;

                    public ErrorResponse(int code, String message) {
                        this(code, message, null);
                    }
                }
                """);

        // 8. 配置文件
        System.out.println("=".repeat(60));
        System.out.println("【8. 配置文件】");
        System.out.println("""
                # application.yml

                spring:
                  application:
                    name: user-service
                  profiles:
                    active: dev
                  jpa:
                    hibernate:
                      ddl-auto: update
                    show-sql: true

                management:
                  endpoints:
                    web:
                      exposure:
                        include: health,info,metrics

                ---
                # application-dev.yml

                server:
                  port: 8080

                spring:
                  datasource:
                    url: jdbc:h2:mem:testdb
                    driver-class-name: org.h2.Driver
                  h2:
                    console:
                      enabled: true
                      path: /h2-console

                ---
                # application-prod.yml

                server:
                  port: 80

                spring:
                  datasource:
                    url: jdbc:mysql://localhost:3306/userdb
                    username: ${DB_USERNAME}
                    password: ${DB_PASSWORD}
                """);

        // 9. 测试
        System.out.println("=".repeat(60));
        System.out.println("【9. API 测试】");
        System.out.println("""
                启动应用:
                mvn spring-boot:run

                测试 API:

                # 创建用户
                curl -X POST http://localhost:8080/api/users \\
                  -H "Content-Type: application/json" \\
                  -d '{"username":"john","email":"john@example.com","password":"123456"}'

                # 查询用户
                curl http://localhost:8080/api/users
                curl http://localhost:8080/api/users/1

                # 更新用户
                curl -X PUT http://localhost:8080/api/users/1 \\
                  -H "Content-Type: application/json" \\
                  -d '{"username":"john_updated","email":"john@example.com"}'

                # 删除用户
                curl -X DELETE http://localhost:8080/api/users/1

                # 健康检查
                curl http://localhost:8080/actuator/health

                # H2 控制台
                http://localhost:8080/h2-console
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 使用 Spring Initializr 快速创建项目");
        System.out.println("💡 遵循分层架构: Controller → Service → Repository");
        System.out.println("💡 使用 DTO 隔离实体和接口");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 项目总结:
 * 
 * RESTful API 开发要点:
 * 
 * 1. 分层架构:
 * - Controller: 接口层
 * - Service: 业务层
 * - Repository: 数据层
 * 
 * 2. 数据传输:
 * - Entity: 数据库映射
 * - DTO: 接口传输
 * 
 * 3. 异常处理:
 * - @RestControllerAdvice
 * - 统一响应格式
 * 
 * 4. 配置管理:
 * - 多环境配置
 * - 敏感信息外部化
 * 
 * 🎯 扩展任务:
 * 1. 添加 Swagger 文档
 * 2. 添加 JWT 认证
 * 3. 添加单元测试
 * 4. 添加分页和排序
 */
