package phase03;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Phase 3 - Lesson 3: LinkedList
 * 
 * 🎯 学习目标:
 * 1. 理解 LinkedList 的特点
 * 2. 掌握 LinkedList 作为 List 的使用
 * 3. 了解 LinkedList 作为 Deque 的使用
 * 4. 对比 ArrayList 和 LinkedList 的性能
 */
public class LinkedListDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 3 - Lesson 3: LinkedList ===\n");

        // ==================== 1. LinkedList 特点 ====================
        System.out.println("【1. LinkedList 特点】");
        System.out.println("LinkedList 是双向链表实现");
        System.out.println("  - 每个节点包含: prev, element, next");
        System.out.println("  - 实现了 List 和 Deque 接口");
        System.out.println("  - 适合频繁在头尾增删的场景");

        // ==================== 2. 基本 List 操作 ====================
        System.out.println("\n【2. 基本 List 操作】");

        LinkedList<String> list = new LinkedList<>();

        // 添加
        list.add("B");
        list.add("D");
        list.addFirst("A"); // 头部添加
        list.addLast("E"); // 尾部添加
        list.add(2, "C"); // 指定位置

        System.out.println("列表: " + list);
        System.out.println("getFirst(): " + list.getFirst());
        System.out.println("getLast(): " + list.getLast());
        System.out.println("get(2): " + list.get(2));

        // 删除
        list.removeFirst();
        list.removeLast();
        System.out.println("removeFirst/Last后: " + list);

        // ==================== 3. 作为 Deque (双端队列) ====================
        System.out.println("\n【3. 作为 Deque (双端队列)】");

        Deque<String> deque = new LinkedList<>();

        // 队列操作 (FIFO)
        deque.offer("First"); // 尾部添加
        deque.offer("Second");
        deque.offer("Third");
        System.out.println("队列: " + deque);
        System.out.println("peek(): " + deque.peek()); // 查看头部
        System.out.println("poll(): " + deque.poll()); // 移除头部
        System.out.println("poll后: " + deque);

        // 栈操作 (LIFO)
        System.out.println("\n栈操作:");
        Deque<String> stack = new LinkedList<>();
        stack.push("Bottom");
        stack.push("Middle");
        stack.push("Top");
        System.out.println("栈: " + stack);
        System.out.println("pop(): " + stack.pop());
        System.out.println("pop后: " + stack);

        // ==================== 4. Deque 方法对比 ====================
        System.out.println("\n【4. Deque 方法对比】");
        System.out.println("┌───────────────┬───────────────┬───────────────┐");
        System.out.println("│     操作      │  抛出异常     │  返回特殊值   │");
        System.out.println("├───────────────┼───────────────┼───────────────┤");
        System.out.println("│ 头部插入      │ addFirst(e)   │ offerFirst(e) │");
        System.out.println("│ 尾部插入      │ addLast(e)    │ offerLast(e)  │");
        System.out.println("│ 头部移除      │ removeFirst() │ pollFirst()   │");
        System.out.println("│ 尾部移除      │ removeLast()  │ pollLast()    │");
        System.out.println("│ 头部查看      │ getFirst()    │ peekFirst()   │");
        System.out.println("│ 尾部查看      │ getLast()     │ peekLast()    │");
        System.out.println("└───────────────┴───────────────┴───────────────┘");

        // ==================== 5. 队列和栈的应用 ====================
        System.out.println("\n【5. 队列和栈的应用】");

        // 模拟任务队列
        System.out.println("任务队列示例:");
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.offer("任务1: 读取文件");
        taskQueue.offer("任务2: 处理数据");
        taskQueue.offer("任务3: 保存结果");

        while (!taskQueue.isEmpty()) {
            String task = taskQueue.poll();
            System.out.println("  执行: " + task);
        }

        // 模拟浏览器历史
        System.out.println("\n浏览器历史栈:");
        Deque<String> history = new LinkedList<>();
        history.push("google.com");
        history.push("github.com");
        history.push("stackoverflow.com");

        System.out.println("  当前页面: " + history.peek());
        System.out.println("  后退...");
        history.pop();
        System.out.println("  当前页面: " + history.peek());

        // ==================== 6. ArrayList vs LinkedList ====================
        System.out.println("\n【6. ArrayList vs LinkedList】");
        System.out.println("┌──────────────────┬─────────────────┬─────────────────┐");
        System.out.println("│       操作       │    ArrayList    │   LinkedList    │");
        System.out.println("├──────────────────┼─────────────────┼─────────────────┤");
        System.out.println("│ get(index)       │     O(1)        │     O(n)        │");
        System.out.println("│ add(末尾)        │     O(1)*       │     O(1)        │");
        System.out.println("│ add(index)       │     O(n)        │     O(n)**      │");
        System.out.println("│ remove(index)    │     O(n)        │     O(n)**      │");
        System.out.println("│ addFirst/Last    │     O(n)/O(1)   │     O(1)        │");
        System.out.println("│ 内存占用         │     较小        │     较大        │");
        System.out.println("└──────────────────┴─────────────────┴─────────────────┘");
        System.out.println("* 均摊复杂度  ** 查找O(n) + 删除O(1)");

        // ==================== 7. 性能测试 ====================
        System.out.println("\n【7. 性能测试】");

        int size = 50000;

        // 头部插入测试
        List<Integer> arrayList = new ArrayList<>();
        LinkedList<Integer> linkedList = new LinkedList<>();

        long start = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            arrayList.addFirst(i);
        }
        long arrayTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            linkedList.addFirst(i);
        }
        long linkedTime = System.currentTimeMillis() - start;

        System.out.println("头部插入 " + size + " 次:");
        System.out.println("  ArrayList: " + arrayTime + "ms");
        System.out.println("  LinkedList: " + linkedTime + "ms");

        // 随机访问测试
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            arrayList.get(size / 2);
        }
        arrayTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            linkedList.get(size / 2);
        }
        linkedTime = System.currentTimeMillis() - start;

        System.out.println("\n随机访问 10000 次:");
        System.out.println("  ArrayList: " + arrayTime + "ms");
        System.out.println("  LinkedList: " + linkedTime + "ms");

        // ==================== 8. 使用建议 ====================
        System.out.println("\n【8. 使用建议】");
        System.out.println("使用 ArrayList 当:");
        System.out.println("  - 需要频繁随机访问");
        System.out.println("  - 主要在末尾增删");
        System.out.println("  - 内存敏感");
        System.out.println();
        System.out.println("使用 LinkedList 当:");
        System.out.println("  - 需要频繁在头部增删");
        System.out.println("  - 需要队列/双端队列/栈");
        System.out.println("  - 很少随机访问");

        System.out.println("\n✅ Phase 3 - Lesson 3 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. LinkedList: 双向链表，实现 List 和 Deque
 * 2. 作为队列: offer/poll/peek
 * 3. 作为栈: push/pop/peek
 * 4. 头尾操作 O(1)，中间操作 O(n)
 * 5. ArrayList: 随机访问快，LinkedList: 头尾操作快
 * 6. LinkedList 内存开销大 (每个节点额外存储两个指针)
 * 
 * 🏃 练习:
 * 1. 用 LinkedList 实现 LRU 缓存
 * 2. 用队列实现生产者-消费者模式
 * 3. 用栈实现括号匹配验证
 */
