package phase01;

/**
 * Phase 1 - Lesson 5: 循环结构
 * 
 * 🎯 学习目标:
 * 1. 掌握 for、while、do-while 循环
 * 2. 理解 break 和 continue 的使用
 * 3. 掌握增强 for 循环
 * 4. 了解循环的嵌套和优化
 */
public class LoopDemo {

    public static void main(String[] args) {
        System.out.println("=== Phase 1 - Lesson 5: 循环结构 ===\n");

        // ==================== 1. for 循环 ====================
        System.out.println("【1. for 循环】");

        // 基础 for 循环
        System.out.print("1 到 5: ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 递减循环
        System.out.print("5 到 1: ");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 步长为 2
        System.out.print("奇数 (步长2): ");
        for (int i = 1; i <= 10; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 省略部分
        System.out.println("\nfor 循环的灵活写法:");
        int j = 0;
        for (; j < 3;) { // 省略初始化和更新
            System.out.print(j++ + " ");
        }
        System.out.println("(省略初始化和更新部分)");

        // ==================== 2. while 循环 ====================
        System.out.println("\n【2. while 循环】");

        // 基础 while
        int count = 1;
        System.out.print("while 循环: ");
        while (count <= 5) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();

        // 计算阶乘
        int n = 5;
        int factorial = 1;
        int temp = n;
        while (temp > 0) {
            factorial *= temp;
            temp--;
        }
        System.out.println(n + "! = " + factorial);

        // ==================== 3. do-while 循环 ====================
        System.out.println("\n【3. do-while 循环】");

        // do-while 至少执行一次
        int x = 1;
        System.out.print("do-while 循环: ");
        do {
            System.out.print(x + " ");
            x++;
        } while (x <= 5);
        System.out.println();

        // 对比 while 和 do-while
        System.out.println("\n对比 (条件一开始就为 false):");
        int y = 10;

        System.out.print("while (y < 5): ");
        while (y < 5) {
            System.out.print(y + " "); // 不执行
            y++;
        }
        System.out.println("(不执行)");

        y = 10;
        System.out.print("do-while (y < 5): ");
        do {
            System.out.print(y + " "); // 执行一次
            y++;
        } while (y < 5);
        System.out.println("(执行了一次)");

        // ==================== 4. 增强 for 循环 (for-each) ====================
        System.out.println("\n【4. 增强 for 循环 (for-each)】");

        int[] numbers = { 10, 20, 30, 40, 50 };

        System.out.print("for-each 遍历数组: ");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();

        // 遍历字符串
        String text = "Hello";
        System.out.print("遍历字符串字符: ");
        for (char c : text.toCharArray()) {
            System.out.print(c + " ");
        }
        System.out.println();

        // ==================== 5. break 和 continue ====================
        System.out.println("\n【5. break 和 continue】");

        // break: 跳出循环
        System.out.print("break 示例 (遇到5停止): ");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) {
                break;
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // continue: 跳过当前迭代
        System.out.print("continue 示例 (跳过偶数): ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // ==================== 6. 带标签的 break/continue ====================
        System.out.println("\n【6. 带标签的 break/continue】");

        // 带标签的 break（跳出外层循环）
        System.out.println("带标签 break:");
        outer: for (int i = 1; i <= 3; i++) {
            for (int k = 1; k <= 3; k++) {
                System.out.println("  i=" + i + ", k=" + k);
                if (i == 2 && k == 2) {
                    System.out.println("  break outer!");
                    break outer; // 跳出外层循环
                }
            }
        }

        // ==================== 7. 嵌套循环 ====================
        System.out.println("\n【7. 嵌套循环】");

        // 打印九九乘法表
        System.out.println("九九乘法表:");
        for (int i = 1; i <= 9; i++) {
            for (int k = 1; k <= i; k++) {
                System.out.printf("%d×%d=%d\t", k, i, k * i);
            }
            System.out.println();
        }

        // 打印图形
        System.out.println("\n打印三角形:");
        int rows = 5;
        for (int i = 1; i <= rows; i++) {
            // 打印空格
            for (int k = 1; k <= rows - i; k++) {
                System.out.print(" ");
            }
            // 打印星号
            for (int k = 1; k <= 2 * i - 1; k++) {
                System.out.print("*");
            }
            System.out.println();
        }

        // ==================== 8. 循环优化技巧 ====================
        System.out.println("\n【8. 循环优化技巧】");

        // 1. 将不变的计算移到循环外
        System.out.println("1. 循环不变量外提:");
        System.out.println("   优化前: for (int i = 0; i < list.size(); i++)");
        System.out.println("   优化后: int size = list.size(); for (int i = 0; i < size; i++)");

        // 2. 尽量减少循环内的对象创建
        System.out.println("\n2. 减少循环内对象创建:");
        System.out.println("   优化前: 在循环内 new StringBuilder()");
        System.out.println("   优化后: 在循环外创建，循环内使用 setLength(0) 重置");

        // 3. 使用合适的循环类型
        System.out.println("\n3. 选择合适的循环类型:");
        System.out.println("   - 需要索引: 普通 for");
        System.out.println("   - 只遍历: for-each");
        System.out.println("   - 条件未知: while");
        System.out.println("   - 至少执行一次: do-while");

        System.out.println("\n✅ Phase 1 - Lesson 5 完成！");
    }
}

/*
 * 📚 知识点总结:
 * 
 * 1. for 循环: 知道循环次数时使用
 * 2. while 循环: 条件为 true 时循环
 * 3. do-while: 至少执行一次
 * 4. for-each: 遍历数组和集合的简洁方式
 * 5. break: 跳出循环
 * 6. continue: 跳过当前迭代
 * 7. 标签: 可以跳出多层嵌套
 * 
 * 🏃 练习:
 * 1. 实现斐波那契数列前20项
 * 2. 打印一个钻石形状
 * 3. 判断一个数是否为素数
 * 4. 找出100以内的所有素数
 */
