# Phase 6: JVM 深入

> **目标**：深入理解 JVM 原理与调优  
> **预计时长**：2 周  
> **前置条件**：Phase 5 完成

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

- 堆（Heap）：对象存储
- 栈（Stack）：方法调用
- 方法区：类信息、常量池
- 程序计数器：当前执行位置

### GC 算法

- 标记-清除
- 复制算法
- 标记-整理
- 分代收集

---

## 📁 文件列表

| 文件                     | 描述           | 状态 |
| ------------------------ | -------------- | ---- |
| `MemoryModelDemo.java`   | 堆、栈、方法区 | ⏳   |
| `HeapDemo.java`          | 堆内存分析     | ⏳   |
| `StackDemo.java`         | 栈溢出演示     | ⏳   |
| `GCBasics.java`          | GC 算法演示    | ⏳   |
| `GCRootsDemo.java`       | GC Roots 分析  | ⏳   |
| `GCTuningDemo.java`      | GC 调优参数    | ⏳   |
| `ClassLoaderDemo.java`   | 类加载器       | ⏳   |
| `CustomClassLoader.java` | 自定义类加载器 | ⏳   |
| `JvmMonitorDemo.java`    | 监控工具使用   | ⏳   |
| `MemoryLeakDemo.java`    | 内存泄漏分析   | ⏳   |

---

## ✅ 完成检查

- [ ] 能够分析 JVM 内存使用情况
- [ ] 理解 GC 日志
- [ ] 能够进行基本的 JVM 调优
- [ ] 完成 JVM 性能分析报告
