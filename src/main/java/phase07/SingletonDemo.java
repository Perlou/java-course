package phase07;

/**
 * Phase 7 - Lesson 2: 单例模式
 * 
 * 🎯 学习目标:
 * 1. 理解单例模式的作用
 * 2. 掌握多种单例实现方式
 * 3. 理解线程安全问题
 * 4. 了解单例的破坏与防御
 */
public class SingletonDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 2: 单例模式");
        System.out.println("=".repeat(60));

        // 单例模式概述
        System.out.println("\n【单例模式概述】");
        System.out.println("""
                意图: 确保一个类只有一个实例，并提供全局访问点

                应用场景:
                - 配置管理器
                - 连接池
                - 线程池
                - 日志对象
                - 缓存

                实现要点:
                - 私有构造函数
                - 静态实例
                - 公共获取方法
                """);

        // 1. 饿汉式
        System.out.println("=".repeat(60));
        System.out.println("【1. 饿汉式 (Eager)】");
        System.out.println("""
                特点: 类加载时就创建实例
                优点: 简单，线程安全
                缺点: 可能造成资源浪费
                """);

        EagerSingleton eager1 = EagerSingleton.getInstance();
        EagerSingleton eager2 = EagerSingleton.getInstance();
        System.out.println("eager1 == eager2: " + (eager1 == eager2));

        // 2. 懒汉式 (线程不安全)
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【2. 懒汉式 - 线程不安全】");
        System.out.println("""
                特点: 第一次使用时创建
                优点: 延迟加载
                缺点: 多线程下可能创建多个实例

                ⚠️  不推荐在多线程环境使用
                """);

        // 3. 懒汉式 (同步方法)
        System.out.println("=".repeat(60));
        System.out.println("【3. 懒汉式 - 同步方法】");
        System.out.println("""
                特点: 使用 synchronized 保证线程安全
                优点: 线程安全
                缺点: 每次获取都要同步，性能较差
                """);

        SynchronizedSingleton sync1 = SynchronizedSingleton.getInstance();
        SynchronizedSingleton sync2 = SynchronizedSingleton.getInstance();
        System.out.println("sync1 == sync2: " + (sync1 == sync2));

        // 4. 双重检查锁 (DCL)
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【4. 双重检查锁 (DCL)】");
        System.out.println("""
                特点: 只在第一次创建时同步
                优点: 线程安全，性能较好
                要点: 必须使用 volatile 防止指令重排

                推荐使用 ✅
                """);

        DCLSingleton dcl1 = DCLSingleton.getInstance();
        DCLSingleton dcl2 = DCLSingleton.getInstance();
        System.out.println("dcl1 == dcl2: " + (dcl1 == dcl2));

        // 5. 静态内部类
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【5. 静态内部类】");
        System.out.println("""
                特点: 利用类加载机制保证线程安全
                优点: 延迟加载，线程安全，简洁
                原理: 内部类在首次使用时才加载

                推荐使用 ✅
                """);

        StaticInnerSingleton inner1 = StaticInnerSingleton.getInstance();
        StaticInnerSingleton inner2 = StaticInnerSingleton.getInstance();
        System.out.println("inner1 == inner2: " + (inner1 == inner2));

        // 6. 枚举单例
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【6. 枚举单例】");
        System.out.println("""
                特点: 利用枚举特性实现单例
                优点:
                - 简洁
                - 天然防止反射和序列化破坏
                - 线程安全

                最推荐的方式 ✅✅
                """);

        EnumSingleton enum1 = EnumSingleton.INSTANCE;
        EnumSingleton enum2 = EnumSingleton.INSTANCE;
        System.out.println("enum1 == enum2: " + (enum1 == enum2));
        enum1.doSomething();

        // 7. 单例的破坏与防御
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【7. 单例的破坏与防御】");
        System.out.println("""
                破坏单例的方式:

                1. 反射攻击
                   Constructor c = Singleton.class.getDeclaredConstructor();
                   c.setAccessible(true);
                   c.newInstance();

                   防御: 构造函数中检查
                   if (instance != null) throw new RuntimeException();

                2. 序列化/反序列化
                   ObjectOutputStream.writeObject(instance);
                   ObjectInputStream.readObject(); // 新实例!

                   防御: 添加 readResolve() 方法
                   private Object readResolve() { return instance; }

                3. 克隆
                   防御: 不实现 Cloneable 或抛异常

                枚举单例天然防御所有攻击! ✅
                """);

        // 8. 各实现方式对比
        System.out.println("=".repeat(60));
        System.out.println("【8. 各实现方式对比】");
        System.out.println("""
                ┌──────────────┬────────┬────────┬────────┬──────────┐
                │   实现方式    │ 线程   │ 延迟   │ 防反射 │ 防序列化 │
                │              │ 安全   │ 加载   │        │          │
                ├──────────────┼────────┼────────┼────────┼──────────┤
                │ 饿汉式        │  ✅    │  ❌    │  ❌    │   ❌     │
                │ 懒汉式-不安全 │  ❌    │  ✅    │  ❌    │   ❌     │
                │ 懒汉式-同步   │  ✅    │  ✅    │  ❌    │   ❌     │
                │ 双重检查锁    │  ✅    │  ✅    │  ❌    │   ❌     │
                │ 静态内部类    │  ✅    │  ✅    │  ❌    │   ❌     │
                │ 枚举         │  ✅    │  ❌    │  ✅    │   ✅     │
                └──────────────┴────────┴────────┴────────┴──────────┘

                推荐:
                - 一般情况: 静态内部类
                - 需要防御: 枚举单例
                """);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 单例模式是最简单但也容易用错的模式");
        System.out.println("💡 优先使用枚举或静态内部类实现");
        System.out.println("💡 考虑使用依赖注入替代单例");
        System.out.println("=".repeat(60));
    }
}

// ==================== 1. 饿汉式 ====================

class EagerSingleton {
    // 类加载时就创建实例
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {
        System.out.println("EagerSingleton 创建");
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}

// ==================== 2. 懒汉式 - 线程不安全 ====================

class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {
    }

    // ⚠️ 线程不安全!
    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}

// ==================== 3. 懒汉式 - 同步方法 ====================

class SynchronizedSingleton {
    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {
        System.out.println("SynchronizedSingleton 创建");
    }

    // 每次都要同步，性能较差
    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }
}

// ==================== 4. 双重检查锁 (DCL) ====================

class DCLSingleton {
    // volatile 防止指令重排
    private static volatile DCLSingleton instance;

    private DCLSingleton() {
        System.out.println("DCLSingleton 创建");
    }

    public static DCLSingleton getInstance() {
        if (instance == null) { // 第一次检查
            synchronized (DCLSingleton.class) {
                if (instance == null) { // 第二次检查
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}

// ==================== 5. 静态内部类 ====================

class StaticInnerSingleton {
    private StaticInnerSingleton() {
        System.out.println("StaticInnerSingleton 创建");
    }

    // 静态内部类在首次访问时才加载
    private static class Holder {
        private static final StaticInnerSingleton INSTANCE = new StaticInnerSingleton();
    }

    public static StaticInnerSingleton getInstance() {
        return Holder.INSTANCE;
    }
}

// ==================== 6. 枚举单例 ====================

enum EnumSingleton {
    INSTANCE;

    public void doSomething() {
        System.out.println("EnumSingleton 执行操作");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 饿汉式: 类加载创建，简单但可能浪费
 * 2. 懒汉式: 延迟加载，需注意线程安全
 * 3. DCL: 双重检查，需要 volatile
 * 4. 静态内部类: 推荐，延迟加载+线程安全
 * 5. 枚举: 最推荐，防御反射和序列化
 * 
 * 🏃 练习:
 * 1. 实现一个配置管理器单例
 * 2. 用反射尝试破坏单例
 * 3. 测试不同实现在多线程下的表现
 */
