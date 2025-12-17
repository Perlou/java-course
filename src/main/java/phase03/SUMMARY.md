# Phase 3 学习总结: 集合框架与 Stream API ✅

> **完成日期**: 2025 年 12 月 16 日  
> **学习时长**: 约 1 周  
> **状态**: 已完成 🎉

---

## 📚 知识点掌握情况

### 1. 字符串处理 ✅

- [x] 理解 String 不可变性和字符串池
- [x] 掌握 StringBuilder 的高效拼接
- [x] 熟练使用常用字符串方法

| 类              | 特点             | 适用场景         |
| --------------- | ---------------- | ---------------- |
| `String`        | 不可变、线程安全 | 少量字符串操作   |
| `StringBuilder` | 可变、线程不安全 | 单线程大量拼接   |
| `StringBuffer`  | 可变、线程安全   | 多线程字符串操作 |

```java
// 字符串常用方法
str.substring(0, 5);
str.split(",");
str.replace("old", "new");
String.format("%s: %d", name, age);
String.join(", ", list);
```

### 2. ArrayList ✅

- [x] 理解动态数组底层实现
- [x] 掌握增删改查操作
- [x] 理解扩容机制 (默认 1.5 倍)
- [x] 熟练使用排序和遍历

```java
List<String> list = new ArrayList<>();
list.add("item");
list.get(0);
list.remove("item");
list.sort(Comparator.naturalOrder());
```

### 3. LinkedList ✅

- [x] 理解双向链表结构
- [x] 掌握 Deque 接口用法
- [x] 实现栈和队列操作

```java
LinkedList<Integer> list = new LinkedList<>();
list.addFirst(1);   // 栈: push
list.removeFirst(); // 栈: pop
list.addLast(2);    // 队列: offer
list.removeFirst(); // 队列: poll
```

**ArrayList vs LinkedList**:
| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| 随机访问 | O(1) | O(n) |
| 头部插入 | O(n) | O(1) |
| 尾部插入 | O(1) 均摊 | O(1) |

### 4. HashSet 与 TreeSet ✅

- [x] 理解 Set 不重复特性
- [x] 掌握 hashCode 和 equals 契约
- [x] 了解 TreeSet 有序性

| 实现类          | 底层结构  | 有序性   | 时间复杂度 |
| --------------- | --------- | -------- | ---------- |
| `HashSet`       | HashMap   | 无序     | O(1)       |
| `LinkedHashSet` | 链表+哈希 | 插入顺序 | O(1)       |
| `TreeSet`       | 红黑树    | 自然排序 | O(log n)   |

```java
// equals 和 hashCode 契约
if (a.equals(b)) {
    a.hashCode() == b.hashCode(); // 必须成立
}
```

### 5. HashMap ✅

- [x] 理解键值对存储
- [x] 掌握 HashMap 底层原理 (数组 + 链表 + 红黑树)
- [x] 熟练使用 Java 8 新方法
- [x] 了解 TreeMap 和 LinkedHashMap

```java
Map<String, Integer> map = new HashMap<>();
map.put("key", 1);
map.get("key");
map.getOrDefault("key", 0);
map.computeIfAbsent("key", k -> 0);
map.merge("key", 1, Integer::sum);
map.forEach((k, v) -> System.out.println(k + ": " + v));
```

**HashMap 原理**:

```
put 流程:
1. 计算 hash(key)
2. hash & (n-1) 确定桶位置
3. 链表长度 ≥ 8 且数组 ≥ 64 → 转红黑树
4. 负载因子 > 0.75 → 扩容
```

### 6. Stream API 基础 ✅

- [x] 理解 Stream 的概念
- [x] 掌握 Stream 创建方式
- [x] 熟练使用中间操作和终端操作
- [x] 理解惰性求值

**创建方式**:

```java
list.stream();           // 从集合
Arrays.stream(arr);      // 从数组
Stream.of(1, 2, 3);      // 直接创建
IntStream.range(0, 10);  // 数值范围
```

**中间操作 (惰性)**:
| 操作 | 描述 | 示例 |
|---|---|---|
| `filter` | 过滤 | `.filter(x -> x > 0)` |
| `map` | 转换 | `.map(String::toUpperCase)` |
| `sorted` | 排序 | `.sorted()` |
| `distinct` | 去重 | `.distinct()` |
| `limit/skip` | 截取 | `.limit(10)` |

**终端操作 (触发执行)**:
| 操作 | 描述 | 返回类型 |
|---|---|---|
| `collect` | 收集结果 | `List/Set/Map` |
| `forEach` | 遍历 | `void` |
| `reduce` | 归约 | `Optional<T>` |
| `count` | 计数 | `long` |

### 7. Stream API 高级 ✅

- [x] 掌握 Collectors 工具类
- [x] 学会分组 (groupingBy) 和分区 (partitioningBy)
- [x] 了解并行流
- [x] 掌握 flatMap 扁平化操作

```java
// Collectors 常用方法
Collectors.toList();
Collectors.toSet();
Collectors.joining(", ");
Collectors.counting();
Collectors.summingDouble(Order::getAmount);
Collectors.groupingBy(Order::getCategory);
Collectors.partitioningBy(o -> o.getPrice() > 100);

// flatMap 扁平化
sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .collect(toList());
```

### 8. 泛型 ✅

- [x] 理解泛型的概念和作用
- [x] 掌握泛型类、接口、方法
- [x] 了解类型擦除
- [x] 掌握通配符和 PECS 原则

```java
// 泛型类
class Box<T> { private T content; }

// 泛型方法
public <T> List<T> arrayToList(T[] arr) { ... }

// 有界类型
public <T extends Number> double sum(T... numbers) { ... }

// 通配符 (PECS: Producer Extends, Consumer Super)
List<?> any;                    // 无界
List<? extends Number> producer; // 上界 - 读取
List<? super Integer> consumer;  // 下界 - 写入
```

### 9. Optional ✅

- [x] 理解 Optional 的作用
- [x] 掌握 Optional 的创建和使用
- [x] 学会正确使用 Optional 避免 NPE
- [x] 掌握 Optional 的链式操作

```java
// 创建
Optional.of(value);           // 非空
Optional.ofNullable(value);   // 可能为空
Optional.empty();             // 空

// 使用
optional.orElse(defaultValue);
optional.orElseGet(() -> compute());
optional.orElseThrow(() -> new Exception());

// 链式操作
optional.filter(u -> u.getAge() > 18)
        .map(User::getName)
        .orElse("Unknown");
```

**最佳实践**:

- ✅ 作为返回值使用
- ❌ 不要作为字段类型
- ❌ 不要作为方法参数

---

## 🎯 实战项目: 数据分析工具

成功完成了 `DataAnalyzer.java` 项目，实现了交互式数据分析工具:

| 功能         | 使用技术                    |
| ------------ | --------------------------- |
| 显示所有订单 | List 遍历、Stream forEach   |
| 按类别统计   | groupingBy、counting        |
| 按月份统计   | groupingBy、summingDouble   |
| 热销产品排行 | sorted、limit               |
| 客户消费分析 | groupingBy、averagingDouble |
| 搜索订单     | filter、collect             |
| 价格区间分析 | 自定义分组函数              |
| 自定义查询   | 多条件 filter               |

---

## 📊 学习文件清单

| #   | 文件                  | 状态 | 核心知识点                 |
| --- | --------------------- | ---- | -------------------------- |
| 1   | `StringDemo.java`     | ✅   | String, StringBuilder      |
| 2   | `ArrayListDemo.java`  | ✅   | List, 遍历, 排序           |
| 3   | `LinkedListDemo.java` | ✅   | Deque, 栈, 队列            |
| 4   | `HashSetDemo.java`    | ✅   | Set, 去重, equals/hashCode |
| 5   | `HashMapDemo.java`    | ✅   | Map, 遍历, Java 8 新方法   |
| 6   | `StreamBasics.java`   | ✅   | 创建, 中间/终端操作        |
| 7   | `StreamAdvanced.java` | ✅   | Collectors, groupingBy     |
| 8   | `GenericsDemo.java`   | ✅   | 泛型类/方法, 通配符, PECS  |
| 9   | `OptionalDemo.java`   | ✅   | 避免 NPE, 链式操作         |
| 10  | `DataAnalyzer.java`   | ✅   | **综合项目**               |

---

## 📁 练习完成情况

| 文件                     | 练习内容        | 状态 |
| ------------------------ | --------------- | ---- |
| `ex/ArrayListDemo.java`  | 动态数组操作    | ✅   |
| `ex/LinkedListDemo.java` | 队列实现        | ✅   |
| `ex/HashSetDemo.java`    | equals/hashCode | ✅   |
| `ex/LRUCache.java`       | LRU 缓存实现    | ✅   |
| `ex/Table.java`          | 简易内存数据库  | ✅   |

---

## 💡 重点心得

### 集合选择指南

```
需要键值对?
├── 是 → 需要排序? → TreeMap
│       └── 否 → 插入顺序? → LinkedHashMap
│               └── 否 → HashMap (最常用)
└── 否 → 允许重复?
        ├── 是 → 随机访问多? → ArrayList
        │       └── 否 → LinkedList
        └── 否 → 需要排序? → TreeSet
                └── 否 → HashSet
```

### Stream 最佳实践

1. **避免副作用**: 不要在 Stream 中修改外部状态
2. **方法引用优先**: `String::toUpperCase` 比 lambda 更简洁
3. **合理使用并行流**: 数据量大且操作独立时才考虑
4. **结合 Optional**: 处理空值更安全

### 关键记忆点

- HashMap 负载因子 0.75，链表长度 ≥ 8 转红黑树
- PECS: Producer Extends, Consumer Super
- Optional 适合返回值，不适合字段/参数
- Stream 只能消费一次

---

## 🚀 下一步计划

Phase 3 已完成！准备进入 **[Phase 4: 异常与日志](../phase04/README.md)**

Phase 4 将学习:

- 异常处理机制 (try-catch-finally)
- 自定义异常
- 日志框架 (SLF4J, Logback)
- 断言与调试

---

> 📝 _本总结由学习过程自动生成于 2025-12-16_
