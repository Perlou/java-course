package phase08;

/**
 * Phase 8 - Lesson 2: Bean 生命周期
 * 
 * 🎯 学习目标:
 * 1. 理解 Bean 的完整生命周期
 * 2. 了解各生命周期回调方法
 * 3. 掌握 Bean 作用域
 */
public class BeanLifecycle {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 2: Bean 生命周期");
        System.out.println("=".repeat(60));

        // 1. Bean 生命周期概述
        System.out.println("\n【1. Bean 生命周期概述】");
        System.out.println("""
                Bean 的完整生命周期:

                ┌─────────────────────────────────────────────────────────┐
                │                    Bean 生命周期                         │
                ├─────────────────────────────────────────────────────────┤
                │                                                         │
                │  1. 实例化 (Instantiation)                             │
                │     ↓ new Bean() 或反射创建                            │
                │                                                         │
                │  2. 属性注入 (Populate Properties)                     │
                │     ↓ 注入依赖                                         │
                │                                                         │
                │  3. Aware 接口回调                                     │
                │     ↓ BeanNameAware, ApplicationContextAware 等        │
                │                                                         │
                │  4. BeanPostProcessor.postProcessBeforeInitialization  │
                │     ↓ 初始化前置处理                                   │
                │                                                         │
                │  5. 初始化 (Initialization)                            │
                │     ↓ @PostConstruct, InitializingBean, init-method    │
                │                                                         │
                │  6. BeanPostProcessor.postProcessAfterInitialization   │
                │     ↓ 初始化后置处理 (AOP 代理在此创建)                │
                │                                                         │
                │  7. 使用 (In Use)                                      │
                │     ↓ Bean 可以正常使用                                │
                │                                                         │
                │  8. 销毁 (Destruction)                                 │
                │     @PreDestroy, DisposableBean, destroy-method        │
                │                                                         │
                └─────────────────────────────────────────────────────────┘
                """);

        // 2. 模拟生命周期
        System.out.println("【2. 模拟 Bean 生命周期】");

        LifecycleContainer container = new LifecycleContainer();

        // 创建并注册 Bean
        LifecycleBean bean = container.createBean(LifecycleBean.class);

        System.out.println("\n使用 Bean:");
        bean.doSomething();

        // 销毁 Bean
        System.out.println("\n销毁容器:");
        container.destroy();

        // 3. 生命周期回调方式
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【3. 生命周期回调方式】");
        System.out.println("""
                初始化回调 (三选一):

                1. @PostConstruct 注解 (推荐) ✅
                   @PostConstruct
                   public void init() { }

                2. 实现 InitializingBean 接口
                   public void afterPropertiesSet() { }

                3. XML 配置 init-method
                   <bean init-method="init"/>

                执行顺序: @PostConstruct → InitializingBean → init-method

                ─────────────────────────────────────────

                销毁回调 (三选一):

                1. @PreDestroy 注解 (推荐) ✅
                   @PreDestroy
                   public void cleanup() { }

                2. 实现 DisposableBean 接口
                   public void destroy() { }

                3. XML 配置 destroy-method
                   <bean destroy-method="cleanup"/>

                执行顺序: @PreDestroy → DisposableBean → destroy-method
                """);

        // 4. Bean 作用域
        System.out.println("=".repeat(60));
        System.out.println("【4. Bean 作用域 (Scope)】");
        System.out.println("""
                ┌────────────┬────────────────────────────────────────┐
                │   作用域    │              说明                      │
                ├────────────┼────────────────────────────────────────┤
                │ singleton  │ 单例，容器只创建一个实例 (默认)        │
                │ prototype  │ 原型，每次请求创建新实例               │
                │ request    │ 每个 HTTP 请求一个实例 (Web)           │
                │ session    │ 每个 HTTP Session 一个实例 (Web)       │
                │ application│ 每个 ServletContext 一个实例 (Web)     │
                └────────────┴────────────────────────────────────────┘

                配置方式:
                @Scope("prototype")
                @Component
                public class MyBean { }

                或
                @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
                """);

        // 演示单例 vs 原型
        System.out.println("单例 vs 原型演示:");

        ScopeContainer scopeContainer = new ScopeContainer();

        System.out.println("\nSingleton 作用域:");
        Object s1 = scopeContainer.getSingletonBean("singletonBean");
        Object s2 = scopeContainer.getSingletonBean("singletonBean");
        System.out.println("  s1 == s2: " + (s1 == s2) + " (同一实例)");

        System.out.println("\nPrototype 作用域:");
        Object p1 = scopeContainer.getPrototypeBean("prototypeBean");
        Object p2 = scopeContainer.getPrototypeBean("prototypeBean");
        System.out.println("  p1 == p2: " + (p1 == p2) + " (不同实例)");

        // 5. BeanPostProcessor
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【5. BeanPostProcessor】");
        System.out.println("""
                BeanPostProcessor: Bean 后置处理器

                作用: 在 Bean 初始化前后进行额外处理

                public interface BeanPostProcessor {
                    // 初始化前调用
                    Object postProcessBeforeInitialization(Object bean, String beanName);

                    // 初始化后调用
                    Object postProcessAfterInitialization(Object bean, String beanName);
                }

                应用场景:
                - AOP 代理创建
                - 属性校验
                - Bean 包装

                Spring AOP 就是通过 BeanPostProcessor 实现的!
                在 postProcessAfterInitialization 中创建代理对象
                """);

        // 6. Aware 接口
        System.out.println("=".repeat(60));
        System.out.println("【6. Aware 接口】");
        System.out.println("""
                Aware 接口: 让 Bean 感知容器信息

                常用 Aware 接口:
                ┌──────────────────────────┬────────────────────────┐
                │        接口              │      注入内容          │
                ├──────────────────────────┼────────────────────────┤
                │ BeanNameAware            │ Bean 的名称            │
                │ BeanFactoryAware         │ BeanFactory 容器       │
                │ ApplicationContextAware  │ ApplicationContext     │
                │ EnvironmentAware         │ Environment 环境变量   │
                │ ResourceLoaderAware      │ ResourceLoader         │
                └──────────────────────────┴────────────────────────┘

                示例:
                @Component
                public class MyBean implements ApplicationContextAware {
                    private ApplicationContext ctx;

                    @Override
                    public void setApplicationContext(ApplicationContext ctx) {
                        this.ctx = ctx;  // 可以获取其他 Bean
                    }
                }
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 Bean 生命周期: 实例化 → 注入 → 初始化 → 使用 → 销毁");
        System.out.println("💡 推荐使用 @PostConstruct 和 @PreDestroy");
        System.out.println("💡 默认作用域是 singleton (单例)");
        System.out.println("=".repeat(60));
    }
}

// ==================== 模拟 Bean 生命周期 ====================

class LifecycleBean {
    private String value;

    public LifecycleBean() {
        System.out.println("1. 实例化: 构造函数调用");
    }

    public void setValue(String value) {
        this.value = value;
        System.out.println("2. 属性注入: value = " + value);
    }

    public void setBeanName(String name) {
        System.out.println("3. Aware 回调: Bean 名称 = " + name);
    }

    public void postProcessBeforeInit() {
        System.out.println("4. BeanPostProcessor 前置处理");
    }

    public void init() {
        System.out.println("5. 初始化: @PostConstruct / init-method");
    }

    public void postProcessAfterInit() {
        System.out.println("6. BeanPostProcessor 后置处理 (AOP 代理在此创建)");
    }

    public void doSomething() {
        System.out.println("7. 使用: 执行业务逻辑");
    }

    public void destroy() {
        System.out.println("8. 销毁: @PreDestroy / destroy-method");
    }
}

class LifecycleContainer {

    public LifecycleBean createBean(Class<LifecycleBean> clazz) {
        System.out.println("--- Bean 创建过程 ---");

        // 1. 实例化
        LifecycleBean bean = new LifecycleBean();

        // 2. 属性注入
        bean.setValue("测试值");

        // 3. Aware 回调
        bean.setBeanName("lifecycleBean");

        // 4. BeanPostProcessor 前置
        bean.postProcessBeforeInit();

        // 5. 初始化
        bean.init();

        // 6. BeanPostProcessor 后置
        bean.postProcessAfterInit();

        System.out.println("--- Bean 创建完成 ---");
        return bean;
    }

    public void destroy() {
        System.out.println("--- 容器销毁 ---");
        // 实际会调用所有 Bean 的销毁方法
    }
}

// ==================== 模拟 Bean 作用域 ====================

class ScopeContainer {
    private final java.util.Map<String, Object> singletonBeans = new java.util.HashMap<>();

    public Object getSingletonBean(String name) {
        // Singleton: 总是返回同一实例
        return singletonBeans.computeIfAbsent(name, k -> {
            System.out.println("  创建 Singleton Bean: " + name);
            return new Object();
        });
    }

    public Object getPrototypeBean(String name) {
        // Prototype: 每次创建新实例
        System.out.println("  创建 Prototype Bean: " + name);
        return new Object();
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Bean 生命周期:
 * 实例化 → 属性注入 → Aware → 前置处理 → 初始化 → 后置处理 → 使用 → 销毁
 * 
 * 2. 生命周期回调:
 * - @PostConstruct / @PreDestroy (推荐)
 * - InitializingBean / DisposableBean
 * - init-method / destroy-method
 * 
 * 3. Bean 作用域:
 * - singleton: 单例 (默认)
 * - prototype: 原型
 * - request/session: Web 应用
 * 
 * 4. BeanPostProcessor:
 * - 在 Bean 初始化前后进行处理
 * - AOP 代理在此创建
 */
