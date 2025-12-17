# Phase 3: 集合框架与 Stream API

> **目标**：掌握 Java 集合框架与 Stream API  
> **预计时长**：1 周  
> **前置条件**：Phase 2 完成  
> **状态**: ✅ 学习资料已创建

---

## 🎯 学习目标

完成本阶段后，你将能够：

1. 掌握 String 和 StringBuilder 的使用
2. 理解集合框架的体系结构
3. 熟练使用 List、Set、Map
4. 理解 HashMap 的实现原理
5. 掌握 Stream API 的使用
6. 理解泛型和 Optional

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
└── LinkedHashMap
```

### Stream 操作流程

```
数据源 → 中间操作 (filter/map/sorted) → 终端操作 (collect/forEach/reduce)
```

---

## 📁 文件列表

| #   | 文件                                         | 描述               | 知识点                     |
| --- | -------------------------------------------- | ------------------ | -------------------------- |
| 1   | [StringDemo.java](./StringDemo.java)         | 字符串操作         | String, StringBuilder      |
| 2   | [ArrayListDemo.java](./ArrayListDemo.java)   | ArrayList 操作     | List, 遍历, 排序           |
| 3   | [LinkedListDemo.java](./LinkedListDemo.java) | LinkedList 与队列  | Deque, 栈, 队列            |
| 4   | [HashSetDemo.java](./HashSetDemo.java)       | HashSet 与 TreeSet | Set, 去重, equals/hashCode |
| 5   | [HashMapDemo.java](./HashMapDemo.java)       | HashMap 使用       | Map, 遍历, Java 8 新方法   |
| 6   | [StreamBasics.java](./StreamBasics.java)     | Stream 基础        | 创建, 中间/终端操作        |
| 7   | [StreamAdvanced.java](./StreamAdvanced.java) | Stream 高级        | Collectors, groupingBy     |
| 8   | [GenericsDemo.java](./GenericsDemo.java)     | 泛型               | 泛型类/方法, 通配符        |
| 9   | [OptionalDemo.java](./OptionalDemo.java)     | Optional 使用      | 避免 NPE, 链式操作         |
| 10  | [DataAnalyzer.java](./DataAnalyzer.java)     | 🎯 **实战项目**    | 数据分析工具               |

---

## 🚀 运行方式

```bash
# 进入项目目录
cd /Users/perlou/Desktop/personal/java-course

# 编译项目
mvn compile

# 运行示例 (替换类名即可)
mvn exec:java -Dexec.mainClass="phase03.StringDemo"
mvn exec:java -Dexec.mainClass="phase03.StreamBasics"
mvn exec:java -Dexec.mainClass="phase03.DataAnalyzer"
```

---

## 📖 学习建议

### 学习顺序

1. **Day 1**: StringDemo
2. **Day 2**: ArrayListDemo → LinkedListDemo
3. **Day 3**: HashSetDemo → HashMapDemo
4. **Day 4**: StreamBasics → StreamAdvanced
5. **Day 5**: GenericsDemo → OptionalDemo
6. **Day 6-7**: DataAnalyzer 项目 + 练习

### 学习方法

1. 先运行代码，观察输出
2. 理解每个集合类的特点和适用场景
3. 多练习 Stream API，培养函数式思维
4. 完成每个文件末尾的练习题

---

## ✅ 完成检查

- [ ] 理解 String 不可变性和 StringBuilder 性能优势
- [ ] 掌握 ArrayList 和 LinkedList 的区别
- [ ] 理解 hashCode 和 equals 的关系
- [ ] 能够分析 HashMap 的工作原理
- [ ] 熟练使用 Stream API 处理数据
- [ ] 理解泛型的作用和 PECS 原则
- [ ] 正确使用 Optional 避免 NPE
- [ ] 完成数据分析工具项目

---

## 🎯 实战项目: 数据分析工具

`DataAnalyzer.java` 是本阶段的综合项目，功能包括：

- 订单数据展示和搜索
- 按类别/月份/价格区间统计
- 热销产品排行榜
- 客户消费分析
- 自定义查询功能

**运行方式:**

```bash
mvn exec:java -Dexec.mainClass="phase03.DataAnalyzer"
```

---

> 📝 完成本阶段后，请在 `LEARNING_PLAN.md` 中更新进度，然后进入 [Phase 4: 异常与日志](../phase04/README.md)
