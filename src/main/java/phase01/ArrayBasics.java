package phase01;

import java.util.Arrays;

/**
 * Phase 1 - Lesson 7: 数组基础
 * 
 * 🎯 学习目标:
 * 1. 掌握数组的创建和初始化
 * 2. 掌握数组的遍历和常用操作
 * 3. 理解数组在内存中的存储
 * 4. 了解 Arrays 工具类的使用
 */
public class ArrayBasics {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 7: 数组基础 ===\n");

        // ==================== 1. 数组的声明和创建 ====================
        System.out.println("【1. 数组的声明和创建】");

        // 方式1: 先声明后创建
        int[] arr1;
        arr1 = new int[5]; // 创建长度为5的数组，默认值为0
        System.out.println("int[] arr1 = new int[5] → " + Arrays.toString(arr1));

        // 方式2: 声明时创建
        int[] arr2 = new int[3];
        System.out.println("int[] arr2 = new int[3] → " + Arrays.toString(arr2));

        // 方式3: 静态初始化
        int[] arr3 = { 1, 2, 3, 4, 5 };
        System.out.println("int[] arr3 = {1,2,3,4,5} → " + Arrays.toString(arr3));

        // 方式4: 匿名数组
        int[] arr4 = new int[] { 10, 20, 30 };
        System.out.println("int[] arr4 = new int[]{10,20,30} → " + Arrays.toString(arr4));

        // ==================== 2. 数组的访问和修改 ====================
        System.out.println("\n【2. 数组的访问和修改】");

        int[] numbers = { 10, 20, 30, 40, 50 };
        System.out.println("原数组: " + Arrays.toString(numbers));
        System.out.println("数组长度: " + numbers.length);
        System.out.println("第一个元素 (索引0): " + numbers[0]);
        System.out.println("最后一个元素 (索引length-1): " + numbers[numbers.length - 1]);

        // 修改元素
        numbers[2] = 300;
        System.out.println("修改 numbers[2] = 300 后: " + Arrays.toString(numbers));

        // ==================== 3. 数组遍历 ====================
        System.out.println("\n【3. 数组遍历】");

        String[] fruits = { "Apple", "Banana", "Orange", "Grape" };

        // 普通 for 循环
        System.out.print("普通 for: ");
        for (int i = 0; i < fruits.length; i++) {
            System.out.print(fruits[i] + " ");
        }
        System.out.println();

        // 增强 for 循环
        System.out.print("for-each: ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();

        // 使用 Arrays.toString()
        System.out.println("Arrays.toString(): " + Arrays.toString(fruits));

        // ==================== 4. 数组的默认值 ====================
        System.out.println("\n【4. 数组的默认值】");

        int[] intArr = new int[3];
        double[] doubleArr = new double[3];
        boolean[] boolArr = new boolean[3];
        String[] strArr = new String[3];

        System.out.println("int[]: " + Arrays.toString(intArr) + " (默认 0)");
        System.out.println("double[]: " + Arrays.toString(doubleArr) + " (默认 0.0)");
        System.out.println("boolean[]: " + Arrays.toString(boolArr) + " (默认 false)");
        System.out.println("String[]: " + Arrays.toString(strArr) + " (默认 null)");

        // ==================== 5. Arrays 工具类 ====================
        System.out.println("\n【5. Arrays 工具类】");

        int[] data = { 5, 2, 8, 1, 9, 3 };
        System.out.println("原数组: " + Arrays.toString(data));

        // 排序
        int[] sorted = data.clone(); // 先复制
        Arrays.sort(sorted);
        System.out.println("排序后: " + Arrays.toString(sorted));

        // 二分查找（需要先排序）
        int index = Arrays.binarySearch(sorted, 5);
        System.out.println("查找 5 的索引: " + index);

        // 填充
        int[] filled = new int[5];
        Arrays.fill(filled, 7);
        System.out.println("填充 7: " + Arrays.toString(filled));

        // 复制
        int[] copied = Arrays.copyOf(data, 8); // 扩容复制
        System.out.println("复制扩容: " + Arrays.toString(copied));

        int[] range = Arrays.copyOfRange(data, 1, 4); // 范围复制
        System.out.println("范围复制 [1,4): " + Arrays.toString(range));

        // 比较
        int[] a = { 1, 2, 3 };
        int[] b = { 1, 2, 3 };
        int[] c = { 1, 2, 4 };
        System.out.println("a.equals(b): " + Arrays.equals(a, b));
        System.out.println("a.equals(c): " + Arrays.equals(a, c));

        // ==================== 6. 数组作为参数和返回值 ====================
        System.out.println("\n【6. 数组作为参数和返回值】");

        int[] original = { 1, 2, 3, 4, 5 };
        System.out.println("原数组: " + Arrays.toString(original));

        // 数组作为参数（引用传递）
        modifyArray(original);
        System.out.println("方法修改后: " + Arrays.toString(original));

        // 数组作为返回值
        int[] doubled = doubleArray(original);
        System.out.println("返回翻倍数组: " + Arrays.toString(doubled));

        // ==================== 7. 可变参数 (varargs) ====================
        System.out.println("\n【7. 可变参数 (varargs)】");

        System.out.println("sum(1, 2, 3) = " + sum(1, 2, 3));
        System.out.println("sum(10, 20) = " + sum(10, 20));
        System.out.println("sum(1, 2, 3, 4, 5) = " + sum(1, 2, 3, 4, 5));

        // ==================== 8. 常见数组操作 ====================
        System.out.println("\n【8. 常见数组操作】");

        int[] arr = { 3, 1, 4, 1, 5, 9, 2, 6 };

        // 求最大值
        int max = arr[0];
        for (int num : arr) {
            if (num > max)
                max = num;
        }
        System.out.println("最大值: " + max);

        // 求和
        int total = 0;
        for (int num : arr) {
            total += num;
        }
        System.out.println("总和: " + total);

        // 求平均值
        double average = (double) total / arr.length;
        System.out.printf("平均值: %.2f%n", average);

        // 反转数组
        int[] reversed = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            reversed[i] = arr[arr.length - 1 - i];
        }
        System.out.println("反转: " + Arrays.toString(reversed));

        System.out.println("\n✅ Phase 1 - Lesson 7 完成！");
    }

    // 修改数组元素
    static void modifyArray(int[] arr) {
        arr[0] = 100; // 会修改原数组
    }

    // 返回一个新数组
    static int[] doubleArray(int[] arr) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i] * 2;
        }
        return result;
    }

    // 可变参数方法
    static int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 数组创建: new int[n] 或 {1,2,3}
 * 2. 索引从 0 开始，最大索引是 length-1
 * 3. 数组长度固定，创建后不能改变
 * 4. Arrays 工具类: sort, binarySearch, fill, copy, equals
 * 5. 数组是引用类型，作为参数传递时共享数据
 * 6. 可变参数 int... 本质是数组
 * 
 * 🏃 练习:
 * 1. 实现冒泡排序
 * 2. 实现二分查找
 * 3. 找出数组中的第二大元素
 * 4. 合并两个有序数组
 */
