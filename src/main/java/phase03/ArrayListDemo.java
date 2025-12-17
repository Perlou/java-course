package phase03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Phase 3 - Lesson 2: ArrayList
 * 
 * 🎯 学习目标:
 * 1. 理解集合框架的体系结构
 * 2. 掌握 ArrayList 的使用
 * 3. 了解 ArrayList 的底层原理
 * 4. 学会集合的常用操作
 */
public class ArrayListDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 2: ArrayList ===\n");

        // ==================== 1. 集合框架概述 ====================
        System.out.println("【1. 集合框架概述】");
        System.out.println("Java 集合框架体系:");
        System.out.println("  Collection (接口)");
        System.out.println("    ├── List: 有序，可重复");
        System.out.println("    │     ├── ArrayList");
        System.out.println("    │     └── LinkedList");
        System.out.println("    ├── Set: 无序，不可重复");
        System.out.println("    │     ├── HashSet");
        System.out.println("    │     └── TreeSet");
        System.out.println("    └── Queue: 队列");
        System.out.println("  Map (接口): 键值对");
        System.out.println("    ├── HashMap");
        System.out.println("    └── TreeMap");

        // ==================== 2. 创建 ArrayList ====================
        System.out.println("\n【2. 创建 ArrayList】");

        // 方式1: 普通创建
        List<String> list1 = new ArrayList<>();
        list1.add("Apple");
        list1.add("Banana");

        // 方式2: 指定初始容量
        List<String> list2 = new ArrayList<>(100);

        // 方式3: 从其他集合创建
        List<String> list3 = new ArrayList<>(list1);

        // 方式4: Arrays.asList (固定大小!)
        List<String> list4 = Arrays.asList("A", "B", "C");
        // list4.add("D"); // 会抛出 UnsupportedOperationException

        // 方式5: List.of (不可变!)
        List<String> list5 = List.of("X", "Y", "Z");
        // list5.add("W"); // 会抛出 UnsupportedOperationException

        // 方式6: 可变列表 (推荐)
        List<String> list6 = new ArrayList<>(List.of("1", "2", "3"));
        list6.add("4"); // OK

        System.out.println("list1: " + list1);
        System.out.println("list6: " + list6);

        // ==================== 3. 基本操作 ====================
        System.out.println("\n【3. 基本操作】");

        List<String> fruits = new ArrayList<>();

        // 添加元素
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");
        fruits.add(1, "Grape"); // 在索引1处插入
        System.out.println("添加后: " + fruits);

        // 获取元素
        System.out.println("get(0): " + fruits.get(0));
        System.out.println("size(): " + fruits.size());
        System.out.println("isEmpty(): " + fruits.isEmpty());

        // 修改元素
        fruits.set(0, "Mango");
        System.out.println("set后: " + fruits);

        // 查找
        System.out.println("contains(\"Banana\"): " + fruits.contains("Banana"));
        System.out.println("indexOf(\"Orange\"): " + fruits.indexOf("Orange"));

        // 删除
        fruits.remove("Banana");
        System.out.println("remove后: " + fruits);
        fruits.remove(0); // 按索引删除
        System.out.println("remove(0)后: " + fruits);

        // ==================== 4. 遍历方式 ====================
        System.out.println("\n【4. 遍历方式】");

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        // 方式1: for 循环
        System.out.print("for 循环: ");
        for (int i = 0; i < numbers.size(); i++) {
            System.out.print(numbers.get(i) + " ");
        }
        System.out.println();

        // 方式2: for-each
        System.out.print("for-each: ");
        for (Integer n : numbers) {
            System.out.print(n + " ");
        }
        System.out.println();

        // 方式3: Iterator
        System.out.print("Iterator: ");
        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        // 方式4: forEach + Lambda
        System.out.print("forEach: ");
        numbers.forEach(n -> System.out.print(n + " "));
        System.out.println();

        // 方式5: Stream
        System.out.print("Stream: ");
        numbers.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        // ==================== 5. 批量操作 ====================
        System.out.println("\n【5. 批量操作】");

        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D", "E"));
        System.out.println("原始列表: " + list);

        // addAll
        list.addAll(List.of("F", "G"));
        System.out.println("addAll后: " + list);

        // removeAll
        list.removeAll(List.of("A", "C", "E"));
        System.out.println("removeAll后: " + list);

        // retainAll (保留交集)
        List<String> list7 = new ArrayList<>(List.of("B", "D", "F", "X"));
        list7.retainAll(list);
        System.out.println("retainAll后: " + list7);

        // clear
        List<String> temp = new ArrayList<>(list);
        temp.clear();
        System.out.println("clear后: " + temp);

        // ==================== 6. 排序 ====================
        System.out.println("\n【6. 排序】");

        List<Integer> nums = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3));
        System.out.println("排序前: " + nums);

        // 自然排序
        Collections.sort(nums);
        System.out.println("sort后 (升序): " + nums);

        // 逆序
        Collections.sort(nums, Collections.reverseOrder());
        System.out.println("reverseOrder后: " + nums);

        // 使用 Comparator
        List<String> words = new ArrayList<>(List.of("apple", "Banana", "Cherry"));
        words.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("忽略大小写排序: " + words);

        // Lambda 自定义排序
        words.sort((s1, s2) -> s2.length() - s1.length()); // 按长度降序
        System.out.println("按长度降序: " + words);

        // ==================== 7. 转换操作 ====================
        System.out.println("\n【7. 转换操作】");

        List<String> items = new ArrayList<>(List.of("A", "B", "C"));

        // toArray
        String[] arr1 = items.toArray(new String[0]);
        System.out.println("toArray: " + Arrays.toString(arr1));

        // subList (视图，非拷贝!)
        List<String> sub = items.subList(0, 2);
        System.out.println("subList(0,2): " + sub);

        // ==================== 8. 底层原理 ====================
        System.out.println("\n【8. ArrayList 底层原理】");
        System.out.println("底层: Object[] 数组");
        System.out.println("默认容量: 10");
        System.out.println("扩容: 1.5 倍 (oldCapacity + oldCapacity >> 1)");
        System.out.println();
        System.out.println("时间复杂度:");
        System.out.println("  get(i)    : O(1) - 随机访问");
        System.out.println("  add(e)    : O(1) 均摊 - 末尾添加");
        System.out.println("  add(i,e)  : O(n) - 需要移动元素");
        System.out.println("  remove(i) : O(n) - 需要移动元素");
        System.out.println("  contains  : O(n) - 线性查找");

        // ==================== 9. 常见陷阱 ====================
        System.out.println("\n【9. 常见陷阱】");

        // 陷阱1: ConcurrentModificationException
        System.out.println("陷阱1: 遍历时修改");
        List<Integer> numList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        // 错误方式:
        // for (Integer n : numList) {
        // if (n % 2 == 0) numList.remove(n); // ConcurrentModificationException
        // }

        // 正确方式1: Iterator.remove()
        Iterator<Integer> iter = numList.iterator();
        while (iter.hasNext()) {
            if (iter.next() % 2 == 0) {
                iter.remove();
            }
        }
        System.out.println("Iterator.remove()后: " + numList);

        // 正确方式2: removeIf (Java 8+)
        numList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        numList.removeIf(n -> n % 2 == 0);
        System.out.println("removeIf后: " + numList);

        // 陷阱2: Integer 与 int 的 remove
        System.out.println("\n陷阱2: remove(int) vs remove(Object)");
        List<Integer> intList = new ArrayList<>(List.of(10, 20, 30));
        intList.remove(1); // 删除索引1的元素
        System.out.println("remove(1) - 按索引: " + intList);

        intList = new ArrayList<>(List.of(10, 20, 30));
        intList.remove(Integer.valueOf(20)); // 删除值为20的元素
        System.out.println("remove(Integer.valueOf(20)) - 按值: " + intList);

        System.out.println("\n✅ Phase 3 - Lesson 2 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. ArrayList: 基于数组，随机访问O(1)，增删O(n)
 * 2. 创建方式: new, Arrays.asList, List.of
 * 3. 常用方法: add, get, set, remove, contains, size
 * 4. 遍历: for, for-each, Iterator, forEach, Stream
 * 5. 排序: Collections.sort, list.sort, Comparator
 * 6. 底层: 数组，默认10，1.5倍扩容
 * 7. 注意: 遍历时不能直接修改，用 Iterator 或 removeIf
 * 
 * 🏃 练习:
 * 1. 实现一个去重方法
 * 2. 找出两个列表的交集和并集
 * 3. 按多个条件排序
 */
