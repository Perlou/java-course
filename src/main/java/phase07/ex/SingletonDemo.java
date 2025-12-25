package phase07.ex;

public class SingletonDemo {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Phase 7 - Lesson 2: 单例模式");
        System.out.println("=".repeat(60));

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
        System.out.println("Syn 创建");
    }

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
        if (instance == null) {
            synchronized (DCLSingleton.class) {
                if (instance == null) {
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
