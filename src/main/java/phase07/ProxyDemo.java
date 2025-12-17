package phase07;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Phase 7 - Lesson 5: 代理模式
 * 
 * 🎯 学习目标:
 * 1. 理解代理模式的作用
 * 2. 掌握静态代理实现
 * 3. 掌握 JDK 动态代理
 * 4. 了解 CGLIB 代理
 */
public class ProxyDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 5: 代理模式");
        System.out.println("=".repeat(60));

        // 代理模式概述
        System.out.println("\n【代理模式概述】");
        System.out.println("""
                意图: 为其他对象提供一个代理以控制对这个对象的访问

                应用场景:
                - 远程代理: RPC 调用
                - 虚拟代理: 延迟加载
                - 保护代理: 权限控制
                - 日志代理: 记录方法调用
                - 缓存代理: 缓存结果

                结构:
                ┌─────────────┐
                │   Client    │
                └──────┬──────┘
                       │
                       ▼
                ┌─────────────┐     ┌─────────────┐
                │   Proxy     │────▶│ RealSubject │
                └─────────────┘     └─────────────┘
                       │                    │
                       └────────┬───────────┘
                                ▼
                        ┌─────────────┐
                        │  Subject    │
                        └─────────────┘
                """);

        // 1. 静态代理
        System.out.println("=".repeat(60));
        System.out.println("【1. 静态代理】");
        System.out.println("""
                特点: 编译时确定代理类
                优点: 简单直观
                缺点: 需要为每个接口创建代理类
                """);

        // 真实对象
        UserService realService = new UserServiceImpl();

        // 静态代理
        UserService proxy = new UserServiceStaticProxy(realService);

        System.out.println("静态代理调用:");
        proxy.addUser("Alice");
        proxy.deleteUser("Bob");

        // 2. JDK 动态代理
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【2. JDK 动态代理】");
        System.out.println("""
                特点: 运行时动态生成代理类
                要求: 目标类必须实现接口
                核心: Proxy.newProxyInstance()
                      InvocationHandler
                """);

        UserService jdkProxy = (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class<?>[] { UserService.class },
                new LoggingHandler(realService));

        System.out.println("\nJDK 动态代理调用:");
        jdkProxy.addUser("Charlie");
        jdkProxy.deleteUser("David");

        // 查看代理类信息
        System.out.println("\n代理类信息:");
        System.out.println("  代理类: " + jdkProxy.getClass().getName());
        System.out.println("  接口: " + Arrays.toString(jdkProxy.getClass().getInterfaces()));

        // 3. 代理应用示例
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【3. 代理应用示例】");

        // 性能监控代理
        System.out.println("\n性能监控代理:");
        Calculator realCalc = new CalculatorImpl();
        Calculator calcProxy = (Calculator) Proxy.newProxyInstance(
                Calculator.class.getClassLoader(),
                new Class<?>[] { Calculator.class },
                new PerformanceHandler(realCalc));

        int result = calcProxy.add(10, 20);
        System.out.println("结果: " + result);

        // 缓存代理
        System.out.println("\n缓存代理:");
        DataService realData = new DataServiceImpl();
        DataService cacheProxy = (DataService) Proxy.newProxyInstance(
                DataService.class.getClassLoader(),
                new Class<?>[] { DataService.class },
                new CachingHandler(realData));

        System.out.println("第一次调用:");
        cacheProxy.getData("key1");
        System.out.println("第二次调用 (命中缓存):");
        cacheProxy.getData("key1");
        System.out.println("不同 key:");
        cacheProxy.getData("key2");

        // 4. CGLIB 代理简介
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【4. CGLIB 代理】");
        System.out.println("""
                特点:
                - 通过继承目标类实现代理
                - 不需要接口
                - 需要引入 CGLIB 依赖

                原理:
                - 运行时生成目标类的子类
                - 子类重写方法并添加增强逻辑

                限制:
                - 无法代理 final 类和 final 方法

                使用:
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(TargetClass.class);
                enhancer.setCallback(new MethodInterceptor() {
                    public Object intercept(Object obj, Method method,
                            Object[] args, MethodProxy proxy) {
                        // 增强逻辑
                        return proxy.invokeSuper(obj, args);
                    }
                });
                TargetClass proxy = (TargetClass) enhancer.create();
                """);

        // 5. JDK 代理 vs CGLIB
        System.out.println("=".repeat(60));
        System.out.println("【5. JDK 代理 vs CGLIB】");
        System.out.println("""
                ┌────────────┬───────────────────┬───────────────────┐
                │   特性     │    JDK 动态代理    │      CGLIB        │
                ├────────────┼───────────────────┼───────────────────┤
                │ 实现方式   │ 实现接口          │ 继承目标类         │
                │ 要求      │ 必须有接口         │ 不能是 final 类   │
                │ 性能      │ 较高              │ 创建慢，调用快     │
                │ 依赖      │ JDK 内置          │ 需要引入依赖       │
                │ 使用场景  │ 有接口的类         │ 没有接口的类       │
                └────────────┴───────────────────┴───────────────────┘

                Spring AOP:
                - 有接口: 默认使用 JDK 动态代理
                - 无接口: 使用 CGLIB
                - 可强制使用 CGLIB: proxyTargetClass=true
                """);

        // 6. 代理模式在框架中的应用
        System.out.println("=".repeat(60));
        System.out.println("【6. 代理模式在框架中的应用】");
        System.out.println("""
                1. Spring AOP
                   - 事务管理 (@Transactional)
                   - 日志记录
                   - 权限验证

                2. MyBatis
                   - Mapper 接口代理

                3. Dubbo
                   - 远程服务代理

                4. RPC 框架
                   - 远程调用透明化
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 代理模式可以在不修改目标类的情况下增强功能");
        System.out.println("💡 JDK 动态代理需要接口，CGLIB 通过继承实现");
        System.out.println("💡 Spring AOP 大量使用代理模式");
        System.out.println("=".repeat(60));
    }
}

// ==================== 接口和实现 ====================

interface UserService {
    void addUser(String name);

    void deleteUser(String name);
}

class UserServiceImpl implements UserService {
    @Override
    public void addUser(String name) {
        System.out.println("  添加用户: " + name);
    }

    @Override
    public void deleteUser(String name) {
        System.out.println("  删除用户: " + name);
    }
}

// ==================== 静态代理 ====================

class UserServiceStaticProxy implements UserService {
    private final UserService target;

    public UserServiceStaticProxy(UserService target) {
        this.target = target;
    }

    @Override
    public void addUser(String name) {
        System.out.println("[静态代理] 开始调用 addUser");
        target.addUser(name);
        System.out.println("[静态代理] 调用结束");
    }

    @Override
    public void deleteUser(String name) {
        System.out.println("[静态代理] 开始调用 deleteUser");
        target.deleteUser(name);
        System.out.println("[静态代理] 调用结束");
    }
}

// ==================== JDK 动态代理 ====================

class LoggingHandler implements InvocationHandler {
    private final Object target;

    public LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("[JDK代理] 调用: " + method.getName() + ", 参数: " + Arrays.toString(args));
        long start = System.currentTimeMillis();

        Object result = method.invoke(target, args);

        long duration = System.currentTimeMillis() - start;
        System.out.println("[JDK代理] 完成, 耗时: " + duration + "ms");

        return result;
    }
}

// ==================== 性能监控代理示例 ====================

interface Calculator {
    int add(int a, int b);
}

class CalculatorImpl implements Calculator {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

class PerformanceHandler implements InvocationHandler {
    private final Object target;

    public PerformanceHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.nanoTime();
        Object result = method.invoke(target, args);
        long duration = System.nanoTime() - start;
        System.out.println("  方法 " + method.getName() + " 执行耗时: " + duration + " ns");
        return result;
    }
}

// ==================== 缓存代理示例 ====================

interface DataService {
    String getData(String key);
}

class DataServiceImpl implements DataService {
    @Override
    public String getData(String key) {
        System.out.println("  从数据库查询: " + key);
        return "data-" + key;
    }
}

class CachingHandler implements InvocationHandler {
    private final Object target;
    private final Map<String, Object> cache = new HashMap<>();

    public CachingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String cacheKey = method.getName() + ":" + Arrays.toString(args);

        if (cache.containsKey(cacheKey)) {
            System.out.println("  [缓存命中] " + cacheKey);
            return cache.get(cacheKey);
        }

        Object result = method.invoke(target, args);
        cache.put(cacheKey, result);
        System.out.println("  [缓存存储] " + cacheKey);

        return result;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 静态代理: 编译时确定，简单但不灵活
 * 2. JDK 动态代理: 需要接口，使用反射
 * 3. CGLIB: 通过继承实现，无需接口
 * 
 * 代理模式应用:
 * - 日志记录
 * - 性能监控
 * - 事务管理
 * - 缓存
 * - 远程调用
 * 
 * 🏃 练习:
 * 1. 实现一个权限验证代理
 * 2. 实现一个异常重试代理
 * 3. 分析 Spring @Transactional 的实现原理
 */
