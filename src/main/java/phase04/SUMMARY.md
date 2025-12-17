# Phase 4 学习总结: IO 与 NIO ✅

> **完成日期**: 2025 年 12 月 16 日  
> **学习时长**: 约 1 周  
> **状态**: 已完成 🎉

---

## 📚 知识点掌握情况

### 1. File 类与路径操作 ✅

- [x] 理解 File 类的作用（代表文件或目录的抽象路径）
- [x] 掌握文件/目录的创建、删除、重命名
- [x] 熟练使用 list() 和 listFiles() 遍历目录
- [x] 掌握 Path 和 Paths (NIO.2)

```java
// File 常用方法
file.exists();
file.isFile();
file.isDirectory();
file.createNewFile();
file.mkdir();
file.mkdirs();
file.delete();
file.listFiles();

// Path (推荐)
Path path = Paths.get("src", "main", "java");
path.resolve("file.txt");
path.getParent();
path.normalize();
```

### 2. 字节流 ✅

- [x] 理解 InputStream/OutputStream 体系
- [x] 掌握 FileInputStream/FileOutputStream
- [x] 熟练使用缓冲流 BufferedInputStream/BufferedOutputStream
- [x] 掌握 DataInputStream/DataOutputStream 基本类型读写

**字节流体系**:

```
InputStream/OutputStream
├── FileInputStream/FileOutputStream    文件读写
├── BufferedInputStream/BufferedOutputStream  缓冲（性能优化）
├── DataInputStream/DataOutputStream    基本类型
└── ObjectInputStream/ObjectOutputStream 对象序列化
```

**核心方法**:
| 方法 | 描述 |
|------|------|
| `read()` | 读取一个字节，返回 -1 表示结束 |
| `read(byte[])` | 批量读取 |
| `write(int)` | 写入一个字节 |
| `write(byte[])` | 批量写入 |
| `flush()` | 刷新缓冲区 |
| `close()` | 关闭流 |

### 3. 字符流 ✅

- [x] 理解字符流与字节流的区别
- [x] 掌握 FileReader/FileWriter
- [x] 熟练使用 BufferedReader/BufferedWriter
- [x] 正确处理字符编码

```java
// 指定编码 (推荐)
try (BufferedReader br = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(file),
            StandardCharsets.UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}

// PrintWriter 格式化输出
PrintWriter pw = new PrintWriter(new FileWriter(file));
pw.println("Hello");
pw.printf("Name: %s, Age: %d%n", name, age);
```

**字节流 vs 字符流**:
| 特性 | 字节流 | 字符流 |
|------|--------|--------|
| 处理单位 | 字节 (byte) | 字符 (char) |
| 适用场景 | 二进制文件 | 文本文件 |
| 编码处理 | 手动 | 自动 |

### 4. NIO Buffer ✅

- [x] 理解 Buffer 的核心概念
- [x] 掌握 position, limit, capacity 属性
- [x] 熟练使用 flip(), clear(), rewind(), compact()
- [x] 了解直接缓冲区与堆缓冲区

```java
ByteBuffer buffer = ByteBuffer.allocate(1024);

// 写入数据
buffer.put((byte) 'H');
buffer.put("ello".getBytes());

// 切换为读模式
buffer.flip();

// 读取数据
while (buffer.hasRemaining()) {
    System.out.print((char) buffer.get());
}

// 清空准备写入
buffer.clear();
```

**Buffer 关键方法**:
| 方法 | 作用 |
|------|------|
| `flip()` | 切换为读模式 |
| `clear()` | 清空，准备写入 |
| `rewind()` | 重置 position 为 0 |
| `compact()` | 保留未读数据，准备继续写入 |

### 5. NIO Channel ✅

- [x] 理解 Channel 的概念（双向读写）
- [x] 掌握 FileChannel 的使用
- [x] 了解 transferTo/transferFrom 高效复制
- [x] 理解内存映射文件 MappedByteBuffer

```java
// 使用 Channel 读写
try (FileChannel channel = FileChannel.open(
        path, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
    ByteBuffer buf = ByteBuffer.allocate(1024);
    channel.read(buf);
    buf.flip();
    channel.write(buf);
}

// 高效文件复制
srcChannel.transferTo(0, srcChannel.size(), destChannel);
```

### 6. Files 工具类 ✅

- [x] 掌握文件读写: readString, writeString, readAllLines
- [x] 掌握文件操作: copy, move, delete
- [x] 熟练使用 walk, list, find 遍历目录
- [x] 了解文件属性操作

```java
// 文件读写 (Java 11+)
String content = Files.readString(path);
Files.writeString(path, "Hello");
List<String> lines = Files.readAllLines(path);

// 流式处理大文件
try (Stream<String> stream = Files.lines(path)) {
    long count = stream.filter(line -> !line.isEmpty()).count();
}

// 目录遍历
try (Stream<Path> stream = Files.walk(dir)) {
    stream.filter(Files::isRegularFile)
          .forEach(System.out::println);
}

// 文件查找
try (Stream<Path> stream = Files.find(dir, Integer.MAX_VALUE,
        (path, attrs) -> attrs.isRegularFile()
                      && path.toString().endsWith(".java"))) {
    stream.forEach(System.out::println);
}
```

### 7. 对象序列化 ✅

- [x] 理解 Serializable 接口
- [x] 掌握 ObjectInputStream/ObjectOutputStream
- [x] 理解 transient 关键字
- [x] 了解 serialVersionUID 版本控制

```java
// 序列化
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("object.ser"))) {
    oos.writeObject(person);
}

// 反序列化
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("object.ser"))) {
    Person p = (Person) ois.readObject();
}
```

**序列化注意事项**:

- 实现 `Serializable` 接口
- `transient` 修饰的字段不序列化
- 显式声明 `serialVersionUID`
- 考虑使用 JSON 替代

---

## 🎯 实战项目: 文件批量处理工具

成功完成了 `FileTool.java` 项目，实现了交互式文件处理工具:

| 功能       | 使用技术                    |
| ---------- | --------------------------- |
| 目录浏览   | Files.walk, Files.list      |
| 文件搜索   | PathMatcher, glob 模式      |
| 代码统计   | Files.lines, Stream API     |
| 最大文件   | Files.size, sorted          |
| 重复检测   | 基于文件大小的分组          |
| 批量重命名 | Files.move                  |
| 文件合并   | BufferedWriter, Files.lines |

---

## 📊 学习文件清单

| #   | 文件                     | 状态 | 核心知识点                |
| --- | ------------------------ | ---- | ------------------------- |
| 1   | `FileIODemo.java`        | ✅   | File, Path, Paths         |
| 2   | `ByteStreamDemo.java`    | ✅   | InputStream, OutputStream |
| 3   | `CharStreamDemo.java`    | ✅   | Reader, Writer, 编码      |
| 4   | `NioBasics.java`         | ✅   | ByteBuffer, FileChannel   |
| 5   | `NioFileDemo.java`       | ✅   | Files, walk, find         |
| 6   | `SerializationDemo.java` | ✅   | Serializable, transient   |
| 7   | `FileTool.java`          | ✅   | **综合项目**              |

---

## 💡 重点心得

### IO vs NIO

```
传统 IO                    NIO
─────────────────────    ─────────────────────
面向流 (Stream)           面向缓冲区 (Buffer)
阻塞 IO                   非阻塞 IO
无选择器                  Selector 多路复用
一个线程一个连接          一个线程多个连接
```

### 最佳实践

1. **始终使用 try-with-resources**

   ```java
   try (BufferedReader br = new BufferedReader(...)) {
       // 自动关闭资源
   }
   ```

2. **使用缓冲流提高性能**

   ```java
   new BufferedInputStream(new FileInputStream(file))
   ```

3. **明确指定字符编码**

   ```java
   new InputStreamReader(fis, StandardCharsets.UTF_8)
   ```

4. **大文件使用流式处理**

   ```java
   Files.lines(path).filter(...).forEach(...)
   ```

5. **新代码优先使用 NIO.2**
   ```java
   Files.readString(), Files.writeString(), Files.walk()
   ```

### 常见陷阱

- ❌ 忘记关闭流导致资源泄漏
- ❌ 使用默认编码导致乱码
- ❌ 逐字节读写导致性能差
- ❌ 序列化敏感数据
- ❌ 不处理 IOException

---

## 🚀 下一步计划

Phase 4 已完成！准备进入 **[Phase 5: 并发编程基础](../phase05/README.md)**

Phase 5 将学习:

- 线程的创建和生命周期
- synchronized 同步机制
- wait/notify 线程通信
- 线程池 ExecutorService
- 并发工具类

---

> 📝 _本总结由学习过程自动生成于 2025-12-16_
