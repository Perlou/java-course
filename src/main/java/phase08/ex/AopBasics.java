package phase08.ex;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class AopBasics {
    public static void main(String[] args) {

        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 3: AOP 面向切面编程");
        System.out.println("=".repeat(60));

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
