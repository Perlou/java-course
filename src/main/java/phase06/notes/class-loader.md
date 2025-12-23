# JVM 类加载子系统详解

## 📌 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         JVM 内存结构                              │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────┐                       │
│  │         类加载子系统 (Class Loader)    │  ◀── 本文重点         │
│  │    .class 文件 ──▶ 加载 ──▶ 内存       │                       │
│  └──────────────────────────────────────┘                       │
│                        ▼                                        │
│  ┌──────────────────────────────────────┐                       │
│  │         运行时数据区 (Runtime Data)    │                       │
│  │   方法区 | 堆 | 栈 | 程序计数器 | 本地方法栈 │                    │
│  └──────────────────────────────────────┘                       │
│                        ▼                                        │
│  ┌──────────────────────────────────────┐                       │
│  │           执行引擎 (Execution)         │                       │
│  └──────────────────────────────────────┘                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 类加载的完整过程

```
        加载 Loading
             │
             ▼
    ┌─────────────────┐
    │      链接        │
    │   Linking       │
    │  ┌───────────┐  │
    │  │  验证     │  │
    │  │ Verify    │  │
    │  ├───────────┤  │
    │  │  准备     │  │
    │  │ Prepare   │  │
    │  ├───────────┤  │
    │  │  解析     │  │
    │  │ Resolve   │  │
    │  └───────────┘  │
    └─────────────────┘
             │
             ▼
      初始化 Initialization
             │
             ▼
        使用 Using
             │
             ▼
        卸载 Unloading
```

---

## 📋 各阶段详解

### 1️⃣ 加载 (Loading)

```java
// 类加载器要完成的工作：
```

| 步骤               | 说明                                                   |
| ------------------ | ------------------------------------------------------ |
| ① 获取二进制流     | 通过类的全限定名获取 `.class` 文件的二进制字节流       |
| ② 转换为方法区结构 | 将字节流代表的静态存储结构转化为方法区的运行时数据结构 |
| ③ 生成 Class 对象  | 在堆中生成一个代表该类的 `java.lang.Class` 对象        |

```
┌────────────┐      ┌────────────┐      ┌────────────┐
│ .class文件  │ ──▶ │   方法区    │ ──▶  │ Class对象   │
│ (磁盘/网络) │      │ (类的元数据) │      │   (堆中)    │
└────────────┘      └────────────┘      └────────────┘
```

---

### 2️⃣ 链接 (Linking)

#### 🔹 验证 (Verify)

```
确保 .class 文件的字节流符合 JVM 规范，不会危害虚拟机安全

┌─────────────────────────────────────────────┐
│  文件格式验证 → 魔数是否为 0xCAFEBABE        │
│  元数据验证   → 是否有父类、是否继承了final类  │
│  字节码验证   → 指令是否合法、跳转是否正确     │
│  符号引用验证 → 引用的类、方法、字段是否存在    │
└─────────────────────────────────────────────┘
```

#### 🔹 准备 (Prepare)

```java
// 为类的静态变量分配内存并设置默认初始值（零值）
public class Example {
    // 准备阶段：value = 0（不是123！）
    // 初始化阶段：value = 123
    private static int value = 123;

    // 常量在准备阶段就赋真实值
    // 准备阶段：CONSTANT = 100
    private static final int CONSTANT = 100;
}
```

| 数据类型 | 默认零值 |
| -------- | -------- |
| int      | 0        |
| long     | 0L       |
| boolean  | false    |
| float    | 0.0f     |
| 引用类型 | null     |

#### 🔹 解析 (Resolve)

```
将常量池中的符号引用替换为直接引用

符号引用：用一组符号来描述引用的目标（如类名字符串）
直接引用：直接指向目标的指针、偏移量或句柄
```

```
┌─────────────────┐          ┌─────────────────┐
│  符号引用        │          │  直接引用        │
│ "java/lang/Object"│  ──▶   │  0x7f3a2b00     │
│   (字符串描述)    │          │  (内存地址)      │
└─────────────────┘          └─────────────────┘
```

---

### 3️⃣ 初始化 (Initialization)

```java
// 执行类构造器 <clinit>() 方法
public class InitExample {
    // 静态变量赋值
    static int a = 10;

    // 静态代码块
    static {
        a = 20;
        System.out.println("类初始化执行");
    }
}
```

**触发初始化的场景（主动引用）：**

| 场景           | 示例                    |
| -------------- | ----------------------- |
| new 实例化对象 | `new User()`            |
| 访问静态变量   | `User.name`             |
| 调用静态方法   | `User.getName()`        |
| 反射调用       | `Class.forName("User")` |
| 主类           | 包含 main() 方法的类    |
| 子类初始化     | 先触发父类初始化        |

---

## 🏗️ 类加载器层次结构

```
                    ┌─────────────────────────┐
                    │   Bootstrap ClassLoader │ ← C++实现，加载核心类库
                    │   (启动类加载器)          │   %JAVA_HOME%/lib
                    │   加载: rt.jar 等        │
                    └───────────┬─────────────┘
                                │ 父类加载器
                                ▼
                    ┌─────────────────────────┐
                    │  Extension ClassLoader  │ ← Java实现
                    │  (扩展类加载器)           │   %JAVA_HOME%/lib/ext
                    │  加载: ext/*.jar        │
                    └───────────┬─────────────┘
                                │ 父类加载器
                                ▼
                    ┌─────────────────────────┐
                    │ Application ClassLoader │ ← Java实现
                    │ (应用程序类加载器)        │   classpath
                    │ 加载: 用户类路径的类      │
                    └───────────┬─────────────┘
                                │ 父类加载器
                                ▼
                    ┌─────────────────────────┐
                    │   Custom ClassLoader    │ ← 用户自定义
                    │   (自定义类加载器)        │
                    └─────────────────────────┘
```

### 查看类加载器代码

```java
public class ClassLoaderDemo {
    public static void main(String[] args) {
        // 应用程序类加载器
        ClassLoader appLoader = ClassLoaderDemo.class.getClassLoader();
        System.out.println("App: " + appLoader);
        // 输出: sun.misc.Launcher$AppClassLoader@18b4aac2

        // 扩展类加载器
        ClassLoader extLoader = appLoader.getParent();
        System.out.println("Ext: " + extLoader);
        // 输出: sun.misc.Launcher$ExtClassLoader@1b6d3586

        // 启动类加载器（C++实现，返回null）
        ClassLoader bootLoader = extLoader.getParent();
        System.out.println("Boot: " + bootLoader);
        // 输出: null

        // String类由启动类加载器加载
        System.out.println("String: " + String.class.getClassLoader());
        // 输出: null
    }
}
```

---

## 🔒 双亲委派模型

### 工作原理

```
                         加载请求: "java.lang.String"
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Application ClassLoader                       │
│   ① 收到加载请求 → ② 委派给父加载器                               │
└─────────────────────────────────────────────────────────────────┘
                                    │ 委派
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Extension ClassLoader                         │
│   ① 收到委派 → ② 继续委派给父加载器                               │
└─────────────────────────────────────────────────────────────────┘
                                    │ 委派
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Bootstrap ClassLoader                         │
│   ① 收到委派 → ② 在rt.jar中找到 → ③ 加载成功，返回               │
└─────────────────────────────────────────────────────────────────┘
                                    │ 返回
                                    ▼
                              加载完成 ✓
```

### 源码实现

```java
protected Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
        // 1. 检查类是否已被加载
        Class<?> c = findLoadedClass(name);

        if (c == null) {
            try {
                if (parent != null) {
                    // 2. 委派给父类加载器
                    c = parent.loadClass(name, false);
                } else {
                    // 3. 父加载器为null，委派给Bootstrap
                    c = findBootstrapClassOrNull(name);
                }
            } catch (ClassNotFoundException e) {
                // 父类加载器无法加载
            }

            if (c == null) {
                // 4. 父类加载器无法加载，自己尝试加载
                c = findClass(name);
            }
        }
        return c;
    }
}
```

### 双亲委派的优点

| 优点             | 说明                                          |
| ---------------- | --------------------------------------------- |
| **安全性**       | 防止核心类被篡改（如自定义 java.lang.String） |
| **避免重复加载** | 父加载器加载过的类，子加载器不会重复加载      |
| **层次清晰**     | 保证 Java 程序的稳定运行                      |

---

## 🔧 自定义类加载器

```java
public class MyClassLoader extends ClassLoader {

    private String classPath;

    public MyClassLoader(String classPath) {
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // 1. 读取.class文件的字节数组
            byte[] classData = loadClassData(name);
            if (classData == null) {
                throw new ClassNotFoundException();
            }
            // 2. 调用defineClass将字节数组转换为Class对象
            return defineClass(name, classData, 0, classData.length);
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
    }

    private byte[] loadClassData(String className) throws IOException {
        String path = classPath + File.separator
                    + className.replace('.', File.separatorChar) + ".class";
        try (FileInputStream fis = new FileInputStream(path);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }
}

// 使用
MyClassLoader loader = new MyClassLoader("/custom/path");
Class<?> clazz = loader.loadClass("com.example.MyClass");
Object instance = clazz.newInstance();
```

---

## ⚡ 打破双亲委派模型

### 场景一：SPI 机制（Service Provider Interface）

```
问题：Bootstrap加载器加载了接口（如JDBC的Driver），
     但实现类在classpath中，需要Application加载器加载

解决：线程上下文类加载器
```

```java
// JDBC 加载驱动的实现
ServiceLoader<Driver> loader = ServiceLoader.load(Driver.class);
// 内部使用线程上下文类加载器
// Thread.currentThread().getContextClassLoader()
```

### 场景二：热部署/热替换

```java
// Tomcat等容器需要实现应用隔离和热部署
// 每个Web应用使用独立的类加载器
WebAppClassLoader loader1; // 应用1
WebAppClassLoader loader2; // 应用2
// 不同应用可以加载相同全限定名但不同版本的类
```

---

## 📊 总结图

```
┌────────────────────────────────────────────────────────────────┐
│                      类加载子系统全流程                          │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│   .class ──▶ [加载] ──▶ [验证] ──▶ [准备] ──▶ [解析] ──▶ [初始化] │
│      │         │                                       │       │
│      │         ▼                                       ▼       │
│      │    类加载器                                  <clinit>   │
│      │   (双亲委派)                                 静态变量赋值  │
│      │                                             静态代码块   │
│      ▼                                                         │
│   Class对象 ──▶ 存入方法区 ──▶ 供程序使用                        │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---
