package phase03;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Phase 3 - Lesson 4: HashSet 与 TreeSet
 * 
 * 🎯 学习目标:
 * 1. 理解 Set 的特点 (无序、不重复)
 * 2. 掌握 HashSet 的使用
 * 3. 了解 TreeSet 的排序功能
 * 4. 理解 hashCode 和 equals 的关系
 */
public class HashSetDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 4: HashSet 与 TreeSet ===\n");

        // ==================== 1. Set 特点 ====================
        System.out.println("【1. Set 特点】");
        System.out.println("Set 是不包含重复元素的集合");
        System.out.println("  - 无序 (HashSet)");
        System.out.println("  - 有序 (TreeSet/LinkedHashSet)");
        System.out.println("  - 不允许重复元素");

        // ==================== 2. HashSet 基本使用 ====================
        System.out.println("\n【2. HashSet 基本使用】");

        Set<String> set = new HashSet<>();

        // 添加元素
        set.add("Apple");
        set.add("Banana");
        set.add("Orange");
        set.add("Apple"); // 重复，不会添加

        System.out.println("HashSet: " + set);
        System.out.println("size(): " + set.size()); // 3, 不是4

        // 检查和删除
        System.out.println("contains(\"Banana\"): " + set.contains("Banana"));
        set.remove("Banana");
        System.out.println("remove后: " + set);

        // ==================== 3. 去重应用 ====================
        System.out.println("\n【3. 去重应用】");

        List<Integer> listWithDups = Arrays.asList(1, 2, 3, 2, 1, 4, 5, 4, 3);
        System.out.println("原始列表: " + listWithDups);

        Set<Integer> unique = new HashSet<>(listWithDups);
        System.out.println("去重后: " + unique);

        // 保持顺序去重
        Set<Integer> orderedUnique = new LinkedHashSet<>(listWithDups);
        System.out.println("保持顺序去重: " + orderedUnique);

        // ==================== 4. TreeSet (有序) ====================
        System.out.println("\n【4. TreeSet (有序)】");

        TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.add(5);
        treeSet.add(2);
        treeSet.add(8);
        treeSet.add(1);
        treeSet.add(9);

        System.out.println("TreeSet (自动排序): " + treeSet);
        System.out.println("first(): " + treeSet.first());
        System.out.println("last(): " + treeSet.last());
        System.out.println("higher(5): " + treeSet.higher(5)); // >5 的最小值
        System.out.println("lower(5): " + treeSet.lower(5)); // <5 的最大值
        System.out.println("floor(6): " + treeSet.floor(6)); // <=6 的最大值
        System.out.println("ceiling(6): " + treeSet.ceiling(6)); // >=6 的最小值

        // 范围操作
        System.out.println("subSet(2,8): " + treeSet.subSet(2, 8));
        System.out.println("headSet(5): " + treeSet.headSet(5));
        System.out.println("tailSet(5): " + treeSet.tailSet(5));

        // ==================== 5. 自定义对象放入 Set ====================
        System.out.println("\n【5. 自定义对象放入 Set】");

        Set<Person> personSet = new HashSet<>();
        personSet.add(new Person("张三", 25));
        personSet.add(new Person("李四", 30));
        personSet.add(new Person("张三", 25)); // 重复！取决于 equals/hashCode

        System.out.println("Person Set: ");
        for (Person p : personSet) {
            System.out.println("  " + p);
        }
        System.out.println("size: " + personSet.size());

        // ==================== 6. hashCode 和 equals ====================
        System.out.println("\n【6. hashCode 和 equals】");
        System.out.println("规则:");
        System.out.println("  1. 两个对象 equals 相等，hashCode 必须相同");
        System.out.println("  2. hashCode 相同，不一定 equals 相等");
        System.out.println("  3. 重写 equals 必须重写 hashCode");
        System.out.println();

        Person p1 = new Person("张三", 25);
        Person p2 = new Person("张三", 25);

        System.out.println("p1.equals(p2): " + p1.equals(p2));
        System.out.println("p1.hashCode(): " + p1.hashCode());
        System.out.println("p2.hashCode(): " + p2.hashCode());

        // ==================== 7. LinkedHashSet ====================
        System.out.println("\n【7. LinkedHashSet (保持插入顺序)】");

        Set<String> linkedSet = new LinkedHashSet<>();
        linkedSet.add("C");
        linkedSet.add("A");
        linkedSet.add("B");

        System.out.println("LinkedHashSet: " + linkedSet); // 保持插入顺序: C, A, B

        // ==================== 8. Set 操作 ====================
        System.out.println("\n【8. Set 操作 (交集、并集、差集)】");

        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));

        System.out.println("set1: " + set1);
        System.out.println("set2: " + set2);

        // 并集
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        System.out.println("并集: " + union);

        // 交集
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("交集: " + intersection);

        // 差集
        Set<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("差集 (set1 - set2): " + difference);

        // ==================== 9. 性能对比 ====================
        System.out.println("\n【9. Set 实现类对比】");
        System.out.println("┌──────────────────┬───────────────┬───────────────┬───────────────┐");
        System.out.println("│     实现类       │   有序性      │  时间复杂度   │   底层结构    │");
        System.out.println("├──────────────────┼───────────────┼───────────────┼───────────────┤");
        System.out.println("│ HashSet          │   无序        │    O(1)       │   HashMap     │");
        System.out.println("│ LinkedHashSet    │   插入顺序    │    O(1)       │ HashMap+链表  │");
        System.out.println("│ TreeSet          │   自然排序    │   O(log n)    │   红黑树      │");
        System.out.println("└──────────────────┴───────────────┴───────────────┴───────────────┘");

        System.out.println("\n✅ Phase 3 - Lesson 4 完成！");
    }
}

/**
 * Person 类 - 正确实现 equals 和 hashCode
 */
class Person implements Comparable<Person> {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        return age == other.age && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public int compareTo(Person other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0)
            return nameCompare;
        return Integer.compare(this.age, other.age);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. Set: 不允许重复元素的集合
 * 2. HashSet: 无序，O(1)，基于 HashMap
 * 3. LinkedHashSet: 保持插入顺序，O(1)
 * 4. TreeSet: 自然排序或自定义排序，O(log n)
 * 5. 自定义对象必须正确实现 equals 和 hashCode
 * 6. 集合操作: 并集(addAll), 交集(retainAll), 差集(removeAll)
 * 
 * 🏃 练习:
 * 1. 使用 Set 统计文章中有多少个不同的单词
 * 2. 实现一个利用 LinkedHashSet 的 LRU 缓存
 * 3. 使用 TreeSet 实现排行榜功能
 */
