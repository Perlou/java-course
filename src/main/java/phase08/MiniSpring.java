package phase08;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Phase 8 - 实战项目: 手写简易 Spring IoC
 * 
 * 🎯 项目目标:
 * 1. 实现一个简易的 IoC 容器
 * 2. 支持注解扫描和自动注入
 * 3. 理解 Spring 核心原理
 */
public class MiniSpring {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("   🌱 Mini Spring - 手写 IoC 容器");
        System.out.println("=".repeat(60));

        // 创建 IoC 容器
        System.out.println("\n【1. 创建 IoC 容器】");
        ApplicationContext ctx = new ApplicationContext();

        // 注册 Bean (模拟注解扫描)
        System.out.println("\n【2. 注册 Bean】");
        ctx.registerBean(MiniUserDao.class);
        ctx.registerBean(MiniOrderDao.class);
        ctx.registerBean(MiniUserService.class);
        ctx.registerBean(MiniOrderService.class);

        // 初始化容器
        System.out.println("\n【3. 初始化容器 - 依赖注入】");
        ctx.refresh();

        // 获取并使用 Bean
        System.out.println("\n【4. 获取并使用 Bean】");
        MiniUserService userService = ctx.getBean(MiniUserService.class);
        userService.register("张三");

        System.out.println();
        MiniOrderService orderService = ctx.getBean(MiniOrderService.class);
        orderService.createOrder("订单001");

        // 显示容器中的所有 Bean
        System.out.println("\n【5. 容器中的 Bean】");
        ctx.listBeans();

        // 单例验证
        System.out.println("\n【6. 单例验证】");
        MiniUserService us1 = ctx.getBean(MiniUserService.class);
        MiniUserService us2 = ctx.getBean(MiniUserService.class);
        System.out.println("us1 == us2: " + (us1 == us2) + " (单例)");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("【Mini Spring 实现的功能】");
        System.out.println("""
                ✅ IoC 容器
                ✅ Bean 注册和获取
                ✅ 单例 Bean 管理
                ✅ 依赖注入 (@Autowired)
                ✅ 初始化回调 (@PostConstruct)

                Spring 还有:
                - 注解扫描 (@ComponentScan)
                - AOP 代理
                - 事件机制
                - 更多作用域
                - 条件注入 (@Conditional)
                - ...
                """);
        System.out.println("=".repeat(60));
    }
}

// ==================== 自定义注解 ====================

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Component {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Autowired {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface PostConstruct {
}

// ==================== IoC 容器实现 ====================

class ApplicationContext {
    // Bean 定义
    private final Map<Class<?>, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();

    // 单例 Bean 缓存
    private final Map<Class<?>, Object> singletonBeans = new ConcurrentHashMap<>();

    // Bean 定义
    static class BeanDefinition {
        Class<?> beanClass;
        String beanName;

        BeanDefinition(Class<?> clazz) {
            this.beanClass = clazz;
            this.beanName = clazz.getSimpleName();
        }
    }

    // 注册 Bean
    public void registerBean(Class<?> clazz) {
        BeanDefinition bd = new BeanDefinition(clazz);
        beanDefinitions.put(clazz, bd);
        System.out.println("  注册 Bean: " + clazz.getSimpleName());
    }

    // 初始化容器
    public void refresh() {
        // 1. 实例化所有 Bean
        for (Class<?> clazz : beanDefinitions.keySet()) {
            getBean(clazz);
        }

        // 2. 依赖注入
        for (Object bean : singletonBeans.values()) {
            injectDependencies(bean);
        }

        // 3. 调用初始化方法
        for (Object bean : singletonBeans.values()) {
            callPostConstruct(bean);
        }

        System.out.println("  容器初始化完成!");
    }

    // 获取 Bean
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        // 先从缓存获取
        if (singletonBeans.containsKey(clazz)) {
            return (T) singletonBeans.get(clazz);
        }

        // 创建 Bean
        try {
            T bean = clazz.getDeclaredConstructor().newInstance();
            singletonBeans.put(clazz, bean);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("创建 Bean 失败: " + clazz.getName(), e);
        }
    }

    // 依赖注入
    private void injectDependencies(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                Object dependency = findBean(fieldType);

                if (dependency != null) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, dependency);
                        System.out.println("  注入: " + clazz.getSimpleName() +
                                "." + field.getName() + " <- " +
                                dependency.getClass().getSimpleName());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("注入失败", e);
                    }
                }
            }
        }
    }

    // 查找 Bean (支持接口类型)
    private Object findBean(Class<?> type) {
        // 精确匹配
        if (singletonBeans.containsKey(type)) {
            return singletonBeans.get(type);
        }

        // 接口匹配
        for (Map.Entry<Class<?>, Object> entry : singletonBeans.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    // 调用 @PostConstruct 方法
    private void callPostConstruct(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(bean);
                } catch (Exception e) {
                    throw new RuntimeException("初始化方法调用失败", e);
                }
            }
        }
    }

    // 列出所有 Bean
    public void listBeans() {
        singletonBeans
                .forEach((clazz, bean) -> System.out.println("  " + clazz.getSimpleName() + " : " + bean.hashCode()));
    }
}

// ==================== 示例 Bean ====================

interface IMiniUserDao {
    void save(String data);
}

@Component
class MiniUserDao implements IMiniUserDao {
    @Override
    public void save(String data) {
        System.out.println("    [MiniUserDao] 保存用户: " + data);
    }
}

interface IMiniOrderDao {
    void save(String data);
}

@Component
class MiniOrderDao implements IMiniOrderDao {
    @Override
    public void save(String data) {
        System.out.println("    [MiniOrderDao] 保存订单: " + data);
    }
}

@Component
class MiniUserService {

    @Autowired
    private MiniUserDao userDao;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        initialized = true;
        System.out.println("  [MiniUserService] @PostConstruct 初始化完成");
    }

    public void register(String username) {
        System.out.println("  [MiniUserService] 注册用户: " + username + " (initialized=" + initialized + ")");
        userDao.save(username);
    }
}

@Component
class MiniOrderService {

    @Autowired
    private MiniOrderDao orderDao;

    @Autowired
    private MiniUserDao userDao; // 可以注入多个依赖

    public void createOrder(String orderId) {
        System.out.println("  [MiniOrderService] 创建订单: " + orderId);
        orderDao.save(orderId);
    }
}

/*
 * 📚 项目总结:
 * 
 * 手写 Mini Spring IoC 容器实现了:
 * 
 * 1. Bean 容器
 * - 存储 Bean 定义
 * - 管理单例 Bean
 * 
 * 2. 依赖注入
 * - @Autowired 字段注入
 * - 按类型查找依赖
 * 
 * 3. 生命周期回调
 * - @PostConstruct 初始化
 * 
 * 实际 Spring 还有:
 * - 更复杂的 Bean 生命周期
 * - 多种作用域
 * - AOP 代理
 * - 事件发布
 * - 条件注入
 * - 配置属性绑定
 * - ...
 * 
 * 🎯 扩展任务:
 * 1. 添加 @Scope 支持 prototype
 * 2. 添加构造器注入
 * 3. 添加简单的 AOP 代理
 * 4. 添加包扫描功能
 */
