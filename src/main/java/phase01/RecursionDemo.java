package phase01;

/**
 * Phase 1 - Lesson 11: 递归算法
 * 
 * 🎯 学习目标:
 * 1. 理解递归的概念
 * 2. 掌握递归的基本结构
 * 3. 实现常见的递归算法
 */
public class RecursionDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 11: 递归算法 ===\n");

        // ==================== 1. 递归基础 ====================
        System.out.println("【1. 递归基础】");
        System.out.println("递归的两个要素:");
        System.out.println("  1. 基准条件 (Base Case): 递归终止的条件");
        System.out.println("  2. 递归步骤 (Recursive Case): 将问题分解为更小的子问题");

        // ==================== 2. 阶乘 ====================
        System.out.println("\n【2. 阶乘 n!】");
        System.out.println("公式: n! = n × (n-1) × ... × 2 × 1");
        System.out.println("递归定义: n! = n × (n-1)!, 其中 0! = 1");

        for (int i = 0; i <= 10; i++) {
            System.out.printf("  %2d! = %d%n", i, factorial(i));
        }

        // ==================== 3. 斐波那契数列 ====================
        System.out.println("\n【3. 斐波那契数列】");
        System.out.println("定义: F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2)");

        System.out.print("前 15 项: ");
        for (int i = 0; i < 15; i++) {
            System.out.print(fibonacciMemo(i) + " ");
        }
        System.out.println();

        // ==================== 4. 汉诺塔 ====================
        System.out.println("\n【4. 汉诺塔】");
        System.out.println("规则: 将所有盘子从 A 移到 C，每次只能移一个，大盘不能放小盘上");

        int disks = 3;
        System.out.println("移动 " + disks + " 个盘子的步骤:");
        hanoi(disks, 'A', 'C', 'B');

        // ==================== 5. 二分查找 (递归版) ====================
        System.out.println("\n【5. 二分查找 (递归版)】");
        int[] sortedArr = { 1, 3, 5, 7, 9, 11, 13, 15 };
        int target = 7;
        int index = binarySearchRecursive(sortedArr, target, 0, sortedArr.length - 1);
        System.out.println("在数组中查找 " + target + ": 索引 = " + index);

        // ==================== 6. 求和 ====================
        System.out.println("\n【6. 递归求和】");
        int n = 100;
        System.out.println("1 + 2 + ... + " + n + " = " + sumRecursive(n));

        // 数组求和
        int[] arr = { 1, 2, 3, 4, 5 };
        System.out.println("数组 [1,2,3,4,5] 之和 = " + arraySum(arr, 0));

        // ==================== 7. 字符串处理 ====================
        System.out.println("\n【7. 字符串递归处理】");

        String str = "Hello";
        System.out.println("反转 \"" + str + "\" = \"" + reverse(str) + "\"");

        String palindrome = "racecar";
        System.out.println("\"" + palindrome + "\" 是回文? " + isPalindrome(palindrome));

        // ==================== 8. 递归 vs 迭代 ====================
        System.out.println("\n【8. 递归 vs 迭代】");
        System.out.println("┌──────────────┬──────────────┬──────────────┐");
        System.out.println("│     特点     │     递归     │     迭代     │");
        System.out.println("├──────────────┼──────────────┼──────────────┤");
        System.out.println("│ 代码可读性   │ 通常更简洁   │ 可能更长     │");
        System.out.println("│ 性能         │ 较低(栈开销) │ 较高         │");
        System.out.println("│ 内存使用     │ 可能栈溢出   │ 固定         │");
        System.out.println("│ 适用场景     │ 树/图遍历    │ 简单循环     │");
        System.out.println("└──────────────┴──────────────┴──────────────┘");

        // ==================== 9. 尾递归优化 ====================
        System.out.println("\n【9. 尾递归优化】");
        System.out.println("尾递归: 递归调用是方法的最后一个操作");
        System.out.println("尾递归阶乘: factorialTail(5, 1) = " + factorialTail(5, 1));
        System.out.println("注意: Java 编译器不保证尾递归优化，建议复杂场景用迭代");

        System.out.println("\n✅ Phase 1 - Lesson 11 完成！");
    }

    // ========== 阶乘 ==========
    static long factorial(int n) {
        if (n <= 1)
            return 1; // 基准条件
        return n * factorial(n - 1); // 递归步骤
    }

    // 尾递归版本
    static long factorialTail(int n, long accumulator) {
        if (n <= 1)
            return accumulator;
        return factorialTail(n - 1, n * accumulator);
    }

    // ========== 斐波那契 ==========

    // 简单递归 (效率低，有大量重复计算)
    static int fibonacci(int n) {
        if (n <= 1)
            return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // 带记忆化的递归 (优化版)
    static long[] memo = new long[100];

    static long fibonacciMemo(int n) {
        if (n <= 1)
            return n;
        if (memo[n] != 0)
            return memo[n];
        memo[n] = fibonacciMemo(n - 1) + fibonacciMemo(n - 2);
        return memo[n];
    }

    // ========== 汉诺塔 ==========
    static int moveCount = 0;

    static void hanoi(int n, char from, char to, char aux) {
        if (n == 1) {
            moveCount++;
            System.out.println("  " + moveCount + ". " + from + " → " + to);
            return;
        }
        hanoi(n - 1, from, aux, to); // 将 n-1 个盘子从 from 移到 aux
        moveCount++;
        System.out.println("  " + moveCount + ". " + from + " → " + to); // 移动最大的盘子
        hanoi(n - 1, aux, to, from); // 将 n-1 个盘子从 aux 移到 to
    }

    // ========== 二分查找 ==========
    static int binarySearchRecursive(int[] arr, int target, int left, int right) {
        if (left > right)
            return -1; // 基准条件: 未找到

        int mid = left + (right - left) / 2;
        if (arr[mid] == target)
            return mid; // 基准条件: 找到

        if (arr[mid] > target) {
            return binarySearchRecursive(arr, target, left, mid - 1);
        } else {
            return binarySearchRecursive(arr, target, mid + 1, right);
        }
    }

    // ========== 求和 ==========
    static int sumRecursive(int n) {
        if (n == 1)
            return 1;
        return n + sumRecursive(n - 1);
    }

    static int arraySum(int[] arr, int index) {
        if (index >= arr.length)
            return 0;
        return arr[index] + arraySum(arr, index + 1);
    }

    // ========== 字符串处理 ==========
    static String reverse(String str) {
        if (str.isEmpty())
            return str;
        return reverse(str.substring(1)) + str.charAt(0);
    }

    static boolean isPalindrome(String str) {
        if (str.length() <= 1)
            return true;
        if (str.charAt(0) != str.charAt(str.length() - 1))
            return false;
        return isPalindrome(str.substring(1, str.length() - 1));
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 递归要素: 基准条件 + 递归步骤
 * 2. 经典递归: 阶乘、斐波那契、汉诺塔
 * 3. 记忆化: 避免重复计算，提高效率
 * 4. 尾递归: 递归调用在最后，理论上可优化
 * 5. 递归 vs 迭代: 各有优劣，根据场景选择
 * 
 * 🏃 练习:
 * 1. 实现递归版的快速排序
 * 2. 实现递归版的归并排序
 * 3. 实现求最大公约数 (欧几里得算法)
 * 4. 实现全排列生成
 */
