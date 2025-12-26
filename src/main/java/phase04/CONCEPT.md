# Phase 4: IO 与文件操作 - 核心概念

> IO 是程序与外部世界交换数据的桥梁

---

## 🎯 IO 体系全景图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Java IO 体系                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  传统 IO (java.io)                                                 │
│  ├── 字节流 (Byte Stream)                                          │
│  │   ├── InputStream / OutputStream  (抽象基类)                    │
│  │   ├── FileInputStream / FileOutputStream                        │
│  │   ├── BufferedInputStream / BufferedOutputStream                │
│  │   └── DataInputStream / DataOutputStream                        │
│  │                                                                  │
│  └── 字符流 (Character Stream)                                     │
│      ├── Reader / Writer  (抽象基类)                               │
│      ├── FileReader / FileWriter                                   │
│      ├── BufferedReader / BufferedWriter                           │
│      └── InputStreamReader / OutputStreamWriter                    │
│                                                                     │
│  NIO (java.nio) - New IO                                           │
│  ├── Channel - 双向通道                                            │
│  ├── Buffer - 数据缓冲区                                           │
│  ├── Selector - 多路复用器                                         │
│  └── Files/Path - 文件操作工具类                                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📝 字节流 vs 字符流

```
┌─────────────────────────────────────────────────────────────────────┐
│                    字节流 vs 字符流                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  字节流 (Byte Stream)              字符流 (Character Stream)        │
│  ─────────────────────            ─────────────────────            │
│  • 以字节为单位 (8 bit)           • 以字符为单位 (16 bit)           │
│  • 处理二进制数据                  • 处理文本数据                    │
│  • 图片、视频、音频                • 文本文件、配置文件              │
│  • InputStream/OutputStream       • Reader/Writer                   │
│                                                                     │
│  选择原则:                                                          │
│  • 文本文件 → 字符流                                               │
│  • 二进制文件 → 字节流                                             │
│  • 不确定 → 字节流                                                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 💾 文件读写

### 传统方式

```java
// 字节流读取
try (FileInputStream fis = new FileInputStream("file.txt")) {
    byte[] buffer = new byte[1024];
    int len;
    while ((len = fis.read(buffer)) != -1) {
        // 处理数据
    }
}

// 字符流读取 (推荐使用 BufferedReader)
try (BufferedReader reader = new BufferedReader(
        new FileReader("file.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

// 字符流写入
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("file.txt"))) {
    writer.write("Hello World");
    writer.newLine();
}
```

### NIO 方式 (推荐)

```java
// 读取所有行
List<String> lines = Files.readAllLines(Path.of("file.txt"));

// 读取所有字节
byte[] bytes = Files.readAllBytes(Path.of("file.txt"));

// 写入文件
Files.writeString(Path.of("file.txt"), "Hello World");
Files.write(Path.of("file.txt"), lines);

// 复制文件
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

// 移动文件
Files.move(source, target);

// 删除文件
Files.delete(Path.of("file.txt"));
Files.deleteIfExists(Path.of("file.txt"));
```

---

## 🔄 NIO 核心组件

```
┌─────────────────────────────────────────────────────────────────────┐
│                        NIO 三大组件                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Channel (通道)                                                     │
│  ─────────────────────────────────────────────────────────────     │
│  • 双向数据传输                                                     │
│  • FileChannel, SocketChannel, ServerSocketChannel                 │
│                                                                     │
│  Buffer (缓冲区)                                                    │
│  ─────────────────────────────────────────────────────────────     │
│  • 数据容器                                                         │
│  • ByteBuffer, CharBuffer, IntBuffer...                            │
│  • 核心属性: capacity, position, limit                             │
│                                                                     │
│  Selector (选择器)                                                  │
│  ─────────────────────────────────────────────────────────────     │
│  • 多路复用器                                                       │
│  • 一个线程管理多个 Channel                                         │
│  • 适用于高并发网络编程                                             │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Buffer 状态

```
写模式:
┌───────────────────────────────────────┐
│ ■ ■ ■ ■ ■ □ □ □ □ □                   │
│ 0       ↑           ↑                 │
│      position     limit=capacity      │
└───────────────────────────────────────┘

flip() 后 (切换到读模式):
┌───────────────────────────────────────┐
│ ■ ■ ■ ■ ■ □ □ □ □ □                   │
│ ↑         ↑         ↑                 │
│ position limit   capacity             │
└───────────────────────────────────────┘
```

---

## 📁 文件操作

### Path 与 Files

```java
// 创建 Path
Path path = Path.of("dir", "file.txt");
Path path = Paths.get("/home/user/file.txt");

// Path 操作
path.getFileName();      // 文件名
path.getParent();        // 父目录
path.toAbsolutePath();   // 绝对路径
path.resolve("sub");     // 拼接路径

// Files 常用操作
Files.exists(path);              // 是否存在
Files.isDirectory(path);         // 是否是目录
Files.isRegularFile(path);       // 是否是普通文件
Files.size(path);                // 文件大小
Files.createDirectory(path);     // 创建目录
Files.createDirectories(path);   // 创建多级目录

// 遍历目录
try (Stream<Path> stream = Files.list(dir)) {
    stream.forEach(System.out::println);
}

// 递归遍历
try (Stream<Path> stream = Files.walk(dir)) {
    stream.filter(Files::isRegularFile)
          .forEach(System.out::println);
}
```

---

## 🔐 序列化

```java
// 实现 Serializable 接口
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private transient String password;  // 不序列化
}

// 序列化（写入文件）
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("user.dat"))) {
    oos.writeObject(user);
}

// 反序列化（读取文件）
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("user.dat"))) {
    User user = (User) ois.readObject();
}
```

---

## ⚠️ 最佳实践

```
1. 使用 try-with-resources 自动关闭资源
   try (InputStream is = ...) { }

2. 使用缓冲流提高性能
   BufferedReader, BufferedWriter, BufferedInputStream...

3. 优先使用 NIO Files 工具类
   Files.readString(), Files.writeString()

4. 大文件使用流式处理
   Files.lines(path).forEach(...)

5. 指定字符编码
   new InputStreamReader(fis, StandardCharsets.UTF_8)
```

---

## 📖 学习要点

```
✅ 理解字节流和字符流的区别
✅ 掌握 try-with-resources 语法
✅ 熟练使用 Files 工具类
✅ 理解 NIO Buffer 的状态转换
✅ 掌握对象序列化与反序列化
```

---

> 接下来学习多线程编程：[Phase 5 README](../phase05/README.md)
