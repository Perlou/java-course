# Phase 6: JVM 深入

> **目标**：深入理解 JVM 原理与调优  
> **预计时长**：2 周  
> **前置条件**：Phase 5 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解 JVM 内存模型
2. 掌握垃圾回收机制
3. 理解类加载过程
4. 能够进行 JVM 调优
5. 能够分析和解决内存问题

---

## 📚 核心概念

### JVM 内存结构

```
┌─────────────────────────────────────────────────────────┐
│                   运行时数据区                           │
├──────────────────────┬──────────────────────────────────┤
│    线程私有区域       │         线程共享区域             │
├──────────────────────┼──────────────────────────────────┤
│  程序计数器           │           堆 (Heap)              │
│  虚拟机栈             │         方法区 (元空间)          │
│  本地方法栈           │                                  │
└──────────────────────┴──────────────────────────────────┘
```

### GC 算法

| 算法      | 特点             | 应用场景 |
| --------- | ---------------- | -------- |
| 标记-清除 | 简单，有碎片     | 早期实现 |
| 复制算法  | 无碎片，空间减半 | 新生代   |
| 标记-整理 | 无碎片，需移动   | 老年代   |

### 垃圾收集器

- **G1**: JDK9+ 默认，可预测停顿
- **ZGC**: 超低延迟 (<10ms)
- **Shenandoah**: 低延迟

---

## 📁 文件列表

| #   | 文件                                           | 描述            | 知识点                       |
| --- | ---------------------------------------------- | --------------- | ---------------------------- |
| 1   | [MemoryModelDemo.java](./MemoryModelDemo.java) | JVM 内存模型    | 堆、栈、方法区、JMM          |
| 2   | [HeapStackDemo.java](./HeapStackDemo.java)     | 堆与栈详解      | 对象分配、逃逸分析           |
| 3   | [GCDemo.java](./GCDemo.java)                   | 垃圾回收        | GC 算法、收集器、引用类型    |
| 4   | [ClassLoaderDemo.java](./ClassLoaderDemo.java) | 类加载机制      | 双亲委派、自定义 ClassLoader |
| 5   | [JvmTuningDemo.java](./JvmTuningDemo.java)     | JVM 调优        | 监控工具、参数、GC 日志      |
| 6   | [JvmMonitor.java](./JvmMonitor.java)           | 🎯 **实战项目** | JVM 监控报告生成器           |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行示例
mvn exec:java -Dexec.mainClass="phase06.MemoryModelDemo"
mvn exec:java -Dexec.mainClass="phase06.GCDemo"
mvn exec:java -Dexec.mainClass="phase06.ClassLoaderDemo"

# 运行实战项目
mvn exec:java -Dexec.mainClass="phase06.JvmMonitor"
```

### 带 JVM 参数运行

```bash
# 设置堆大小
mvn exec:java -Dexec.mainClass="phase06.HeapStackDemo" \
  -Dexec.args="" \
  -Dexec.jvmArgs="-Xms128m -Xmx256m"

# 开启 GC 日志
mvn exec:java -Dexec.mainClass="phase06.GCDemo" \
  -Dexec.jvmArgs="-Xlog:gc*"
```

---

## 🔧 常用 JVM 工具

```bash
# 列出 Java 进程
jps -l

# 查看 JVM 参数
jinfo -flags <pid>

# 监控 GC
jstat -gcutil <pid> 1000

# 生成堆 dump
jmap -dump:format=b,file=heap.hprof <pid>

# 生成线程 dump
jstack <pid>

# 综合诊断工具
jcmd <pid> help
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1-2**: MemoryModelDemo - JVM 架构和内存区域
2. **Day 3-4**: HeapStackDemo - 堆栈深入理解
3. **Day 5-6**: GCDemo - 垃圾回收原理 (重点)
4. **Day 7-8**: ClassLoaderDemo - 类加载机制
5. **Day 9-10**: JvmTuningDemo - 调优方法
6. **Day 11-14**: JvmMonitor 项目 + 工具实践

### 学习方法

1. 使用不同 JVM 参数运行程序
2. 用 jstat/jmap/jstack 实际操作
3. 分析 GC 日志
4. 模拟 OOM 并诊断

---

## ✅ 完成检查

- [ ] 理解 JVM 内存区域划分
- [ ] 掌握对象创建过程
- [ ] 理解 GC 算法和收集器选择
- [ ] 理解双亲委派模型
- [ ] 能够使用 jps/jstat/jmap/jstack
- [ ] 能够分析 GC 日志
- [ ] 完成 JVM 监控报告项目

---

## 🎯 实战项目: JVM 监控报告生成器

`JvmMonitor.java` 是本阶段的综合项目，功能包括：

- 生成完整 JVM 诊断报告
- 实时内存监控
- GC 监控和统计
- 线程状态分析
- 模拟内存泄漏
- GC 压力测试

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase06.JvmMonitor"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 7: 设计模式](../phase07/README.md)
