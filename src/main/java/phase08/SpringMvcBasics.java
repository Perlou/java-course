package phase08;

/**
 * Phase 8 - Lesson 4: Spring MVC
 * 
 * 🎯 学习目标:
 * 1. 理解 MVC 架构模式
 * 2. 掌握 Spring MVC 工作流程
 * 3. 了解常用注解和组件
 */
public class SpringMvcBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 4: Spring MVC");
        System.out.println("=".repeat(60));

        // 1. MVC 架构模式
        System.out.println("\n【1. MVC 架构模式】");
        System.out.println("""
                MVC = Model-View-Controller

                ┌─────────────────────────────────────────────────────────┐
                │                     MVC 架构                            │
                │                                                         │
                │    ┌─────────┐    ┌─────────────┐    ┌─────────┐       │
                │    │  View   │◀───│ Controller  │───▶│  Model  │       │
                │    │  (视图)  │    │   (控制器)   │    │ (模型)  │       │
                │    └─────────┘    └─────────────┘    └─────────┘       │
                │         │                │                │            │
                │         │                │                │            │
                │    展示数据          处理请求          业务数据         │
                │    用户界面          调度流程          业务逻辑         │
                └─────────────────────────────────────────────────────────┘

                各层职责:
                - Model: 数据和业务逻辑
                - View: 用户界面展示
                - Controller: 接收请求，调用 Model，选择 View
                """);

        // 2. Spring MVC 工作流程
        System.out.println("=".repeat(60));
        System.out.println("【2. Spring MVC 工作流程】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                Spring MVC 处理流程                       │
                │                                                         │
                │   客户端                                                │
                │     │                                                   │
                │     │ 1. HTTP 请求                                     │
                │     ▼                                                   │
                │  ┌───────────────────┐                                 │
                │  │ DispatcherServlet │  前端控制器                     │
                │  └─────────┬─────────┘                                 │
                │            │ 2. 查询 Handler                           │
                │            ▼                                           │
                │  ┌───────────────────┐                                 │
                │  │  HandlerMapping   │  处理器映射                     │
                │  └─────────┬─────────┘                                 │
                │            │ 3. 返回 HandlerExecutionChain             │
                │            ▼                                           │
                │  ┌───────────────────┐                                 │
                │  │  HandlerAdapter   │  处理器适配器                   │
                │  └─────────┬─────────┘                                 │
                │            │ 4. 调用 Handler                           │
                │            ▼                                           │
                │  ┌───────────────────┐                                 │
                │  │    Controller     │  控制器 (我们编写)              │
                │  └─────────┬─────────┘                                 │
                │            │ 5. 返回 ModelAndView                      │
                │            ▼                                           │
                │  ┌───────────────────┐                                 │
                │  │   ViewResolver    │  视图解析器                     │
                │  └─────────┬─────────┘                                 │
                │            │ 6. 渲染视图                               │
                │            ▼                                           │
                │   客户端 (响应)                                        │
                └─────────────────────────────────────────────────────────┘
                """);

        // 3. 常用注解
        System.out.println("=".repeat(60));
        System.out.println("【3. Spring MVC 常用注解】");
        System.out.println("""
                控制器注解:
                ┌──────────────────┬────────────────────────────────────┐
                │      注解         │              说明                  │
                ├──────────────────┼────────────────────────────────────┤
                │ @Controller       │ 标识控制器类                      │
                │ @RestController   │ @Controller + @ResponseBody       │
                │ @RequestMapping   │ 映射请求路径                      │
                │ @GetMapping       │ 映射 GET 请求                     │
                │ @PostMapping      │ 映射 POST 请求                    │
                │ @PutMapping       │ 映射 PUT 请求                     │
                │ @DeleteMapping    │ 映射 DELETE 请求                  │
                └──────────────────┴────────────────────────────────────┘

                参数绑定注解:
                ┌──────────────────┬────────────────────────────────────┐
                │ @RequestParam    │ 绑定请求参数 ?name=value          │
                │ @PathVariable    │ 绑定路径变量 /users/{id}          │
                │ @RequestBody     │ 绑定请求体 (JSON → 对象)          │
                │ @RequestHeader   │ 绑定请求头                        │
                │ @CookieValue     │ 绑定 Cookie 值                    │
                │ @ModelAttribute  │ 绑定表单到对象                    │
                └──────────────────┴────────────────────────────────────┘

                响应注解:
                ┌──────────────────┬────────────────────────────────────┐
                │ @ResponseBody    │ 返回值写入响应体 (对象 → JSON)    │
                │ @ResponseStatus  │ 设置响应状态码                    │
                └──────────────────┴────────────────────────────────────┘
                """);

        // 4. 示例代码
        System.out.println("=".repeat(60));
        System.out.println("【4. RESTful Controller 示例】");
        System.out.println("""
                @RestController
                @RequestMapping("/api/users")
                public class UserController {

                    @Autowired
                    private UserService userService;

                    // GET /api/users
                    @GetMapping
                    public List<User> list() {
                        return userService.findAll();
                    }

                    // GET /api/users/123
                    @GetMapping("/{id}")
                    public User getById(@PathVariable Long id) {
                        return userService.findById(id);
                    }

                    // GET /api/users?name=张三
                    @GetMapping(params = "name")
                    public List<User> search(@RequestParam String name) {
                        return userService.findByName(name);
                    }

                    // POST /api/users
                    @PostMapping
                    @ResponseStatus(HttpStatus.CREATED)
                    public User create(@RequestBody @Valid User user) {
                        return userService.save(user);
                    }

                    // PUT /api/users/123
                    @PutMapping("/{id}")
                    public User update(@PathVariable Long id, @RequestBody User user) {
                        user.setId(id);
                        return userService.update(user);
                    }

                    // DELETE /api/users/123
                    @DeleteMapping("/{id}")
                    @ResponseStatus(HttpStatus.NO_CONTENT)
                    public void delete(@PathVariable Long id) {
                        userService.delete(id);
                    }
                }
                """);

        // 5. RESTful 设计规范
        System.out.println("=".repeat(60));
        System.out.println("【5. RESTful API 设计】");
        System.out.println("""
                RESTful API 设计原则:

                1. 使用名词表示资源
                   ✅ /users, /orders, /products
                   ❌ /getUsers, /createOrder

                2. HTTP 方法表示操作
                   GET    - 查询
                   POST   - 创建
                   PUT    - 全量更新
                   PATCH  - 部分更新
                   DELETE - 删除

                3. 使用复数
                   ✅ /users
                   ❌ /user

                4. 层级关系
                   /users/{userId}/orders
                   /orders/{orderId}/items

                5. 状态码
                   200 OK - 成功
                   201 Created - 创建成功
                   204 No Content - 删除成功
                   400 Bad Request - 请求错误
                   401 Unauthorized - 未认证
                   403 Forbidden - 无权限
                   404 Not Found - 资源不存在
                   500 Internal Error - 服务器错误
                """);

        // 6. 统一异常处理
        System.out.println("=".repeat(60));
        System.out.println("【6. 统一异常处理】");
        System.out.println("""
                @RestControllerAdvice
                public class GlobalExceptionHandler {

                    @ExceptionHandler(ResourceNotFoundException.class)
                    @ResponseStatus(HttpStatus.NOT_FOUND)
                    public ErrorResponse handleNotFound(ResourceNotFoundException e) {
                        return new ErrorResponse(404, e.getMessage());
                    }

                    @ExceptionHandler(MethodArgumentNotValidException.class)
                    @ResponseStatus(HttpStatus.BAD_REQUEST)
                    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
                        String msg = e.getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .map(f -> f.getField() + ": " + f.getDefaultMessage())
                            .collect(Collectors.joining(", "));
                        return new ErrorResponse(400, msg);
                    }

                    @ExceptionHandler(Exception.class)
                    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    public ErrorResponse handleAll(Exception e) {
                        log.error("Unexpected error", e);
                        return new ErrorResponse(500, "服务器内部错误");
                    }
                }

                record ErrorResponse(int code, String message) {}
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 @RestController = @Controller + @ResponseBody");
        System.out.println("💡 RESTful: 资源名词 + HTTP 动词");
        System.out.println("💡 使用 @RestControllerAdvice 统一处理异常");
        System.out.println("=".repeat(60));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. MVC 架构:
 * - Model: 数据/业务
 * - View: 视图/展示
 * - Controller: 控制/调度
 * 
 * 2. Spring MVC 流程:
 * DispatcherServlet → HandlerMapping → HandlerAdapter
 * → Controller → ViewResolver → View
 * 
 * 3. 常用注解:
 * - @RestController, @RequestMapping
 * - @GetMapping, @PostMapping
 * - @RequestParam, @PathVariable, @RequestBody
 * 
 * 4. RESTful 设计:
 * - 名词资源
 * - HTTP 动词
 * - 合适的状态码
 */
