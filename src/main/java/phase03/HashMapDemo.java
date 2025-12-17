package phase03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Phase 3 - Lesson 5: HashMap
 * 
 * 🎯 学习目标:
 * 1. 理解 Map 的概念 (键值对)
 * 2. 掌握 HashMap 的使用
 * 3. 了解 HashMap 的底层原理
 * 4. 学会选择合适的 Map 实现
 */
public class HashMapDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 5: HashMap ===\n");

        // ==================== 1. Map 基本概念 ====================
        System.out.println("【1. Map 基本概念】");
        System.out.println("Map: 存储键值对 (Key-Value) 的集合");
        System.out.println("  - 键不能重复");
        System.out.println("  - 值可以重复");
        System.out.println("  - 一个键最多映射到一个值");

        // ==================== 2. 创建和基本操作 ====================
        System.out.println("\n【2. 创建和基本操作】");

        Map<String, Integer> scores = new HashMap<>();

        // put: 添加/更新
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        scores.put("Alice", 98); // 更新 Alice 的值

        System.out.println("Map: " + scores);
        System.out.println("size(): " + scores.size());

        // get: 获取
        System.out.println("get(\"Bob\"): " + scores.get("Bob"));
        System.out.println("get(\"David\"): " + scores.get("David")); // null
        System.out.println("getOrDefault: " + scores.getOrDefault("David", 0));

        // 检查
        System.out.println("containsKey(\"Bob\"): " + scores.containsKey("Bob"));
        System.out.println("containsValue(92): " + scores.containsValue(92));

        // 删除
        scores.remove("Charlie");
        System.out.println("remove后: " + scores);

        // ==================== 3. 遍历方式 ====================
        System.out.println("\n【3. 遍历方式】");

        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        // 方式1: keySet
        System.out.print("keySet: ");
        for (String key : map.keySet()) {
            System.out.print(key + "=" + map.get(key) + " ");
        }
        System.out.println();

        // 方式2: values
        System.out.print("values: ");
        for (Integer value : map.values()) {
            System.out.print(value + " ");
        }
        System.out.println();

        // 方式3: entrySet (推荐)
        System.out.print("entrySet: ");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        // 方式4: forEach (Java 8+)
        System.out.print("forEach: ");
        map.forEach((k, v) -> System.out.print(k + "=" + v + " "));
        System.out.println();

        // ==================== 4. Java 8+ 新方法 ====================
        System.out.println("\n【4. Java 8+ 新方法】");

        Map<String, Integer> wordCount = new HashMap<>();

        // putIfAbsent: 不存在时才添加
        wordCount.putIfAbsent("hello", 0);
        wordCount.putIfAbsent("hello", 100); // 不会覆盖
        System.out.println("putIfAbsent后: " + wordCount);

        // computeIfAbsent: 不存在时计算并添加
        wordCount.computeIfAbsent("world", k -> k.length());
        System.out.println("computeIfAbsent后: " + wordCount);

        // compute: 计算新值
        wordCount.compute("hello", (k, v) -> (v == null) ? 1 : v + 1);
        System.out.println("compute后: " + wordCount);

        // merge: 合并值
        wordCount.merge("hello", 1, Integer::sum);
        wordCount.merge("hello", 1, Integer::sum);
        System.out.println("merge后: " + wordCount);

        // ==================== 5. 单词计数示例 ====================
        System.out.println("\n【5. 单词计数示例】");

        String text = "hello world hello java world hello";
        Map<String, Integer> counter = new HashMap<>();

        for (String word : text.split(" ")) {
            counter.merge(word, 1, Integer::sum);
        }
        System.out.println("单词统计: " + counter);

        // ==================== 6. HashMap 底层原理 ====================
        System.out.println("\n【6. HashMap 底层原理】");
        System.out.println("结构: 数组 + 链表 + 红黑树 (Java 8+)");
        System.out.println();
        System.out.println("关键参数:");
        System.out.println("  - 默认容量: 16");
        System.out.println("  - 负载因子: 0.75");
        System.out.println("  - 树化阈值: 链表长度 >= 8 且容量 >= 64");
        System.out.println();
        System.out.println("工作流程:");
        System.out.println("  1. 计算 key 的 hashCode");
        System.out.println("  2. 通过扰动函数得到 hash 值");
        System.out.println("  3. index = (n-1) & hash 确定桶位置");
        System.out.println("  4. 处理冲突: 链表 → 红黑树");

        // ==================== 7. TreeMap (有序) ====================
        System.out.println("\n【7. TreeMap (有序)】");

        TreeMap<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Charlie", 3);
        treeMap.put("Alice", 1);
        treeMap.put("Bob", 2);

        System.out.println("TreeMap (按键排序): " + treeMap);
        System.out.println("firstKey(): " + treeMap.firstKey());
        System.out.println("lastKey(): " + treeMap.lastKey());
        System.out.println("subMap: " + treeMap.subMap("Alice", "Charlie"));

        // ==================== 8. LinkedHashMap (保持顺序) ====================
        System.out.println("\n【8. LinkedHashMap (保持顺序)】");

        // 保持插入顺序
        Map<String, Integer> linkedMap = new LinkedHashMap<>();
        linkedMap.put("C", 3);
        linkedMap.put("A", 1);
        linkedMap.put("B", 2);
        System.out.println("插入顺序: " + linkedMap);

        // 访问顺序 (LRU)
        Map<String, Integer> lruMap = new LinkedHashMap<>(16, 0.75f, true);
        lruMap.put("A", 1);
        lruMap.put("B", 2);
        lruMap.put("C", 3);
        lruMap.get("A"); // 访问 A
        System.out.println("访问顺序: " + lruMap); // A 移到末尾

        // ==================== 9. Map 实现对比 ====================
        System.out.println("\n【9. Map 实现类对比】");
        System.out.println("┌──────────────────┬───────────────┬───────────────┬───────────────┐");
        System.out.println("│     实现类       │   有序性      │  时间复杂度   │   线程安全    │");
        System.out.println("├──────────────────┼───────────────┼───────────────┼───────────────┤");
        System.out.println("│ HashMap          │   无序        │    O(1)       │     否        │");
        System.out.println("│ LinkedHashMap    │ 插入/访问顺序 │    O(1)       │     否        │");
        System.out.println("│ TreeMap          │   按键排序    │   O(log n)    │     否        │");
        System.out.println("│ ConcurrentHashMap│   无序        │    O(1)       │     是        │");
        System.out.println("│ Hashtable        │   无序        │    O(1)       │   是(过时)    │");
        System.out.println("└──────────────────┴───────────────┴───────────────┴───────────────┘");

        // ==================== 10. 常见用法 ====================
        System.out.println("\n【10. 常见用法】");

        // 分组
        List<Student> students = List.of(
                new Student("张三", "A班"),
                new Student("李四", "B班"),
                new Student("王五", "A班"),
                new Student("赵六", "B班"));

        Map<String, List<Student>> byClass = new HashMap<>();
        for (Student s : students) {
            byClass.computeIfAbsent(s.className(), k -> new ArrayList<>()).add(s);
        }
        System.out.println("按班级分组: " + byClass);

        System.out.println("\n✅ Phase 3 - Lesson 5 完成！");
    }
}

record Student(String name, String className) {
}

/*
 * 📚 知识点总结:
 * 
 * 1. Map: 键值对集合，键不能重复
 * 2. HashMap: 无序，O(1)，最常用
 * 3. LinkedHashMap: 保持插入/访问顺序
 * 4. TreeMap: 按键排序，O(log n)
 * 5. Java 8+: computeIfAbsent, merge, forEach
 * 6. 底层: 数组 + 链表 + 红黑树
 * 7. equals/hashCode 对于键非常重要
 * 
 * 🏃 练习:
 * 1. 实现一个简单的 LRU 缓存
 * 2. 统计文本中每个字符出现的次数
 * 3. 用 Map 实现简单的内存数据库
 */
