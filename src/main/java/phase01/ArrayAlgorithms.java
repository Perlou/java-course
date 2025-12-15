package phase01;

import java.util.Arrays;

/**
 * Phase 1 - Lesson 8: 数组算法
 * 
 * 🎯 学习目标:
 * 1. 实现常见的排序算法
 * 2. 实现常见的查找算法
 * 3. 理解算法的时间复杂度
 */
public class ArrayAlgorithms {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 8: 数组算法 ===\n");

        // ==================== 1. 冒泡排序 ====================
        System.out.println("【1. 冒泡排序】O(n²)");

        int[] bubbleArr = { 64, 34, 25, 12, 22, 11, 90 };
        System.out.println("原数组: " + Arrays.toString(bubbleArr));

        bubbleSort(bubbleArr);
        System.out.println("排序后: " + Arrays.toString(bubbleArr));

        // ==================== 2. 选择排序 ====================
        System.out.println("\n【2. 选择排序】O(n²)");

        int[] selectArr = { 64, 34, 25, 12, 22, 11, 90 };
        System.out.println("原数组: " + Arrays.toString(selectArr));

        selectionSort(selectArr);
        System.out.println("排序后: " + Arrays.toString(selectArr));

        // ==================== 3. 插入排序 ====================
        System.out.println("\n【3. 插入排序】O(n²)");

        int[] insertArr = { 64, 34, 25, 12, 22, 11, 90 };
        System.out.println("原数组: " + Arrays.toString(insertArr));

        insertionSort(insertArr);
        System.out.println("排序后: " + Arrays.toString(insertArr));

        // ==================== 4. 快速排序 ====================
        System.out.println("\n【4. 快速排序】O(n log n)");

        int[] quickArr = { 64, 34, 25, 12, 22, 11, 90 };
        System.out.println("原数组: " + Arrays.toString(quickArr));

        quickSort(quickArr, 0, quickArr.length - 1);
        System.out.println("排序后: " + Arrays.toString(quickArr));

        // ==================== 5. 线性查找 ====================
        System.out.println("\n【5. 线性查找】O(n)");

        int[] searchArr = { 10, 25, 30, 45, 50, 65, 70 };
        int target = 45;

        int linearResult = linearSearch(searchArr, target);
        System.out.println("在 " + Arrays.toString(searchArr) + " 中查找 " + target);
        System.out.println("线性查找结果: 索引 " + linearResult);

        // ==================== 6. 二分查找 ====================
        System.out.println("\n【6. 二分查找】O(log n)");

        int binaryResult = binarySearch(searchArr, target);
        System.out.println("二分查找结果: 索引 " + binaryResult);

        // 递归版本
        int binaryRecResult = binarySearchRecursive(searchArr, target, 0, searchArr.length - 1);
        System.out.println("递归二分查找结果: 索引 " + binaryRecResult);

        // ==================== 7. 其他常用算法 ====================
        System.out.println("\n【7. 其他常用算法】");

        int[] arr = { 3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5 };

        // 数组去重（统计不同元素）
        System.out.println("原数组: " + Arrays.toString(arr));
        int[] unique = removeDuplicates(arr);
        System.out.println("去重后: " + Arrays.toString(unique));

        // 找出出现次数最多的元素
        int mostFrequent = findMostFrequent(arr);
        System.out.println("出现最多的元素: " + mostFrequent);

        // ==================== 8. 算法复杂度总结 ====================
        System.out.println("\n【8. 算法复杂度总结】");
        System.out.println("┌─────────────┬──────────────┬──────────────┐");
        System.out.println("│ 算法        │ 时间复杂度   │ 空间复杂度   │");
        System.out.println("├─────────────┼──────────────┼──────────────┤");
        System.out.println("│ 冒泡排序    │ O(n²)        │ O(1)         │");
        System.out.println("│ 选择排序    │ O(n²)        │ O(1)         │");
        System.out.println("│ 插入排序    │ O(n²)        │ O(1)         │");
        System.out.println("│ 快速排序    │ O(n log n)   │ O(log n)     │");
        System.out.println("│ 线性查找    │ O(n)         │ O(1)         │");
        System.out.println("│ 二分查找    │ O(log n)     │ O(1)         │");
        System.out.println("└─────────────┴──────────────┴──────────────┘");

        System.out.println("\n✅ Phase 1 - Lesson 8 完成！");
    }

    // ========== 排序算法 ==========

    /**
     * 冒泡排序
     * 思想: 相邻元素两两比较，大的往后移
     */
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            // 如果没有交换，说明已排序
            if (!swapped)
                break;
        }
    }

    /**
     * 选择排序
     * 思想: 每次选择最小的元素放到前面
     */
    static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            // 交换
            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
        }
    }

    /**
     * 插入排序
     * 思想: 将元素插入到已排序部分的正确位置
     */
    static void insertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            // 将比 key 大的元素后移
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    /**
     * 快速排序
     * 思想: 分治法，选择基准元素，分成两部分递归排序
     */
    static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIdx = partition(arr, low, high);
            quickSort(arr, low, pivotIdx - 1);
            quickSort(arr, pivotIdx + 1, high);
        }
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    // ========== 查找算法 ==========

    /**
     * 线性查找
     */
    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 二分查找（迭代版）
     * 前提: 数组必须有序
     */
    static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2; // 防止溢出
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 二分查找（递归版）
     */
    static int binarySearchRecursive(int[] arr, int target, int left, int right) {
        if (left > right) {
            return -1;
        }
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            return binarySearchRecursive(arr, target, mid + 1, right);
        } else {
            return binarySearchRecursive(arr, target, left, mid - 1);
        }
    }

    // ========== 其他算法 ==========

    /**
     * 数组去重
     */
    static int[] removeDuplicates(int[] arr) {
        int[] sorted = Arrays.copyOf(arr, arr.length);
        Arrays.sort(sorted);

        int uniqueCount = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] != sorted[i - 1]) {
                uniqueCount++;
            }
        }

        int[] result = new int[uniqueCount];
        result[0] = sorted[0];
        int index = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] != sorted[i - 1]) {
                result[index++] = sorted[i];
            }
        }
        return result;
    }

    /**
     * 找出出现次数最多的元素
     */
    static int findMostFrequent(int[] arr) {
        int[] sorted = Arrays.copyOf(arr, arr.length);
        Arrays.sort(sorted);

        int maxCount = 1, currentCount = 1;
        int maxElement = sorted[0];

        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] == sorted[i - 1]) {
                currentCount++;
            } else {
                currentCount = 1;
            }
            if (currentCount > maxCount) {
                maxCount = currentCount;
                maxElement = sorted[i];
            }
        }
        return maxElement;
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. 冒泡排序: 简单但效率低 O(n²)
 * 2. 选择排序: 简单但效率低 O(n²)
 * 3. 插入排序: 对近乎有序的数组效率高
 * 4. 快速排序: 实际最常用，平均 O(n log n)
 * 5. 线性查找: 无序数组适用 O(n)
 * 6. 二分查找: 有序数组适用 O(log n)
 * 
 * 🏃 练习:
 * 1. 实现归并排序
 * 2. 实现堆排序
 * 3. 实现查找第 k 大的元素
 * 4. 实现数组的旋转
 */
