package phase08;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Phase 8 - Lesson 3: AOP 面向切面编程
 * 
 * 🎯 学习目标:
 * 1. 理解 AOP 的概念和术语
 * 2. 了解 AOP 的实现原理
 * 3. 掌握常见的 AOP 应用场景
 */
public class AopBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 3: AOP 面向切面编程");
        System.out.println("=".repeat(60));

        // 1. 什么是 AOP
        System.out.println("\n【1. 什么是 AOP】");
        System.out.println("""
                AOP = Aspect Oriented Programming (面向切面编程)

                问题场景:
                ┌─────────────────────────────────────────────────────────┐
                │  多个方法都需要添加相同的横切关注点:                    │
                │                                                         │
                │  UserService.createUser()  ━━━┓                        │
                │  UserService.updateUser()  ━━━┫                        │
                │  OrderService.createOrder()━━━╋━━▶ 日志、事务、权限    │
                │  PaymentService.pay()      ━━━┫                        │
                │  ...                       ━━━┛                        │
                │                                                         │
                │  传统方式: 每个方法都写一遍 😰                         │
                │  AOP 方式: 统一处理，代码复用 😊                       │
                └─────────────────────────────────────────────────────────┘

                AOP 核心思想:
                - 将横切关注点 (日志、事务、安全) 从业务逻辑中分离
                - 通过切面统一处理
                - 不修改原有代码
                """);

        // 2. AOP 术语
        System.out.println("=".repeat(60));
        System.out.println("【2. AOP 术语】");
        System.out.println("""
                ┌────────────────┬────────────────────────────────────────┐
                │     术语        │              说明                      │
                ├────────────────┼────────────────────────────────────────┤
                │ Aspect (切面)   │ 包含切点和通知的模块                   │
                │ Join Point     │ 程序执行的点 (方法调用、异常抛出)       │
                │ Pointcut       │ 匹配 Join Point 的表达式               │
                │ Advice (通知)   │ 在切点处执行的动作                     │
                │ Target         │ 被代理的目标对象                       │
                │ Proxy          │ 代理对象                               │
                │ Weaving        │ 将切面织入目标对象的过程               │
                └────────────────┴────────────────────────────────────────┘

                图示:
                ┌─────────────────────────────────────────────────────────┐
                │                    Aspect (切面)                         │
                │  ┌─────────────────────────────────────────────────────┐│
                │  │  Pointcut: execution(* com.example.service.*.*(..))││
                │  ├─────────────────────────────────────────────────────┤│
                │  │  Advice:                                            ││
                │  │  - @Before: 方法执行前                              ││
                │  │  - @After: 方法执行后                               ││
                │  │  - @AfterReturning: 正常返回后                      ││
                │  │  - @AfterThrowing: 抛出异常后                       ││
                │  │  - @Around: 环绕通知 (最强大)                       ││
                │  └─────────────────────────────────────────────────────┘│
                └─────────────────────────────────────────────────────────┘
                """);

        // 3. Advice 类型
        System.out.println("=".repeat(60));
        System.out.println("【3. Advice (通知) 类型】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                  方法执行时间线                          │
                │                                                         │
                │  @Before ──▶ [方法执行] ──▶ @AfterReturning            │
                │     │                              │                    │
                │     │                              │                    │
                │     └──────── @Around ─────────────┘                    │
                │                                                         │
                │              │ 异常                                     │
                │              ▼                                          │
                │        @AfterThrowing                                   │
                │                                                         │
                │  @After: 无论成功还是异常都执行 (finally)              │
                └─────────────────────────────────────────────────────────┘

                示例代码:
                @Aspect
                @Component
                public class LoggingAspect {

                    @Before("execution(* com.example.service.*.*(..))")
                    public void logBefore(JoinPoint jp) {
                        System.out.println("Before: " + jp.getSignature());
                    }

                    @AfterReturning(value="execution(...)", returning="result")
                    public void logAfter(Object result) {
                        System.out.println("Return: " + result);
                    }

                    @Around("execution(...)")
                    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
                        long start = System.currentTimeMillis();
                        Object result = pjp.proceed();  // 执行目标方法
                        long time = System.currentTimeMillis() - start;
                        System.out.println("耗时: " + time + "ms");
                        return result;
                    }
                }
                """);

        // 4. Pointcut 表达式
        System.out.println("=".repeat(60));
        System.out.println("【4. Pointcut 表达式】");
        System.out.println("""
                execution 表达式语法:
                execution(修饰符? 返回类型 包名.类名.方法名(参数类型) 异常?)

                常用示例:
                ┌─────────────────────────────────────────────────────────┐
                │ execution(* com.example.service.*.*(..))               │
                │ 匹配 service 包下所有类的所有方法                       │
                ├─────────────────────────────────────────────────────────┤
                │ execution(public * *(..))                              │
                │ 匹配所有 public 方法                                   │
                ├─────────────────────────────────────────────────────────┤
                │ execution(* save*(..))                                 │
                │ 匹配所有以 save 开头的方法                             │
                ├─────────────────────────────────────────────────────────┤
                │ execution(* com.example..*.*(..))                      │
                │ 匹配 com.example 及子包下所有方法                      │
                └─────────────────────────────────────────────────────────┘

                其他表达式:
                - @annotation(com.example.Log): 匹配带指定注解的方法
                - within(com.example.service.*): 匹配类型
                - args(String): 匹配参数类型
                """);

        // 5. AOP 实现原理 - JDK 动态代理演示
        System.out.println("=".repeat(60));
        System.out.println("【5. AOP 实现原理】");
        System.out.println("""
                Spring AOP 两种代理方式:

                1. JDK 动态代理 (有接口时使用)
                   - 基于接口
                   - InvocationHandler

                2. CGLIB 代理 (无接口时使用)
                   - 基于继承
                   - MethodInterceptor
                """);

        System.out.println("\n模拟 AOP - JDK 动态代理:");

        // 目标对象
        BusinessService target = new BusinessServiceImpl();

        // 创建代理
        BusinessService proxy = (BusinessService) Proxy.newProxyInstance(
                BusinessService.class.getClassLoader(),
                new Class<?>[] { BusinessService.class },
                new LoggingAspect(target));

        // 通过代理调用
        proxy.process("测试数据");

        System.out.println();
        proxy.calculate(10, 20);

        // 6. AOP 应用场景
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【6. AOP 常见应用场景】");
        System.out.println("""
                1. 日志记录
                   - 方法调用日志
                   - 参数和返回值记录

                2. 事务管理
                   - @Transactional
                   - 自动提交/回滚

                3. 权限验证
                   - 检查用户权限
                   - 登录状态验证

                4. 性能监控
                   - 方法执行时间
                   - 慢查询告警

                5. 异常处理
                   - 统一异常处理
                   - 异常日志记录

                6. 缓存
                   - @Cacheable
                   - 方法结果缓存
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 AOP 核心: 横切关注点与业务逻辑分离");
        System.out.println("💡 Spring AOP 基于代理实现 (JDK/CGLIB)");
        System.out.println("💡 @Transactional 是 AOP 最典型的应用");
        System.out.println("=".repeat(60));
    }
}

// ==================== 模拟 AOP ====================

interface BusinessService {
    void process(String data);

    int calculate(int a, int b);
}

class BusinessServiceImpl implements BusinessService {
    @Override
    public void process(String data) {
        System.out.println("  处理业务数据: " + data);
    }

    @Override
    public int calculate(int a, int b) {
        int result = a + b;
        System.out.println("  计算结果: " + a + " + " + b + " = " + result);
        return result;
    }
}

// 模拟切面 (使用 JDK 动态代理)
class LoggingAspect implements InvocationHandler {
    private final Object target;

    public LoggingAspect(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // @Before
        System.out.println("[@Before] 调用方法: " + method.getName() +
                ", 参数: " + Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            // 执行目标方法 (@Around 中的 proceed)
            Object result = method.invoke(target, args);

            // @AfterReturning
            System.out.println("[@AfterReturning] 返回值: " + result);

            return result;

        } catch (Exception e) {
            // @AfterThrowing
            System.out.println("[@AfterThrowing] 异常: " + e.getMessage());
            throw e;
        } finally {
            // @After
            long time = System.currentTimeMillis() - start;
            System.out.println("[@After] 执行耗时: " + time + "ms");
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. AOP 概念:
 * - 面向切面编程
 * - 分离横切关注点
 * 
 * 2. 核心术语:
 * - Aspect: 切面
 * - Pointcut: 切点
 * - Advice: 通知
 * - Join Point: 连接点
 * 
 * 3. Advice 类型:
 * - @Before, @After, @AfterReturning
 * - @AfterThrowing, @Around
 * 
 * 4. 实现原理:
 * - JDK 动态代理 (有接口)
 * - CGLIB (无接口)
 * 
 * 5. 应用场景:
 * - 日志、事务、权限、缓存
 */
