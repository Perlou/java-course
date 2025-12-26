# Phase 3: 集合框架与 Stream API - 核心概念

> 集合是 Java 中存储和操作数据的核心工具

---

## 🎯 集合框架全景图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Java 集合框架                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│                        Collection                                   │
│                            │                                        │
│         ┌──────────────────┼──────────────────┐                    │
│         ↓                  ↓                  ↓                    │
│       List               Set               Queue                   │
│    (有序可重复)        (无序不重复)       (先进先出)                │
│         │                  │                  │                    │
│    ┌────┴────┐        ┌────┴────┐        ┌────┴────┐              │
│    ↓         ↓        ↓         ↓        ↓         ↓              │
│ ArrayList LinkedList HashSet TreeSet PriorityQueue Deque          │
│                                                                     │
│                           Map                                       │
│                      (键值对映射)                                   │
│                            │                                        │
│              ┌─────────────┼─────────────┐                         │
│              ↓             ↓             ↓                         │
│          HashMap      TreeMap     LinkedHashMap                    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📋 List - 有序集合

### ArrayList vs LinkedList

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ArrayList vs LinkedList                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ArrayList (动态数组)              LinkedList (双向链表)            │
│  ─────────────────────            ─────────────────────            │
│                                                                     │
│  ┌───┬───┬───┬───┬───┐          ┌───┐   ┌───┐   ┌───┐            │
│  │ 0 │ 1 │ 2 │ 3 │ 4 │          │ A │⟷│ B │⟷│ C │            │
│  └───┴───┴───┴───┴───┘          └───┘   └───┘   └───┘            │
│                                                                     │
│  随机访问: O(1) ✅                随机访问: O(n) ❌                │
│  头部插入: O(n) ❌                头部插入: O(1) ✅                │
│  尾部插入: O(1) ✅                尾部插入: O(1) ✅                │
│  内存连续: 是                     内存连续: 否                      │
│                                                                     │
│  适用场景: 频繁查询              适用场景: 频繁插入删除             │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 常用操作

```java
List<String> list = new ArrayList<>();
list.add("A");              // 添加元素
list.add(0, "B");          // 指定位置插入
list.get(0);               // 获取元素
list.set(0, "C");          // 修改元素
list.remove(0);            // 删除元素
list.size();               // 获取大小
list.contains("A");        // 判断包含
list.indexOf("A");         // 查找索引
```

---

## 🔗 Set - 不重复集合

### Set 类型对比

| 类型          | 特点         | 底层结构      | 顺序            |
| ------------- | ------------ | ------------- | --------------- |
| HashSet       | 最常用       | HashMap       | 无序            |
| LinkedHashSet | 保持插入顺序 | LinkedHashMap | 插入顺序        |
| TreeSet       | 自动排序     | 红黑树        | 自然顺序/比较器 |

### HashSet 原理

```
┌─────────────────────────────────────────────────────────────────────┐
│                       HashSet 去重原理                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  添加元素时:                                                        │
│  1. 计算 hashCode()                                                │
│  2. 如果哈希位置为空，直接存入                                      │
│  3. 如果哈希位置有元素，调用 equals() 判断                          │
│  4. equals() 返回 true，不存入（重复）                              │
│  5. equals() 返回 false，链表存储（哈希冲突）                       │
│                                                                     │
│  ⚠️ 自定义对象必须正确重写 hashCode() 和 equals()                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🗺️ Map - 键值对映射

### HashMap 结构

```
┌─────────────────────────────────────────────────────────────────────┐
│                    HashMap 结构 (JDK 8+)                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  数组 + 链表 + 红黑树                                               │
│                                                                     │
│  ┌───┬───┬───┬───┬───┬───┬───┬───┐                                │
│  │ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │  ← 数组 (桶)                   │
│  └───┴─┬─┴───┴─┬─┴───┴───┴───┴───┘                                │
│        ↓       ↓                                                    │
│      ┌───┐   ┌───┐                                                 │
│      │K,V│   │K,V│  ← 链表节点                                     │
│      └─┬─┘   └─┬─┘                                                 │
│        ↓       ↓                                                    │
│      ┌───┐   ┌───┐                                                 │
│      │K,V│   │K,V│                                                 │
│      └───┘   └─┬─┘                                                 │
│                ↓                                                    │
│           (红黑树)  ← 链表长度 > 8 时转为红黑树                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 常用操作

```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);                    // 添加键值对
map.get("A");                       // 获取值
map.getOrDefault("B", 0);           // 获取值或默认值
map.containsKey("A");               // 判断键存在
map.containsValue(1);               // 判断值存在
map.remove("A");                    // 删除键值对
map.keySet();                       // 获取所有键
map.values();                       // 获取所有值
map.entrySet();                     // 获取所有键值对
```

---

## 🌊 Stream API

### Stream 处理流程

```
┌─────────────────────────────────────────────────────────────────────┐
│                       Stream 流水线                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  数据源 → 中间操作 → 中间操作 → ... → 终端操作 → 结果               │
│                                                                     │
│  list.stream()                                                      │
│      .filter(x -> x > 0)      // 中间操作: 过滤                     │
│      .map(x -> x * 2)         // 中间操作: 转换                     │
│      .sorted()                // 中间操作: 排序                     │
│      .collect(Collectors.toList());  // 终端操作: 收集              │
│                                                                     │
│  特点:                                                              │
│  • 惰性求值: 中间操作不立即执行                                     │
│  • 一次性使用: 终端操作后流关闭                                     │
│  • 不修改源数据                                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 常用操作

```java
// 过滤
list.stream().filter(x -> x > 10)

// 转换
list.stream().map(x -> x.toUpperCase())

// 排序
list.stream().sorted()
list.stream().sorted(Comparator.reverseOrder())

// 去重
list.stream().distinct()

// 限制/跳过
list.stream().limit(5)
list.stream().skip(3)

// 收集
list.stream().collect(Collectors.toList())
list.stream().collect(Collectors.toSet())
list.stream().collect(Collectors.toMap(k -> k, v -> v))

// 聚合
list.stream().count()
list.stream().max(Comparator.naturalOrder())
list.stream().reduce(0, Integer::sum)

// 匹配
list.stream().anyMatch(x -> x > 10)
list.stream().allMatch(x -> x > 0)
list.stream().noneMatch(x -> x < 0)
```

---

## 🔤 String 处理

```java
// 创建
String s1 = "Hello";
String s2 = new String("Hello");

// 常用方法
s1.length();              // 长度
s1.charAt(0);             // 获取字符
s1.substring(1, 3);       // 子串
s1.indexOf("e");          // 查找
s1.split(",");            // 分割
s1.trim();                // 去除首尾空格
s1.replace("a", "b");     // 替换
s1.toUpperCase();         // 大写
s1.toLowerCase();         // 小写

// StringBuilder (可变字符串)
StringBuilder sb = new StringBuilder();
sb.append("Hello").append(" ").append("World");
String result = sb.toString();
```

---

## 📖 学习要点

```
✅ 掌握 List、Set、Map 的使用场景
✅ 理解 ArrayList vs LinkedList 的区别
✅ 理解 HashSet 去重原理 (hashCode + equals)
✅ 掌握 HashMap 的基本结构
✅ 熟练使用 Stream API 进行数据处理
✅ 了解 Optional 避免空指针
```

---

> 接下来学习 IO 与文件操作：[Phase 4 README](../phase04/README.md)
