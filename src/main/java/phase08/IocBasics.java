package phase08;

/**
 * Phase 8 - Lesson 1: IoC 与依赖注入
 * 
 * 🎯 学习目标:
 * 1. 理解 IoC (控制反转) 的概念
 * 2. 理解 DI (依赖注入) 的原理
 * 3. 了解 Spring 容器的作用
 * 
 * ⚠️ 注意: 本课程是原理讲解，不需要引入 Spring 依赖
 * 后续可以使用 Spring Initializr 创建实际项目
 */
public class IocBasics {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 1: IoC 与依赖注入");
        System.out.println("=".repeat(60));

        // 1. 什么是 IoC
        System.out.println("\n【1. 什么是 IoC (控制反转)】");
        System.out.println("""
                传统方式 vs IoC:

                传统方式 (紧耦合):
                ┌─────────────────────────────────────────────────────────┐
                │  class UserService {                                    │
                │      private UserDao userDao = new UserDaoImpl();      │
                │      //                        ↑ 自己创建依赖           │
                │  }                                                      │
                │                                                         │
                │  问题:                                                  │
                │  - UserService 与 UserDaoImpl 紧耦合                   │
                │  - 更换实现需要修改代码                                 │
                │  - 难以测试                                            │
                └─────────────────────────────────────────────────────────┘

                IoC 方式 (解耦):
                ┌─────────────────────────────────────────────────────────┐
                │  class UserService {                                    │
                │      private UserDao userDao;  // 只声明依赖           │
                │                                                         │
                │      public UserService(UserDao userDao) {             │
                │          this.userDao = userDao;  // 外部注入          │
                │      }                                                  │
                │  }                                                      │
                │                                                         │
                │  好处:                                                  │
                │  - UserService 只依赖 UserDao 接口                     │
                │  - 实现可随时替换                                      │
                │  - 容易测试 (Mock)                                     │
                └─────────────────────────────────────────────────────────┘

                IoC = 控制反转
                - 控制: 对象的创建和依赖关系管理
                - 反转: 从自己控制 → 交给容器控制
                """);

        // 演示传统方式
        System.out.println("传统方式演示:");
        TraditionalUserService traditional = new TraditionalUserService();
        traditional.register("张三");

        // 演示 IoC 方式
        System.out.println("\nIoC 方式演示:");
        UserDao dao = new UserDaoImpl(); // 容器负责创建
        IocUserService iocService = new IocUserService(dao); // 容器注入
        iocService.register("李四");

        // 2. 依赖注入方式
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【2. 依赖注入方式】");
        System.out.println("""
                三种依赖注入方式:

                1. 构造器注入 (推荐) ✅
                   public UserService(UserDao userDao) {
                       this.userDao = userDao;
                   }

                   优点:
                   - 依赖不可变
                   - 必须的依赖明确
                   - 便于测试

                2. Setter 注入
                   public void setUserDao(UserDao userDao) {
                       this.userDao = userDao;
                   }

                   场景: 可选依赖

                3. 字段注入 (不推荐) ❌
                   @Autowired
                   private UserDao userDao;

                   问题:
                   - 隐藏依赖
                   - 难以测试
                   - 可能为 null
                """);

        // 3. Spring 容器
        System.out.println("=".repeat(60));
        System.out.println("【3. Spring 容器】");
        System.out.println("""
                Spring 容器 (IoC Container):

                ┌─────────────────────────────────────────────────────────┐
                │                    Spring 容器                          │
                │  ┌─────────────────────────────────────────────────┐   │
                │  │                 Bean 工厂                        │   │
                │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐            │   │
                │  │  │ Bean A  │ │ Bean B  │ │ Bean C  │ ...        │   │
                │  │  └─────────┘ └─────────┘ └─────────┘            │   │
                │  └─────────────────────────────────────────────────┘   │
                │                                                         │
                │  职责:                                                  │
                │  - 创建 Bean 实例                                      │
                │  - 管理 Bean 生命周期                                  │
                │  - 注入 Bean 依赖                                      │
                └─────────────────────────────────────────────────────────┘

                两种容器:
                - BeanFactory: 基础容器，延迟加载
                - ApplicationContext: 高级容器，预加载，推荐使用
                """);

        // 4. Bean 配置方式
        System.out.println("=".repeat(60));
        System.out.println("【4. Bean 配置方式】");
        System.out.println("""
                1. XML 配置 (传统)
                   <bean id="userService" class="com.example.UserService">
                       <property name="userDao" ref="userDao"/>
                   </bean>

                2. 注解配置 (推荐) ✅
                   @Service
                   public class UserService {
                       @Autowired
                       private UserDao userDao;
                   }

                   常用注解:
                   - @Component: 通用组件
                   - @Service: 服务层
                   - @Repository: 数据层
                   - @Controller: 控制层
                   - @Autowired: 自动注入
                   - @Qualifier: 指定注入哪个 Bean

                3. Java 配置类
                   @Configuration
                   public class AppConfig {
                       @Bean
                       public UserService userService(UserDao userDao) {
                           return new UserService(userDao);
                       }
                   }
                """);

        // 5. 模拟 Spring 容器
        System.out.println("=".repeat(60));
        System.out.println("【5. 模拟 Spring 容器】");

        SimpleContainer container = new SimpleContainer();

        // 注册 Bean
        container.registerBean("userDao", new UserDaoImpl());
        container.registerBean("orderDao", new OrderDaoImpl());

        // 获取 Bean
        UserDao userDaoBean = (UserDao) container.getBean("userDao");
        OrderDao orderDaoBean = (OrderDao) container.getBean("orderDao");

        // 使用 Bean
        userDaoBean.save("用户数据");
        orderDaoBean.save("订单数据");

        System.out.println("\n容器中的 Bean:");
        container.listBeans();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 IoC 核心: 对象创建和依赖管理交给容器");
        System.out.println("💡 DI 核心: 容器将依赖自动注入到对象中");
        System.out.println("💡 推荐使用构造器注入，便于测试");
        System.out.println("=".repeat(60));
    }
}

// ==================== 传统方式 (紧耦合) ====================

class TraditionalUserService {
    // 直接 new，紧耦合
    private UserDao userDao = new UserDaoImpl();

    public void register(String name) {
        System.out.println("  [传统] 注册用户: " + name);
        userDao.save(name);
    }
}

// ==================== IoC 方式 (解耦) ====================

interface UserDao {
    void save(String data);
}

class UserDaoImpl implements UserDao {
    @Override
    public void save(String data) {
        System.out.println("  保存用户: " + data);
    }
}

class IocUserService {
    private final UserDao userDao; // 只定义依赖

    // 构造器注入
    public IocUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(String name) {
        System.out.println("  [IoC] 注册用户: " + name);
        userDao.save(name);
    }
}

// ==================== 模拟 Spring 容器 ====================

interface OrderDao {
    void save(String data);
}

class OrderDaoImpl implements OrderDao {
    @Override
    public void save(String data) {
        System.out.println("  保存订单: " + data);
    }
}

class SimpleContainer {
    private final java.util.Map<String, Object> beans = new java.util.HashMap<>();

    public void registerBean(String name, Object bean) {
        beans.put(name, bean);
        System.out.println("注册 Bean: " + name + " -> " + bean.getClass().getSimpleName());
    }

    public Object getBean(String name) {
        return beans.get(name);
    }

    public void listBeans() {
        beans.forEach((name, bean) -> System.out.println("  " + name + ": " + bean.getClass().getSimpleName()));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. IoC (控制反转):
 * - 对象创建控制权从代码转移到容器
 * - 解耦组件依赖
 * 
 * 2. DI (依赖注入):
 * - 容器自动注入依赖
 * - 构造器注入 (推荐)
 * - Setter 注入
 * - 字段注入 (不推荐)
 * 
 * 3. Spring 容器:
 * - BeanFactory: 基础
 * - ApplicationContext: 推荐
 * 
 * 4. 配置方式:
 * - XML (传统)
 * - 注解 (推荐)
 * - Java 配置类
 * 
 * 🏃 后续学习:
 * 使用 Spring Initializr 创建实际项目练习
 */
