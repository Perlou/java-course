package phase06;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Phase 6 - Lesson 4: 类加载机制
 * 
 * 🎯 学习目标:
 * 1. 理解类加载过程
 * 2. 掌握类加载器体系
 * 3. 理解双亲委派模型
 * 4. 学会自定义类加载器
 */
public class ClassLoaderDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("Phase 6 - Lesson 4: 类加载机制");
        System.out.println("=".repeat(60));

        // 1. 类加载过程
        System.out.println("\n【1. 类加载过程】");
        System.out.println("""
                类的生命周期:

                ┌─────────┐ ┌─────────────────────────────┐ ┌────────┐
                │  加载   │→│           链接              │→│ 初始化 │
                │ Loading │ │ Verification→Preparation    │ │ Init   │
                │         │ │           →Resolution       │ │        │
                └─────────┘ └─────────────────────────────┘ └────────┘
                                                                  ↓
                ┌─────────┐ ┌──────────┐
                │  使用   │→│  卸载    │
                │  Using  │ │ Unloading│
                └─────────┘ └──────────┘

                详细说明:
                1. 加载 (Loading)
                   - 通过类名获取字节码
                   - 字节码可来自 JAR、网络、动态生成等
                   - 创建 Class 对象

                2. 验证 (Verification)
                   - 确保字节码符合 JVM 规范
                   - 文件格式、元数据、字节码、符号引用验证

                3. 准备 (Preparation)
                   - 为静态变量分配内存
                   - 设置零值 (不是初始值!)
                   - static int x = 10; 此时 x = 0

                4. 解析 (Resolution)
                   - 符号引用 → 直接引用
                   - 可选，可推迟到使用时

                5. 初始化 (Initialization)
                   - 执行 <clinit> 方法
                   - 执行静态变量赋值和静态代码块
                """);

        // 2. 类加载器体系
        System.out.println("【2. 类加载器体系】");
        System.out.println("""
                ┌─────────────────────────────────────────────────────────┐
                │                  Bootstrap ClassLoader                   │
                │                    (启动类加载器)                        │
                │              加载 JAVA_HOME/lib 中核心类                 │
                │              如 rt.jar, java.lang.*                      │
                │              由 C++ 实现，Java中为 null                  │
                └───────────────────────┬─────────────────────────────────┘
                                        ↓
                ┌─────────────────────────────────────────────────────────┐
                │               Extension ClassLoader                      │
                │                 (扩展类加载器)                           │
                │              JDK9+: Platform ClassLoader                │
                │              加载 JAVA_HOME/lib/ext 中类                │
                └───────────────────────┬─────────────────────────────────┘
                                        ↓
                ┌─────────────────────────────────────────────────────────┐
                │               Application ClassLoader                    │
                │                 (应用类加载器)                           │
                │              加载 classpath 中的类                       │
                │              系统默认类加载器                            │
                └───────────────────────┬─────────────────────────────────┘
                                        ↓
                ┌─────────────────────────────────────────────────────────┐
                │                Custom ClassLoader                        │
                │                 (自定义类加载器)                         │
                │              用户自定义，可加载任意位置的类             │
                └─────────────────────────────────────────────────────────┘
                """);

        // 3. 查看类加载器
        System.out.println("【3. 查看类加载器】");

        // 应用类加载器
        ClassLoader appClassLoader = ClassLoaderDemo.class.getClassLoader();
        System.out.println("ClassLoaderDemo 的类加载器: " + appClassLoader);

        // 扩展类加载器 (父加载器)
        ClassLoader extClassLoader = appClassLoader.getParent();
        System.out.println("父加载器: " + extClassLoader);

        // 启动类加载器 (祖父)
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println("祖父加载器: " + bootstrapClassLoader + " (Bootstrap为null)");

        // 核心类的加载器
        System.out.println("\n核心类的加载器:");
        System.out.println("String.class: " + String.class.getClassLoader());
        System.out.println("Object.class: " + Object.class.getClassLoader());

        // 4. 双亲委派模型
        System.out.println("\n【4. 双亲委派模型】");
        System.out.println("""
                加载类的流程:

                ┌─────────────────────────────────────────────────┐
                │              请求加载 com.example.MyClass        │
                └───────────────────────┬─────────────────────────┘
                                        ↓
                ┌───────────────────────────────────────────────────┐
                │ App ClassLoader: 检查是否已加载                   │
                │   未加载 → 委派给父加载器                         │
                └─────────────────────────┬─────────────────────────┘
                                          ↓
                ┌───────────────────────────────────────────────────┐
                │ Ext ClassLoader: 检查是否已加载                   │
                │   未加载 → 委派给父加载器                         │
                └─────────────────────────┬─────────────────────────┘
                                          ↓
                ┌───────────────────────────────────────────────────┐
                │ Bootstrap ClassLoader: 检查是否已加载             │
                │   未加载 → 尝试自己加载                           │
                │   加载失败 → 返回给子加载器处理                   │
                └─────────────────────────┬─────────────────────────┘
                                          ↓
                ┌───────────────────────────────────────────────────┐
                │ 向下传递，每层尝试加载                             │
                │ 直到某层成功加载或全部失败                        │
                └───────────────────────────────────────────────────┘

                双亲委派的优点:
                1. 避免类的重复加载
                2. 保护核心类不被篡改 (安全)
                   - 自定义 java.lang.String 不会被加载
                """);

        // 5. 类加载的时机
        System.out.println("【5. 类加载的时机】");
        System.out.println("""
                主动使用 (会触发初始化):
                1. new 创建对象
                2. 访问静态变量 (非 final 常量)
                3. 调用静态方法
                4. 反射 Class.forName()
                5. 初始化子类时先初始化父类
                6. main 方法所在的类

                被动使用 (不会触发初始化):
                1. 通过子类引用父类的静态字段
                2. 通过数组定义来引用类
                3. 引用类的 final 常量
                """);

        System.out.println("演示类加载时机:");

        // final 常量不会触发初始化
        System.out.println("访问 final 常量: " + NotInit.CONSTANT);

        // 访问静态变量会触发初始化
        System.out.println("访问静态变量: " + WillInit.value);

        // 6. Class.forName vs ClassLoader.loadClass
        System.out.println("\n【6. Class.forName vs ClassLoader.loadClass】");
        System.out.println("""
                Class.forName(className)
                - 加载 + 链接 + 初始化
                - 会执行静态代码块
                - 常用于 JDBC 驱动加载

                ClassLoader.loadClass(className)
                - 只加载，不初始化
                - 延迟初始化
                - 适合只需要类信息而不执行的场景
                """);

        System.out.println("使用 Class.forName 加载:");
        Class.forName("phase06.ForNameTest");

        System.out.println("\n使用 ClassLoader.loadClass 加载:");
        ClassLoader.getSystemClassLoader().loadClass("phase06.LoadClassTest");
        System.out.println("(注意 LoadClassTest 的静态块没有执行)");

        // 7. 自定义类加载器
        System.out.println("\n【7. 自定义类加载器】");
        System.out.println("""
                使用场景:
                1. 从非标准位置加载类 (网络、数据库、加密文件)
                2. 类隔离 (不同模块加载相同类的不同版本)
                3. 热部署 (重新加载修改后的类)

                实现方式:
                - 继承 ClassLoader
                - 重写 findClass() 方法 (推荐)
                - 遵循双亲委派: 调用 defineClass()

                破坏双亲委派:
                - 重写 loadClass() 方法
                - 典型案例: JDBC、Tomcat、OSGI
                """);

        // 自定义类加载器示例
        System.out.println("\n自定义 ClassLoader 示例:");

        MyClassLoader myLoader = new MyClassLoader();
        try {
            // 这里只是演示，实际加载会失败因为没有对应的类文件
            Class<?> clazz = myLoader.loadClass("phase06.ClassLoaderDemo");
            System.out.println("加载类: " + clazz.getName());
            System.out.println("类加载器: " + clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            System.out.println("预期: 类由父加载器加载");
        }

        // 8. 模块系统 (JDK9+)
        System.out.println("\n【8. 模块系统 (JDK9+)】");
        System.out.println("""
                JDK9 引入模块系统 (JPMS):
                - 类加载器变化
                - 模块路径替代部分类路径
                - 更严格的封装

                模块化后的类加载器:
                ┌────────────────────┐
                │ Bootstrap Loader   │ ← java.base 模块
                └─────────┬──────────┘
                          ↓
                ┌────────────────────┐
                │ Platform Loader    │ ← 平台模块
                │ (原 Extension)     │
                └─────────┬──────────┘
                          ↓
                ┌────────────────────┐
                │ Application Loader │ ← 应用模块/类路径
                └────────────────────┘
                """);

        // 获取模块信息
        Module module = ClassLoaderDemo.class.getModule();
        System.out.println("当前类所在模块: " + module.getName());

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 类加载采用双亲委派，保证核心类安全");
        System.out.println("💡 Class.forName 会初始化类，loadClass 不会");
        System.out.println("💡 自定义 ClassLoader 重写 findClass 方法");
        System.out.println("=".repeat(60));
    }
}

/**
 * 不会被初始化 - final 常量
 */
class NotInit {
    static final String CONSTANT = "常量";

    static {
        System.out.println("NotInit 被初始化");
    }
}

/**
 * 会被初始化 - 静态变量
 */
class WillInit {
    static int value = 100;

    static {
        System.out.println("WillInit 被初始化");
    }
}

/**
 * Class.forName 测试
 */
class ForNameTest {
    static {
        System.out.println("  ForNameTest 静态代码块执行!");
    }
}

/**
 * ClassLoader.loadClass 测试
 */
class LoadClassTest {
    static {
        System.out.println("  LoadClassTest 静态代码块执行!");
    }
}

/**
 * 自定义类加载器
 */
class MyClassLoader extends ClassLoader {

    private String classPath = "target/classes/";

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = loadClassData(name);
        if (data == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, data, 0, data.length);
    }

    private byte[] loadClassData(String name) {
        String path = classPath + name.replace('.', '/') + ".class";
        try (FileInputStream fis = new FileInputStream(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 类加载过程:
 * 加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载
 * 
 * 2. 类加载器:
 * - Bootstrap: 核心类
 * - Platform/Extension: 平台/扩展类
 * - Application: 应用类
 * - Custom: 自定义
 * 
 * 3. 双亲委派:
 * - 向上委派，向下加载
 * - 保证类唯一性和安全性
 * 
 * 4. 初始化时机:
 * - new、静态成员访问、反射等触发
 * - final 常量不触发初始化
 * 
 * 🏃 练习:
 * 1. 实现一个从网络加载类的 ClassLoader
 * 2. 实现一个加密类加载器
 * 3. 尝试加载自定义的 java.lang.String 观察结果
 */
