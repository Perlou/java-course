# Phase 4: IO 与 NIO

> **目标**：掌握文件操作与 IO 编程  
> **预计时长**：1 周  
> **前置条件**：Phase 3 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解 Java IO 流体系
2. 熟练进行文件读写操作
3. 掌握 NIO 的 Buffer 和 Channel
4. 理解对象序列化机制
5. 使用 Files 工具类进行现代文件操作

---

## 📚 核心概念

### IO 流体系

```
InputStream/OutputStream (字节流)
├── FileInputStream/FileOutputStream
├── BufferedInputStream/BufferedOutputStream
├── DataInputStream/DataOutputStream
└── ObjectInputStream/ObjectOutputStream

Reader/Writer (字符流)
├── FileReader/FileWriter
├── BufferedReader/BufferedWriter
├── InputStreamReader/OutputStreamWriter
└── PrintWriter
```

### NIO 核心组件

```
Buffer (缓冲区)
├── ByteBuffer
├── CharBuffer
├── IntBuffer
└── ...

Channel (通道)
├── FileChannel
├── SocketChannel
└── ...

Files (工具类)
├── readString/writeString
├── readAllLines/write
├── walk/list/find
└── copy/move/delete
```

---

## 📁 文件列表

| #   | 文件                                               | 描述               | 知识点                    |
| --- | -------------------------------------------------- | ------------------ | ------------------------- |
| 1   | [FileIODemo.java](./FileIODemo.java)               | File 类与路径      | File, Path, Paths         |
| 2   | [ByteStreamDemo.java](./ByteStreamDemo.java)       | 字节流操作         | InputStream, OutputStream |
| 3   | [CharStreamDemo.java](./CharStreamDemo.java)       | 字符流操作         | Reader, Writer, 编码      |
| 4   | [NioBasics.java](./NioBasics.java)                 | NIO Buffer/Channel | ByteBuffer, FileChannel   |
| 5   | [NioFileDemo.java](./NioFileDemo.java)             | Files 工具类       | Files, walk, find         |
| 6   | [SerializationDemo.java](./SerializationDemo.java) | 对象序列化         | Serializable, transient   |
| 7   | [FileTool.java](./FileTool.java)                   | 🎯 **实战项目**    | 文件批量处理工具          |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行示例 (替换类名即可)
mvn exec:java -Dexec.mainClass="phase04.FileIODemo"
mvn exec:java -Dexec.mainClass="phase04.ByteStreamDemo"
mvn exec:java -Dexec.mainClass="phase04.NioBasics"

# 运行实战项目 (交互式)
mvn exec:java -Dexec.mainClass="phase04.FileTool"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1**: FileIODemo - 理解 File 和 Path
2. **Day 2**: ByteStreamDemo - 字节流操作
3. **Day 3**: CharStreamDemo - 字符流与编码
4. **Day 4**: NioBasics - Buffer 和 Channel
5. **Day 5**: NioFileDemo - Files 工具类
6. **Day 6**: SerializationDemo - 序列化
7. **Day 7**: FileTool 实战项目

### 学习方法

1. 先运行每个 Demo，观察输出
2. 理解 IO 和 NIO 的区别
3. 重点掌握 try-with-resources
4. 完成每个文件末尾的练习题

---

## ✅ 完成检查

- [ ] 理解字节流和字符流的区别
- [ ] 掌握 BufferedReader/BufferedWriter
- [ ] 能正确处理字符编码
- [ ] 理解 Buffer 的 flip/clear/compact
- [ ] 熟练使用 Files 工具类
- [ ] 理解序列化和 transient
- [ ] 完成文件批量处理工具项目

---

## 🎯 实战项目: 文件批量处理工具

`FileTool.java` 是本阶段的综合项目，功能包括：

- 目录浏览和文件列表
- 文件搜索（通配符支持）
- 代码行数统计
- 最大文件查找
- 重复文件检测
- 批量重命名
- 文本文件合并

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase04.FileTool"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 5: 并发编程基础](../phase05/README.md)
