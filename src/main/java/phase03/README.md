# Phase 3: 集合框架

> **目标**：掌握 Java 集合框架与 Stream API  
> **预计时长**：1 周  
> **前置条件**：Phase 2 完成

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 理解集合框架的体系结构
2. 熟练使用 List、Set、Map
3. 理解 HashMap 的实现原理
4. 掌握 Stream API 的使用
5. 了解线程安全的集合类

---

## 📚 核心概念

### 集合体系

```
Collection
├── List (有序，可重复)
│   ├── ArrayList
│   └── LinkedList
├── Set (无序，不重复)
│   ├── HashSet
│   └── TreeSet
└── Queue (队列)

Map (键值对)
├── HashMap
├── TreeMap
└── ConcurrentHashMap
```

---

## 📁 文件列表

| 文件                     | 描述                | 状态 |
| ------------------------ | ------------------- | ---- |
| `ArrayListDemo.java`     | ArrayList 操作      | ⏳   |
| `LinkedListDemo.java`    | LinkedList 与 Deque | ⏳   |
| `HashSetDemo.java`       | HashSet 与 TreeSet  | ⏳   |
| `HashMapDemo.java`       | HashMap 原理与使用  | ⏳   |
| `TreeMapDemo.java`       | TreeMap 与排序      | ⏳   |
| `ConcurrentMapDemo.java` | 线程安全 Map        | ⏳   |
| `StreamBasics.java`      | Stream 创建与操作   | ⏳   |
| `StreamAdvanced.java`    | Collector 与归约    | ⏳   |

---

## ✅ 完成检查

- [ ] 理解各集合类的特点和适用场景
- [ ] 能够分析 HashMap 的工作原理
- [ ] 熟练使用 Stream API 处理数据
- [ ] 完成数据统计分析工具项目
