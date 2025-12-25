package phase08.ex;

public class IocBasics {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 8 - Lesson 1: IoC 与依赖注入");
        System.out.println("=".repeat(60));

        // 1. 什么是 IoC
        System.out.println("\n【1. 什么是 IoC (控制反转)】");
        TraditionalUserService traditional = new TraditionalUserService();
        traditional.register("张三");

        // IoC
        UserDao dao = new UserDaoImpl();
        // IocUserService iocService =

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
