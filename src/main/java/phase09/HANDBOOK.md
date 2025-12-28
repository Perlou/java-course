# Spring Boot 常用注解详解

## 一、核心启动注解

```java
@SpringBootApplication  // 组合注解，包含下面三个
├── @SpringBootConfiguration  // 标识配置类
├── @EnableAutoConfiguration  // 开启自动配置
└── @ComponentScan            // 组件扫描
```

---

## 二、控制器注解

| 注解              | 说明                                         |
| ----------------- | -------------------------------------------- |
| `@Controller`     | 标识控制器，返回视图                         |
| `@RestController` | = `@Controller` + `@ResponseBody`，返回 JSON |
| `@ResponseBody`   | 将返回值序列化为 JSON                        |

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ...
}
```

---

## 三、请求映射注解

```java
// 通用映射
@RequestMapping(value = "/path", method = RequestMethod.GET)

// 简化写法（推荐）
@GetMapping("/users")        // 查询
@PostMapping("/users")       // 新增
@PutMapping("/users/{id}")   // 修改
@DeleteMapping("/users/{id}") // 删除
@PatchMapping("/users/{id}") // 部分更新
```

---

## 四、参数绑定注解

```java
@GetMapping("/users/{id}")
public User getUser(
    @PathVariable Long id,                    // 路径参数: /users/1
    @RequestParam String name,                // 查询参数: ?name=xxx
    @RequestParam(defaultValue = "1") int page,
    @RequestHeader("Authorization") String token,  // 请求头
    @CookieValue("sessionId") String session,      // Cookie
    @RequestBody UserDTO dto                       // 请求体JSON
) {
    // ...
}
```

| 注解             | 用途              | 示例          |
| ---------------- | ----------------- | ------------- |
| `@PathVariable`  | 获取 URL 路径变量 | `/users/{id}` |
| `@RequestParam`  | 获取查询参数      | `?name=xxx`   |
| `@RequestBody`   | 获取请求体(JSON)  | POST 请求体   |
| `@RequestHeader` | 获取请求头        | Authorization |
| `@CookieValue`   | 获取 Cookie 值    | sessionId     |

---

## 五、依赖注入注解

```java
@Service
public class UserService {

    // ✅ 推荐：构造器注入
    private final UserRepository userRepository;

    @Autowired  // Spring 4.3+ 单构造器可省略
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 字段注入
    @Autowired
    private OrderService orderService;

    // 按名称注入
    @Resource(name = "redisTemplate")
    private RedisTemplate template;

    // 注入配置值
    @Value("${app.name}")
    private String appName;
}
```

| 注解         | 说明                          |
| ------------ | ----------------------------- |
| `@Autowired` | 按类型自动注入                |
| `@Resource`  | 按名称注入（JSR-250）         |
| `@Qualifier` | 配合@Autowired 指定 Bean 名称 |
| `@Value`     | 注入配置文件值                |

---

## 六、组件注册注解

```
@Component        // 通用组件
    ├── @Controller   // 控制层
    ├── @Service      // 业务层
    ├── @Repository   // 数据层（有异常转换功能）
    └── @Configuration // 配置类
```

---

## 七、配置类注解

```java
@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    @Primary  // 同类型多个Bean时，优先使用此Bean
    public DataSource primaryDataSource() {
        return new HikariDataSource();
    }
}
```

```java
// 配置属性绑定
@ConfigurationProperties(prefix = "app.datasource")
@Component
public class DataSourceProperties {
    private String url;
    private String username;
    // getters/setters
}
```

---

## 八、条件注解

```java
@ConditionalOnClass(RedisTemplate.class)      // 类存在时生效
@ConditionalOnMissingClass("com.xxx.Xxx")     // 类不存在时生效
@ConditionalOnBean(DataSource.class)          // Bean存在时生效
@ConditionalOnMissingBean                     // Bean不存在时生效
@ConditionalOnProperty(name="enabled", havingValue="true")  // 配置匹配时生效
@ConditionalOnWebApplication                  // Web环境时生效
```

---

## 九、数据校验注解

```java
public class UserDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "长度2-20")
    private String username;

    @Email(message = "邮箱格式错误")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Min(0) @Max(150)
    private Integer age;
}

// 控制器中使用
@PostMapping("/users")
public Result create(@Valid @RequestBody UserDTO dto) {
    // ...
}
```

---

## 十、JPA/数据库注解

```java
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", length = 50, nullable = false)
    private String username;

    @Transient  // 不映射到数据库
    private String temp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @CreatedDate
    private LocalDateTime createTime;
}
```

---

## 十一、事务注解

```java
@Service
public class OrderService {

    @Transactional(
        rollbackFor = Exception.class,      // 回滚异常类型
        propagation = Propagation.REQUIRED, // 传播行为
        isolation = Isolation.READ_COMMITTED, // 隔离级别
        timeout = 30                        // 超时时间(秒)
    )
    public void createOrder(Order order) {
        // 事务操作
    }
}
```

---

## 十二、AOP 注解

```java
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    @Before("serviceLayer()")
    public void before(JoinPoint jp) { }

    @After("serviceLayer()")
    public void after(JoinPoint jp) { }

    @Around("serviceLayer()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 前置逻辑
        Object result = pjp.proceed();
        // 后置逻辑
        return result;
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void afterReturning(Object result) { }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void afterThrowing(Exception ex) { }
}
```

---

## 十三、定时任务注解

```java
@EnableScheduling  // 启动类添加
@Component
public class ScheduledTasks {

    @Scheduled(fixedRate = 5000)         // 每5秒执行
    @Scheduled(fixedDelay = 5000)        // 上次完成后5秒执行
    @Scheduled(cron = "0 0 2 * * ?")     // cron表达式（每天凌晨2点）
    public void task() {
        // ...
    }
}
```

---

## 十四、异步注解

```java
@EnableAsync  // 启动类添加
@Service
public class AsyncService {

    @Async
    public CompletableFuture<String> asyncMethod() {
        // 异步执行
        return CompletableFuture.completedFuture("done");
    }
}
```

---

## 📋 注解速查表

| 分类       | 常用注解                                              |
| ---------- | ----------------------------------------------------- |
| **启动**   | `@SpringBootApplication`                              |
| **控制器** | `@RestController`, `@RequestMapping`                  |
| **参数**   | `@PathVariable`, `@RequestParam`, `@RequestBody`      |
| **注入**   | `@Autowired`, `@Resource`, `@Value`                   |
| **组件**   | `@Component`, `@Service`, `@Repository`               |
| **配置**   | `@Configuration`, `@Bean`, `@ConfigurationProperties` |
| **校验**   | `@Valid`, `@NotNull`, `@NotBlank`                     |
| **事务**   | `@Transactional`                                      |
| **AOP**    | `@Aspect`, `@Around`, `@Before`                       |
| **定时**   | `@Scheduled`, `@EnableScheduling`                     |
